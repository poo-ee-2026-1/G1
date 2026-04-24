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
### SIMULADOR DE RÁDIO-VISIBILIDADE 

## Introdução
  A conectividade sem fio representa um dos pilares fundamentais da infraestrutura tecnológica moderna, sendo essencial para a integração de sistemas residenciais, comerciais e industriais. Com o aumento da demanda por transmissão de dados e a necessidade de redes cada vez mais estáveis, torna-se crucial o desenvolvimento de ferramentas que permitam simular e analisar a viabilidade de enlaces de rádio entre diferentes pontos geográficos. Sistemas de análise de rádio visibilidade cumprem este papel ao fornecer informações detalhadas sobre a propagação do sinal, possibilitando a identificação de obstáculos que possam causar obstruções e auxiliando na definição da altura ideal das torres e na potência dos equipamentos. Esses sistemas permitem prever o comportamento da Zona de Fresnel, que consiste em um elipsóide de revolução ao redor da linha de visada direta entre as antenas transmissoras e receptoras, além de considerar a influência da curvatura da Terra para reduzir custos de instalação e garantir uma gestão eficiente do espectro de frequências.

## Objetivo 
  O objetivo central deste projeto é simular a viabilidade de comunicação via rádio entre dois pontos a partir do tipo de equipamento e da localização geográfica das torres. O sistema permite registrar as antenas presentes no enlace, mostrar a margem de folga da Zona de Fresnel por trecho e calcular a distância total do enlace em quilômetros, estimando com precisão se o sinal sofrerá obstruções críticas que impeçam a transmissão. Para alcançar este propósito, o simulador integra a modelagem técnica dos elementos do enlace com o cálculo físico da propagação eletromagnética. O núcleo computacional processa os parâmetros geográficos e aplica a equação da primeira Zona de Fresnel para calcular o raio de visada livre no espaço tridimensional. Com base nesse raio, o software executa análises que consideram a altura do relevo e verificam a margem de folga necessária para prever perdas de sinal.
  
## Motivo
  A necessidade de prever o comportamento de links de rádio surge devido à crescente dependência de redes sem fio para comunicação crítica e internet de alta velocidade. Entretanto, na maioria dos casos, projetistas enfrentam dificuldades em visualizar como o relevo e os obstáculos impactam o sinal antes da instalação física dos equipamentos. Essa falta de simulação prévia dificulta a identificação de "zonas cegas" e impede a adoção de medidas corretivas, como o aumento da altura das torres ou a mudança de frequências, resultando em enlaces instáveis ou inoperantes. Assim, surge a necessidade de um sistema capaz de monitorar e estimar a visibilidade de rádio entre diferentes dispositivos elétricos, permitindo ao usuário acompanhar de forma clara e organizada se o caminho físico entre o transmissor e o receptor cumpre os requisitos mínimos de propagação eletromagnética.
  
## Cronograma

- 16/03 - Definição do tema do projeto: Estudo de rádio visibilidade e propagação;

- 23/03 - Definição do cronograma de desenvolvimento;

- 30/03 - Estrutura base do programa: Setup do projeto e arquitetura de pacotes;

- 06/04 - Definições de objetos e classes: Modelagem de Torres, Antenas e Coordenadas;

- 13/04 - Aprimoramento das características dos objetos: Implementação de Ganho, Frequência e Altura;

- 20/04 - Implementação do cálculo de distância geodésica: Integração de parâmetros geográficos;

- 27/04 - Desenvolvimento do algoritmo de cálculo: Aplicação da equação da primeira Zona de Fresnel;

- 04/05 - Criação da lógica de análise de obstrução: Processamento de relevo e margem de folga;

- 11/05 - Testes de validação de visibilidade: Verificação de obstruções críticas e bugs;

- 18/05 - Formatação da saída de dados: Exibição da distância total e status do enlace;

- 25/05 - Revisão final do código e documentação;

- 01/06 - Seminário e apresentação dos resultados.

## Classes

1. TelaGrafico:
- Herança: Esta classe herda de javax.swing.JFrame (extends JFrame), aproveitando todo o comportamento de uma janela de aplicativo padrão do Java. A sua classe interna PainelGrafico herda de javax.swing.JPanel.
- Polimorfismo: Apresenta polimorfismo de sobreposição (override) na classe interna PainelGrafico, sobrescrevendo o método paintComponent(Graphics g) da superclasse JPanel para desenhar formas gráficas customizadas, como a linha de visada, terreno e Zonas de Fresnel. Também utiliza polimorfismo ao instanciar classes anônimas que estendem MouseAdapter e MouseMotionAdapter, reescrevendo comportamentos de eventos de mouse (ex: mouseMoved).
- Encapsulamento: Os dados internos de estado do gráfico, como res (o resultado da simulação) e coordenadas de rastreamento do mouse (mouseX, mouseY), são declarados como private na classe aninhada, impedindo a modificação indevida por outras classes.
   
2. TelaMapa:
- Herança: Herda de javax.swing.JFrame.
- Polimorfismo: Implementa polimorfismo de substituição ao usar classes anônimas de MouseAdapter, sobrescrevendo mouseClicked(MouseEvent e) para capturar a posição do mouse no mapa.
- Encapsulamento: Os componentes visuais (como botões btnLimpar, btnSimular), variáveis de contagem (contadorTorres) e objetos de mapa (map, coordenadas) são protegidos com acesso private. A classe controla os fluxos garantindo que o mapa e os estados não sejam expostos abertamente.

3. Torre:
- Herança: Nenhuma explicita (herda diretamente da classe base Object).
- Encapsulamento (Forte): Possui um encapsulamento estrito. Os atributos (latitude, longitude, altitudeTerreno, alturaEstrutura e o objeto antena) são private. Além disso, os métodos setters (setAlturaEstrutura, setAntena) executam lógicas de validação que disparam exceções (IllegalArgumentException) caso o programa tente definir uma altura de antena superior à altura da torre, protegendo a consistência da regra de negócio.

4. Enlace:
- Herança / Polimorfismo: Não se aplicam diretamente.
- Encapsulamento: Os atributos torreA, torreB, frequencia e distancia são privados. O construtor é protegido com validações que impedem que um enlace seja criado com dados null. A lógica matemática para cálculo da distância geodésica está oculta em um método privado calcularDistancia(), blindando a complexidade do uso externo.

5. Antena:
- Herança / Polimorfismo: Não se aplicam diretamente.
- Encapsulamento: Seus atributos altura e potenciaTransmissao são não apenas private, mas também final (imutáveis após a construção do objeto), expostos somente por meio de getters. Garantindo que o estado da antena não mude de forma não prevista.

6. Usuario:
- Encapsulamento: Representa os usuários do sistema. Atributos clássicos de dados e credenciais (nome, email, senha) declarados como private, garantindo o acesso indireto via métodos Getters e Setters públicos para evitar que dados sejam gravados de maneira irregular.

7. ClienteTopografia:
- Encapsulamento: Encapsula de forma elegante a dependência HttpClient e as constantes sensíveis (como API_URL) declarando-as como private. Os consumidores da classe apenas utilizam métodos públicos simplificados (getAltitude e getPerfilTerreno), sem precisarem conhecer as regras de rede (HTTP), a conversão de JSON ou como as requisições estão sendo construídas.

8. ProcessadorRelevo e CalculoFresnel:
- São classes focadas em serviços utilitários e algoritmos matemáticos e físicos.
- Encapsulamento: Elas evitam gerenciar estados, não guardam o histórico do cálculo. Ao invés disso, os métodos recebem parâmetros, processam (por exemplo, fórmulas complexas da Zona de Fresnel) e entregam os resultados.

9. Main:
- Herança / Polimorfismo: Utiliza polimorfismo através de lambdas (() -> new TelaMapa()) que implicitamente implementam a interface Runnable esperada pelo SwingUtilities.
- Encapsulamento: Emprega o uso de classes internas estáticas aninhadas (DadosObstaculo, ObstaculoInfo, Resultado) para empacotar informações de maneira encapsulada e organizada entre as chamadas dos processos lógicos da classe. O método executarSimulacao é o grande orquestrador e mantém grande parte do fluxo transacional enclausurado em sua estrutura.
