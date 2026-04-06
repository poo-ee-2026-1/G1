package br.ufg.simulador.models;

public class Torre 
{
    // 1. Atributos de Localização Geográfica 
    private double latitude;
    private double longitude;
    private double altitudeTerreno; // A altitude do chão (MDT) em metros
    
    // 2. Atributos Físicos e Equipamentos 
    private double alturaEstrutura; // A altura da torre de metal em metros
    private double potenciaEquipamento; // Potência de transmissão
    private int quantidadeAntenas; // Para registrar as antenas presentes no enlace [cite: 9]
    
    // 3. Método Construtor
    // É chamado no momento em que criamos uma "nova Torre()" no sistema
    public Torre(double latitude, double longitude, double altitudeTerreno, double alturaEstrutura) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitudeTerreno = altitudeTerreno;
        this.alturaEstrutura = alturaEstrutura;
    }

    // 4. Métodos de Ação (Comportamento)
    // Calcula a altura total da ponta da antena em relação ao nível do mar
    public double getAltitudeTotalAntena() {
        return this.altitudeTerreno + this.alturaEstrutura;
    }

    // 5. Getters e Setters (Exemplos para acessar os dados protegidos)
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getAlturaEstrutura() {
        return alturaEstrutura;
    }

    public void setAlturaEstrutura(double alturaEstrutura) {
        this.alturaEstrutura = alturaEstrutura;
    }

    public int getQuantidadeAntenas() {
        return quantidadeAntenas;
    }

    public void setQuantidadeAntenas(int quantidadeAntenas) {
        this.quantidadeAntenas = quantidadeAntenas; // Permite registrar as antenas [cite: 9]
    }
    
    // (Os demais getters e setters seguiriam o mesmo padrão...)
}
