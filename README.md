# Trabalho Prático — Qualidade e Teste

Repositório do grupo Tela Azul para o trabalho prático da disciplina de Qualidade e Teste. O sistema escolhido para aplicação dos conceitos de teste é o WinxBank, um sistema bancário digital desenvolvido em Java.

---

## Documentaçao de Teste


### Entrega 1 (21/09/2026)

- **Plano de Teste:** [Plano de Teste](https://docs.google.com/document/d/1WCm11oAZAqIH7ckfDWwhfKcvYEKneLt2FQv9zj_mc-w/edit)
- **Casos de teste unitários:**
- **Casos de teste manuais:** [Testes manuais](https://docs.google.com/document/d/13xAV0YUfUv9gwO8ZtsbtuWy3olUkobn4wkEHsu-MIl0/edit)
- **Bugs encontrados:**

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
└── docs/                      ← documentação
    └── ai/
         ├── AI-LOG.md         ← registro do uso de IA
└── winxbank/                  ← sistema sob teste (Java + Maven)
    ├── pom.xml
    ├── README.md              ← documentação detalhada do WinxBank
    └── src/br/winxbank
            ├── Main.java
            ├── exception/        ← exceções customizadas
            ├── geradordedocumentos/  ← geração de extrato / informe
            ├── random/           ← geração de números aleatórios
            ├── repository/       ← persistência em arquivo (JSON / texto)
            ├── sistemabancario/  ← Banco, Conta, ContaCorrente, ContaPoupanca, Cartao, CartaoCredito, Movimentacao
            ├── sistemaclientes/  ← Cliente, ClienteWinx, RegistroDeClientes
            └── tempo/            ← simulação do passar dos meses (Ano)

```

---

## Equipe

| Membro | Responsabilidades |
|--------|-------------------|
| João Victor       |          |
| Vinicius Gomes    |          |
| Marcello Bimbatti |          |

---

## Referências

- [README detalhado do WinxBank](winxbank/README.md)
- [Repositório original do projeto](https://github.com/repo-software-testing-courses/Sistema_Bancario_POO)
