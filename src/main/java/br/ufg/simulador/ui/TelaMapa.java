package br.ufg.simulador.ui;

public class TelaMapa 
{
    public void exibirMapa() 
    {
        System.out.println("Exibindo mapa com as torres e obstáculos...");
    }

    public void plotarPerfilTerreno(double[] perfil) {
        System.out.println("Plotando perfil do terreno com " + perfil.length + " pontos.");
    }

}
