package br.winxbank.sistemaclientes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegistroDeClientesTest {

    private RegistroDeClientes registro;

    @BeforeEach
    void setUp() {
        registro = new RegistroDeClientes();
    }

    @Test
    void retornarTrueQuandoCpfNaoExistir() {
        boolean cpfLivre = registro.checarCpf("12345678900");
        assertTrue(cpfLivre);
    }

    @Test
    void retornarFalsoCpfJaCadastrado() {
        Cliente cliente = new Cliente("carol", "200");
        registro.cadastrarCliente(cliente);

        boolean cpfLivre = registro.checarCpf("200");
        assertFalse(cpfLivre);
    }

    @Test
    void buscarClientePorCpf() {
        Cliente cliente = new Cliente("Marcello", "999");
        registro.cadastrarCliente(cliente);

        Cliente resultado = registro.retornarCliente("999");
        assertNotNull(resultado);
        assertEquals("Marcello", resultado.getNome());
    }

    @Test
    void removerClienteComSucesso() {
        Cliente cliente = new Cliente("Marcello", "999");
        registro.cadastrarCliente(cliente);

        registro.removerCliente(cliente);
        assertTrue(registro.isListaVazia());
    }

    @Test
    void atualizarDadosDoCliente() {
        Cliente clienteAntigo = new Cliente("Marcello Antigo", "999");
        registro.cadastrarCliente(clienteAntigo);

        Cliente clienteNovo = new Cliente("Marcello Atualizado", "999");
        registro.atualizarCliente(clienteNovo);
        
        Cliente resultado = registro.retornarCliente("999");
        assertEquals("Marcello Atualizado", resultado.getNome());
    }

    @Test
    void manterInstanciaDeClienteWinx() {
        ClienteWinx clienteWinx = new ClienteWinx("Marcello ClienteWinx", "888", 0);
        registro.cadastrarCliente(clienteWinx);
        
        Cliente retornado = registro.retornarCliente("888");
        assertTrue(retornado instanceof ClienteWinx);
    }

    @Test
    void esvaziarListaDeClientes() {
        registro.getClientes().add(new Cliente("Teste1", "001"));
        registro.getClientes().add(new Cliente("Teste2", "002"));

        registro.limparListaDeClientes();
        assertTrue(registro.getClientes().isEmpty());
    }
}
