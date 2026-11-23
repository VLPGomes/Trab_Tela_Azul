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

3.\
**Informação**: Ajuda na criação de testes unitários para a classe CartaoCredito \
**Responsável**: João Victor \
**Atividade**: Testes unitários  \
**Ferramenta**: Gemini  \
**Prompt/instrução**: "Você é um testador experimente de testes unitários. Quais cenários posso utilizar para gerar casos de testes para a classe Java CartaoCredito." \
**Resultado**: A ferramenta forneceu cenários para criar os testes para a classe.  \
**Decisão**: Utilizei os cenários para criar os casos de testes da classe CartaoCredito. \
**Validação**: Testes validados com execução local no terminal.

4.\
**Informação**: Expansão dos testes unitários com isolamento de dependências (Mockito) e ajuda na criacão dos testes de integração da classe CartaoCredito \
**Responsável**: João Victor \
**Atividade**: Testes unitários, técnicas funcionais (PCE/AVL) e testes de integração  \
**Ferramenta**: Gemini  \
**Prompt/instrução**: "Me ajude a expandir os testes unitários da classe CartaoCredito isolando dependências com Mockito, aplicando técnicas funcionais, e a estruturar um teste de integração." \
**Resultado**: A ferramenta sugeriu cenários de quivalência/valor limite, uso de mocks para Ano e Banco e o cenário de integração na virada de ano.  \
**Decisão**: Adaptei e implementei os casos em CartaoCreditoTest e CartaoCreditoIntegracaoTest. \
**Validação**: Conferência manual das asserções em relação às RNs da CartaoCredito, verificação do isolamento real via verify nos mocks e execução bem-sucedida da suíte via Maven.

5.\
**Informação**: Implementação do servidor web local (WinxBankWebServer) para viabilizar os testes de sistema com Selenium \
**Responsável**: João Victor \
**Atividade**: Adaptação de interface web para testes de sistema \
**Ferramenta**: Gemini \
**Prompt/instrução**: "Me ajude a criar uma interface web local simples (WinxBankWebServer) conectada às regras de negócio do WinxBank para permitir a execução de testes de sistema com Selenium." \
**Resultado**: A ferramenta forneceu a implementação de um servidor HTTP local em Java expondo as operações de cartão de crédito, pagamento de fatura e virada de mês. \
**Decisão**: Tive alguns problemas de implementação, mas após alguns ajustes consegui implementar a classe WinxBankWebServer no pacote web sem alterar as regras de negócio originais do sistema. \
**Validação**: Conferência de que o servidor apenas delegava as chamadas para as classes reais do domínio (CartaoCredito, ContaCorrente, Banco e Ano), verificação manual das respostas no navegador e execução dos testes E2E do Selenium contra a interface.

6.\
**Informação**: Apoio na criação dos testes de sistema E2E com Selenium WebDriver \
**Responsável**: João Victor \
**Atividade**: Testes de sistema (requisitos funcionais RF01–RF04 e requisito não funcional RNF01) \
**Ferramenta**: Gemini \
**Prompt/instrução**: "Me ajude na implementação de testes de sistema com Selenium para o fluxo de cartão de crédito do WinxBank, incluindo validação de desempenho conforme a ISO 25010." \
**Resultado**: A ferramenta forneceu a estrutura dos cenários ponta a ponta em WinxBankSeleniumTest cobrindo compra, limite, pagamento, juros e tempo de resposta (< 2s). \
**Decisão**: Implementei os testes E2E no pacote sistema integrados ao WinxBankWebServer. \
**Validação**: Verificação da rastreabilidade de cada método com os requisitos RF01–RF04 e RNF01, conferência das mensagens e estados finais na interface web e execução local via Selenium.

7.\
**Informação**: Atualização do Plano de Teste e do README.md para a Entrega 2 \
**Responsável**: João Victor \
**Atividade**: Documentação de testes \
**Ferramenta**: Gemini \
**Prompt/instrução**: "Me ajude a revisar o Plano de Teste da Entrega 1 e o README do repositório para incluir as atividades e ferramentas da Entrega 2." \
**Resultado**: A ferramenta sugeriu ajustes na Introdução, Escopo, Metodologia (fases, técnicas e completude), tabela de ferramentas, acrônimos e estrutura do README. \
**Decisão**: Apliquei as revisões no Google Docs (v2.0) e atualizei o README.md no GitHub. \
**Validação**: Revisão crítica de cada seção frente aos critérios da Entrega 2, correção de inconsistências conceituais (como o uso de dublês nos testes unitários) e conferência dos links e caminhos do repositório.

8.\
**Informação**: Expansão dos testes unitários de RegistroDeClientes para cobertura estrutural e mutação \
**Responsável**: Marcello Bimbatti Mazzochi\
**Atividade**: Testes unitários, cobertura de arestas e teste de mutação (PITest) \
**Ferramenta**: Gemini \
**Prompt/instrução**: "Análise o arquivo RegistroDeClientesTest.java) para realizar testes de mutação, estrutural e SonarQube." \
**Resultado**: A ferramenta sugeriu a expansão da suíte com testes parametrizados (@ParameterizedTest) para CPFs vazios/inválidos, cenários de remoção e atualização de clientes inexistentes, além dos comandos Maven para PITest e SonarQube. \
**Decisão**: Aceitei os testes parametrizados e cenários de borda.
**Validação**: Análise do relatório HTML do JaCoCo para confirmar o percurso em todas as arestas dos laços/condicionais e inspeção dos mutantes gerados pelo PITest (verificando quais mutantes sobreviveram e reforçando os oráculos de teste para eliminar mutações de remoção e alteração de estado).

9.\
**Informação**: Revisão dos testes de integração, isolamento com mocks e testes de sistema \
**Responsável**: Marcello Bimbatti Mazzochi \
**Atividade**: Testes de integração, Mockito e testes de sistema \
**Ferramenta**: Gemini \
**Prompt/instrução**: "Análise os códigos AtendimentoServiceTest, ClienteContaIntegracaoTest, RegistroDeClientesTest e WinxBankSystemTest." \
**Resultado**: A ferramenta apontou que AtendimentoServiceTest invocava o mock diretamente sem passar pela classe real e listou as pendências documentais. \
**Decisão**: Corrigi a asserção no teste de integração e reestruturei os testes com Mockito em `RegistroDeClientesMockTest.java` para validar o isolamento real de dependências. \
**Validação**: Revisão dos testes das classes Cliente, ClienteWinx e RegistroDeClientes. Garanti que os resultados batiam com os dados criados nos testes, que os mocks (Mockito) estão simulando corretamente as dependências reais e que a lista de clientes é limpa (limparListaDeClientes()) antes de cada novo teste para não misturar os dados.

10.\
**Informação**: Implementação de testes de sistema (Selenium) e requisito não funcional de desempenho para Clientes \
**Responsável**: Marcello Bimbatti Mazzochi \
**Atividade**: Testes de sistema (E2E) com Selenium e RNF de desempenho (ISO/IEC 25010) \
**Ferramenta**: Gemini \
**Prompt/instrução**: "Adaptar o arquivo WinxBankWebServer.java e desenvolver testes de sistema para Clientes. \
**Resultado**: A ferramenta adaptou o servidor web (`WinxBankWebServer.java`) com endpoints de gestão de clientes sem bloqueio de `Scanner(System.in)` e gerou o teste de sistema `ClienteSeleniumTest.java` . \
**Decisão**: Aceitei a solução ajustando as rotas HTML para o padrão `formaction` já usado no servidor do grupo e definindo a porta dedicada `8086` para evitar conflito com a porta `8085` do `WinxBankSeleniumTest`. 
**Validação**: Testei a página web do WinxBankWebServer para confirmar que os formulários enviam os dados corretamente para o RegistroDeClientes sem travar. Também conferi se as portas 8085 e 8086 não interferem uma na outra e medi a velocidade de resposta do servidor de acordo com a meta da ISO/IEC 25010.

11.\
**Informação**: Configuração do Maven para cobertura (JaCoCo), testes de mutação (PITest) e estruturação do relatório de técnicas de teste \
**Responsável**: João Victor \
**Atividade**: Testes estruturais (JaCoCo), testes baseados em defeitos (PITest) e troubleshooting de build \
**Ferramenta**: Gemini \
**Prompt/instrução**: "Me ajude a configurar e executar o JaCoCo e o PITest via Maven para a classe CartaoCredito, resolver erros de compilação/geração de relatório e estruturar o relatório de técnicas de teste." \
**Resultado**: A ferramenta forneceu os comandos filtrados do Maven (-Dtest, -DtargetClasses, mvn jacoco:report), orientações para limpeza de cache de build (mvn clean test) e estruturação das seções do relatório. \
**Decisão**: Configurei os plugins no pom.xml, executei as análises de cobertura e mutação e redigi o relatório consolidando as técnicas funcional, estrutural e baseada em defeitos. \
**Validação**: Conferência dos relatórios HTML gerados localmente (target/site/jacoco e target/pit-reports), verificação das métricas reais de linhas/mutantes e teste dos comandos em cópia limpa do repositório.

12.\
**Informação**: Apoio na inspeção de código-fonte e refatoração de CartaoCredito.java e Banco.java \
**Responsável**: João Victor \
**Atividade**: Inspeção de código-fonte e manutenção preventiva \
**Ferramenta**: Gemini \
**Prompt/instrução**: "Me ajude a analisar os apontamentos de inspeção de código em CartaoCredito.java e Banco.java e a documentar o relatório de inspeção." \
**Resultado**: A ferramenta auxiliou na interpretação dos alertas de qualidade/boas práticas e na organização do relatório de inspeção com o antes e depois das melhorias. \
**Decisão**: Apliquei apenas as refatorações pertinentes sem alterar as regras de negócio e documentei os apontamentos no Relatório de Inspeção do código-fonte. \
**Validação**: Reexecução completa dos testes unitários e de integração da classe CartaoCredito após as alterações, garantindo 100% de aprovação sem regressões. 
