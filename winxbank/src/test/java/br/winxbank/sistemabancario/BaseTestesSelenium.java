package br.winxbank.sistemabancario;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import br.winxbank.web.WinxBankWebServer;
import java.io.IOException;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.*;


// Essa classe serve apenas como base para os testes com o Selenium para as classes Conta, ContaCorrente e ContaPoupanca
// para facilitar o acesso a funções de inicialização e finalização da aplicação web, e de acesso aos elementos da página

@Execution(ExecutionMode.SAME_THREAD)
public abstract class BaseTestesSelenium {
    protected WebDriver driver;
    protected WinxBankWebServer servidor;
    protected static final String URL = "http://localhost:8085/";

    @BeforeEach
    void iniciarAmbiente() throws IOException {
        servidor = new WinxBankWebServer();
        servidor.iniciar(8085);
        FirefoxOptions options = new FirefoxOptions();
        driver = new FirefoxDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.get(URL);
    }

    @AfterEach
    void encerrarAmbiente() {
        if (driver != null) {
            driver.quit();
        }
        if (servidor != null) {
            servidor.parar();
        }
    }

    protected String texto(String id) {
        return driver.findElement(By.id(id)).getText();
    }

    protected double valor(String id) {
        return Double.parseDouble(texto(id).replace(",", "."));
    }

    protected void enviarFormulario(String rota, String valor) {
        WebElement formulario = driver.findElement(By.cssSelector("form[action='" + rota + "']"));
        formulario.findElement(By.name("valor")).sendKeys(valor);
        formulario.findElement(By.cssSelector("button[type='submit']")).click();
    }

    protected void clicarFormulario(String rota) {
        driver.findElement(By.cssSelector("form[action='" + rota + "'] button")).click();
    }

    protected void verificarMensagem(String mensagem) {
        assertTrue(texto("mensagem").contains(mensagem), "Mensagem encontrada: " + texto("mensagem"));
    }
}
