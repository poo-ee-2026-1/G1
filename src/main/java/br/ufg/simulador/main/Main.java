package br.ufg.simulador.main;

import br.ufg.simulador.api.ClienteTopografia;
import br.ufg.simulador.core.CalculoFresnel;
import br.ufg.simulador.core.ProcessadorRelevo;
import br.ufg.simulador.models.Enlace;
import br.ufg.simulador.models.Obstaculo;
import br.ufg.simulador.models.Torre;
import br.ufg.simulador.reports.GeradorDeRelatorios;
import br.ufg.simulador.ui.TelaMapa;

public class Main 
{
    public static void main(String[] args) 
    {
        ProcessadorRelevo processador = new ProcessadorRelevo();

        CalculoFresnel calculo = new CalculoFresnel();

        Enlace enlace = new Enlace();

        Obstaculo obstaculo = new Obstaculo();

        ClienteTopografia cliente = new ClienteTopografia();

        // Coordenadas da Torre A
        double latA = -16.6799;
        double lonA = -49.2550;
        double altitudeChaoA = cliente.getAltitude(latA, lonA); // Chamada da API

        // Coordenadas da Torre B
        double latB = -16.6850;
        double lonB = -49.2600;
        double altitudeChaoB = cliente.getAltitude(latB, lonB); // Chamada da API

        // Criando as Torres com as altitudes dinâmicas vindas da API
        Torre torreA = new Torre(latA, lonA, altitudeChaoA, 30.0);
        Torre torreB = new Torre(latB, lonB, altitudeChaoB, 45.0);

        // Registrando a quantidade de antenas
        torreA.setQuantidadeAntenas(1);
        torreB.setQuantidadeAntenas(1);

        // --- Análise do Perfil do Terreno ---

        // Define a quantidade de pontos (amostras) que queremos do relevo entre as torres.
        // Um número maior resulta em maior precisão, mas mais dados. 100 é um bom começo.
        int numeroDeAmostras = 100;

        // Busca o perfil do terreno entre a Torre A e a Torre B
        double[] perfilDoTerreno = cliente.getPerfilTerreno(latA, lonA, latB, lonB, numeroDeAmostras);

        System.out.println("Perfil do terreno obtido com " + perfilDoTerreno.length + " amostras.");

        TelaMapa tela = new TelaMapa();
        GeradorDeRelatorios gerador = new GeradorDeRelatorios();
    }
}
