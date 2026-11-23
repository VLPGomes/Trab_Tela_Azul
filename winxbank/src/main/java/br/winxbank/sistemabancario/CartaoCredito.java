package br.winxbank.sistemabancario;

import br.winxbank.tempo.Ano;

import java.util.Scanner;
import java.util.logging.Logger;

/**
- @author Carol
- Esta classe é responsável por representar uma entidade CartaoCredito.
 */
public class CartaoCredito extends Cartao implements OperacoesAutomaticas{

    private static final Logger LOGGER = Logger.getLogger(CartaoCredito.class.getName());
    private double fatura;
    private int indexMesDaFatura;
    private boolean faturaPaga;
    private double limite;

    /**
- Construtor padrão do cartão de crédito
- @param numero
- @param csv
     */
    public CartaoCredito(int numero, int csv) {
        super(numero, csv);
        this.limite = 1000; //as contas correntes iniciam com esse limite quando criadas pela primeira vez
        this.faturaPaga = true; // [Alteração] Garante que o cartão inicie sem fatura pendente
    }


    /**
- Construtor alternativo para leitura de json.
- @param fatura
- @param indexMesDaFatura
- @param faturaPaga
- @param limite
- @param numero
- @param csv
     */
    public CartaoCredito(double fatura, int indexMesDaFatura, boolean faturaPaga, double limite, int numero, int csv) {
        super(numero, csv);
        this.fatura = fatura;
        this.indexMesDaFatura = indexMesDaFatura;
        this.faturaPaga = faturaPaga;
        this.limite = limite;

    }

    /**
- Método responsável por creditar um valor da fatura do cartão de crédito.
- Quando esse método for chamado, é atribuído um mês referente àquela fatura.
- @param valor
     */
    // [Alteração] Retorno alterado de void para boolean para sinalizar se a compra foi aprovada ou recusada
    public boolean creditar(double valor){
        // [Alteração] Validação para impedir compras com valor negativo ou zerado
        if(valor <= 0){
            throw new IllegalArgumentException("O valor da compra deve ser maior que zero.");
        }
        // [Alteração] Só atualiza a fatura e o mês de referência se a compra couber no limite
        if(valor + this.fatura <= this.limite){
            setFatura(valor);
            this.indexMesDaFatura = Ano.getInstancia().getIndexMesAtual();
            return true;
        }
        return false;
    }

    /**
- Método responsável por ajustar o limite do cartão
     */
    public void ajustarLimite(){
        @SuppressWarnings("resource")
        Scanner sc = new Scanner(System.in);
        LOGGER.info("Digite o valor do limite do seu cartão que deseja ajustar: ");
        double novoLimite = sc.nextDouble();
        ajustarLimite(novoLimite); // [Alteração] Delega para o método desacoplado com validações

    }

    // [Alteração] Método desacoplado do console para permitir validação e testes unitários
    public void ajustarLimite(double novoLimite){
        if(novoLimite <= 0){
            throw new IllegalArgumentException("O limite do cartão deve ser maior que zero.");
        }
        if(novoLimite < this.fatura){
            throw new IllegalArgumentException("O novo limite não pode ser menor que a fatura atual.");
        }
        this.limite = novoLimite;
    }

    /**
- Setter com regra de negócio de que se o valor setado for menor ou igual ao limite, permitir modificação.
- Além disso, há mudança de status de fatura paga dependendo do valor setado no momento neste setter.
- @param valor
     */
    public void setFatura(double valor) {
        if(valor + fatura <= this.limite){
            this.fatura += valor;
        }
        if(this.fatura <= 0){
            this.faturaPaga = true;
        }
        else{
            this.faturaPaga = false;
        }
    }

    public double getFatura() {
        return fatura;
    }

    // [Alteração] Getter adicionado para permitir consulta e validação do limite nos testes
    public double getLimite() {
        return limite;
    }

    /**
- Método responsável por cobrar jurus de uma fatura conforme meses passados.
     */
    public void cobrarJurus(){
        // [Alteração] Trata a virada de ano (dezembro = 11 para janeiro = 0) e impede cobrança em fatura zerada
        int mesAtual = Ano.getInstancia().getIndexMesAtual();
        boolean virouMesOuAno = mesAtual > this.indexMesDaFatura || (this.indexMesDaFatura == 11 && mesAtual == 0);

        if (!this.faturaPaga && this.fatura > 0 && virouMesOuAno){
            double faturaAnterior = this.fatura;
            this.fatura += this.fatura * taxaJurus/100;
            this.indexMesDaFatura = mesAtual; // [Alteração] Atualiza o mês de referência para evitar cobrança duplicada no mesmo mês
            movimentacaoBancaria(this.fatura - faturaAnterior);
        }

    }

    /**
- Método responsável por gerar receita ao banco do valor pago a mais da cobrança de jurus em cima de uma fatura.
- @param valor
     */
    public void movimentacaoBancaria(double valor) {
        Banco.getInstancia().setReceitas(valor);
    }

}
