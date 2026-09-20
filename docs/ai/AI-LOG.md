# Registro de Uso de Inteligência Artificial

## Histórico de Interações

**Informação**: Descrição \
**Responsável**: Integrante que realizou a interação \
**Atividade**: Atividade do trabalho associada  \
**Ferramenta**: ChatGPT, Claude, Copilot etc.  \
**Prompt/instrução**: Prompt ou instrução utilizada \
**Resultado**: Breve descrição da resposta da IA  \
**Decisão**: O que foi aceito, alterado ou rejeitado  \
**Validação**: Como o resultado foi verificado  
(Para cada uso relevante deverão ser registradas, sempre que aplicável, as informações acima.​)

1.\
**Informação**: Criação de testes unitários para a classe RegistroDeClientes \
**Responsável**: Marcello Bimbatti \
**Atividade**:  Testes unitários \
**Ferramenta**: Gemini  \
**Prompt/instrução**: Analisar o código de RegistroDeClientes.java e sugerir testes unitários em JUnit \
**Resultado**: A ferramenta gerou uma estrutura testes \
**Decisão**: Aceitei a estrutura de testes \
**Validação**: Execução local dos testes no terminal, validando que todos os 7 testes passaram  

2.\
**Informação**: Melhoria da classe RegistroDeClientes \
**Responsável**: Marcello Bimbatti \
**Atividade**: Tentativa de melhoria do código dos testes unitários de RegistroDeClientes  \
**Ferramenta**: Gemini  \
**Prompt/instrução**: Refatore os testes unitários da classe RegistroDeClientes para melhorar a estrutura dos casos de teste \
**Resultado**: A ferramenta sugeriu um código refatorado que quebrou a compilação ao instanciar o Singleton com new RegistroDeClientes()  \
**Decisão**: **Rejeitado** a mudança após identificar a falha de compilação e mantida a chamada via RegistroDeClientes.getInstancia() \
**Validação**: A execução do código gerado falhou na IDE durante a compilação
