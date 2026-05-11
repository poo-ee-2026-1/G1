package br.ufg.simulador.models;

public class Torre 
{
    // 1. Atributos de Localização Geográfica 
    private double latitude;
    private double longitude;
    private double altitudeTerreno; // A altitude do chão (MDT) em metros
    
    // 2. Atributos Físicos e Equipamentos 
    private double alturaEstrutura; // A altura da torre de metal em metros
    private java.util.List<Antena> antenas = new java.util.ArrayList<>();
    private int antenaSelecionadaIndex = 0; // Qual antena será usada no enlace principal
    
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
        Antena antenaUsada = getAntena();
        double alturaInstalacao = (antenaUsada != null) ? antenaUsada.getAltura() : this.alturaEstrutura;
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
        for (Antena a : antenas) {
            if (a.getAltura() > alturaEstrutura) {
                throw new IllegalArgumentException("A nova altura da torre (" + alturaEstrutura + "m) não pode ser menor que a altura de instalação de uma das antenas (" + a.getAltura() + "m).");
            }
        }
        this.alturaEstrutura = alturaEstrutura;
    }

    public Antena getAntena() {
        if (antenas.isEmpty()) return null;
        if (antenaSelecionadaIndex >= 0 && antenaSelecionadaIndex < antenas.size()) {
            return antenas.get(antenaSelecionadaIndex);
        }
        return antenas.get(0);
    }

    public java.util.List<Antena> getAntenas() {
        return antenas;
    }

    public void setAntenaSelecionadaIndex(int index) {
        this.antenaSelecionadaIndex = index;
    }

    public void adicionarAntena(Antena antena) {
        if (antena != null && antena.getAltura() > this.alturaEstrutura) {
            throw new IllegalArgumentException("A antena não pode ser instalada a uma altura (" + antena.getAltura() + "m) maior que a própria estrutura da torre (" + this.alturaEstrutura + "m).");
        }
        this.antenas.add(antena);
    }
    
    // (Os demais getters e setters seguiriam o mesmo padrão...)
}
