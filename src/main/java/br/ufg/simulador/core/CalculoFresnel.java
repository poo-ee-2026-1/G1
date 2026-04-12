package br.ufg.simulador.core;

public class CalculoFresnel 
{

    /**
     * Calcula o raio da primeira Zona de Fresnel em um ponto específico do trajeto.
     * 
     * @param d1 Distância da torre A até o ponto (em km)
     * @param d2 Distância do ponto até a torre B (em km)
     * @param frequencia Frequência do enlace (em GHz)
     * @return O raio da primeira zona de Fresnel (em metros)
     */
    public double calcularRaioFresnel(double d1, double d2, double frequencia) {
        double distanciaTotal = d1 + d2;
        if (distanciaTotal <= 0 || frequencia <= 0) {
            return 0.0;
        }
        
        return 17.32 * Math.sqrt((d1 * d2) / (frequencia * distanciaTotal));
    }

    /**
     * Calcula a altitude da reta imaginária do sinal (Linha de Visada / LoS) em um ponto.
     * 
     * @param altAntenaA Altitude total da antena A (chão + torre + instalação) em metros
     * @param altAntenaB Altitude total da antena B (chão + torre + instalação) em metros
     * @param d1 Distância da torre A até o ponto (em km)
     * @param d2 Distância do ponto até a torre B (em km)
     * @return A altitude do sinal de rádio naquele ponto exato (em metros)
     */
    public double calcularAltitudeLinhaVisada(double altAntenaA, double altAntenaB, double d1, double d2) {
        double distanciaTotal = d1 + d2;
        if (distanciaTotal <= 0) return altAntenaA;
        
        // Regra de três (interpolação linear) para saber a altura da reta do sinal no ponto específico
        return altAntenaA + ((altAntenaB - altAntenaA) * (d1 / distanciaTotal));
    }

    /**
     * Verifica se o terreno toca ou invade a Zona de Fresnel.
     * 
     * @param altitudeVisada A altitude da linha de visada no ponto (em metros)
     * @param altitudeTerreno A altitude do terreno no ponto (em metros)
     * @param raioFresnel O raio da zona de Fresnel no ponto (em metros)
     * @param porcentagemLivre A porcentagem do raio que DEVE estar livre (normalmente 0.6, ou seja, 60% para rádio)
     * @return true se o terreno estiver bloqueando o sinal de forma crítica
     */
    public boolean verificarObstrucao(double altitudeVisada, double altitudeTerreno, double raioFresnel, double porcentagemLivre) {
        double folgaAtual = altitudeVisada - altitudeTerreno;
        double folgaExigida = raioFresnel * porcentagemLivre;
        
        // Se temos menos folga do que a física exige, o sinal está obstruído!
        return folgaAtual < folgaExigida;
    }

}
