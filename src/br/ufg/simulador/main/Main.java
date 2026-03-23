package br.ufg.simulador.main;

import br.ufg.simulador.core.FluxoDeCarga;
import br.ufg.simulador.core.Sistema;
import br.ufg.simulador.models.Barra;
import br.ufg.simulador.models.LinhaDeTransmissao;

public class Main 
{
    public static void main(String[] args) 
    {
        // Ponto de entrada do seu programa
        System.out.println("Hello World!");

        // Criação do sistema
        Sistema sistema = new Sistema();

        // Simulação do sistema
        sistema.simular();

        // Exemplo de criação de um fluxo de carga
        FluxoDeCarga fluxo = new FluxoDeCarga(50.0); // Valor do fluxo de carga
        System.out.println("Valor do fluxo de carga: " + fluxo.getValor());

        // Exemplo de criação de uma barra
        Barra barra1 = new Barra(1, "Barra 1", 1.0, 0.0);
        System.out.println("Nome da barra: " + barra1.getNome());

        // Exemplo de criação de uma linha de transmissão
        LinhaDeTransmissao linha = new LinhaDeTransmissao(1, barra1, barra1, 0.01, 0.05, 0.001, 100.0);
        System.out.println("ID da linha de transmissão: " + linha.getId());
        System.out.println("Resistência da linha: " + linha.getResistencia());
        System.out.println("Reatância da linha: " + linha.getReatancia());
        System.out.println("Susceptância shunt da linha: " + linha.getSusceptanciaShunt());
        System.out.println("Capacidade máxima da linha: " + linha.getCapacidadeMaxima());
        System.out.println("Barra de origem da linha: " + linha.getBarraOrigem().getNome());
        System.out.println("Barra de destino da linha: " + linha.getBarraDestino().getNome());
        

    }
}
