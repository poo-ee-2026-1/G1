package br.ufg.simulador.models;

public class Antena 
{
    private final double altura;
    private final double potenciaTransmissao; // Potência de transmissão da antena em dBm
    private final double ganho; // Ganho da antena em dBi
    private final double sensibilidade; // Sensibilidade do receptor em dBm

    public Antena(double altura, double potenciaTransmissao, double ganho, double sensibilidade) {
        this.altura = altura;
        this.potenciaTransmissao = potenciaTransmissao;
        this.ganho = ganho;
        this.sensibilidade = sensibilidade;
    }

    public double getAltura() {
        return altura;
    }

    public double getPotenciaTransmissao() {
        return potenciaTransmissao;
    }

    public double getGanho() {
        return ganho;
    }

    public double getSensibilidade() {
        return sensibilidade;
    }
}
