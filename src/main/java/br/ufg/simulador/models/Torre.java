package br.ufg.simulador.models;

public class Torre 
{
    // 1. Atributos de Localização Geográfica 
    private double latitude;
    private double longitude;
    private double altitudeTerreno; // A altitude do chão (MDT) em metros
    
    // 2. Atributos Físicos e Equipamentos 
    private double alturaEstrutura; // A altura da torre de metal em metros
    private Antena antena; // Composição: a torre agora "tem uma" antena
    
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
        // A altitude total considera que a altura da antena é a sua posição de instalação na torre
        double alturaInstalacao = (this.antena != null) ? this.antena.getAltura() : this.alturaEstrutura;
        return this.altitudeTerreno + alturaInstalacao;
    }

    // 5. Getters e Setters (Exemplos para acessar os dados protegidos)
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public double getAltitudeTerreno() {
        return altitudeTerreno;
    }

    public void setAltitudeTerreno(double altitudeTerreno) {
        this.altitudeTerreno = altitudeTerreno;
    }

    public double getAlturaEstrutura() {
        return alturaEstrutura;
    }

    public void setAlturaEstrutura(double alturaEstrutura) {
        if (this.antena != null && this.antena.getAltura() > alturaEstrutura) {
            throw new IllegalArgumentException("A nova altura da torre (" + alturaEstrutura + "m) não pode ser menor que a altura de instalação da antena (" + this.antena.getAltura() + "m).");
        }
        this.alturaEstrutura = alturaEstrutura;
    }

    public Antena getAntena() {
        return antena;
    }

    public void setAntena(Antena antena) {
        if (antena != null && antena.getAltura() > this.alturaEstrutura) {
            throw new IllegalArgumentException("A antena não pode ser instalada a uma altura (" + antena.getAltura() + "m) maior que a própria estrutura da torre (" + this.alturaEstrutura + "m).");
        }
        this.antena = antena;
    }
    
    // (Os demais getters e setters seguiriam o mesmo padrão...)
}
