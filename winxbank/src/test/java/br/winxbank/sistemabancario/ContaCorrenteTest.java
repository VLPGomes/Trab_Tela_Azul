package br.winxbank.sistemabancario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import br.winxbank.exception.ValueIsHigherThanBalanceException;
import br.winxbank.tempo.Ano;

class ContaCorrenteTest {
    private ContaCorrente conta;
    private Cartao cartaoDebito;
    private CartaoCredito cartaoCredito;

    @BeforeEach
    void setUp() {
        cartaoDebito = mock(Cartao.class);
        cartaoCredito = mock(CartaoCredito.class);
        conta = new ContaCorrente(123, 1000.0, cartaoDebito, 0.0, cartaoCredito);
    }

    @Test
    void testPagarFaturaParcialmente() {
        when(cartaoCredito.getFatura()).thenReturn(400.0);
        conta.pagarFatura(150.0);
        assertEquals(850.0, conta.getSaldo(), 0.001);
        verify(cartaoCredito).setFatura(-150.0);
    }

    @Test
    void testPagarFaturaIntegralmente() {
        when(cartaoCredito.getFatura()).thenReturn(400.0);
        conta.pagarFatura(400.0);
        assertEquals(600.0, conta.getSaldo(), 0.001);
        verify(cartaoCredito).setFatura(-400.0);
    }

    @Test
    void testPagarFaturaMaiorQueSaldo() {
        when(cartaoCredito.getFatura()).thenReturn(400.0);
        assertThrows(ValueIsHigherThanBalanceException.class, () -> conta.pagarFatura(1200.0));
        assertAll(
        	() -> assertEquals(1000.0, conta.getSaldo(), 0.001),
        	() -> verify(cartaoCredito, never()).setFatura(anyDouble())
        );
    }

    @Test
    void testPagarFaturaMaiorQueDivida() {
        when(cartaoCredito.getFatura()).thenReturn(400.0);
        assertThrows(IllegalArgumentException.class, () -> conta.pagarFatura(500.0));
        assertAll(
        	() -> assertEquals(1000.0, conta.getSaldo(), 0.001),
        	() -> verify(cartaoCredito, never()).setFatura(anyDouble())
        );
    }

    @Test
    void testDescontarTaxa() {
        Banco bancoMock = mock(Banco.class);
        try (MockedStatic<Banco> bancoStatic = mockStatic(Banco.class);
        	MockedStatic<Ano> anoStatic = mockStatic(Ano.class)) {
       		bancoStatic.when(Banco::getInstancia).thenReturn(bancoMock);
       		Ano anoMock = mock(Ano.class);
       		anoStatic.when(Ano::getInstancia).thenReturn(anoMock);
        		when(anoMock.getMesAtual()).thenReturn("Janeiro");
        		conta.descontarTaxa();
        		assertAll(
        			() -> assertEquals(987.0, conta.getSaldo(), 0.001),
        			() -> assertEquals(1, conta.getExtrato().size()),
        			() -> assertEquals(13.0, conta.getExtrato().get(0).getDinheiroMovimentado(), 0.001),
        			() -> assertEquals(Movimentacao.TipoDaMovimentacao.SAIDA, conta.getExtrato().get(0).getTipoDaMovimentacao())
        		);
        		verify(bancoMock).setReceitas(13.0);
        }
    }
}