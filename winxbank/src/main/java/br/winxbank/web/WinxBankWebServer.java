package br.winxbank.web;

import br.winxbank.sistemabancario.Banco;
import br.winxbank.sistemabancario.Cartao;
import br.winxbank.sistemabancario.CartaoCredito;
import br.winxbank.sistemabancario.ContaCorrente;
import br.winxbank.sistemaclientes.Cliente;
import br.winxbank.sistemaclientes.RegistroDeClientes;
import br.winxbank.tempo.Ano;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class WinxBankWebServer {

    private HttpServer server;
    private ContaCorrente conta;
    private CartaoCredito cartaoCredito;
    private String ultimaMensagem = "Bem-vindo ao WinxBank";
    private String infoCliente = "";

    public void iniciar(int porta) throws IOException {
        resetarEstado();
        server = HttpServer.create(new InetSocketAddress(porta), 0);

        server.createContext("/", this::handlePaginaInicial);
        server.createContext("/comprar-credito", this::handleComprarCredito);
        server.createContext("/pagar-fatura", this::handlePagarFatura);
        server.createContext("/virar-mes", this::handleVirarMes);
        server.createContext("/cadastrar-cliente", this::handleCadastrarCliente);
        server.createContext("/buscar-cliente", this::handleBuscarCliente);
        server.createContext("/remover-cliente", this::handleRemoverCliente);

        server.setExecutor(null);
        server.start();
    }

    public void parar() {
        if (server != null) {
            server.stop(0);
        }
    }

    public void resetarEstado() {
        Banco.getInstancia().receitas = 0;
        Banco.getInstancia().despesas = 0;
        Ano.getInstancia().setMesAtual("Abril");

        Cartao cartaoDebito = new Cartao(1234, 123);
        this.cartaoCredito = new CartaoCredito(5678, 456); // limite inicial = 1000.0
        this.conta = new ContaCorrente(1001, 1000.0, cartaoDebito, 0.0, cartaoCredito);
        this.ultimaMensagem = "Bem-vindo ao WinxBank";
        this.infoCliente = "";
        RegistroDeClientes.getInstancia().limparListaDeClientes();
    }

    private void handlePaginaInicial(HttpExchange exchange) throws IOException {
        enviarHtml(exchange, gerarHtml());
    }

    private void handleComprarCredito(HttpExchange exchange) throws IOException {
        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            Map<String, String> params = lerFormulario(exchange);
            double valor = Double.parseDouble(params.getOrDefault("valor", "0"));
            double faturaAntes = cartaoCredito.getFatura();

            cartaoCredito.creditar(valor);

            if (cartaoCredito.getFatura() > faturaAntes) {
                ultimaMensagem = "Compra no crédito aprovada!";
            } else {
                ultimaMensagem = "Compra recusada: limite excedido.";
            }
        }
        redirecionarParaInicio(exchange);
    }

    private void handlePagarFatura(HttpExchange exchange) throws IOException {
        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            Map<String, String> params = lerFormulario(exchange);
            double valor = Double.parseDouble(params.getOrDefault("valor", "0"));

            conta.pagarFatura(valor);
            ultimaMensagem = "Pagamento de fatura realizado!";
        }
        redirecionarParaInicio(exchange);
    }

    private void handleVirarMes(HttpExchange exchange) throws IOException {
        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            Ano.getInstancia().setMesAtual("Maio");
            cartaoCredito.cobrarJurus();
            ultimaMensagem = "Virada de mês processada.";
        }
        redirecionarParaInicio(exchange);
    }

    private void handleCadastrarCliente(HttpExchange exchange) throws IOException {
        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            Map<String, String> params = lerFormulario(exchange);
            String nome = params.getOrDefault("nome", "");
            String cpf = params.getOrDefault("cpf", "");
            RegistroDeClientes registro = RegistroDeClientes.getInstancia();

            if (!cpf.isBlank() && registro.checarCpf(cpf)) {
                registro.getClientes().add(new Cliente(nome, cpf));
                ultimaMensagem = "Cliente cadastrado com sucesso!";
            } else {
                ultimaMensagem = "Erro: CPF já cadastrado.";
            }
        }
        redirecionarParaInicio(exchange);
    }

    private void handleBuscarCliente(HttpExchange exchange) throws IOException {
        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            Map<String, String> params = lerFormulario(exchange);
            String cpf = params.getOrDefault("cpf", "");
            Cliente encontrado = RegistroDeClientes.getInstancia().retornarCliente(cpf);

            if (encontrado != null) {
                infoCliente = encontrado.getNome() + " - " + encontrado.getClass().getSimpleName();
                ultimaMensagem = "Cliente encontrado!";
            } else {
                infoCliente = "";
                ultimaMensagem = "Cliente não encontrado.";
            }
        }
        redirecionarParaInicio(exchange);
    }

    private void handleRemoverCliente(HttpExchange exchange) throws IOException {
        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            Map<String, String> params = lerFormulario(exchange);
            String cpf = params.getOrDefault("cpf", "");
            RegistroDeClientes registro = RegistroDeClientes.getInstancia();
            Cliente paraRemover = registro.retornarCliente(cpf);

            if (paraRemover != null) {
                registro.removerCliente(paraRemover);
                infoCliente = "";
                ultimaMensagem = "Cliente removido com sucesso!";
            } else {
                ultimaMensagem = "Cliente não encontrado.";
            }
        }
        redirecionarParaInicio(exchange);
    }

    private String gerarHtml() {
        return """
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head>
                    <meta charset="UTF-8">
                    <title>WinxBank</title>
                </head>
                <body>
                    <h1>WinxBank - Painel da Conta</h1>
                    <p>Mensagem: <span id="mensagem">%s</span></p>
                    <p>Saldo Conta Corrente: R$ <span id="saldo">%s</span></p>
                    <p>Fatura Cartão de Crédito: R$ <span id="fatura">%s</span></p>
                    <p>Receitas do Banco: R$ <span id="receitas">%s</span></p>

                    <hr>
                    <h3>Comprar no Crédito</h3>
                    <form method="POST" action="/comprar-credito">
                        <input id="input-compra" name="valor" type="text" placeholder="Valor da compra">
                        <button id="btn-comprar" type="submit">Comprar</button>
                    </form>

                    <h3>Pagar Fatura</h3>
                    <form method="POST" action="/pagar-fatura">
                        <input id="input-pagamento" name="valor" type="text" placeholder="Valor do pagamento">
                        <button id="btn-pagar" type="submit">Pagar Fatura</button>
                    </form>

                    <h3>Processamento Mensal</h3>
                    <form method="POST" action="/virar-mes">
                        <button id="btn-virar-mes" type="submit">Virar Mês e Cobrar Juros</button>
                    </form>

                    <hr>
                    <h3>Gestão de Clientes</h3>
                    <form method="POST" action="/cadastrar-cliente">
                        <input id="input-nome" name="nome" type="text" placeholder="Nome do cliente">
                        <input id="input-cpf" name="cpf" type="text" placeholder="CPF">
                        <button id="btn-cadastrar" type="submit">Cadastrar</button>
                        <button id="btn-buscar" type="submit" formaction="/buscar-cliente">Buscar</button>
                        <button id="btn-remover" type="submit" formaction="/remover-cliente">Remover</button>
                    </form>
                    <p>Cliente: <span id="cliente-info">%s</span></p>
                </body>
                </html>
                """.formatted(
                ultimaMensagem,
                String.format(Locale.US, "%.2f", conta.getSaldo()),
                String.format(Locale.US, "%.2f", cartaoCredito.getFatura()),
                String.format(Locale.US, "%.2f", Banco.getInstancia().getReceitas()),
                infoCliente
        );
    }

    private Map<String, String> lerFormulario(HttpExchange exchange) throws IOException {
        String corpo = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, String> params = new HashMap<>();
        for (String par : corpo.split("&")) {
            String[] kv = par.split("=");
            if (kv.length == 2) {
                params.put(
                        URLDecoder.decode(kv[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(kv[1], StandardCharsets.UTF_8)
                );
            }
        }
        return params;
    }

    private void redirecionarParaInicio(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Location", "/");
        exchange.sendResponseHeaders(303, -1);
        exchange.close();
    }

    private void enviarHtml(HttpExchange exchange, String html) throws IOException {
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    // Opcional: permite rodar o servidor manualmente e abrir http://localhost:8085 no navegador
    public static void main(String[] args) throws IOException {
        new WinxBankWebServer().iniciar(8085);
        System.out.println("WinxBank Web rodando em http://localhost:8085");
    }
}
