package br.winxbank.sistemaclientes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtendimentoServiceTest {

    @Mock
    private RegistroDeClientes registroMock;

    @Test
    void deveProcessarAtendimentoComSucessoQuandoClienteExiste() {
        Cliente clienteFicticio = new Cliente("Marcello", "11122233344");
        when(registroMock.retornarCliente("11122233344")).thenReturn(clienteFicticio);

        Cliente encontrado = registroMock.retornarCliente("11122233344");

        assertNotNull(encontrado);
        assertEquals("Marcello", encontrado.getNome());
        verify(registroMock, times(1)).retornarCliente("11122233344");
    }
}
