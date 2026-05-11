package br.ufg.simulador.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.SwingUtilities;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.MapPolygonImpl;
import org.openstreetmap.gui.jmapviewer.OsmMercator;

import br.ufg.simulador.models.Antena;

public class TelaMapa extends JFrame {
    
    private JMapViewer map;
    private int contadorTorres = 0;
    private Coordinate coordA;
    private Coordinate coordB;
    private JButton btnLimpar;
    private JButton btnSimular;
    private List<br.ufg.simulador.main.Main.DadosObstaculo> listaObstaculos = new ArrayList<>();
    private br.ufg.simulador.main.Main.DadosTorre configA = new br.ufg.simulador.main.Main.DadosTorre();
    private br.ufg.simulador.main.Main.DadosTorre configB = new br.ufg.simulador.main.Main.DadosTorre();
    private JPanel painelLateral;
    private JButton btnToggleMenu;
    private JButton btnDesfazer;
    private JLabel lblInstrucao;
    private JTextField txtFrequencia;

    public TelaMapa() {
        // Configurações básicas da Janela
        super("Simulador de Rádio-Visibilidade - Seleção de Torres");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centraliza a janela na tela do computador
        setLayout(new BorderLayout());
        
        // Configurações padrão iniciais ao abrir o mapa
        configA.alturaEstrutura = 30.0;
        configA.antenas.add(new Antena(30.0, 30.0, 15.0, -90.0));
        configB.alturaEstrutura = 45.0;
        configB.antenas.add(new Antena(45.0, 30.0, 15.0, -90.0));

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
                        
                        // Atualiza as configurações de mapa
                        if (contadorTorres == 1) { configA.latitude = coord.getLat(); configA.longitude = coord.getLon(); }
                        else { configB.latitude = coord.getLat(); configB.longitude = coord.getLon(); }
                        
                        System.out.printf("%s registrada! Latitude: %.6f | Longitude: %.6f\n", 
                            nomeTorre, coord.getLat(), coord.getLon());
                            
                        // Cria um marcador visual no mapa
                        MapMarkerDot marker = new MapMarkerDot(nomeTorre, coord);
                        marker.setBackColor((contadorTorres == 1) ? Color.BLUE : Color.GREEN);
                        map.addMapMarker(marker);

                        // Habilita o botão de abrir o menu assim que a 1ª torre for colocada no mapa
                        btnToggleMenu.setEnabled(true);

                        btnDesfazer.setEnabled(true); // Habilita o botão desfazer

                        lblInstrucao.setText("Mova o mouse para posicionar a Torre B...");

                        if (contadorTorres == 2) {
                            // Desenha uma linha vermelha conectando as duas torres
                            MapPolygonImpl linha = new MapPolygonImpl(Arrays.asList(coordA, coordB, coordA));
                            linha.setColor(Color.RED);
                            map.addMapPolygon(linha);

                            // Centraliza a visão do mapa com animação suave
                            animarMapaParaEnquadrar(coordA, coordB);

                            btnSimular.setEnabled(true);
                            lblInstrucao.setText(String.format("Torres posicionadas! Distância: %.2f km", calcularDistancia(coordA, coordB)));
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

        // Captura o movimento do mouse para exibir a distância em tempo real
        map.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (contadorTorres == 1 && coordA != null) {
                    Coordinate coordAtual = (Coordinate) map.getPosition(e.getPoint());
                    if (coordAtual != null) {
                        double dist = calcularDistancia(coordA, coordAtual);
                        lblInstrucao.setText(String.format("Posicione a Torre B. Distância atual: %.2f km", dist));
                    }
                }
            }
        });

        // Criando painel de instrução dinâmico no topo da tela
        lblInstrucao = new JLabel("Clique no mapa para posicionar a Torre A e a Torre B", SwingConstants.CENTER);
        lblInstrucao.setFont(new Font("Arial", Font.BOLD, 14));
        lblInstrucao.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(lblInstrucao, BorderLayout.NORTH);
        add(map, BorderLayout.CENTER);

        painelLateral = criarPainelLateral();
        painelLateral.setVisible(false); // Mantém o painel oculto por padrão
        add(painelLateral, BorderLayout.EAST);

        // Criando e configurando o botão "Limpar Mapa"
        btnLimpar = new JButton("Limpar Mapa");
        btnLimpar.setFont(new Font("Arial", Font.BOLD, 14));
        btnLimpar.addActionListener(e -> {
            contadorTorres = 0;
            listaObstaculos.clear();
            map.removeAllMapMarkers(); // Remove os pontos vermelhos
            map.removeAllMapPolygons(); // Remove a linha
            btnSimular.setEnabled(false);
            btnDesfazer.setEnabled(false);
            btnToggleMenu.setEnabled(false);
            btnToggleMenu.setText("Configurar Antenas >>");
            painelLateral.setVisible(false);
            lblInstrucao.setText("Clique no mapa para posicionar a Torre A e a Torre B");
        });

        // Criando e configurando o botão "Desfazer"
        btnDesfazer = new JButton("Desfazer");
        btnDesfazer.setFont(new Font("Arial", Font.BOLD, 14));
        btnDesfazer.setEnabled(false);
        btnDesfazer.addActionListener(e -> {
            if (contadorTorres == 2) {
                contadorTorres = 1;
                map.removeAllMapPolygons();
                map.removeAllMapMarkers();
                
                // Re-adiciona apenas o marcador da Torre A
                MapMarkerDot markerA = new MapMarkerDot("Torre A", coordA);
                markerA.setBackColor(Color.BLUE);
                map.addMapMarker(markerA);
                
                btnSimular.setEnabled(false);
                lblInstrucao.setText("Mova o mouse para posicionar a Torre B...");
            } else if (contadorTorres == 1) {
                contadorTorres = 0;
                map.removeAllMapMarkers();
                
                btnDesfazer.setEnabled(false);
                btnToggleMenu.setEnabled(false);
                btnToggleMenu.setText("Configurar Antenas >>");
                painelLateral.setVisible(false);
                lblInstrucao.setText("Clique no mapa para posicionar a Torre A e a Torre B");
            }
        });

        // Criando botão para expandir/minimizar o painel lateral
        btnToggleMenu = new JButton("Configurar Antenas >>");
        btnToggleMenu.setFont(new Font("Arial", Font.BOLD, 14));
        btnToggleMenu.setEnabled(false);
        btnToggleMenu.addActionListener(e -> {
            boolean isVisible = painelLateral.isVisible();
            painelLateral.setVisible(!isVisible);
            btnToggleMenu.setText(isVisible ? "Configurar Antenas >>" : "Esconder Configurações <<");
        });

        // Criando e configurando o botão "Simular Enlace"
        btnSimular = new JButton("Simular Enlace");
        btnSimular.setFont(new Font("Arial", Font.BOLD, 14));
        btnSimular.setEnabled(false);
        btnSimular.addActionListener(e -> {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            btnLimpar.setEnabled(false);
            btnSimular.setEnabled(false);

            double freqSelecionada = 2.4;
            try {
                freqSelecionada = Double.parseDouble(txtFrequencia.getText().replace(",", "."));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Valor de frequência inválido. Utilizando 2.4 GHz por padrão.", "Aviso", JOptionPane.WARNING_MESSAGE);
                txtFrequencia.setText("2.4");
            }
            final double frequenciaSimulacao = freqSelecionada;

            new Thread(() -> {
                br.ufg.simulador.main.Main.Resultado resultadoSim = br.ufg.simulador.main.Main.executarSimulacao(configA, configB, listaObstaculos, frequenciaSimulacao);
                
                SwingUtilities.invokeLater(() -> {
                    setCursor(Cursor.getDefaultCursor());
                    btnLimpar.setEnabled(true);
                    btnSimular.setEnabled(true);

                    String textoStatus;
                    int tipoIcone;
                    if (resultadoSim.viavel) {
                        textoStatus = "VIÁVEL! O enlace possui visada e o sinal recebido está acima da sensibilidade do receptor.";
                        tipoIcone = JOptionPane.INFORMATION_MESSAGE;
                    } else {
                        if (resultadoSim.obstrucaoFresnel) {
                            textoStatus = "INVIÁVEL! Foram detectadas obstruções críticas na Zona de Fresnel.";
                        } else { // Implica que o sinal é muito fraco
                            textoStatus = String.format("INVIÁVEL! Sinal recebido (%.2f dBm) é muito fraco para a sensibilidade do receptor (%.2f dBm).", resultadoSim.potenciaRecepcao, resultadoSim.sensibilidadeReceptor);
                        }
                        tipoIcone = JOptionPane.ERROR_MESSAGE;
                    }
                    String mensagemFinal = String.format("Simulação finalizada!\n\nDistância Total: %.2f km\nAntena Principal Torre A: %s\nAntena Principal Torre B: %s\n\nStatus do Enlace: %s", resultadoSim.distancia, resultadoSim.antenaAInfo, resultadoSim.antenaBInfo, textoStatus);
                    String tituloJanela = resultadoSim.viavel ? "Resultado: Enlace Viável" : "Resultado: Enlace Inviável";
                    JOptionPane.showMessageDialog(null, mensagemFinal, tituloJanela, tipoIcone);
                    
                    new TelaGrafico(resultadoSim).setVisible(true);
                });
            }).start();
        });

        JPanel painelInferior = new JPanel();
        painelInferior.add(btnToggleMenu);
        painelInferior.add(btnDesfazer);
        painelInferior.add(btnLimpar);
        painelInferior.add(btnSimular);
        add(painelInferior, BorderLayout.SOUTH);
        
        // Exibe a tela
        setVisible(true);
    }

    private double calcularDistancia(Coordinate c1, Coordinate c2) {
        double dLat = Math.toRadians(c2.getLat() - c1.getLat());
        double dLon = Math.toRadians(c2.getLon() - c1.getLon());
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(c1.getLat())) * Math.cos(Math.toRadians(c2.getLat())) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return 6371.0 * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    private void animarMapaParaEnquadrar(Coordinate c1, Coordinate c2) {
        final double targetLat = (c1.getLat() + c2.getLat()) / 2.0;
        final double targetLon = (c1.getLon() + c2.getLon()) / 2.0;
        final Coordinate targetCenter = new Coordinate(targetLat, targetLon);

        final double minLat = Math.min(c1.getLat(), c2.getLat());
        final double maxLat = Math.max(c1.getLat(), c2.getLat());
        final double minLon = Math.min(c1.getLon(), c2.getLon());
        final double maxLon = Math.max(c1.getLon(), c2.getLon());

        final int targetZoom = getFitZoom(new Coordinate(maxLat, minLon), new Coordinate(minLat, maxLon));

        final Coordinate startCenter = map.getPosition();
        final int duration = 500; // ms
        final int steps = 25;
        final int delay = duration / steps;
        final long startTime = System.currentTimeMillis();

        new Timer(delay, e -> {
            long elapsed = System.currentTimeMillis() - startTime;
            double progress = Math.min(1.0, (double) elapsed / duration);

            double newLat = startCenter.getLat() + (targetCenter.getLat() - startCenter.getLat()) * progress;
            double newLon = startCenter.getLon() + (targetCenter.getLon() - startCenter.getLon()) * progress;

            map.setDisplayPosition(new Coordinate(newLat, newLon), map.getZoom());

            if (progress >= 1.0) {
                ((Timer) e.getSource()).stop();
                map.setZoom(targetZoom); // Ajusta o zoom no final da animação de pan
            }
        }).start();
    }

    private int getFitZoom(Coordinate topLeft, Coordinate botRight) {
        int zoom = map.getTileController().getTileSource().getMaxZoom();
        while (zoom > map.getTileController().getTileSource().getMinZoom()) {
            int x1 = lonToX(topLeft.getLon(), zoom);
            int y1 = latToY(topLeft.getLat(), zoom);
            int x2 = lonToX(botRight.getLon(), zoom);
            int y2 = latToY(botRight.getLat(), zoom);
            // Adiciona uma margem de 20 pixels para não ficar colado nas bordas
            if (Math.abs(x1 - x2) < map.getWidth() - 40 && Math.abs(y1 - y2) < map.getHeight() - 40) {
                break;
            }
            zoom--;
        }
        return zoom;
    }

    private int lonToX(double lon, int zoom) {
        double tiles = Math.pow(2, zoom);
        return (int) ((lon + 180.0) / 360.0 * 256.0 * tiles);
    }

    private int latToY(double lat, int zoom) {
        double latRad = Math.toRadians(lat);
        double tiles = Math.pow(2, zoom);
        double mercN = Math.log(Math.tan(Math.PI / 4.0 + latRad / 2.0));
        return (int) ((1.0 - mercN / Math.PI) / 2.0 * 256.0 * tiles);
    }

    private JPanel criarPainelLateral() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setPreferredSize(new Dimension(300, 0));
        painel.setBorder(BorderFactory.createTitledBorder("Configuração de Antenas"));

        JPanel painelFreq = new JPanel();
        painelFreq.setBorder(BorderFactory.createTitledBorder("Parâmetros do Enlace"));
        painelFreq.add(new JLabel("Frequência (GHz): "));
        txtFrequencia = new JTextField("2.4", 5);
        painelFreq.add(txtFrequencia);
        painel.add(painelFreq);

        painel.add(criarPainelTorre("Torre A", configA));
        painel.add(criarPainelTorre("Torre B", configB));

        return painel;
    }

    private JPanel criarPainelTorre(String titulo, br.ufg.simulador.main.Main.DadosTorre config) {
        JPanel p = new JPanel(new BorderLayout());
        javax.swing.border.TitledBorder border = BorderFactory.createTitledBorder(titulo + " (Alt: " + config.alturaEstrutura + "m)");
        p.setBorder(border);
        
        DefaultListModel<String> listModel = new DefaultListModel<>();
        Runnable atualizarLista = () -> {
            listModel.clear();
            border.setTitle(titulo + " (Alt: " + config.alturaEstrutura + "m)");
            p.repaint();
            for (int i = 0; i < config.antenas.size(); i++) {
                Antena ant = config.antenas.get(i);
                listModel.addElement("Antena " + (i+1) + " | Alt: " + ant.getAltura() + "m | Pot: " + ant.getPotenciaTransmissao() + "dBm | Ganho: " + ant.getGanho() + "dBi | Sens: " + ant.getSensibilidade() + "dBm");
            }
        };
        atualizarLista.run();
        
        JList<String> jList = new JList<>(listModel);
        
        // Limpa a seleção se clicar em um espaço vazio dentro da lista
        jList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                int index = jList.locationToIndex(e.getPoint());
                if (index == -1 || !jList.getCellBounds(index, index).contains(e.getPoint())) {
                    jList.clearSelection();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(jList);
        // Limpa a seleção se clicar no fundo do painel de scroll
        scrollPane.getViewport().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                jList.clearSelection();
            }
        });
        p.add(scrollPane, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new java.awt.GridLayout(0, 1)); // Linhas dinâmicas para ocultar/mostrar botões

        JButton btnEditarTorre = new JButton("Editar Altura da Torre");
        btnEditarTorre.addActionListener(e -> {
            String novaAltStr = JOptionPane.showInputDialog(null, "Nova altura da Torre (m):", config.alturaEstrutura);
            if (novaAltStr != null) {
                try {
                    double alturaNova = Double.parseDouble(novaAltStr.replace(",", "."));
                    boolean temConflito = false;
                    for (Antena ant : config.antenas) {
                        if (ant.getAltura() > alturaNova) temConflito = true;
                    }
                    if (temConflito) {
                        JOptionPane.showMessageDialog(null, "Erro: Há antenas instaladas acima de " + alturaNova + "m.\nRemova-as ou altere suas alturas primeiro.", "Altura Inválida", JOptionPane.ERROR_MESSAGE);
                    } else {
                        config.alturaEstrutura = alturaNova;
                        atualizarLista.run(); // Atualiza o título do painel e re-renderiza
                    }
                } catch (Exception ex) { JOptionPane.showMessageDialog(null, "Valor numérico inválido inserido!"); }
            }
        });

        JButton btnNovaAntena = new JButton("Adicionar Antena");
        btnNovaAntena.addActionListener(e -> {
            String alt = JOptionPane.showInputDialog(null, "Altura da Instalação da Antena (m):", config.alturaEstrutura);
            String pot = JOptionPane.showInputDialog(null, "Potência de Transmissão (dBm):", "30");
            String ganho = JOptionPane.showInputDialog(null, "Ganho da Antena (dBi):", "15");
            String sens = JOptionPane.showInputDialog(null, "Sensibilidade do Receptor (dBm):", "-90");
            if (alt != null && pot != null && ganho != null && sens != null) {
                try {
                    double alturaAntena = Double.parseDouble(alt.replace(",", "."));
                    double potAntena = Double.parseDouble(pot.replace(",", "."));
                    double ganhoAntena = Double.parseDouble(ganho.replace(",", "."));
                    double sensAntena = Double.parseDouble(sens.replace(",", "."));
                    
                    if (alturaAntena > config.alturaEstrutura) {
                        JOptionPane.showMessageDialog(null, "Erro: A antena (" + alturaAntena + "m) não pode ser instalada acima do topo da torre (" + config.alturaEstrutura + "m).", "Altura Inválida", JOptionPane.ERROR_MESSAGE);
                    } else {
                        config.antenas.add(new Antena(alturaAntena, potAntena, ganhoAntena, sensAntena));
                        atualizarLista.run(); // Atualiza a renderização na lista
                    }
                } catch (Exception ex) { JOptionPane.showMessageDialog(null, "Valores inválidos inseridos!"); }
            }
        });
        
        JButton btnEditarAntena = new JButton("Editar Antena Selecionada");
        btnEditarAntena.setVisible(false); // Oculto por padrão
        btnEditarAntena.addActionListener(e -> {
            int index = jList.getSelectedIndex();
            if (index != -1) {
                Antena antAtual = config.antenas.get(index);
                String alt = JOptionPane.showInputDialog(null, "Nova Altura da Instalação da Antena (m):", antAtual.getAltura());
                if (alt != null) {
                    String pot = JOptionPane.showInputDialog(null, "Nova Potência de Transmissão (dBm):", antAtual.getPotenciaTransmissao());
                    if (pot != null) {
                        String ganho = JOptionPane.showInputDialog(null, "Novo Ganho da Antena (dBi):", antAtual.getGanho());
                        if (ganho != null) { 
                            String sens = JOptionPane.showInputDialog(null, "Nova Sensibilidade do Receptor (dBm):", antAtual.getSensibilidade());
                            if (sens != null) {
                                try {
                                    double alturaAntena = Double.parseDouble(alt.replace(",", "."));
                                    double potAntena = Double.parseDouble(pot.replace(",", "."));
                                    double ganhoAntena = Double.parseDouble(ganho.replace(",", "."));
                                    double sensAntena = Double.parseDouble(sens.replace(",", "."));
                                    
                                    if (alturaAntena > config.alturaEstrutura) {
                                        JOptionPane.showMessageDialog(null, "Erro: A antena (" + alturaAntena + "m) não pode ser instalada acima do topo da torre (" + config.alturaEstrutura + "m).", "Altura Inválida", JOptionPane.ERROR_MESSAGE);
                                    } else {
                                        // Substitui a antena antiga pela nova no mesmo índice
                                        config.antenas.set(index, new Antena(alturaAntena, potAntena, ganhoAntena, sensAntena));
                                        atualizarLista.run();
                                    }
                                } catch (Exception ex) { JOptionPane.showMessageDialog(null, "Valores numéricos inválidos inseridos!"); }
                            }
                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Selecione uma antena na lista para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });

        JButton btnRemoverAntena = new JButton("Remover Antena Selecionada");
        btnRemoverAntena.setVisible(false); // Oculto por padrão
        btnRemoverAntena.addActionListener(e -> {
            int index = jList.getSelectedIndex();
            if (index != -1) {
                if (config.antenas.size() > 1) { // Garante que a torre não ficará sem nenhuma antena
                    config.antenas.remove(index);
                    jList.clearSelection(); // Limpa a seleção após remover para esconder os botões
                    atualizarLista.run();
                } else {
                    JOptionPane.showMessageDialog(null, "A torre precisa de pelo menos uma antena configurada!", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Selecione uma antena na lista para remover.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Oculta ou exibe os botões dependendo se há algo selecionado na lista
        jList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean temSelecao = !jList.isSelectionEmpty();
                btnEditarAntena.setVisible(temSelecao);
                btnRemoverAntena.setVisible(temSelecao);
                p.revalidate(); // Reajusta o painel visualmente
                p.repaint();
            }
        });

        // Limpa a seleção se clicar no painel em volta da lista ou dos botões
        p.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                jList.clearSelection();
            }
        });

        painelBotoes.add(btnEditarTorre);
        painelBotoes.add(btnNovaAntena);
        painelBotoes.add(btnEditarAntena);
        painelBotoes.add(btnRemoverAntena);
        p.add(painelBotoes, BorderLayout.SOUTH);
        return p;
    }
}