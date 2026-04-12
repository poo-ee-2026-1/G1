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
        
        public Resultado(boolean viavel, double distancia, double[] distanciasPonto, double[] elevacaoTerreno, double[] linhaVisada, double[] fresnelInferior, List<ObstaculoInfo> obstaculos, double alturaTorreA, double alturaTorreB) {
            this.viavel = viavel;
            this.distancia = distancia;
            this.distanciasPonto = distanciasPonto;
            this.elevacaoTerreno = elevacaoTerreno;
            this.linhaVisada = linhaVisada;
            this.fresnelInferior = fresnelInferior;
            this.obstaculos = obstaculos;
            this.alturaTorreA = alturaTorreA;
            this.alturaTorreB = alturaTorreB;
        }
    }

    // Novo método que receberá os dados do clique no mapa e executará o fluxo
    public static Resultado executarSimulacao(double latA, double lonA, double latB, double lonB, List<DadosObstaculo> dadosObstaculos) 
    {
        ProcessadorRelevo processador = new ProcessadorRelevo();

        CalculoFresnel calculo = new CalculoFresnel();

        ClienteTopografia cliente = new ClienteTopografia();

        // Coordenadas da Torre A agora vêm dos parâmetros do método
        double altitudeChaoA = cliente.getAltitude(latA, lonA); // Chamada da API

        // Coordenadas da Torre B agora vêm dos parâmetros do método
        double altitudeChaoB = cliente.getAltitude(latB, lonB); // Chamada da API

        // Criando as Torres com as altitudes dinâmicas vindas da API
        Torre torreA = new Torre(latA, lonA, altitudeChaoA, 30.0);
        Torre torreB = new Torre(latB, lonB, altitudeChaoB, 45.0);

        // Criando e associando antenas às torres (altura de instalação na torre, potência de transmissão em dBm)
        torreA.setAntena(new Antena(30.0, 30.0)); // Antena instalada no topo da torre de 30m
        torreB.setAntena(new Antena(45.0, 30.0)); // Antena instalada no topo da torre de 45m

        // Criando o Enlace apenas após as Torres reais estarem configuradas com os dados da API
        Enlace enlace = new Enlace(torreA, torreB, 2.4); // Frequência de 2.4 GHz (Wi-Fi)

        // --- Análise do Perfil do Terreno ---

        // A API utilizada (SRTM90m) possui resolução de aproximadamente 90 metros por ponto de coleta.
        // Calculamos a proporção ideal de 1 amostra a cada 90 metros.
        double distanciaEmMetros = enlace.getDistancia() * 1000.0;
        int numeroDeAmostras = (int) Math.ceil(distanciaEmMetros / 90.0);
        
        // Trava de segurança para limites da API (OpenTopoData restringe acima de 512)
        if (numeroDeAmostras < 10) numeroDeAmostras = 10;
        if (numeroDeAmostras > 500) numeroDeAmostras = 500;

        // Busca o perfil do terreno entre a Torre A e a Torre B
        double[] perfilDoTerreno = cliente.getPerfilTerreno(latA, lonA, latB, lonB, numeroDeAmostras);

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
        boolean enlaceViavel = true;
        double distanciaTotal = enlace.getDistancia();
        double freq = enlace.getFrequencia();
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
        System.out.println("\nIniciando análise de visibilidade e Zona de Fresnel...");
        
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
                enlaceViavel = false;
                System.out.printf("ALERTA: Obstrução no relevo detectada a %.2f km da Torre A! (Visada: %.2fm | Terreno: %.2fm | Raio: %.2fm)\n", d1, altVisada, altTerrenoPonto, raioFresnel);
            }
        }

        if (enlaceViavel) {
            System.out.println("\nSTATUS DO ENLACE: VIÁVEL! A primeira Zona de Fresnel possui margem de folga adequada.");
        } else {
            System.out.println("\nSTATUS DO ENLACE: INVIÁVEL! Foram detectadas obstruções críticas no trajeto.");
        }

        GeradorDeRelatorios gerador = new GeradorDeRelatorios();
        
        return new Resultado(enlaceViavel, distanciaTotal, arrayDist, arrayTerreno, arrayVisada, arrayFresnel, obstaculosGrafico, altAntenaA - torreA.getAltitudeTerreno(), altAntenaB - torreB.getAltitudeTerreno());
    }
}
