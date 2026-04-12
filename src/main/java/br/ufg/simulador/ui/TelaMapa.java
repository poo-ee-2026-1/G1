package br.ufg.simulador.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.MapPolygonImpl;

public class TelaMapa extends JFrame {
    
    private JMapViewer map;
    private int contadorTorres = 0;
    private Coordinate coordA;
    private Coordinate coordB;
    private JButton btnLimpar;
    private JButton btnSimular;
    private List<br.ufg.simulador.main.Main.DadosObstaculo> listaObstaculos = new ArrayList<>();

    public TelaMapa() {
        // Configurações básicas da Janela
        super("Simulador de Rádio-Visibilidade - Seleção de Torres");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centraliza a janela na tela do computador
        setLayout(new BorderLayout());

        // Instancia o visualizador de mapas
        map = new JMapViewer();
        
        // Centralizando o mapa no Brasil (Ex: Goiás, zoom nível 10)
        map.setDisplayPosition(new Coordinate(-16.68, -49.25), 10);

        // Adiciona um "Ouvinte de Mouse" para capturar os cliques no mapa
        map.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Verifica se foi um clique com o botão esquerdo
                if (e.getButton() == MouseEvent.BUTTON1) {
                    
                    // MÁGICA: Converte o pixel clicado na tela (X, Y) para Latitude e Longitude
                    Coordinate coord = (Coordinate) map.getPosition(e.getPoint());
                    
                    if (contadorTorres < 2) {
                        contadorTorres++;
                        String nomeTorre = (contadorTorres == 1) ? "Torre A" : "Torre B";
                        
                        if (contadorTorres == 1) coordA = coord;
                        else coordB = coord;
                        
                        System.out.printf("%s registrada! Latitude: %.6f | Longitude: %.6f\n", 
                            nomeTorre, coord.getLat(), coord.getLon());
                            
                        // Cria um marcador visual no mapa
                        MapMarkerDot marker = new MapMarkerDot(nomeTorre, coord);
                        marker.setBackColor((contadorTorres == 1) ? Color.BLUE : Color.GREEN);
                        map.addMapMarker(marker);

                        if (contadorTorres == 2) {
                            // Desenha uma linha vermelha conectando as duas torres
                            MapPolygonImpl linha = new MapPolygonImpl(Arrays.asList(coordA, coordB, coordA));
                            linha.setColor(Color.RED);
                            map.addMapPolygon(linha);

                            btnSimular.setEnabled(true);
                            JOptionPane.showMessageDialog(null, "Ambas as torres foram selecionadas!\n\nAgora você pode:\n1. Clicar no botão 'Simular' para iniciar o cálculo.");
                        }
                    } else {
                        /* FUNCIONALIDADE DE OBSTÁCULOS OCULTADA TEMPORARIAMENTE
                        // Lógica para adicionar obstáculos dinamicamente
                        String nome = JOptionPane.showInputDialog("Digite o nome do obstáculo (Ex: Prédio A):");
                        if (nome != null && !nome.trim().isEmpty()) {
                            String alturaStr = JOptionPane.showInputDialog("Digite a altura do obstáculo em metros (Ex: 15):");
                            if (alturaStr != null && !alturaStr.trim().isEmpty()) {
                                try {
                                    double altura = Double.parseDouble(alturaStr.replace(",", "."));
                                    listaObstaculos.add(new br.ufg.simulador.main.Main.DadosObstaculo(nome, coord.getLat(), coord.getLon(), altura));
                                    
                                    MapMarkerDot marker = new MapMarkerDot(nome, coord);
                                    marker.setBackColor(Color.MAGENTA);
                                    map.addMapMarker(marker);
                                } catch (NumberFormatException ex) {
                                    JOptionPane.showMessageDialog(null, "Altura inválida! O obstáculo não foi adicionado.");
                                }
                            }
                        }
                        */
                    }
                }
            }
        });

        // Adicionando um painel de instrução no topo da tela
        JLabel instrucao = new JLabel("Clique no mapa para posicionar a Torre A e a Torre B", SwingConstants.CENTER);
        instrucao.setFont(new Font("Arial", Font.BOLD, 14));
        instrucao.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(instrucao, BorderLayout.NORTH);
        add(map, BorderLayout.CENTER);

        // Criando e configurando o botão "Limpar Mapa"
        btnLimpar = new JButton("Limpar Mapa");
        btnLimpar.setFont(new Font("Arial", Font.BOLD, 14));
        btnLimpar.addActionListener(e -> {
            contadorTorres = 0;
            listaObstaculos.clear();
            map.removeAllMapMarkers(); // Remove os pontos vermelhos
            map.removeAllMapPolygons(); // Remove a linha
            btnSimular.setEnabled(false);
        });

        // Criando e configurando o botão "Simular Enlace"
        btnSimular = new JButton("Simular Enlace");
        btnSimular.setFont(new Font("Arial", Font.BOLD, 14));
        btnSimular.setEnabled(false);
        btnSimular.addActionListener(e -> {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            btnLimpar.setEnabled(false);
            btnSimular.setEnabled(false);

            new Thread(() -> {
                br.ufg.simulador.main.Main.Resultado resultadoSim = br.ufg.simulador.main.Main.executarSimulacao(coordA.getLat(), coordA.getLon(), coordB.getLat(), coordB.getLon(), listaObstaculos);
                
                SwingUtilities.invokeLater(() -> {
                    setCursor(Cursor.getDefaultCursor());
                    btnLimpar.setEnabled(true);
                    btnSimular.setEnabled(true);

                    String textoStatus = resultadoSim.viavel ? "VIÁVEL! A primeira Zona de Fresnel possui margem de folga adequada." : "INVIÁVEL! Foram detectadas obstruções críticas no trajeto.";
                    String mensagemFinal = String.format("Simulação finalizada!\n\nDistância Total: %.2f km\nStatus do Enlace: %s", resultadoSim.distancia, textoStatus);
                    int tipoIcone = resultadoSim.viavel ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE;
                    String tituloJanela = resultadoSim.viavel ? "Resultado: Enlace Viável" : "Resultado: Enlace Inviável";
                    JOptionPane.showMessageDialog(null, mensagemFinal, tituloJanela, tipoIcone);
                    
                    new TelaGrafico(resultadoSim).setVisible(true);
                });
            }).start();
        });

        JPanel painelInferior = new JPanel();
        painelInferior.add(btnLimpar);
        painelInferior.add(btnSimular);
        add(painelInferior, BorderLayout.SOUTH);
        
        // Exibe a tela
        setVisible(true);
    }
}