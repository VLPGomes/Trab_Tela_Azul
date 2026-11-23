package br.winxbank.sistemabancario;
import br.winxbank.tempo.Ano;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ContaCorrenteIntegracaoTest {
    private ContaCorrente conta;
    private CartaoCredito cartaoCredito;
    private Banco banco;

    @BeforeEach
    void preparar() {
        Ano.getInstancia().setMesAtual("Janeiro");
        banco = Banco.getInstancia();
        banco.receitas = 0.0;
        banco.despesas = 0.0;
        cartaoCredito = new CartaoCredito(2222, 456);
        conta = new ContaCorrente(201, 1000.0, new Cartao(1111, 123), 0.0, cartaoCredito);
    }

    @Test
    void testCompraNoCreditoEPagamentoParcial() {
        assertTrue(cartaoCredito.creditar(400.0));
        assertAll(
        	() -> assertEquals(400.0, cartaoCredito.getFatura(), 0.001),
        	() -> assertEquals(1000.0, conta.getSaldo(), 0.001)
        );
        conta.pagarFatura(150.0);
        assertAll(
        	() -> assertEquals(850.0, conta.getSaldo(), 0.001),
        	() -> assertEquals(250.0, cartaoCredito.getFatura(), 0.001)
        );
    }

    @Test
    void testPagamentoIntegralQuitaFatura() {
        assertTrue(cartaoCredito.creditar(400.0));
        conta.pagarFatura(400.0);
        assertAll(
        	() -> assertEquals(600.0, conta.getSaldo(), 0.001),
        	() -> assertEquals(0.0, cartaoCredito.getFatura(), 0.001)
        );
    }

    @Test
    void testPagamentoMaiorQueFatura() {
        assertTrue(cartaoCredito.creditar(400.0));
        assertThrows(IllegalArgumentException.class, () -> conta.pagarFatura(500.0));
        assertAll(
        	() -> assertEquals(1000.0, conta.getSaldo(), 0.001),
        	() -> assertEquals(400.0, cartaoCredito.getFatura(), 0.001)
        );
    }

    @Test
    void testTaxaDeManutencao() {
        conta.descontarTaxa();
        assertAll(
        	() -> assertEquals(987.0, conta.getSaldo(), 0.001),
        	() -> assertEquals(13.0, banco.getReceitas(), 0.001),
        	() -> assertEquals(0.0, banco.getDespesas(), 0.001),
        	() -> assertEquals(1, conta.getExtrato().size()),
        	() -> assertEquals(13.0, conta.getExtrato().get(0).getDinheiroMovimentado(), 0.001),
        	() -> assertEquals(Movimentacao.TipoDaMovimentacao.SAIDA, conta.getExtrato().get(0).getTipoDaMovimentacao()),
        	() -> assertEquals("Janeiro", conta.getExtrato().get(0).getMesAtual())
        );
    }

    @Test
    void testDuasTaxas() {
        conta.descontarTaxa();
        conta.descontarTaxa();
        assertAll(
        	() -> assertEquals(974.0, conta.getSaldo(), 0.001),
        	() -> assertEquals(26.0, banco.getReceitas(), 0.001),
        	() -> assertEquals(2, conta.getExtrato().size())
        );
    }
    
    @Test
    void testPixEntreContaCorrenteEPoupanca() {
        ContaCorrente orig = new ContaCorrente(101, 1000.0, new Cartao(1111, 123), 500.0, new CartaoCredito(2222, 456));
        ContaPoupanca dest = new ContaPoupanca(102, 300.0, new Cartao(3333, 789), 200.0);
        orig.fazerPix(dest, 250.0);
        assertAll(
            () -> assertEquals(750.0, orig.getSaldo(), 0.001),
            () -> assertEquals(550.0, dest.getSaldo(), 0.001),
            () -> assertEquals(500.0, orig.getDividaDeEmprestimo(), 0.001),
            () -> assertEquals(200.0, dest.getDividaDeEmprestimo(), 0.001)
        );
    }

    @Test
    void testCartaoDebitoDescontaSaldo() {
        Cartao cartao = new Cartao(1111, 123);
        ContaCorrente conta = new ContaCorrente(103, 1000.0, cartao, 0.0, new CartaoCredito(2222, 456));
        cartao.debitar(conta, 175.0);
        assertAll(
            () -> assertSame(cartao, conta.getCartao()),
            () -> assertEquals(825.0, conta.getSaldo(), 0.001)
        );
    }

    @Test
    void testEmprestimoPagoSaldoDisponivel() {
        ContaCorrente conta = new ContaCorrente(104, 1000.0, new Cartao(1111, 123), 500.0, new CartaoCredito(2222, 456));
        conta.requisitarEmprestimo(400.0);
        assertAll(
            () -> assertEquals(1400.0, conta.getSaldo(), 0.001),
            () -> assertEquals(900.0, conta.getDividaDeEmprestimo(), 0.001)
        );
        conta.pagarParcelaDeEmprestimo(200.0);
        assertAll(
            () -> assertEquals(1200.0, conta.getSaldo(), 0.001),
            () -> assertEquals(700.0, conta.getDividaDeEmprestimo(), 0.001)
        );
    }

    @Test
    void testPixInvalidoNaoAlteraContas() {
        ContaCorrente orig = new ContaCorrente(105, 100.0, new Cartao(1111, 123), 0.0, new CartaoCredito(2222, 456));
        ContaPoupanca dest = new ContaPoupanca(106, 300.0, new Cartao(3333, 789), 0.0);
        assertThrows(IllegalArgumentException.class, () -> orig.fazerPix(dest, -10.0));
        assertAll(
            () -> assertEquals(100.0, orig.getSaldo(), 0.001),
            () -> assertEquals(300.0, dest.getSaldo(), 0.001)
        );
    }
}
