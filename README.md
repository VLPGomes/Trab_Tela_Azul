# Trabalho Prático — Qualidade e Teste

Repositório do grupo Tela Azul para o trabalho prático da disciplina de Qualidade e Teste. O sistema escolhido para aplicação dos conceitos de teste é o WinxBank, um sistema bancário digital desenvolvido em Java.

---

## Documentaçao de Teste


### Entrega 1 (21/09/2026)

- **Plano de Teste:** [Plano de Teste](https://docs.google.com/document/d/1WCm11oAZAqIH7ckfDWwhfKcvYEKneLt2FQv9zj_mc-w/edit)
- **Casos de teste unitários:** [winxbank/src/test/java/br/winxbank](winxbank/src/test/java/br/winxbank)
- **Casos de teste manuais:** [Testes manuais](https://docs.google.com/document/d/13xAV0YUfUv9gwO8ZtsbtuWy3olUkobn4wkEHsu-MIl0/edit) + [TestLink](https://github.com/VLPGomes/Trab_Tela_Azul/blob/main/docs/Teste%20Manual%20TestLink%20CT002%20-%20Realizar%20compra%20com%20Cartao%20de%20cr%C3%A9dito.pdf)
- **Bugs encontrados:** [Issues](https://github.com/VLPGomes/Trab_Tela_Azul/issues)
- **Apresentação:** [Entrega 1](https://docs.google.com/presentation/d/1VC48kdokELuNO8g4aND4SimM0JrBxuKSh3BCZBXf8co/edit?usp=sharing)

### Entrega 2


---


## O Sistema WinxBank

O WinxBank é um sistema bancário digital que simula as operações de um banco através de uma interface de linha de comando. Os dados de clientes, banco e mês corrente são armazenados em arquivos (`clientes.json`, `banco.txt`, `mesAtual.txt`) entre execuções.

### Funcionalidades Principais

**Gestão de usuários**
- Cadastrar novo cliente
- Logar em um cliente existente
- Apagar usuário
- Listar / limpar lista de clientes

**Gestão de contas**
- Abrir conta corrente ou poupança
- Fechar conta
- Cliente que atinja saldo `>= 100.000` é promovido a **ClienteWinx** (cliente premium)

**Operações financeiras**
- Depositar / sacar
- Comprar (no débito ou no crédito)
- Realizar PIX entre contas
- Pagar fatura do cartão de crédito
- Ajustar limite do cartão de crédito
- Requisitar / pagar parcela de empréstimo
- Converter pontos de compra em saldo (exclusivo do ClienteWinx)

**Documentos e relatórios**
- Gerar extrato da conta
- Gerar informe de rendimento (poupança)
- Visualizar dados do banco (receitas / despesas)

### Regras de negócio relevantes

- **Conta Corrente:** desconta taxa mensal de manutenção (`R$ 13,00`), cobra juros sobre fatura em atraso do cartão de crédito.
- **Conta Poupança:** rende mensalmente sobre o saldo (taxa configurada em `OperacoesAutomaticas.rendimentoMensalPoupanca`).
- **Empréstimo:** divida sofre cobrança de juros mensais.
- **Cartão de crédito:** valida limite ao registrar nova fatura; juros sobre fatura não paga.
- **Simulação de tempo:** a classe `Ano` faz o mês passar a cada N interações do usuário, disparando as operações automáticas (juros, taxas, rendimentos).

---

## Como Executar

### Pré-requisitos

- Java 17 ([JDK 17.0.1](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html))
- Maven 3.8.5+
- (opcional) IntelliJ IDEA Community

### Compilar e executar

A partir da raiz do repositório:

```bash
cd winxbank
mvn spring-boot:run
```

### Rodar os testes unitários

```bash
cd winxbank
mvn test
```

---

## Estrutura do Repositório

```
.
├── README.md                  ← este arquivo
├── banco.txt                  ← estado persistido do banco
├── clientes.json              ← clientes persistidos
├── mesAtual.txt               ← mês atual da simulação
└── docs/                      ← documentação do trabalho
    ├── Teste manual TestLink  ← PDF do teste manual utilizando TestLink
    └── ai/
         ├── AI-LOG.md         ← registro do uso de IA
└── winxbank/                  ← sistema sob teste (Java + Maven)
    ├── pom.xml
    ├── README.md              ← documentação detalhada do WinxBank
    └── src/
        ├── main/java/br/winxbank/
        |        ├── Main.java
        |        ├── exception/        ← exceções customizadas
        |        ├── geradordedocumentos/  ← geração de extrato / informe
        |        ├── random/           ← geração de números aleatórios
        |        ├── repository/       ← persistência em arquivo (JSON / texto)
        |        ├── sistemabancario/  ← Banco, Conta, ContaCorrente, ContaPoupanca, Cartao, CartaoCredito, Movimentacao
        |        ├── sistemaclientes/  ← Cliente, ClienteWinx, RegistroDeClientes
        |        └── tempo/            ← simulação do passar dos meses (Ano)
        └── test/java/br/winxbank/
                 ├── sistemabancario/  ← testes unitários do sistema bancário
                 └── sistemaclientes/  ← testes unitários do sistema de clientes

```

---

## Equipe

| Membro | Responsabilidades |
|--------|-------------------|
| João Victor       | [CartaoCredito.java](winxbank/src/test/java/br/winxbank/sistemabancario/CartaoCreditoTest.java) + [Casos de teste manual da Classe CartaoCredito.java](https://docs.google.com/document/d/13xAV0YUfUv9gwO8ZtsbtuWy3olUkobn4wkEHsu-MIl0/edit?tab=t.0) + [TestLink](https://github.com/VLPGomes/Trab_Tela_Azul/blob/main/docs/Teste%20Manual%20TestLink%20CT002%20-%20Realizar%20compra%20com%20Cartao%20de%20cr%C3%A9dito.pdf) + [Plano de Testes](https://docs.google.com/document/d/1WCm11oAZAqIH7ckfDWwhfKcvYEKneLt2FQv9zj_mc-w/edit) |
| Vinicius Gomes    | [Conta.java](winxbank/src/test/java/br/winxbank/sistemabancario/ContaTest.java) + [Casos de teste manual da classe Conta.java](https://docs.google.com/document/d/13xAV0YUfUv9gwO8ZtsbtuWy3olUkobn4wkEHsu-MIl0/edit?tab=t.0) + [Plano de Testes](https://docs.google.com/document/d/1WCm11oAZAqIH7ckfDWwhfKcvYEKneLt2FQv9zj_mc-w/edit)|
| Marcello Bimbatti | [RegistroDeClientes](https://github.com/VLPGomes/Trab_Tela_Azul/blob/main/winxbank/src/test/java/br/winxbank/sistemaclientes/RegistroDeClientesTest.java)|

---

## Referências

- [README detalhado do WinxBank](winxbank/README.md)
- [Repositório original do projeto](https://github.com/repo-software-testing-courses/Sistema_Bancario_POO)
