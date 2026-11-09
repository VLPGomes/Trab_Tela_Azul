package br.winxbank.sistemaclientes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class WinxBankSystemTest {

    private RegistroDeClientes registro;

    @BeforeEach
    void setUp() {
        registro = RegistroDeClientes.getInstancia();
        registro.limparListaDeClientes();
    }

    @Test
    void deveExecutarFluxoCompletoDoSistema() throws InterruptedException {
        Cliente cliente1 = new Cliente("Marcello Sistema", "11122233344");
        ClienteWinx cliente2 = new ClienteWinx("Carol Sistema", "55566677788", 500);

        registro.getClientes().add(cliente1);
        registro.getClientes().add(cliente2);

        assertEquals(2, registro.getClientes().size());
        assertNotNull(registro.retornarCliente("11122233344"));
        assertNotNull(registro.retornarCliente("55566677788"));

        Cliente clienteAtualizado = new Cliente("Marcello Sistema Atualizado", "11122233344");
        registro.atualizarCliente(clienteAtualizado);

        Cliente resultadoFinal = registro.retornarCliente("11122233344");
        assertEquals("Marcello Sistema Atualizado", resultadoFinal.getNome());

        registro.removerCliente(cliente1);
        assertNull(registro.retornarCliente("11122233344"));
        assertEquals(1, registro.getClientes().size());
    }

    @Test
    void deveAtenderRequisitoNaoFuncionalDeDesempenho() {
        assertTimeoutPreemptively(Duration.ofMillis(50), () -> {
            for (int i = 0; i < 1000; i++) {
                String cpf = String.format("%011d", i);
                registro.getClientes().add(new Cliente("Cliente " + i, cpf));
            }

            Cliente busca = registro.retornarCliente("00000000999");
            assertNotNull(busca);
        });
    }
}
