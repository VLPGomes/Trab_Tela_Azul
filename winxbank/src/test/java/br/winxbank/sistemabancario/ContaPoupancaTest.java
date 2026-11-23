package br.winxbank.sistemabancario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import br.winxbank.tempo.Ano; 

class ContaPoupancaTest {
    private ContaPoupanca conta;
    private Cartao cartao;

    @BeforeEach
    void setUp() {
        cartao = mock(Cartao.class);
        conta = new ContaPoupanca(123, 1000.0, cartao, 0.0);
    }

    @Test
    void testAcrescentarRendimento() {
        Banco bancoMock = mock(Banco.class);
        try (MockedStatic<Banco> bancoStatic = mockStatic(Banco.class);
        MockedStatic<Ano> anoStatic = mockStatic(Ano.class)) {
        	bancoStatic.when(Banco::getInstancia).thenReturn(bancoMock);
        	Ano anoMock = mock(Ano.class);
        	anoStatic.when(Ano::getInstancia).thenReturn(anoMock);
        	when(anoMock.getMesAtual()).thenReturn("Janeiro");
            conta.acrescentarRendimento();
            assertAll(
            	() -> assertEquals(1008.0, conta.getSaldo(), 0.001),
            	() -> assertEquals(1, conta.getInformeRendimento().size()),
            	() -> assertEquals(8.0, conta.getInformeRendimento().get(0).getDinheiroMovimentado(), 0.001),
                () -> assertEquals(Movimentacao.TipoDaMovimentacao.ENTRADA, conta.getInformeRendimento().get(0).getTipoDaMovimentacao())
            );
            verify(bancoMock).setDespesas(8.0);
        }
    }

    @Test
    void testAcrescentarRendimentoComSaldoZero() {
        conta = new ContaPoupanca(123, 0.0, cartao, 0.0);
        Banco bancoMock = mock(Banco.class);
        try (MockedStatic<Banco> bancoStatic = mockStatic(Banco.class);
        MockedStatic<Ano> anoStatic = mockStatic(Ano.class)) {
        	bancoStatic.when(Banco::getInstancia).thenReturn(bancoMock);
            Ano anoMock = mock(Ano.class);
            anoStatic.when(Ano::getInstancia).thenReturn(anoMock);
            conta.acrescentarRendimento();
            assertAll(
            	() -> assertEquals(0.0, conta.getSaldo(), 0.001),
            	() -> assertTrue(conta.getInformeRendimento().isEmpty())
            );
            verify(bancoMock, never()).setDespesas(anyDouble());
        }
    }
}