package br.winxbank.sistemabancario;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ContaSeleniumTest extends BaseTestesSelenium {
    @Test
    void testDepositar() {
        enviarFormulario("/depositar", "200");
        assertAll(
            () -> assertEquals(1200.0, valor("saldo"), 0.001),
            () -> verificarMensagem("Depósito realizado!")
        );
    }

    @Test
    void testSacar() {
        enviarFormulario("/sacar", "300");
        assertAll(
            () -> assertEquals(700.0, valor("saldo"), 0.001),
            () -> verificarMensagem("Saque realizado!")
        );
    }

    @Test
    void testSaqueMaiorQueSaldo() {
        enviarFormulario("/sacar", "1200");
        assertAll(
            () -> assertEquals(1000.0, valor("saldo"), 0.001),
            () -> verificarMensagem("Saque recusado: saldo insuficiente.")
        );
    }

    @Test
    void testDepositoNegativo() {
        enviarFormulario("/depositar", "-100");
        assertAll(
            () -> assertEquals(1000.0, valor("saldo"), 0.001),
            () -> verificarMensagem("Depósito recusado: valor inválido.")
        );
    }

    @Test
    void testPixEntreContas() {
        enviarFormulario("/pix", "250");
        assertAll(
            () -> assertEquals(750.0, valor("saldo"), 0.001),
            () -> assertEquals(750.0, valor("saldo-poupanca"), 0.001),
            () -> verificarMensagem("PIX enviado à poupança!")
        );
    }

    @Test
    void testPixMaiorQueSaldo() {
        enviarFormulario("/pix", "1200");
        assertAll(
            () -> assertEquals(1000.0, valor("saldo"), 0.001),
            () -> assertEquals(500.0, valor("saldo-poupanca"), 0.001),
            () -> verificarMensagem("PIX recusado: saldo insuficiente.")
        );
    }

    @Test
    void testRequisitarEmprestimo() {
        enviarFormulario("/emprestimo", "500");
        assertAll(
            () -> assertEquals(1500.0, valor("saldo"), 0.001),
            () -> assertEquals(500.0, valor("divida"), 0.001),
            () -> verificarMensagem("Empréstimo solicitado!")
        );
    }

    @Test
    void testPagarParcelaEmprestimo() {
        enviarFormulario("/emprestimo", "500");
        enviarFormulario("/pagar-emprestimo", "200");
        assertAll(
            () -> assertEquals(1300.0, valor("saldo"), 0.001),
            () -> assertEquals(300.0, valor("divida"), 0.001),
            () -> verificarMensagem("Parcela de empréstimo paga!")
        );
    }

    @Test
    void testPagarEmprestimoMaiorQueDivida() {
        enviarFormulario("/emprestimo", "200");
        enviarFormulario("/pagar-emprestimo", "300");
        assertAll(
            () -> assertEquals(1200.0, valor("saldo"), 0.001),
            () -> assertEquals(200.0, valor("divida"), 0.001),
            () -> verificarMensagem("Pagamento recusado: saldo ou dívida insuficiente.")
        );
    }

    @Test
    void testExtratoAposDepositoESaque() {
        enviarFormulario("/depositar", "200");
        enviarFormulario("/sacar", "100");
        String extrato = driver.findElement(org.openqa.selenium.By.xpath("//h3[text()='Extrato da Conta Corrente']" + "/following-sibling::pre[1]")).getText();
        assertAll(
            () -> assertEquals(1100.0, valor("saldo"), 0.001),
            () -> assertTrue(extrato.contains("ENTRADA: R$ 200.00")),
            () -> assertTrue(extrato.contains("SAIDA: R$ 100.00"))
        );
    }
}
