package br.winxbank.sistema;

import br.winxbank.web.WinxBankWebServer;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class WinxBankSeleniumTest {

    private static final int PORTA = 8085;
    private static final String URL_BASE = "http://localhost:" + PORTA;

    private static WinxBankWebServer servidor;
    private static WebDriver driver;

    @BeforeAll
    static void iniciarAmbiente() throws Exception {
        servidor = new WinxBankWebServer();
        servidor.iniciar(PORTA);

        ChromeOptions options = new ChromeOptions();
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    }

    @BeforeEach
    void resetarCenario() {
        servidor.resetarEstado();
        driver.get(URL_BASE);
    }

    @AfterAll
    static void encerrarAmbiente() {
        if (driver != null) {
            driver.quit();
        }
        if (servidor != null) {
            servidor.parar();
        }
    }

    private void clicarEAguardarRecarregar(By seletorBotao) {
        WebElement botao = driver.findElement(seletorBotao);
        botao.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.stalenessOf(botao));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("mensagem")));
    }

    @Test
    @DisplayName("RF01: Compra no crédito dentro do limite deve atualizar fatura na interface")
    void deveRealizarCompraNoCreditoComSucesso() {
        driver.findElement(By.id("input-compra")).sendKeys("350.00");
        clicarEAguardarRecarregar(By.id("btn-comprar"));

        assertEquals("Compra no crédito aprovada!", driver.findElement(By.id("mensagem")).getText());
        assertEquals("350.00", driver.findElement(By.id("fatura")).getText());
        assertEquals("1000.00", driver.findElement(By.id("saldo")).getText());
    }

    @Test
    @DisplayName("RF02: Compra acima do limite do cartão (1000.00) deve ser recusada")
    void deveRecusarCompraAcimaDoLimite() {
        driver.findElement(By.id("input-compra")).sendKeys("1500.00");
        clicarEAguardarRecarregar(By.id("btn-comprar"));

        assertEquals("Compra recusada: limite excedido.", driver.findElement(By.id("mensagem")).getText());
        assertEquals("0.00", driver.findElement(By.id("fatura")).getText());
    }

    @Test
    @DisplayName("RF03: Pagamento de fatura deve deduzir saldo da conta e zerar fatura")
    void devePagarFaturaPelaContaCorrente() {
        driver.findElement(By.id("input-compra")).sendKeys("400.00");
        clicarEAguardarRecarregar(By.id("btn-comprar"));

        driver.findElement(By.id("input-pagamento")).sendKeys("400.00");
        clicarEAguardarRecarregar(By.id("btn-pagar"));

        assertEquals("Pagamento de fatura realizado!", driver.findElement(By.id("mensagem")).getText());
        assertEquals("0.00", driver.findElement(By.id("fatura")).getText());
        assertEquals("600.00", driver.findElement(By.id("saldo")).getText());
    }

    @Test
    @DisplayName("RF04: Virada de mês com fatura em aberto deve cobrar juros e gerar receita ao banco")
    void deveCobrarJurosNaViradaDeMes() {
        driver.findElement(By.id("input-compra")).sendKeys("100.00");
        clicarEAguardarRecarregar(By.id("btn-comprar"));

        clicarEAguardarRecarregar(By.id("btn-virar-mes"));

        // 100.00 * 12.75 = 1275.00 | Receita gerada = 1175.00
        assertEquals("112.75", driver.findElement(By.id("fatura")).getText());
        assertEquals("12.75", driver.findElement(By.id("receitas")).getText());
    }

    @Test
    @DisplayName("RNF01 (Desempenho): Transação completa no navegador deve responder em menos de 2 segundos")
    void deveExecutarTransacaoDentroDoTempoLimiteDeDesempenho() {
        Instant inicio = Instant.now();

        driver.findElement(By.id("input-compra")).sendKeys("200.00");
        clicarEAguardarRecarregar(By.id("btn-comprar"));

        String fatura = driver.findElement(By.id("fatura")).getText();
        long tempoMs = Duration.between(inicio, Instant.now()).toMillis();

        assertEquals("200.00", fatura);
        assertTrue(tempoMs < 2000,
                "Tempo de resposta acima da meta de 2s definida na ISO 25010: " + tempoMs + " ms");
    }
}
