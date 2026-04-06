package br.ufg.simulador.models;
 
{
	public class Obstaculo {
	    public String nome;
	    // Coordenadas no espaço 3D
	    public double latitude;
	    public double longitude;
	    public double altitudeTerreno; // Altitude do topo do obstáculo
	    
	    public double raio; // Tamanho do obstáculo (importante para saber se o sinal bate nele)

	    public Obstaculo(String nome, double latitude, double longitude, double altitudeTerreno, double raio) {
	        this.nome = nome;
	        this.latitude = latitude;
	        this.longitude = longitude;
	        this.altitudeTerreno = altitudeTerreno;
	        this.raio = raio;
	    }

	}
}
