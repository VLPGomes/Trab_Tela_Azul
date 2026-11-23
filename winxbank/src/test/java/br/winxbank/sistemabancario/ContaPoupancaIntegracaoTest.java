package br.winxbank.sistemabancario;
import br.winxbank.tempo.Ano;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ContaPoupancaIntegracaoTest {
    private ContaPoupanca conta;
    private Banco banco;

    @BeforeEach
    void preparar() {
        Ano.getInstancia().setMesAtual("Janeiro");
        banco = Banco.getInstancia();
        banco.receitas = 0.0;
        banco.despesas = 0.0;
        conta = new ContaPoupanca(301, 1000.0, new Cartao(1111, 123), 0.0);
    }

    @Test
    void testRendimento() {
        conta.acrescentarRendimento();
        assertAll(
        	() -> assertEquals(1008.0, conta.getSaldo(), 0.001),
        	() -> assertEquals(8.0, banco.getDespesas(), 0.001),
        	() -> assertEquals(0.0, banco.getReceitas(), 0.001),
        	() -> assertEquals(1, conta.getInformeRendimento().size()),
        	() -> assertEquals(8.0, conta.getInformeRendimento().get(0).getDinheiroMovimentado(), 0.001),
        	() -> assertEquals(Movimentacao.TipoDaMovimentacao.ENTRADA, conta.getInformeRendimento().get(0).getTipoDaMovimentacao()),
            () -> assertEquals("Janeiro", conta.getInformeRendimento().get(0).getMesAtual())
        );
    }

    @Test
    void testRendimentosSucessivos() {
        conta.acrescentarRendimento();
        Ano.getInstancia().setMesAtual("Fevereiro");
        conta.acrescentarRendimento();
        assertAll(
        	() -> assertEquals(1016.064, conta.getSaldo(), 0.001),
        	() -> assertEquals(16.064, banco.getDespesas(), 0.001),
        	() -> assertEquals(2, conta.getInformeRendimento().size()),
        	() -> assertEquals(8.0, conta.getInformeRendimento().get(0).getDinheiroMovimentado(), 0.001),
        	() -> assertEquals(8.064, conta.getInformeRendimento().get(1).getDinheiroMovimentado(), 0.001),
        	() -> assertEquals("Fevereiro", conta.getInformeRendimento().get(1).getMesAtual())
        );
    }

    @Test
    void testSaldoZeroRendimentoDespesa() {
        conta = new ContaPoupanca(302, 0.0, new Cartao(1111, 123), 0.0);
        conta.acrescentarRendimento();
        assertAll(
        	() -> assertEquals(0.0, conta.getSaldo(), 0.001),
        	() -> assertEquals(0.0, banco.getDespesas(), 0.001),
        	() -> assertTrue(conta.getInformeRendimento().isEmpty())
        );
    }

    @Test
    void testDepositoAntesDoRendimento() {
        conta.depositar(500.0);
        conta.acrescentarRendimento();
        assertAll(
        	() -> assertEquals(1512.0, conta.getSaldo(), 0.001),
        	() -> assertEquals(12.0, banco.getDespesas(), 0.001),
        	() -> assertEquals(12.0, conta.getInformeRendimento().get(0).getDinheiroMovimentado(), 0.001)
        );
    }
    
    @Test
    void testPixEntreContaPoupancaECorrente() {
        ContaPoupanca orig = new ContaPoupanca(107, 1000.0, new Cartao(1111, 123), 500.0);
        ContaCorrente dest = new ContaCorrente(108, 300.0, new Cartao(3333, 789), 200.0, new CartaoCredito(2222, 456));
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
        ContaPoupanca conta = new ContaPoupanca(109, 1000.0, cartao, 0.0);
        cartao.debitar(conta, 175.0);
        assertAll(
            () -> assertSame(cartao, conta.getCartao()),
            () -> assertEquals(825.0, conta.getSaldo(), 0.001)
        );
    }

    @Test
    void testEmprestimoPago() {
        ContaPoupanca conta = new ContaPoupanca(110, 1000.0, new Cartao(1111, 123), 500.0);
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
    void testPixInvalido() {
        ContaPoupanca orig = new ContaPoupanca(111, 100.0, new Cartao(1111, 123), 0.0);
        ContaCorrente dest = new ContaCorrente(112, 300.0, new Cartao(3333, 789), 0.0, new CartaoCredito(2222, 456));
        assertThrows(IllegalArgumentException.class, () -> orig.fazerPix(dest, -10.0));
        assertAll(
            () -> assertEquals(100.0, orig.getSaldo(), 0.001),
            () -> assertEquals(300.0, dest.getSaldo(), 0.001)
        );
    }
}
