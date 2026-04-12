package br.ufg.simulador.models;

public class Enlace 
{
    private Torre torreA;
    private Torre torreB;
    private double frequencia; // Frequência em GHz
    private double distancia; // Distância em km

    public Enlace(Torre torreA, Torre torreB, double frequencia) {
        if (torreA == null || torreB == null) {
            throw new IllegalArgumentException("Ambas as torres devem ser fornecidas para criar o enlace.");
        }
        if (torreA.getAntena() == null || torreB.getAntena() == null) {
            throw new IllegalArgumentException("As torres precisam ter antenas configuradas (não nulas) para formar um enlace.");
        }
        this.torreA = torreA;
        this.torreB = torreB;
        this.frequencia = frequencia;
        this.distancia = calcularDistancia();
    }

    private double calcularDistancia() {
        // Cálculo simplificado de distância entre dois pontos (Haversine ou Euclidiana)
        double dLat = Math.toRadians(torreB.getLatitude() - torreA.getLatitude());
        double dLon = Math.toRadians(torreB.getLongitude() - torreA.getLongitude());
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(torreA.getLatitude())) * Math.cos(Math.toRadians(torreB.getLatitude())) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return 6371 * c; // Retorna distância em quilômetros
    }

    public Torre getTorreA() {
        return torreA;
    }

    public void setTorreA(Torre torreA) {
        this.torreA = torreA;
    }

    public Torre getTorreB() {
        return torreB;
    }

    public void setTorreB(Torre torreB) {
        this.torreB = torreB;
    }

    public double getFrequencia() {
        return frequencia;
    }

    public void setFrequencia(double frequencia) {
        this.frequencia = frequencia;
    }

    public double getDistancia() {
        return distancia;
    }

    public void setDistancia(double distancia) {
        this.distancia = distancia;
    }
}
