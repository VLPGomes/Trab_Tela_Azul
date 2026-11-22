package br.winxbank.sistemaclientes;

import br.winxbank.sistemabancario.Banco;
import br.winxbank.sistemabancario.ContaCorrente;
import br.winxbank.sistemabancario.ContaPoupanca;
import br.winxbank.sistemabancario.Movimentacao;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RegistroDeClientesMockTest {

    private RegistroDeClientes registro;
    private final InputStream systemInOriginal = System.in;

    @BeforeEach
    void setUp() {
        registro = RegistroDeClientes.getInstancia();
        registro.limparListaDeClientes();
    }

    @AfterEach
    void tearDown() {
        System.setIn(systemInOriginal);
    }

    private void simularEntradaUsuario(String dados) {
        System.setIn(new ByteArrayInputStream(dados.getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    void deveCadastrarClienteComumIsolandoBancoEContaCorrente() {
        simularEntradaUsuario("Marcello Comum\n11122233344\n");

        ContaCorrente contaCorrenteMock = mock(ContaCorrente.class, RETURNS_DEEP_STUBS);
        when(contaCorrenteMock.getSaldo()).thenReturn(1500.0);

        Banco bancoMock = mock(Banco.class);
        when(bancoMock.abrirNovaConta()).thenReturn(contaCorrenteMock);

        try (MockedStatic<Banco> bancoStaticMock = mockStatic(Banco.class)) {
            bancoStaticMock.when(Banco::getInstancia).thenReturn(bancoMock);

            registro.cadastrarCliente();

            verify(bancoMock, times(1)).abrirNovaConta();
            verify(contaCorrenteMock, times(1)).setExtrato(any(Movimentacao.class));

            Cliente salvo = registro.retornarCliente("11122233344");
            assertNotNull(salvo);
            assertEquals("Marcello Comum", salvo.getNome());
            assertFalse(salvo instanceof ClienteWinx);
        }
    }

    @Test
    void deveCadastrarClienteWinxComContaPoupancaQuandoSaldoMaiorOuIgualACemMil() {
        simularEntradaUsuario("Marcello VIP\n55566677788\n");

        ContaPoupanca contaPoupancaMock = mock(ContaPoupanca.class, RETURNS_DEEP_STUBS);
        when(contaPoupancaMock.getSaldo()).thenReturn(100000.0);

        Banco bancoMock = mock(Banco.class);
        when(bancoMock.abrirNovaConta()).thenReturn(contaPoupancaMock);

        try (MockedStatic<Banco> bancoStaticMock = mockStatic(Banco.class)) {
            bancoStaticMock.when(Banco::getInstancia).thenReturn(bancoMock);

            registro.cadastrarCliente();

            verify(contaPoupancaMock, times(1)).setExtrato(any(Movimentacao.class));
            verify(contaPoupancaMock, times(1)).setInformeRendimento(any(Movimentacao.class));

            Cliente salvo = registro.retornarCliente("55566677788");
            assertNotNull(salvo);
            assertInstanceOf(ClienteWinx.class, salvo);
        }
    }

    @Test
    void deveCadastrarClienteComumComContaPoupancaQuandoSaldoMenorQueCemMil() {
        simularEntradaUsuario("Carol Poupanca\n22233344455\n");

        ContaPoupanca contaPoupancaMock = mock(ContaPoupanca.class, RETURNS_DEEP_STUBS);
        when(contaPoupancaMock.getSaldo()).thenReturn(500.0);

        Banco bancoMock = mock(Banco.class);
        when(bancoMock.abrirNovaConta()).thenReturn(contaPoupancaMock);

        try (MockedStatic<Banco> bancoStaticMock = mockStatic(Banco.class)) {
            bancoStaticMock.when(Banco::getInstancia).thenReturn(bancoMock);

            registro.cadastrarCliente();

            verify(contaPoupancaMock, times(1)).setInformeRendimento(any(Movimentacao.class));
            Cliente salvo = registro.retornarCliente("22233344455");
            assertNotNull(salvo);
            assertFalse(salvo instanceof ClienteWinx);
        }
    }

    @Test
    void naoDeveCadastrarClienteQuandoCpfJaExistir() {
        registro.getClientes().add(new Cliente("Existente", "99988877766"));
        simularEntradaUsuario("Duplicado\n99988877766\n");

        Banco bancoMock = mock(Banco.class);

        try (MockedStatic<Banco> bancoStaticMock = mockStatic(Banco.class)) {
            bancoStaticMock.when(Banco::getInstancia).thenReturn(bancoMock);

            registro.cadastrarCliente();

            verify(bancoMock, never()).abrirNovaConta();
            assertEquals(1, registro.getClientes().size());
        }
    }

    @Test
    void deveVisualizarDetalhesEImprimirListaComContasMockadas() {
        ContaCorrente ccMock = mock(ContaCorrente.class, RETURNS_DEEP_STUBS);
        ContaPoupanca cpMock = mock(ContaPoupanca.class, RETURNS_DEEP_STUBS);

        Cliente clienteComum = new Cliente("Cliente Comum", "10101010101");
        clienteComum.setContas(ccMock);

        ClienteWinx clienteWinx = new ClienteWinx("Cliente Winx", "20202020202", 50);
        clienteWinx.setContas(cpMock);

        ArrayList<Cliente> lote = new ArrayList<>();
        lote.add(clienteComum);
        lote.add(clienteWinx);
        registro.setClientes(lote);

        assertDoesNotThrow(() -> {
            registro.visualizarDetalhesDoCliente("10101010101");
            registro.visualizarDetalhesDoCliente("20202020202");
            registro.visualizarDetalhesDoCliente("00000000000");
            registro.printarListaDeClientes();
        });

        verify(ccMock, atLeastOnce()).getCartaoCredito();
        verify(cpMock, atLeastOnce()).getTipoDaConta();
    }

    @Test
    void deveLancarNullPointerExceptionQuandoPrimeiroElementoDaListaForNulo() {
        registro.getClientes().add(null);
        assertThrows(NullPointerException.class, () -> registro.retornarCliente("123"));
    }
}
