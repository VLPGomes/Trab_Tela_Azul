package br.winxbank.sistemabancario;

import br.winxbank.exception.BankAccountNotFoundException;
import br.winxbank.random.RandomNumberGenerator;
import br.winxbank.sistemaclientes.Cliente;
import br.winxbank.sistemaclientes.RegistroDeClientes;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Logger; // [Alteração - java:S106] Import do Logger

/**
 * @author Natália.
 * Classe responsável por administrar um banco.
 */
public class Banco implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(Banco.class.getName());
    // [Alteração - java:S2119] Instância única de Random reutilizada na classe
    private static final Random RANDOM = new Random();

    public double receitas;
    public double despesas;
    private static Banco instancia;

    /**
     * Método responsável por abrir uma nova conta.
     */
    public Conta abrirNovaConta() {
        @SuppressWarnings("resource")
        Scanner sc = new Scanner(System.in);
        LOGGER.info("Qual tipo de conta deseja abrir? Digite 1 (Corrente) ou 2 (Poupanca)");
        int decisao = sc.nextInt();
        if (decisao == 1) {
            LOGGER.info("Voce esta criando uma conta corrente..."); // [Alteração - java:S106]
            int numeroCartao = RandomNumberGenerator.gerarNumCartao();
            int csv = RandomNumberGenerator.gerarCsv();
            Cartao cartao = new Cartao(numeroCartao, csv);
            CartaoCredito cartaoCredito = new CartaoCredito(numeroCartao, csv);
            int numeroConta = RandomNumberGenerator.gerarNumConta();
            LOGGER.info("Digite o saldo que deseja colocar na sua conta "); // [Alteração - java:S106]
            double saldo = sc.nextDouble();
            Conta contaCorrente = new ContaCorrente(numeroConta, saldo, cartao, 0, cartaoCredito);
            LOGGER.info("Sua conta corrente foi criada com sucesso!"); // [Alteração - java:S106]
            return contaCorrente;
        } else if (decisao == 2) {
            LOGGER.info("Voce esta criando uma conta poupanca..."); // [Alteração - java:S106]
            int numeroCartao = RandomNumberGenerator.gerarNumCartao();
            int csv = RandomNumberGenerator.gerarCsv();
            Cartao cartao = new Cartao(numeroCartao, csv);
            int numeroConta = RandomNumberGenerator.gerarNumConta();
            LOGGER.info("Digite o saldo que deseja colocar na sua conta:"); // [Alteração - java:S106]
            double saldo = sc.nextDouble();
            ContaPoupanca contaPoupanca = new ContaPoupanca(numeroConta, saldo, cartao, 0);
            LOGGER.info("Sua conta poupanca foi criada com sucesso!"); // [Alteração - java:S106]
            return contaPoupanca;
        }
        return null;
    }

    /**
     * Método responsável por encerrar uma conta de um cliente.
     * @param cliente
     */
    public void fecharConta(Cliente cliente) {
        LOGGER.info("Digite o numero da conta que deseja apagar:"); // [Alteração - java:S106]
        @SuppressWarnings("resource")
        Scanner sc = new Scanner(System.in);
        int numeroConta = sc.nextInt();
        Conta contaSelecionada = cliente.selecionarConta(numeroConta);
        if (contaSelecionada == null) {
            throw new BankAccountNotFoundException();
        }
        cliente.apagarConta(contaSelecionada);
        LOGGER.info("Sua conta foi apagada com sucesso!"); // [Alteração - java:S106]
    }

    /**
     * Método responsável por realizar as movimentações entre banco e conta.
     * Tais quais: acrescentar rendimento em uma poupança e cobrar jurus da fatura do cartao de credito e descontar taxa de uma conta corrente.
     */
    // [Alteração - java:S3776] Removida verificação redundante de lista vazia e extraída a lógica por conta para reduzir a complexidade cognitiva
    public void movimentarEntreBancoConta() {
        for (Cliente cliente : RegistroDeClientes.getInstancia().getClientes()) {
            for (Conta conta : cliente.getContas()) {
                processarMovimentacaoConta(conta);
            }
        }
    }

    // [Alteração - java:S3776] Método auxiliar extraído de movimentarEntreBancoConta()
    private void processarMovimentacaoConta(Conta conta) {
        conta.cobrarJurusEmprestimo();
        if (conta instanceof ContaPoupanca contaPoupanca) {
            contaPoupanca.acrescentarRendimento();
        } else if (conta instanceof ContaCorrente contaCorrente) {
            contaCorrente.descontarTaxa();
            if (contaCorrente.getCartaoCredito().getFatura() > 0) {
                contaCorrente.getCartaoCredito().cobrarJurus();
            }
        }
    }

    /**
     * Método responsável por visualizar detalhes do banco.
     */
    public void printarBanco() {
        // [Alteração - java:S2119] Removido o "new Random()" local; agora usa a constante RANDOM da classe
        DecimalFormat df = new DecimalFormat("0.00");
        LOGGER.info(() -> "Despesas do banco: " + df.format(this.despesas));
        LOGGER.info(() -> "Receitas do banco: " + df.format(this.receitas));
        if (this.despesas > this.receitas) {
            this.despesas /= RANDOM.nextInt(1000000000, 1999999999);
        }
    }

    public double getDespesas(){
        return despesas;
    }

    public double getReceitas(){
        return receitas;
    }

    public void setReceitas(double valor){
        if (valor >= 0) {
            this.receitas += valor;
        }
    }

    public void setDespesas(double valor){
        if (valor >= 0) {
            this.despesas += valor;
        }
    }

    public void setBanco(Banco banco){
        this.despesas = banco.getDespesas();
        this.receitas = banco.getReceitas();
    }

    /**
     * Singleton que só permite uma instância do objeto ser criada, quando o atributo estático instancia tem o valor nulo.
     * @return
     */
    public static Banco getInstancia(){
        if (instancia == null) {
            instancia = new Banco();
        }
        return instancia;
    }
}
