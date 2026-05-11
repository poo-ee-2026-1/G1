package br.ufg.simulador.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import br.ufg.simulador.main.Main.Resultado;

public class TelaGrafico extends JFrame {

    public TelaGrafico(Resultado resultado) {
        super("Perfil de Elevação e Zona de Fresnel");
        setSize(1000, 450); // Aumentado para acomodar a legenda ao lado
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Fecha só o gráfico, não o programa todo
        setLocationRelativeTo(null);
        
        add(new PainelGrafico(resultado));
    }

    private class PainelGrafico extends JPanel {
        private final Resultado res;
        private boolean piscarAlerta = false;
        private int mouseX = -1;
        private int mouseY = -1;

        public PainelGrafico(Resultado res) {
            this.res = res;
            setBackground(Color.WHITE);
            
            // Se o enlace for inviável, cria um timer para fazer as torres piscarem
            if (!res.viavel) {
                Timer timer = new Timer(500, e -> {
                    piscarAlerta = !piscarAlerta;
                    repaint(); // Força o painel a se redesenhar com a nova cor
                });
                timer.start();
            }

            // Adiciona rastreio do mouse para o Tooltip interativo
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    mouseX = e.getX();
                    mouseY = e.getY();
                    repaint(); // Redesenha o gráfico para mostrar o tooltip na nova posição
                }
            });
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseExited(MouseEvent e) {
                    mouseX = -1;
                    mouseY = -1;
                    repaint(); // Esconde o tooltip quando o mouse sai da janela
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            
            // Suaviza as linhas do gráfico (Anti-aliasing)
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int padding = 50;
            int espacoLegenda = 210; // Espaço reservado à direita para a legenda
            int width = getWidth() - padding - espacoLegenda; // O gráfico não vai mais até o final da tela
            int height = getHeight() - (2 * padding);

            // Encontra as elevações mínima e máxima para dimensionar o gráfico
            double minY = Double.MAX_VALUE;
            double maxY = Double.MIN_VALUE;
            for (int i = 0; i < res.elevacaoTerreno.length; i++) {
                minY = Math.min(minY, res.elevacaoTerreno[i]);
                minY = Math.min(minY, res.fresnelInferior[i]);
                maxY = Math.max(maxY, res.linhaVisada[i]);
                maxY = Math.max(maxY, res.elevacaoTerreno[i]);
            }

            // Ajuste para não colar a linha no teto/chão da janela
            minY -= 10; 
            maxY += 20;

            double scaleX = width / res.distancia;
            double scaleY = height / (maxY - minY);

            // --- DESENHAR GRADE DE FUNDO ---
            g2d.setColor(new Color(220, 220, 220)); // Cinza claro para não poluir visualmente
            Stroke pontilhado = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{5f}, 0.0f);
            g2d.setStroke(pontilhado);

            // Grade e Rótulos do Eixo X (Distância)
            int divisoesX = 10;
            for (int i = 0; i <= divisoesX; i++) {
                int x = padding + (i * width / divisoesX);
                g2d.drawLine(x, padding, x, getHeight() - padding); // Linha vertical
                
                g2d.setColor(Color.DARK_GRAY);
                double valorDist = (res.distancia / divisoesX) * i;
                g2d.drawString(String.format("%.1f", valorDist), x - 10, getHeight() - padding + 15);
                g2d.setColor(new Color(220, 220, 220)); // Restaura a cor da grade
            }

            // Grade e Rótulos do Eixo Y (Altitude)
            int divisoesY = 5;
            for (int i = 0; i <= divisoesY; i++) {
                int y = getHeight() - padding - (i * height / divisoesY);
                g2d.drawLine(padding, y, padding + width, y); // Linha horizontal
                
                g2d.setColor(Color.DARK_GRAY);
                double valorAlt = minY + ((maxY - minY) / divisoesY) * i;
                g2d.drawString(String.format("%.0f", valorAlt), padding - 40, y + 5);
                g2d.setColor(new Color(220, 220, 220)); // Restaura a cor da grade
            }

            // --- DESENHAR TERRENO ---
            Polygon polyTerreno = new Polygon();
            polyTerreno.addPoint(padding, getHeight() - padding); // Canto inferior esquerdo
            for (int i = 0; i < res.elevacaoTerreno.length; i++) {
                int x = padding + (int) (res.distanciasPonto[i] * scaleX);
                int y = getHeight() - padding - (int) ((res.elevacaoTerreno[i] - minY) * scaleY);
                polyTerreno.addPoint(x, y);
            }
            polyTerreno.addPoint(padding + width, getHeight() - padding); // Canto inferior direito
            
            g2d.setColor(new Color(139, 69, 19, 150)); // Marrom com transparência
            g2d.fillPolygon(polyTerreno);
            
            // --- DESENHAR FAIXA DE FRESNEL (40% NÃO CRÍTICO) ---
            Polygon polyFresnel40 = new Polygon();
            
            // Borda superior da faixa (Limite de 60% da Zona de Fresnel) - Esquerda para Direita
            for (int i = 0; i < res.elevacaoTerreno.length; i++) {
                double raio = res.linhaVisada[i] - res.fresnelInferior[i];
                double limite60 = res.linhaVisada[i] - (raio * 0.6);
                int x = padding + (int) (res.distanciasPonto[i] * scaleX);
                int y = getHeight() - padding - (int) ((limite60 - minY) * scaleY);
                polyFresnel40.addPoint(x, y);
            }
            
            // Borda inferior da faixa (Limite de 100% da Zona de Fresnel) - Direita para Esquerda
            for (int i = res.elevacaoTerreno.length - 1; i >= 0; i--) {
                int x = padding + (int) (res.distanciasPonto[i] * scaleX);
                int y = getHeight() - padding - (int) ((res.fresnelInferior[i] - minY) * scaleY);
                polyFresnel40.addPoint(x, y);
            }
            
            g2d.setColor(new Color(0, 200, 0, 50)); // Verde translúcido para a margem segura
            g2d.fillPolygon(polyFresnel40);

            // --- DESENHAR ZONA DE FRESNEL INFERIOR (Limite de Obstrução) ---
            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{5}, 0)); // Linha tracejada
            for (int i = 1; i < res.fresnelInferior.length; i++) {
                int x1 = padding + (int) (res.distanciasPonto[i - 1] * scaleX);
                int y1 = getHeight() - padding - (int) ((res.fresnelInferior[i - 1] - minY) * scaleY);
                int x2 = padding + (int) (res.distanciasPonto[i] * scaleX);
                int y2 = getHeight() - padding - (int) ((res.fresnelInferior[i] - minY) * scaleY);
                g2d.drawLine(x1, y1, x2, y2);
            }

            // --- DESENHAR LINHA DE VISADA (LoS) ---
            g2d.setColor(res.viavel ? new Color(0, 180, 0) : Color.RED);
            g2d.setStroke(new BasicStroke(2.0f)); // Linha sólida
            int startX = padding;
            int startY = getHeight() - padding - (int) ((res.linhaVisada[0] - minY) * scaleY);
            int endX = padding + width;
            int endY = getHeight() - padding - (int) ((res.linhaVisada[res.linhaVisada.length - 1] - minY) * scaleY);
            g2d.drawLine(startX, startY, endX, endY);

            // --- DESENHAR OBSTÁCULOS ---
            /* FUNCIONALIDADE DE OBSTÁCULOS OCULTADA TEMPORARIAMENTE
            for (ObstaculoInfo obs : res.obstaculos) {
                int obsX = padding + (int) (obs.distancia * scaleX);
                int obsY = getHeight() - padding - (int) ((obs.altitude - minY) * scaleY);
                
                // Desenha um losango magenta para destacar o obstáculo
                g2d.setColor(Color.MAGENTA);
                Polygon polyObs = new Polygon();
                polyObs.addPoint(obsX, obsY - 8);
                polyObs.addPoint(obsX - 6, obsY);
                polyObs.addPoint(obsX, obsY + 8);
                polyObs.addPoint(obsX + 6, obsY);
                g2d.fillPolygon(polyObs);
                
                // Escreve o nome do obstáculo logo acima do marcador
                g2d.setColor(Color.BLACK);
                g2d.drawString(obs.nome, obsX - 20, obsY - 12);
            }
            */

            // --- DESENHAR TORRES ---
            Color corTorre = res.viavel ? Color.DARK_GRAY : (piscarAlerta ? Color.RED : Color.DARK_GRAY);
            g2d.setColor(corTorre);
            g2d.setStroke(new BasicStroke(4.0f)); // Linha mais grossa para a estrutura da torre

            // Torre A (Início)
            int yChaoA = getHeight() - padding - (int) ((res.elevacaoTerreno[0] - minY) * scaleY);
            int yTopoTorreA = getHeight() - padding - (int) ((res.elevacaoTerreno[0] + res.alturaTorreA - minY) * scaleY);
            g2d.drawLine(startX, yChaoA, startX, yTopoTorreA); // Linha do chão até o topo da torre
            
            // Desenha todas as antenas da Torre A
            for (int i = 0; i < res.alturasAntenasA.length; i++) {
                int yAntena = getHeight() - padding - (int) ((res.elevacaoTerreno[0] + res.alturasAntenasA[i] - minY) * scaleY);
                if (i == res.antenaPrincipalAIndex) {
                    g2d.setColor(Color.BLUE);
                    g2d.fillOval(startX - 6, yAntena - 6, 12, 12); // Antena Principal (Maior)
                } else {
                    g2d.setColor(Color.ORANGE);
                    g2d.fillOval(startX - 4, yAntena - 4, 8, 8); // Secundárias (Menores)
                }
            }
            g2d.setColor(Color.BLACK);
            g2d.drawString(String.format("Torre A (%.0fm)", res.alturaTorreA), startX - 20, yTopoTorreA - 15); // Rótulo

            // Torre B (Fim)
            int lastIndex = res.elevacaoTerreno.length - 1;
            int yChaoB = getHeight() - padding - (int) ((res.elevacaoTerreno[lastIndex] - minY) * scaleY);
            int yTopoTorreB = getHeight() - padding - (int) ((res.elevacaoTerreno[lastIndex] + res.alturaTorreB - minY) * scaleY);
            
            g2d.setColor(corTorre);
            g2d.drawLine(endX, yChaoB, endX, yTopoTorreB); 
            
            // Desenha todas as antenas da Torre B
            for (int i = 0; i < res.alturasAntenasB.length; i++) {
                int yAntena = getHeight() - padding - (int) ((res.elevacaoTerreno[lastIndex] + res.alturasAntenasB[i] - minY) * scaleY);
                if (i == res.antenaPrincipalBIndex) {
                    g2d.setColor(Color.BLUE);
                    g2d.fillOval(endX - 6, yAntena - 6, 12, 12);
                } else {
                    g2d.setColor(Color.ORANGE);
                    g2d.fillOval(endX - 4, yAntena - 4, 8, 8);
                }
            }
            g2d.setColor(Color.BLACK);
            g2d.drawString(String.format("Torre B (%.0fm)", res.alturaTorreB), endX - 20, yTopoTorreB - 15); // Rótulo

            // --- DESENHAR EIXOS ---
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(1.0f));
            g2d.drawLine(padding, padding, padding, getHeight() - padding); // Eixo Y
            g2d.drawLine(padding, getHeight() - padding, padding + width, getHeight() - padding); // Eixo X
            g2d.drawString("Distância (km)", padding + (width / 2) - 40, getHeight() - 15);
            g2d.drawString("Altitude (m)", 10, 30);

            // --- DESENHAR INFO DE SINAL DO ENLACE (BALANÇO) ---
            int infoX = padding + 20;
            int infoY = padding + 20;
            g2d.setColor(new Color(255, 255, 255, 220)); // Fundo Branco Translúcido
            g2d.fillRect(infoX, infoY, 260, 50);
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(1.0f));
            g2d.drawRect(infoX, infoY, 260, 50);
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString(String.format("Atenuação do Ar (FSPL): %.2f dB", res.perdaEspacoLivre), infoX + 10, infoY + 20);
            g2d.drawString(String.format("Sinal Recebido Estimado (Rx): %.2f dBm", res.potenciaRecepcao), infoX + 10, infoY + 40);

            // --- DESENHAR LEGENDA ---
            int legendaWidth = 180;
            int legendaHeight = 155; // Ajustado para incluir as antenas secundárias
            int legendaX = padding + width + 15; // Posiciona fora do gráfico, à direita
            int legendaY = padding; // Alinha com o topo do eixo Y

            // Fundo da legenda (Branco com leve transparência)
            g2d.setColor(new Color(255, 255, 255, 220)); 
            g2d.fillRect(legendaX, legendaY, legendaWidth, legendaHeight);
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(1.0f));
            g2d.drawRect(legendaX, legendaY, legendaWidth, legendaHeight);

            int textX = legendaX + 35;
            int lineX1 = legendaX + 10;
            int lineX2 = legendaX + 25;
            int currentY = legendaY + 20;

            // Item 1: Terreno
            g2d.setColor(new Color(139, 69, 19, 150));
            g2d.fillRect(lineX1, currentY - 10, 15, 10);
            g2d.setColor(Color.BLACK);
            g2d.drawString("Terreno", textX, currentY);
            currentY += 20;

            // Item 2: Linha de Visada (LoS)
            g2d.setColor(res.viavel ? new Color(0, 180, 0) : Color.RED);
            g2d.setStroke(new BasicStroke(2.0f));
            g2d.drawLine(lineX1, currentY - 4, lineX2, currentY - 4);
            g2d.setColor(Color.BLACK);
            g2d.drawString("Linha de Visada", textX, currentY);
            currentY += 20;

            // Item 3: Zona de Fresnel
            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{5}, 0));
            g2d.drawLine(lineX1, currentY - 4, lineX2, currentY - 4);
            g2d.setColor(Color.BLACK);
            g2d.drawString("Zona de Fresnel", textX, currentY);
            currentY += 20;

            // Item 4: Margem 40% (Segura)
            g2d.setColor(new Color(0, 200, 0, 80)); // Verde translúcido ligeiramente mais opaco
            g2d.fillRect(lineX1, currentY - 10, 15, 10);
            g2d.setColor(Color.BLACK);
            g2d.drawString("Margem Segura (40%)", textX, currentY);
            currentY += 20;

            /* FUNCIONALIDADE DE OBSTÁCULOS OCULTADA TEMPORARIAMENTE
            // Item 5: Obstáculo
            g2d.setColor(Color.MAGENTA);
            Polygon polyLegenda = new Polygon();
            polyLegenda.addPoint(lineX1 + 7, currentY - 10);
            polyLegenda.addPoint(lineX1 + 3, currentY - 4);
            polyLegenda.addPoint(lineX1 + 7, currentY + 2);
            polyLegenda.addPoint(lineX1 + 11, currentY - 4);
            g2d.fillPolygon(polyLegenda);
            g2d.setColor(Color.BLACK);
            g2d.drawString("Obstáculo", textX, currentY);
            currentY += 20;
            */

            // Item 5: Torre (Numeração da documentação original corrigida)
            g2d.setColor(Color.DARK_GRAY);
            g2d.setStroke(new BasicStroke(4.0f));
            g2d.drawLine(lineX1 + 7, currentY - 12, lineX1 + 7, currentY);
            g2d.setColor(Color.BLACK);
            g2d.drawString("Torre", textX, currentY);
            currentY += 20;

            // Item 6: Antena Principal
            g2d.setColor(Color.BLUE);
            g2d.fillOval(lineX1 + 1, currentY - 10, 12, 12);
            g2d.setColor(Color.BLACK);
            g2d.drawString("Antena Principal", textX, currentY);
            currentY += 20;

            // Item 7: Outras Antenas
            g2d.setColor(Color.ORANGE);
            g2d.fillOval(lineX1 + 3, currentY - 8, 8, 8);
            g2d.setColor(Color.BLACK);
            g2d.drawString("Outras Antenas", textX, currentY);

            // --- DESENHAR TOOLTIP INTERATIVO (Mouse Crosshair) ---
            if (mouseX >= padding && mouseX <= padding + width && mouseY >= padding && mouseY <= getHeight() - padding) {
                
                // Linhas guias cruzadas (Crosshair)
                g2d.setColor(new Color(100, 100, 100, 150));
                g2d.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 5.0f, new float[]{5f}, 0.0f));
                g2d.drawLine(mouseX, padding, mouseX, getHeight() - padding); // Linha vertical
                g2d.drawLine(padding, mouseY, padding + width, mouseY); // Linha horizontal
                
                // Inverte a matemática da escala para achar os valores reais de distância e altitude no ponto
                double mouseDist = (mouseX - padding) / scaleX;
                double mouseAlt = minY + ((getHeight() - padding - mouseY) / scaleY);
                
                String tooltipText = String.format("Dist: %.2f km | Alt: %.1f m", mouseDist, mouseAlt);
                
                // Configurações da caixa do Tooltip
                g2d.setFont(new Font("Arial", Font.BOLD, 12));
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(tooltipText);
                
                int boxX = mouseX + 15;
                int boxY = mouseY - 25;
                
                // Impede que a caixa saia cortada pela direita ou pelo teto do gráfico
                if (boxX + textWidth + 10 > getWidth()) boxX = mouseX - textWidth - 15;
                if (boxY < 0) boxY = mouseY + 15;
                
                // Desenha a caixa flutuante (Amarelo claro)
                g2d.setColor(new Color(255, 255, 220, 240)); 
                g2d.fillRect(boxX, boxY, textWidth + 10, 20);
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(1.0f));
                g2d.drawRect(boxX, boxY, textWidth + 10, 20);
                
                g2d.drawString(tooltipText, boxX + 5, boxY + 14);
            }
        }
    }
}