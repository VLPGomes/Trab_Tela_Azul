package br.winxbank.sistemabancario;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import static org.junit.jupiter.api.Assertions.*;

class ContaPoupancaSeleniumTest extends BaseTestesSelenium {
    @Test
    void testDepositarPoupanca() {
        enviarFormulario("/depositar-poupanca", "200");
        assertAll(
            () -> assertEquals(700.0, valor("saldo-poupanca"), 0.001),
            () -> verificarMensagem("Depósito na poupança realizado!")
        );
    }

    @Test
    void testDepositoNegativoPoupanca() {
        enviarFormulario("/depositar-poupanca", "-100");
        assertAll(
            () -> assertEquals(500.0, valor("saldo-poupanca"), 0.001),
            () -> verificarMensagem("Depósito na poupança recusado: valor inválido.")
        );
    }

    @Test
    void testDepositosSucessivosPoupanca() {
        enviarFormulario("/depositar-poupanca", "200");
        enviarFormulario("/depositar-poupanca", "300");
        assertEquals(1000.0, valor("saldo-poupanca"), 0.001);
    }

    @Test
    void testRendimentoPoupanca() {
        clicarFormulario("/rendimento-poupanca");
        assertAll(
            () -> assertEquals(504.0, valor("saldo-poupanca"), 0.001),
            () -> assertEquals(4.0, valor("despesas"), 0.001)
        );
    }

    @Test
    void testRendimentoAposDeposito() {
        enviarFormulario("/depositar-poupanca", "500");
        clicarFormulario("/rendimento-poupanca");
        assertAll(
            () -> assertEquals(1008.0, valor("saldo-poupanca"), 0.001),
            () -> assertEquals(8.0, valor("despesas"), 0.001)
        );
    }

    @Test
    void testRendimentosCompostos() {
        clicarFormulario("/rendimento-poupanca");
        clicarFormulario("/rendimento-poupanca");
        assertAll(
            () -> assertEquals(508.032, valor("saldo-poupanca"), 0.01),
            () -> assertEquals(8.032, valor("despesas"), 0.01)
        );
    }

    @Test
    void testInformeDeRendimento() {
        clicarFormulario("/rendimento-poupanca");
        String informe = driver.findElement(By.xpath("//h3[text()='Informe de Rendimento da Poupança']" + "/following-sibling::pre[1]")).getText();
        assertAll(
            () -> assertTrue(informe.contains("ENTRADA")),
            () -> assertTrue(informe.contains("R$ 4.00")),
            () -> assertTrue(informe.contains("Abril"))
        );
    }

    @Test
    void testRendimentoNaoAlteraSaldoCorrente() {
        clicarFormulario("/rendimento-poupanca");
        assertAll(
            () -> assertEquals(1000.0, valor("saldo"), 0.001),
            () -> assertEquals(504.0, valor("saldo-poupanca"), 0.001)
        );
    }
}
