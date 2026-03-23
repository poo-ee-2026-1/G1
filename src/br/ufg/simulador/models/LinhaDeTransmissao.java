package br.ufg.simulador.models;

public class LinhaDeTransmissao 
{
    
    // 1. Identificação e Topologia (Conexões)
    private int id;
    private Barra barraOrigem;
    private Barra barraDestino;
    
    // 2. Parâmetros Elétricos (Geralmente fornecidos em p.u.)
    private double resistencia;       // R (p.u.)
    private double reatancia;         // X (p.u.)
    private double susceptanciaShunt; // B ou B/2 (p.u.) - Efeito capacitivo
    
    // Limite operacional para a análise de contingência
    private double capacidadeMaxima;  // Limite térmico da linha (MVA ou Ampères)

    // 3. Construtor
    public LinhaDeTransmissao(int id, Barra origem, Barra destino, double r, double x, double b, double capacidade) {
        this.id = id;
        this.barraOrigem = origem;
        this.barraDestino = destino;
        this.resistencia = r;
        this.reatancia = x;
        this.susceptanciaShunt = b;
        this.capacidadeMaxima = capacidade;
    }
    
    

    // Método auxiliar importante para a construção da Matriz Ybus
    public void setSusceptanciaShunt(double susceptanciaShunt) {
        this.susceptanciaShunt = susceptanciaShunt;
    }

    public double getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    public void setCapacidadeMaxima(double capacidadeMaxima) {
        this.capacidadeMaxima = capacidadeMaxima;
    }


    // A admitância série (y) é o inverso da impedância (z = r + jx)
    public double getAdmitanciaSerie() {
        double zReal = resistencia;
        double zImag = reatancia;
        double zMagnitudeSquared = zReal * zReal + zImag * zImag;
        return Math.sqrt(zMagnitudeSquared) != 0 ? 1.0 / Math.sqrt(zMagnitudeSquared) : 0.0; // Evitar divisão por zero
    }

    // Como envolve números complexos, você precisará tratar a parte real (Condutância G) 
    

    // e a imaginária (Susceptância B) separadamente nos cálculos da classe Sistema.
    
    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Barra getBarraOrigem() {
        return barraOrigem;
    }

    public void setBarraOrigem(Barra barraOrigem) {
        this.barraOrigem = barraOrigem;
    }

    public Barra getBarraDestino() {
        return barraDestino;
    }

    public void setBarraDestino(Barra barraDestino) {
        this.barraDestino = barraDestino;
    }

    public double getResistencia() {
        return resistencia;
    }

    public void setResistencia(double resistencia) {
        this.resistencia = resistencia;
    }

    public double getReatancia() {
        return reatancia;
    }

    public void setReatancia(double reatancia) {
        this.reatancia = reatancia;
    }

    public double getSusceptanciaShunt() {
        return susceptanciaShunt;
    }
}