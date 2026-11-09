package br.winxbank.sistemabancario.integracao;

import br.winxbank.sistemabancario.Banco;
import br.winxbank.sistemabancario.Cartao;
import br.winxbank.sistemabancario.CartaoCredito;
import br.winxbank.sistemabancario.ContaCorrente;
import br.winxbank.sistemabancario.ContaPoupanca;
import br.winxbank.tempo.Ano;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CartaoCreditoIntegracaoTest {

    private static final double DELTA = 0.0001;

    @BeforeEach
    void configurar() {
        limparEstado();
        Ano.getInstancia().setMesAtual("Novembro");
    }

    @AfterEach
    void limpar() {
        limparEstado();
        Ano.getInstancia().setMesAtual("Novembro");
    }

    @Test
    void cobrarJurosDeFaturaAtrasadaAplicaTaxaEGeraReceitaParaOBanco() {
        CartaoCredito cartao = new CartaoCredito(100.0, 0, false, 2000.0, 1234, 123);
        
        cartao.cobrarJurus();

        assertEquals(1275.0, cartao.getFatura(), DELTA);
        
        assertEquals(1175.0, Banco.getInstancia().getReceitas(), DELTA);
        assertEquals(0.0, Banco.getInstancia().getDespesas(), DELTA);
    }

    @Test
    void pagarFaturaPelaContaCorrenteDeduzSaldoEZeraFaturaDoCartao() {
        Cartao cartaoDebito = new Cartao(1111, 222);
        CartaoCredito cartaoCredito = new CartaoCredito(500.0, 3, false, 1500.0, 3333, 444);
        ContaCorrente conta = new ContaCorrente(9999, 1000.0, cartaoDebito, 0.0, cartaoCredito);

        conta.pagarFatura(500.0);

        assertEquals(500.0, conta.getSaldo(), DELTA);
        
        assertEquals(0.0, cartaoCredito.getFatura(), DELTA);
    }

    @Test
    void debitarUtilizandoHerdadoCartaoDeCreditoReduzSaldoDaConta() {
        CartaoCredito cartaoCredito = new CartaoCredito(0.0, 3, true, 1000.0, 5555, 666);
        ContaPoupanca conta = new ContaPoupanca(7777, 300.0, cartaoCredito, 0.0);

        cartaoCredito.debitar(conta, 120.0);

        assertEquals(180.0, conta.getSaldo(), DELTA);
    }

    @Test
    void comprarNoCreditoPelaContaCorrenteAumentaFaturaDoCartao() {
        Cartao cartaoDebito = new Cartao(1111, 222);
        CartaoCredito cartaoCredito = new CartaoCredito(0.0, 3, true, 1500.0, 3333, 444);
        ContaCorrente conta = new ContaCorrente(9999, 1000.0, cartaoDebito, 0.0, cartaoCredito);

        java.io.InputStream originalSystemIn = System.in;

        try {
            // O método comprar da ContaCorrente exige input do Scanner:
            // "2" para escolher crédito, "1" para confirmar o cartão
            String inputsScanner = "2\n1\n";
            System.setIn(new java.io.ByteArrayInputStream(inputsScanner.getBytes()));

            // A conta aciona a compra, que deve comunicar-se com o método creditar() do CartaoCredito
            conta.comprar(250.0);

            // A fatura do cartão atrelado deve refletir o valor da compra
            assertEquals(250.0, cartaoCredito.getFatura(), DELTA);
        } finally {
            // Restaura o System.in para não quebrar outros testes
            System.setIn(originalSystemIn);
        }
    }

    private void limparEstado() {
        Banco.getInstancia().despesas = 0;
        Banco.getInstancia().receitas = 0;
    }
}