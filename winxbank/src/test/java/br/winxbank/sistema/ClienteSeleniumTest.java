package br.winxbank.sistemaclientes;

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

class ClienteSeleniumTest {

    private static final int PORTA = 8086;
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
    @DisplayName("RF01: Cadastro de novo cliente deve persistir no registro e exibir confirmação")
    void deveCadastrarClienteComSucesso() {
        driver.findElement(By.id("input-nome")).sendKeys("Marcello");
        driver.findElement(By.id("input-cpf")).sendKeys("12345678900");
        clicarEAguardarRecarregar(By.id("btn-cadastrar"));

        assertEquals("Cliente cadastrado com sucesso!", driver.findElement(By.id("mensagem")).getText());
        assertNotNull(RegistroDeClientes.getInstancia().retornarCliente("12345678900"));
    }

    @Test
    @DisplayName("RF02: Cadastro com CPF já existente deve ser recusado")
    void deveRecusarCadastroComCpfDuplicado() {
        RegistroDeClientes.getInstancia().getClientes().add(new Cliente("Carol", "200"));

        driver.findElement(By.id("input-nome")).sendKeys("Outro");
        driver.findElement(By.id("input-cpf")).sendKeys("200");
        clicarEAguardarRecarregar(By.id("btn-cadastrar"));

        assertEquals("Erro: CPF já cadastrado.", driver.findElement(By.id("mensagem")).getText());
        assertEquals(1, RegistroDeClientes.getInstancia().getClientes().size());
    }

    @Test
    @DisplayName("RF03: Busca por CPF deve exibir nome e tipo ClienteWinx na interface")
    void deveBuscarClienteWinxPorCpf() {
        RegistroDeClientes.getInstancia().getClientes().add(new ClienteWinx("Marcello Winx", "888", 10));

        driver.findElement(By.id("input-cpf")).sendKeys("888");
        clicarEAguardarRecarregar(By.id("btn-buscar"));

        String info = driver.findElement(By.id("cliente-info")).getText();
        assertTrue(info.contains("Marcello Winx"));
        assertTrue(info.contains("ClienteWinx"));
    }

    @Test
    @DisplayName("RF04: Remoção por CPF deve excluir o cliente do registro")
    void deveRemoverClienteComSucesso() {
        RegistroDeClientes.getInstancia().getClientes().add(new Cliente("Marcello", "999"));

        driver.findElement(By.id("input-cpf")).sendKeys("999");
        clicarEAguardarRecarregar(By.id("btn-remover"));

        assertEquals("Cliente removido com sucesso!", driver.findElement(By.id("mensagem")).getText());
        assertNull(RegistroDeClientes.getInstancia().retornarCliente("999"));
    }

    @Test
    @DisplayName("RNF01 (Desempenho): Cadastro de cliente via navegador deve responder em menos de 2 segundos")
    void deveCadastrarClienteDentroDoTempoLimite() {
        Instant inicio = Instant.now();

        driver.findElement(By.id("input-nome")).sendKeys("Marcello Rapido");
        driver.findElement(By.id("input-cpf")).sendKeys("77766655544");
        clicarEAguardarRecarregar(By.id("btn-cadastrar"));

        long tempoMs = Duration.between(inicio, Instant.now()).toMillis();
        assertEquals("Cliente cadastrado com sucesso!", driver.findElement(By.id("mensagem")).getText());
        assertTrue(tempoMs < 2000, "Tempo de resposta acima da meta de 2s: " + tempoMs + " ms");
    }
}
