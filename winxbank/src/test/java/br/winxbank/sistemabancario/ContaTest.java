package br.winxbank.sistemabancario;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContaTest {
    private Conta conta;

    @BeforeEach
    void setUp() {
        conta = new Conta(123, 1000.0, null, 500.0) {
        	//Implementação dos métodos comprar e movimentacaoBancaria apenas para instanciar conta, que é abstrata
        	//Do contrário, ocorrerá um erro na execução
        	//Esses métodos serão testados corretamente nas classes de teste de ContaCorrente e ContaPoupanca
            @Override
            public void comprar(double valor) {
                saldo -= valor;
            }
            @Override
            public void movimentacaoBancaria(double valor) {
                saldo += valor;
            }
        };
    }

    @Test
    void testCriarConta() {
    	assertAll(
    		() -> assertEquals(123, conta.getNumeroConta()),
    		() -> assertEquals(1000.0, conta.getSaldo()),
    		() -> assertEquals(500.0, conta.getDividaDeEmprestimo()),
    		() -> assertNull(conta.getCartao()),
    		() -> assertNotNull(conta.getExtrato()),
    		() -> assertTrue(conta.getExtrato().isEmpty())
    	);
    }
    
    @Test
    void testPagarParcelaEmprestimo() {
        conta.pagarParcelaDeEmprestimo(200.0);
        assertEquals(300.0, conta.getDividaDeEmprestimo());
    }
    
    @Test
    void testRequisitarEmprestimo() {
        conta.requisitarEmprestimo(1000.0);
        assertEquals(1500.0, conta.getDividaDeEmprestimo());
    }
    
    @Test
    void testCobrarJurosEmprestimo() {
        conta.cobrarJurusEmprestimo();
        double dividaEsperada = 500.0 - (500.0 / 12.75);
        assertEquals(dividaEsperada, conta.getDividaDeEmprestimo(), 0.000001);
    }
    
    @Test
    void testCobrarJurosSemDivida() {
        Conta contaSemEmprestimo = new Conta(456, 1000.0, null, 0.0) {
            @Override
            public void comprar(double valor) {
                saldo -= valor;
            }
            @Override
            public void movimentacaoBancaria(double valor) {
                saldo += valor;
            }
        };
        contaSemEmprestimo.cobrarJurusEmprestimo();
        assertEquals(0.0, contaSemEmprestimo.getDividaDeEmprestimo());
    }
    
    @Test
    void testPix() {
        Conta contaDestino = new Conta(456, 500.0, null, 0.0) {
            @Override
            public void comprar(double valor) {
                saldo -= valor;
            }

            @Override
            public void movimentacaoBancaria(double valor) {
                saldo += valor;
            }
        };
        conta.fazerPix(contaDestino, 250.0);
        assertAll(
        	() -> assertEquals(750.0, contaDestino.getSaldo(), "Saldo na conta destino incorreto"),
        	() -> assertEquals(750.0, conta.getSaldo(), "Saldo na conta atual incorreto")
        );
    }
    
    @Test
    void testPixInvalido() {
        Conta contaDestino = new Conta(456, 500.0, null, 0.0) {
            @Override
            public void comprar(double valor) {
                saldo -= valor;
            }

            @Override
            public void movimentacaoBancaria(double valor) {
                saldo += valor;
            }
        };
        assertAll(
       		() -> assertThrows(IllegalArgumentException.class, () -> conta.fazerPix(contaDestino, -100.0), "Pix não deveria receber valor negativo"),
       		() -> assertThrows(IllegalArgumentException.class, () -> conta.fazerPix(contaDestino, 0.0), "Pix não deveria receber valor 0")
        );
    }
    
    @Test
    void testSaque() {
        conta.sacar(300.0);
        assertEquals(700.0, conta.getSaldo());
    }
    
    @Test
    void testSaqueInvalido() {
    	assertAll(
    		() -> assertThrows(IllegalArgumentException.class, () -> conta.sacar(-100.0), "Saque não deveria receber valor negativo"),
    		() -> assertThrows(IllegalArgumentException.class, () -> conta.sacar(0.0), "Saque não deveria receber valor 0")
    	);
    }
    
    @Test
    void testDeposito() {
        double valorDepositado = conta.depositar(500.0);
        assertEquals(500.0, valorDepositado);
        assertEquals(1500.0, conta.getSaldo());
    }
    
    @Test
    void testDepositoInvalido() {
    	assertAll(
    		() -> assertThrows(IllegalArgumentException.class, () -> conta.depositar(-100.0), "Depósito não deveria receber valor negativo"),
    		() -> assertThrows(IllegalArgumentException.class, () -> conta.depositar(0.0), "Depósito não deveria receber valor 0")
    	);
    }

    @Test
    void testSetSaldo() {
        conta.setSaldo(250.0);
        assertEquals(250.0, conta.getSaldo());
    }
    
    @Test
    void testAdicionarMovimentacaoExtrato() {
        Movimentacao movimentacao = new Movimentacao(200.0, Movimentacao.TipoDaMovimentacao.ENTRADA);
        conta.setExtrato(movimentacao);
        assertAll(
        	() -> assertEquals(1, conta.getExtrato().size()),
            () -> assertSame(movimentacao, conta.getExtrato().get(0))
        );
    }

    @Test
    void testAdicionarMultiplasMovimentacoes() {
        Movimentacao entrada = new Movimentacao(500.0, Movimentacao.TipoDaMovimentacao.ENTRADA);
        Movimentacao saida = new Movimentacao(200.0, Movimentacao.TipoDaMovimentacao.SAIDA);
        conta.setExtrato(entrada);
        conta.setExtrato(saida);
        assertAll(
        	() -> assertEquals(2, conta.getExtrato().size()),
            () -> assertSame(entrada, conta.getExtrato().get(0)),
            () -> assertSame(saida, conta.getExtrato().get(1))
        );
    }
}