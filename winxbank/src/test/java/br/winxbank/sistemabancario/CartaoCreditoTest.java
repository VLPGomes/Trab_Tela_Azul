package br.winxbank.sistemabancario;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

import br.winxbank.tempo.Ano;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

class CartaoCreditoTest {

    private CartaoCredito cartao;
    private InputStream originalSystemIn;

    @BeforeEach
    void prepararCenario() {
        cartao = new CartaoCredito(4581624, 562);
        originalSystemIn = System.in;
    }

    @AfterEach
    void restaurarSystemIn() {
        System.setIn(originalSystemIn);
    }

    @Test
    void creditarFatura() {
        try (MockedStatic<Ano> mockedAno = mockStatic(Ano.class)) {
            Ano mockAno = mock(Ano.class);
            mockedAno.when(Ano::getInstancia).thenReturn(mockAno);
            when(mockAno.getIndexMesAtual()).thenReturn(11);
            when(mockAno.getMesAtual()).thenReturn("Novembro");

            cartao.creditar(200.0);
            
            assertEquals(200.0, cartao.getFatura(), 0.001);
            verify(mockAno, times(1)).getIndexMesAtual();
            verify(mockAno, times(1)).getMesAtual();
        }
    }

    @Test
    void tentativaEstourarLimite() {
        try (MockedStatic<Ano> mockedAno = mockStatic(Ano.class)) {
            Ano mockAno = mock(Ano.class);
            mockedAno.when(Ano::getInstancia).thenReturn(mockAno);

            cartao.creditar(400.0);
            cartao.creditar(500.0);
            cartao.creditar(300.0);
            
            assertEquals(900.0, cartao.getFatura(),0.001);
        }
    }

    @Test
    void creditoNoLimiteDoCartao() {
        try (MockedStatic<Ano> mockedAno = mockStatic(Ano.class)) {
            Ano mockAno = mock(Ano.class);
            mockedAno.when(Ano::getInstancia).thenReturn(mockAno);

            cartao.creditar(1000.0); 
            assertEquals(1000.0, cartao.getFatura(), 0.001);
        }
    }

    @Test
    void creditoAcimaLimiteDoCartao() {
        try (MockedStatic<Ano> mockedAno = mockStatic(Ano.class)) {
            Ano mockAno = mock(Ano.class);
            mockedAno.when(Ano::getInstancia).thenReturn(mockAno);

            cartao.creditar(1300.0); 
            assertEquals(0.0, cartao.getFatura(), 0.001);
        }
    }

    @Test
    void creditoAposTentativaAcimaLimite() {
        try (MockedStatic<Ano> mockedAno = mockStatic(Ano.class)) {
            Ano mockAno = mock(Ano.class);
            mockedAno.when(Ano::getInstancia).thenReturn(mockAno);

            cartao.creditar(1500.0);
            cartao.creditar(400.0);
            cartao.setFatura(-300.0);
            
            assertEquals(100.0, cartao.getFatura(), "O cartão deve processar a transação válida mesmo após a falha anterior.");
        }
    }

    @Test
    void atingirLimiteComSomaDeCreditos() {
        try (MockedStatic<Ano> mockedAno = mockStatic(Ano.class)) {
            Ano mockAno = mock(Ano.class);
            mockedAno.when(Ano::getInstancia).thenReturn(mockAno);

            cartao.creditar(250.0);
            cartao.creditar(300.0);
            cartao.creditar(400.0);
            cartao.creditar(50.0);
            cartao.creditar(10.0);
            
            assertEquals(1000.0, cartao.getFatura(),0.001, "A soma de várias compras deve respeitar o limite de 1000.0.");
        }
    }

    @Test
    void limiteNegativoNoSetFatura() {
        try (MockedStatic<Ano> mockedAno = mockStatic(Ano.class)) {
            Ano mockAno = mock(Ano.class);
            mockedAno.when(Ano::getInstancia).thenReturn(mockAno);

            cartao.creditar(200.0);
            cartao.setFatura(-300.0);
            
            assertTrue(cartao.getFatura() <= 0);
        }
    }

    @Test
    void pagamentoParcialFatura() {
        try (MockedStatic<Ano> mockedAno = mockStatic(Ano.class)) {
            Ano mockAno = mock(Ano.class);
            mockedAno.when(Ano::getInstancia).thenReturn(mockAno);

            cartao.creditar(1000.0);
            cartao.setFatura(-500.0);
            
            assertEquals(500.0, cartao.getFatura());
        }
    }

    @Test
    void pagamentoTotalFatura() {
        try (MockedStatic<Ano> mockedAno = mockStatic(Ano.class)) {
            Ano mockAno = mock(Ano.class);
            mockedAno.when(Ano::getInstancia).thenReturn(mockAno);

            cartao.creditar(1000.0);
            cartao.setFatura(-1000.0);
            
            assertEquals(0.0, cartao.getFatura());
        }
    }

    @Test
    void cobrarJurosMudancaDeMes() {
        try (MockedStatic<Ano> mockedAno = mockStatic(Ano.class);
             MockedStatic<Banco> mockedBanco = mockStatic(Banco.class)) {
            
            Ano mockAno = mock(Ano.class);
            mockedAno.when(Ano::getInstancia).thenReturn(mockAno);
            
            Banco mockBanco = mock(Banco.class);
            mockedBanco.when(Banco::getInstancia).thenReturn(mockBanco);
            
            when(mockAno.getIndexMesAtual()).thenReturn(11);
            cartao.creditar(600.0);
            
            when(mockAno.getIndexMesAtual()).thenReturn(12); 
            cartao.cobrarJurus();
            
            assertTrue(cartao.getFatura() > 600.0, "A fatura deveria ter aumentado por causa dos juros.");
            verify(mockBanco, times(1)).setReceitas(anyDouble());
        }
    }

    @Test
    void naoCobrarJurosSeMesIgual() {
        try (MockedStatic<Ano> mockedAno = mockStatic(Ano.class);
             MockedStatic<Banco> mockedBanco = mockStatic(Banco.class)) {
            
            Ano mockAno = mock(Ano.class);
            mockedAno.when(Ano::getInstancia).thenReturn(mockAno);      
            
            Banco mockBanco = mock(Banco.class);
            mockedBanco.when(Banco::getInstancia).thenReturn(mockBanco);
            
            when(mockAno.getIndexMesAtual()).thenReturn(11);
            cartao.creditar(600.0);
            
            cartao.cobrarJurus();
            
            assertEquals(600.0, cartao.getFatura(), "A fatura não deve mudar se o mês for o mesmo.");
            verify(mockBanco, never()).setReceitas(anyDouble());
        }
    }

    @Test
    void naoCobrarJurosSeFaturaPaga() {
        try (MockedStatic<Ano> mockedAno = mockStatic(Ano.class);
             MockedStatic<Banco> mockedBanco = mockStatic(Banco.class)) {
            
            Ano mockAno = mock(Ano.class);
            mockedAno.when(Ano::getInstancia).thenReturn(mockAno);
            
            Banco mockBanco = mock(Banco.class);
            mockedBanco.when(Banco::getInstancia).thenReturn(mockBanco);
            
            when(mockAno.getIndexMesAtual()).thenReturn(11);
            cartao.creditar(500.0);
            cartao.setFatura(-500.0); 
            
            when(mockAno.getIndexMesAtual()).thenReturn(12); 
            cartao.cobrarJurus();
            
            assertEquals(0.0, cartao.getFatura());
            verify(mockBanco, never()).setReceitas(anyDouble());
        }
    }

    @Test
    void naoCobrarJurosSeSaldoNegativo() {
        try (MockedStatic<Ano> mockedAno = mockStatic(Ano.class);
             MockedStatic<Banco> mockedBanco = mockStatic(Banco.class)) {
            
            Ano mockAno = mock(Ano.class);
            mockedAno.when(Ano::getInstancia).thenReturn(mockAno);
            
            Banco mockBanco = mock(Banco.class);
            mockedBanco.when(Banco::getInstancia).thenReturn(mockBanco);
            
            when(mockAno.getIndexMesAtual()).thenReturn(11);
            cartao.creditar(400.0);
            cartao.setFatura(-500.0); 
            
            when(mockAno.getIndexMesAtual()).thenReturn(12); 
            cartao.cobrarJurus();
            
            assertEquals(-100.0, cartao.getFatura(), "Não deve aplicar juros sobre saldo negativo(crédito).");
            verify(mockBanco, never()).setReceitas(anyDouble());
        }
    }

    @Test
    void verificarInteracaoComBancoAoCobrarJuros() {
        try (MockedStatic<Ano> mockedAno = mockStatic(Ano.class);
             MockedStatic<Banco> mockedBanco = mockStatic(Banco.class)) {
            
            Ano mockAno = mock(Ano.class);
            mockedAno.when(Ano::getInstancia).thenReturn(mockAno);
            
            Banco mockBanco = mock(Banco.class);
            mockedBanco.when(Banco::getInstancia).thenReturn(mockBanco);
            
            when(mockAno.getIndexMesAtual()).thenReturn(11, 12);
            
            cartao.creditar(500.0);
            cartao.cobrarJurus();   
            
            verify(mockBanco, times(1)).setReceitas(anyDouble());
        }
    }

    @Test
    void ajustarNovoLimite() {
        String novoLimite = "3000,0";
        System.setIn(new ByteArrayInputStream(novoLimite.getBytes()));
        
        cartao.ajustarLimite();
        
        try (MockedStatic<Ano> mockedAno = mockStatic(Ano.class)) {
            Ano mockAno = mock(Ano.class);
            mockedAno.when(Ano::getInstancia).thenReturn(mockAno);
            
            cartao.creditar(2500.0);
            assertEquals(2500.0, cartao.getFatura(), 0.001, "O limite deve ter sido aumentado para 3000, aceitando a compra de 2500.");
        }
    }

    @Test
    void debitarContaCorretamente() {
        Conta contaMock = mock(Conta.class);
        
        cartao.debitar(contaMock, 150.0);
        
        verify(contaMock, times(1)).setSaldo(-150.0);
    }
}
