package br.winxbank.sistemaclientes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClienteContaIntegracaoTest {

    private RegistroDeClientes registro;

    @BeforeEach
    void setUp() {
        registro = RegistroDeClientes.getInstancia();
        registro.limparListaDeClientes();
    }

    @Test
    @DisplayName("Cadastrar ClienteWinx e verificar persistência dos dados no registro")
    void integraRegistroClienteGerenciaSaldo() {
        ClienteWinx cliente = new ClienteWinx("Marcello Integracao", "99988877700", 100);
        registro.getClientes().add(cliente);

        Cliente clienteRecuperado = registro.retornarCliente("99988877700");
        assertNotNull(clienteRecuperado);
        assertEquals("Marcello", clienteRecuperado.getNome());
        assertEquals("99988877700", clienteRecuperado.getCpf());
        assertInstanceOf(ClienteWinx.class, clienteRecuperado);
    }
}
