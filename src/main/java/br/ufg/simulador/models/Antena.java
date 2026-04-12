package br.ufg.simulador.models;

public class Antena 
{
    private final double altura;
    private final double potenciaTransmissao; // Potência de transmissão da antena em dBm

    public Antena(double altura, double potenciaTransmissao) {
        this.altura = altura;
        this.potenciaTransmissao = potenciaTransmissao;
    }

    public double getAltura() {
        return altura;
    }

    public double getPotenciaTransmissao() {
        return potenciaTransmissao;
    }

}
