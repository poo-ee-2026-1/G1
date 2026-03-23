## Getting Started

Welcome to the VS Code Java world. Here is a guideline to help you get started to write Java code in Visual Studio Code.

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

## Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).
### SIMULADOR DE REDES DE TRANSMISSAO- ANALISE DO FLUXO DE CARGA 

## Introdução
  A rede elétrica é um sistema interligado e  altamente complexo, composto por linhas de transmissão e distribuição usinas, subestações, sendo responsável pela geração, transporte e entrega de energia. Tal rede funciona como uma "máquina" sistêmica, garantindo o fluxo contínuo e seguro da eletricidadeé frequentemente descrita como a máquina mais complexa já construída pela humanidade. Diferente de outras infraestruturas de transporte, a energia elétrica em larga escala não pode ser armazenada de forma eficiente; ela deve ser gerada no exato instante em que é consumida. Além disso, a energia não viaja por rotas pré-definidas de forma controlada como os dados em uma rede de internet. O fluxo de potência obedece estritamente às leis da física — especificamente as Leis de Kirchhoff — espalhando-se pelas linhas de transmissão de acordo com a impedância de cada caminho. Nesse cenário, é comum surgirem algumas dúvidas sobre o funcionamento do sistema. Por exemplo, o que ocorre quando uma linha de transmissão falha, se as demais podem ficar sobrecarregadas a ponto de superaquecer e até sofrer danos, e se a tensão em uma determinada cidade pode cair abaixo dos níveis seguros nos horários de maior consumo.

## Objetivo
  O objetivo de um simulador de redes de transmissão surge como uma alternativa para analisar e monitorar o comportamento do sistema, permitindo identificar situações que estejam dentro ou fora das condições normais de operação.
  
## Metódo
  O simulador integra a modelagem Orientada a Objetos para representar os componentes da rede (barras e linhas) com o cálculo matemático do sistema elétrico. Seu núcleo computacional monta a Matriz de Admitância Nodal (Ybus) e aplica o método iterativo de Newton-Raphson para resolver as equações não lineares de fluxo de potência. Com o estado da rede calculado, o software executa análises de contingência, simulando falhas de equipamentos e recalculando o fluxo para prever com segurança possíveis sobrecargas ou quedas de tensão.
  
## Motivo
  Como a infraestrutura é crítica e a energia elétrica não pode ser facilmente armazenada em larga escala, qualquer falha no mundo físico pode resultar em blecautes, danos a equipamentos caros e prejuízos enormes à sociedade. O simulador resolve o desafio de prever esses cenários complexos de forma totalmente segura. Não se pode simplesmente desligar uma linha física para testar se outra vai sobrecarregar. O simulador se faz necessário para prever esse efeito dominó (cascateamento) em um ambiente isolado e seguro.
  
## Cronograma
- 16/03 - Definição do tema do projeto;
- 23/03 - Definição do cronograma;
- 30/03 - Estrutura base do programa;
- 06/04 - Definições de objetos e classes;
- 13/04 - Aprimoramento das características dos objetos;
- 20/04 - Implementação da montagem da Matriz de Admitância (Ybus);
- 27/04 - Desenvolvimento do algoritmo de cálculo (Newton-Raphson);
- 04/05 - Criação da lógica de análise de contingência (simulação de falhas);
- 11/05 - Testes de validação de fluxo e correção de bugs;
- 18/05 - Formatação da saída de dados (exibição de tensões, fluxos e alertas);
- 25/05 - Revisão final do código e preparação dos slides da apresentação;
- 01/06 - Seminário.
