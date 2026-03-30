package br.ufg.simulador.main;

import br.ufg.simulador.api.ClienteTopografia;
import br.ufg.simulador.core.CalculoFresnel;
import br.ufg.simulador.core.ProcessadorRelevo;
import br.ufg.simulador.models.Enlace;
import br.ufg.simulador.models.Obstaculo;
import br.ufg.simulador.models.Torre;
import br.ufg.simulador.reports.GeradorDeRelatorios;
import br.ufg.simulador.ui.TelaMapa;

public class Main 
{
    public static void main(String[] args) 
    {
        ProcessadorRelevo processador = new ProcessadorRelevo();
        CalculoFresnel calculo = new CalculoFresnel();
        Enlace enlace = new Enlace();
        Obstaculo obstaculo = new Obstaculo();
        Torre torre = new Torre();
        ClienteTopografia cliente = new ClienteTopografia();
        TelaMapa tela = new TelaMapa();
        GeradorDeRelatorios gerador = new GeradorDeRelatorios();
    }
}
