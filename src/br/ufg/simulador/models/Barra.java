package br.ufg.simulador.models;

public class Barra 
{
    private int id;
    private String nome;
    private double tensao;
    private double angulo;

    public Barra(int id, String nome, double tensao, double angulo) {
        this.id = id;
        this.nome = nome;
        this.tensao = tensao;
        this.angulo = angulo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getTensao() {
        return tensao;
    }

    public void setTensao(double tensao) {
        this.tensao = tensao;
    }

    public double getAngulo() {
        return angulo;
    }

    public void setAngulo(double angulo) {
        this.angulo = angulo;
    }

}
