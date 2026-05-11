package br.ufg.simulador.core;

public class ProcessadorRelevo 
{
    // Ajusta o relevo natural somando o efeito da curvatura da terra no meio do enlace
    public double[] aplicarCurvaturaTerra(double[] perfilBruto, double distanciaTotalKm) {
        double[] perfilProcessado = new double[perfilBruto.length];
        for (int i = 0; i < perfilBruto.length; i++) {
            // A fórmula de curvatura é: h = (d1 * d2) / (12.74 * K)
            // onde d1 e d2 são as distâncias da torre A e torre B até o ponto i, e K é o fator de curvatura (geralmente 4/3 para rádio)
            double d1 = (distanciaTotalKm / (perfilBruto.length - 1)) * i;
            double d2 = distanciaTotalKm - d1;
            double h = (d1 * d2) / (12.74 * (4.0 / 3.0));
            perfilProcessado[i] = perfilBruto[i] + h;
        }   
        return perfilProcessado;
    }

    // Pega o terreno e posiciona as árvores e prédios por cima dele
    /*public double[] integrarObstaculos(double[] perfilTerreno, List<Obstaculo> obstaculos, Torre torreA, double distanciaTotalKm) {
        if (perfilTerreno == null || obstaculos == null || torreA == null) {
            return perfilTerreno;
        }

        // Clonamos o array para não modificar diretamente os dados puros (boa prática)
        double[] perfilProcessado = perfilTerreno.clone();

        for (Obstaculo obs : obstaculos) {
            // Calcula a distância da Torre A até o obstáculo
            double distAteObstaculo = calcularDistanciaGeodesica(torreA.getLatitude(), torreA.getLongitude(), obs.latitude, obs.longitude);
            
            // Se o obstáculo estiver dentro do percurso do enlace
            if (distAteObstaculo <= distanciaTotalKm) {
                // Encontra em qual índice (amostra do terreno) o obstáculo está localizado
                int index = (int) Math.round((distAteObstaculo / distanciaTotalKm) * (perfilProcessado.length - 1));
                
                // Garante que não fuja dos limites do array e sobrepõe a altitude
                if (index >= 0 && index < perfilProcessado.length) {
                    // Se o topo do obstáculo for mais alto que o terreno natural, atualiza o perfil
                    if (obs.altitudeTerreno > perfilProcessado[index]) {
                        perfilProcessado[index] = obs.altitudeTerreno;
                    }
                }
            }
        }
        return perfilProcessado;
    }*/

    // Método auxiliar para calcular a distância entre duas coordenadas (Fórmula de Haversine)
    public double calcularDistanciaGeodesica(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return 6371.0 * c; // Distância em quilômetros
    }

    // Descobre qual é a maior elevação do trajeto que pode atrapalhar o sinal
    public double encontrarPontoCritico(double[] perfilProcessado) {
        
        if (perfilProcessado == null || perfilProcessado.length == 0) {
            return 0.0;
        }
        
        double maximaElevacao = perfilProcessado[0];
        for (int i = 1; i < perfilProcessado.length; i++) {
            if (perfilProcessado[i] > maximaElevacao) {
                maximaElevacao = perfilProcessado[i];
            }
        }
        return maximaElevacao;
    }

}
