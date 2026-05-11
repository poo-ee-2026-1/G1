package br.ufg.simulador.main;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import br.ufg.simulador.api.ClienteTopografia;
import br.ufg.simulador.core.CalculoFresnel;
import br.ufg.simulador.core.ProcessadorRelevo;
import br.ufg.simulador.models.Antena;
import br.ufg.simulador.models.Enlace;
import br.ufg.simulador.models.Torre;
import br.ufg.simulador.reports.GeradorDeRelatorios;
import br.ufg.simulador.ui.TelaMapa;

public class Main 
{
    public static void main(String[] args) 
    {
        System.setProperty("http.agent", "SimuladorRadioVisibilidade_UFG/1.0 (diegoaugustocostabastos@gmail.com)");
        
        // Inicia a interface gráfica na Thread correta do Swing, evitando avisos da IDE e prevenindo travamentos
        SwingUtilities.invokeLater(() -> new TelaMapa());
    }

    // Classe auxiliar para trafegar os dados dos obstáculos da TelaMapa para o Main
    public static class DadosObstaculo {
        public String nome;
        public double latitude;
        public double longitude;
        public double altura;
        public DadosObstaculo(String nome, double latitude, double longitude, double altura) {
            this.nome = nome;
            this.latitude = latitude;
            this.longitude = longitude;
            this.altura = altura;
        }
    }

    // Classe auxiliar para trafegar as configurações da torre da UI para o Core
    public static class DadosTorre {
        public double latitude;
        public double longitude;
        public double alturaEstrutura;
        public List<Antena> antenas = new ArrayList<>();
    }

    // Classe auxiliar para armazenar dados dos obstáculos no gráfico
    public static class ObstaculoInfo {
        public String nome;
        public double distancia;
        public double altitude;
        
        public ObstaculoInfo(String nome, double distancia, double altitude) {
            this.nome = nome;
            this.distancia = distancia;
            this.altitude = altitude;
        }
    }

    // Classe auxiliar para armazenar múltiplos retornos da simulação
    public static class Resultado {
        public boolean viavel;
        public double distancia;
        public double[] distanciasPonto;
        public double[] elevacaoTerreno;
        public double[] linhaVisada;
        public double[] fresnelInferior;
        public List<ObstaculoInfo> obstaculos;
        public double alturaTorreA;
        public double alturaTorreB;
        public double[] alturasAntenasA;
        public double[] alturasAntenasB;
        public int antenaPrincipalAIndex;
        public int antenaPrincipalBIndex;
        public String antenaAInfo;
        public String antenaBInfo;
        public double perdaEspacoLivre;
        public double potenciaRecepcao;
        public boolean obstrucaoFresnel;
        public double sensibilidadeReceptor;
        
        public Resultado(boolean viavel, double distancia, double[] distanciasPonto, double[] elevacaoTerreno, double[] linhaVisada, double[] fresnelInferior, List<ObstaculoInfo> obstaculos, double alturaTorreA, double alturaTorreB, double[] alturasAntenasA, double[] alturasAntenasB, int antenaPrincipalAIndex, int antenaPrincipalBIndex, String antenaAInfo, String antenaBInfo, double perdaEspacoLivre, double potenciaRecepcao, boolean obstrucaoFresnel, double sensibilidadeReceptor) {
            this.viavel = viavel;
            this.distancia = distancia;
            this.distanciasPonto = distanciasPonto;
            this.elevacaoTerreno = elevacaoTerreno;
            this.linhaVisada = linhaVisada;
            this.fresnelInferior = fresnelInferior;
            this.obstaculos = obstaculos;
            this.alturaTorreA = alturaTorreA;
            this.alturaTorreB = alturaTorreB;
            this.alturasAntenasA = alturasAntenasA;
            this.alturasAntenasB = alturasAntenasB;
            this.antenaPrincipalAIndex = antenaPrincipalAIndex;
            this.antenaPrincipalBIndex = antenaPrincipalBIndex;
            this.antenaAInfo = antenaAInfo;
            this.antenaBInfo = antenaBInfo;
            this.perdaEspacoLivre = perdaEspacoLivre;
            this.potenciaRecepcao = potenciaRecepcao;
            this.obstrucaoFresnel = obstrucaoFresnel;
            this.sensibilidadeReceptor = sensibilidadeReceptor;
        }
    }

    // Novo método que receberá os dados do clique no mapa e executará o fluxo
    public static Resultado executarSimulacao(DadosTorre configA, DadosTorre configB, List<DadosObstaculo> dadosObstaculos, double frequencia) 
    {
        ProcessadorRelevo processador = new ProcessadorRelevo();

        CalculoFresnel calculo = new CalculoFresnel();

        ClienteTopografia cliente = new ClienteTopografia();

        // Coordenadas da Torre A agora vêm dos parâmetros do método
        double altitudeChaoA = cliente.getAltitude(configA.latitude, configA.longitude);

        // Coordenadas da Torre B agora vêm dos parâmetros do método
        double altitudeChaoB = cliente.getAltitude(configB.latitude, configB.longitude);

        // Criando as Torres com as altitudes dinâmicas vindas da API
        Torre torreA = new Torre(configA.latitude, configA.longitude, altitudeChaoA, configA.alturaEstrutura);
        Torre torreB = new Torre(configB.latitude, configB.longitude, altitudeChaoB, configB.alturaEstrutura);

        // Criando e associando antenas às torres (altura de instalação na torre, potência de transmissão em dBm)
        for (Antena ant : configA.antenas) torreA.adicionarAntena(ant);
        for (Antena ant : configB.antenas) torreB.adicionarAntena(ant);

        // Criando o Enlace apenas após as Torres reais estarem configuradas com os dados da API
        Enlace enlace = new Enlace(torreA, torreB, frequencia);

        // --- Análise do Perfil do Terreno ---

        // A API utilizada (SRTM90m) possui resolução de aproximadamente 90 metros por ponto de coleta.
        // Calculamos a proporção ideal de 1 amostra a cada 90 metros.
        double distanciaEmMetros = enlace.getDistancia() * 1000.0;
        int numeroDeAmostras = (int) Math.ceil(distanciaEmMetros / 90.0);
        
        // Trava de segurança para limites da API (OpenTopoData restringe acima de 512)
        if (numeroDeAmostras < 10) numeroDeAmostras = 10;
        if (numeroDeAmostras > 500) numeroDeAmostras = 500;

        // Busca o perfil do terreno entre a Torre A e a Torre B
        double[] perfilDoTerreno = cliente.getPerfilTerreno(configA.latitude, configA.longitude, configB.latitude, configB.longitude, numeroDeAmostras);

        System.out.println("Perfil do terreno obtido com " + perfilDoTerreno.length + " amostras.");

        // Convertendo os dados visuais do mapa em obstáculos reais com a altitude da API
        /*List<Obstaculo> listaObstaculos = new ArrayList<>();
        for (DadosObstaculo dado : dadosObstaculos) {
            double altChao = cliente.getAltitude(dado.latitude, dado.longitude);
            listaObstaculos.add(new Obstaculo(dado.nome, dado.latitude, dado.longitude, altChao, dado.altura));
        }*/
        
        // Integrando os obstáculos ao perfil natural do terreno
        /*double[] perfilProcessado = processador.integrarObstaculos(perfilDoTerreno, listaObstaculos, torreA, enlace.getDistancia());
        System.out.println("Obstáculos integrados ao perfil do terreno com sucesso.");*/

        // Aplicando o efeito da curvatura da Terra ao perfil do terreno
        double[] perfilProcessado = processador.aplicarCurvaturaTerra(perfilDoTerreno, enlace.getDistancia());
        System.out.println("Efeito da curvatura da Terra aplicado com sucesso.");

        // --- Extração de Variáveis ---
        double distanciaTotal = enlace.getDistancia();
        double freq = enlace.getFrequencia();

        // --- Otimização: Pré-cálculo de variáveis independentes das antenas ---
        double[] preCalcD1 = new double[perfilProcessado.length];
        double[] preCalcD2 = new double[perfilProcessado.length];
        double[] preCalcRaioFresnel = new double[perfilProcessado.length];
        for (int k = 0; k < perfilProcessado.length; k++) {
            preCalcD1[k] = (distanciaTotal / (perfilProcessado.length - 1)) * k;
            preCalcD2[k] = distanciaTotal - preCalcD1[k];
            preCalcRaioFresnel[k] = (preCalcD1[k] == 0 || preCalcD2[k] == 0) ? 0 : calculo.calcularRaioFresnel(preCalcD1[k], preCalcD2[k], freq);
        }

        // --- Auto-Teste de Antenas (Encontrar a melhor combinação) ---
        int melhorIa = 0;
        int melhorIb = 0;
        boolean encontrouViavel = false;

        System.out.println("\nIniciando auto-teste de combinações de antenas...");
        for (int ia = 0; ia < configA.antenas.size(); ia++) {
            for (int ib = 0; ib < configB.antenas.size(); ib++) {
                torreA.setAntenaSelecionadaIndex(ia);
                torreB.setAntenaSelecionadaIndex(ib);
                
                double altA = torreA.getAltitudeTotalAntena();
                double altB = torreB.getAltitudeTotalAntena();
                
                boolean viavelAtual = true;
                for (int k = 0; k < perfilProcessado.length; k++) {
                    if (preCalcD1[k] == 0 || preCalcD2[k] == 0) continue;
                    
                    double altVisada = calculo.calcularAltitudeLinhaVisada(altA, altB, preCalcD1[k], preCalcD2[k]);
                    
                    if (calculo.verificarObstrucao(altVisada, perfilProcessado[k], preCalcRaioFresnel[k], 0.6)) {
                        viavelAtual = false;
                        break;
                    }
                }
                
                if (viavelAtual) {
                    encontrouViavel = true;
                    melhorIa = ia;
                    melhorIb = ib;
                    break;
                }
            }
            if (encontrouViavel) break;
        }

        // Se nenhuma combinação for viável, escolhe as antenas mais altas como padrão para minimizar obstruções
        if (!encontrouViavel) {
            double maxAltA = -1;
            for (int i = 0; i < configA.antenas.size(); i++) {
                if (configA.antenas.get(i).getAltura() > maxAltA) {
                    maxAltA = configA.antenas.get(i).getAltura();
                    melhorIa = i;
                }
            }
            double maxAltB = -1;
            for (int i = 0; i < configB.antenas.size(); i++) {
                if (configB.antenas.get(i).getAltura() > maxAltB) {
                    maxAltB = configB.antenas.get(i).getAltura();
                    melhorIb = i;
                }
            }
        }

        // Fixa as antenas principais encontradas
        torreA.setAntenaSelecionadaIndex(melhorIa);
        torreB.setAntenaSelecionadaIndex(melhorIb);

        double altAntenaA = torreA.getAltitudeTotalAntena();
        double altAntenaB = torreB.getAltitudeTotalAntena();

        // Prepara a lista de obstáculos para o gráfico
        List<ObstaculoInfo> obstaculosGrafico = new ArrayList<>();
        /*
        for (Obstaculo obs : listaObstaculos) {
            double dist = processador.calcularDistanciaGeodesica(torreA.getLatitude(), torreA.getLongitude(), obs.latitude, obs.longitude);
            if (dist <= distanciaTotal) {
                // Dica: caso 'obs.nome' dê erro de visibilidade, substitua por 'obs.getNome()'
                obstaculosGrafico.add(new ObstaculoInfo(obs.nome, dist, obs.altitudeTerreno));
            }
        }
        */

        // --- Análise da Zona de Fresnel ---
        
        // Arrays para armazenar os dados do gráfico
        double[] arrayDist = new double[perfilProcessado.length];
        double[] arrayTerreno = new double[perfilProcessado.length];
        double[] arrayVisada = new double[perfilProcessado.length];
        double[] arrayFresnel = new double[perfilProcessado.length];

        for (int i = 0; i < perfilProcessado.length; i++) {
            // Calcula a distância do ponto atual até as torres A e B
            double d1 = (distanciaTotal / (perfilProcessado.length - 1)) * i;
            double d2 = distanciaTotal - d1;
            
            double altVisada = calculo.calcularAltitudeLinhaVisada(altAntenaA, altAntenaB, d1, d2);
            double altTerrenoPonto = perfilProcessado[i];
            double raioFresnel = (d1 == 0 || d2 == 0) ? 0 : calculo.calcularRaioFresnel(d1, d2, freq);

            // Guarda os dados para plotar no gráfico mais tarde
            arrayDist[i] = d1;
            arrayTerreno[i] = altTerrenoPonto;
            arrayVisada[i] = altVisada;
            arrayFresnel[i] = altVisada - raioFresnel; // Base da zona de Fresnel

            // Pula os pontos exatos das torres (onde d1 ou d2 é 0, e o raio de Fresnel seria 0)
            if (d1 == 0 || d2 == 0) continue;

            // Verifica se o terreno invade os 60% (0.6) essenciais da zona de Fresnel
            boolean temObstrucao = calculo.verificarObstrucao(altVisada, altTerrenoPonto, raioFresnel, 0.6);
            
            if (temObstrucao) {
                System.out.printf("ALERTA: Obstrução no relevo detectada a %.2f km da Torre A! (Visada: %.2fm | Terreno: %.2fm | Raio: %.2fm)\n", d1, altVisada, altTerrenoPonto, raioFresnel);
            }
        }

        if (encontrouViavel) {
            System.out.println("\nSTATUS DO ENLACE: VIÁVEL! A primeira Zona de Fresnel possui margem de folga adequada.");
        } else {
            System.out.println("\nSTATUS DO ENLACE: INVIÁVEL! Foram detectadas obstruções críticas no trajeto.");
        }

        GeradorDeRelatorios gerador = new GeradorDeRelatorios();
        
        String infoA = torreA.getAntena() != null ? String.format("Antena %d | Alt: %.1fm | Pot: %.1fdBm | Ganho: %.1fdBi", melhorIa + 1, torreA.getAntena().getAltura(), torreA.getAntena().getPotenciaTransmissao(), torreA.getAntena().getGanho()) : "Nenhuma";
        String infoB = torreB.getAntena() != null ? String.format("Antena %d | Alt: %.1fm | Pot: %.1fdBm | Ganho: %.1fdBi", melhorIb + 1, torreB.getAntena().getAltura(), torreB.getAntena().getPotenciaTransmissao(), torreB.getAntena().getGanho()) : "Nenhuma";

        double[] arrayAntenasA = new double[configA.antenas.size()];
        for(int i=0; i<configA.antenas.size(); i++) arrayAntenasA[i] = configA.antenas.get(i).getAltura();
        
        double[] arrayAntenasB = new double[configB.antenas.size()];
        for(int i=0; i<configB.antenas.size(); i++) arrayAntenasB[i] = configB.antenas.get(i).getAltura();

        // --- Cálculo de Perda e Potência Estimada ---
        // Cálculo de Perda de Espaço Livre (FSPL - Free Space Path Loss) = 20log10(d_km) + 20log10(f_GHz) + 92.45
        double fspl = 20 * Math.log10(distanciaTotal) + 20 * Math.log10(freq) + 92.45;
        
        double txPower = torreA.getAntena() != null ? torreA.getAntena().getPotenciaTransmissao() : 0.0;
        double txGain = torreA.getAntena() != null ? torreA.getAntena().getGanho() : 0.0;
        double rxGain = torreB.getAntena() != null ? torreB.getAntena().getGanho() : 0.0;
        double rxPower = txPower + txGain + rxGain - fspl;
        
        // Verificação final de viabilidade, considerando também a sensibilidade
        double sensibilidadeReceptor = torreB.getAntena() != null ? torreB.getAntena().getSensibilidade() : -120.0; // Padrão seguro
        boolean sinalSuficiente = rxPower > sensibilidadeReceptor;
        
        boolean enlaceViavel = encontrouViavel && sinalSuficiente;

        return new Resultado(enlaceViavel, distanciaTotal, arrayDist, arrayTerreno, arrayVisada, arrayFresnel, obstaculosGrafico, torreA.getAlturaEstrutura(), torreB.getAlturaEstrutura(), arrayAntenasA, arrayAntenasB, melhorIa, melhorIb, infoA, infoB, fspl, rxPower, !encontrouViavel, sensibilidadeReceptor);
    }
}
