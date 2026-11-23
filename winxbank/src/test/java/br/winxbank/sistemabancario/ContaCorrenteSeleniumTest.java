package br.winxbank.sistemabancario;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ContaCorrenteSeleniumTest extends BaseTestesSelenium {
    @Test
    void testComprarNoCredito() {
        enviarFormulario("/comprar-credito", "400");
        assertAll(
            () -> assertEquals(400.0, valor("fatura"), 0.001),
            () -> assertEquals(1000.0, valor("saldo"), 0.001),
            () -> verificarMensagem("Compra no crédito aprovada!")
        );
    }

    @Test
    void testCompraMaiorQueLimite() {
        enviarFormulario("/comprar-credito", "1200");
        assertAll(
            () -> assertEquals(0.0, valor("fatura"), 0.001),
            () -> verificarMensagem("Compra recusada: limite excedido.")
        );
    }

    @Test
    void testPagamentoParcialFatura() {
        enviarFormulario("/comprar-credito", "400");
        enviarFormulario("/pagar-fatura", "150");
        assertAll(
            () -> assertEquals(850.0, valor("saldo"), 0.001),
            () -> assertEquals(250.0, valor("fatura"), 0.001),
            () -> verificarMensagem("Pagamento de fatura realizado!")
        );
    }

    @Test
    void testPagamentoIntegralFatura() {
        enviarFormulario("/comprar-credito", "400");
        enviarFormulario("/pagar-fatura", "400");
        assertAll(
            () -> assertEquals(600.0, valor("saldo"), 0.001),
            () -> assertEquals(0.0, valor("fatura"), 0.001)
        );
    }

    @Test
    void testPagamentoMaiorQueFatura() {
        enviarFormulario("/comprar-credito", "400");
        enviarFormulario("/pagar-fatura", "500");
        assertAll(
            () -> assertEquals(1000.0, valor("saldo"), 0.001),
            () -> assertEquals(400.0, valor("fatura"), 0.001),
            () -> assertTrue(texto("mensagem").contains("recusad"),"Mensagem recebida: " + texto("mensagem"))
        );
    }

    @Test
    void testPagamentoMaiorQueSaldo() {
        enviarFormulario("/comprar-credito", "900");
        enviarFormulario("/sacar", "500");
        enviarFormulario("/pagar-fatura", "700");
        assertAll(
            () -> assertEquals(500.0, valor("saldo"), 0.001),
            () -> assertEquals(900.0, valor("fatura"), 0.001),
            () -> assertTrue(texto("mensagem").contains("recusad"))
        );
    }

    @Test
    void testCompraCreditoAposPagamentoParcial() {
        enviarFormulario("/comprar-credito", "800");
        enviarFormulario("/pagar-fatura", "300");
        enviarFormulario("/comprar-credito", "400");
        assertAll(
            () -> assertEquals(900.0, valor("fatura"), 0.001),
            () -> assertEquals(700.0, valor("saldo"), 0.001),
            () -> verificarMensagem("Compra no crédito aprovada!")
        );
    }

    @Test
    void testJurosAposViradaDeMes() {
        enviarFormulario("/comprar-credito", "400");
        clicarFormulario("/virar-mes");
        assertAll(
            () -> assertEquals(451.0, valor("fatura"), 0.001),
            () -> assertEquals(51.0, valor("receitas"), 0.001),
            () -> verificarMensagem("Virada de mês processada.")
        );
    }
}
