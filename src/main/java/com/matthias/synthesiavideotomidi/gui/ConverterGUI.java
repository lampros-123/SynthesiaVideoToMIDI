package com.matthias.synthesiavideotomidi.gui;

import com.matthias.synthesiavideotomidi.bl.ConverterBL;
import com.matthias.synthesiavideotomidi.bl.Config;
import com.matthias.synthesiavideotomidi.bl.NoteListener;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author Matthias
 */
public class ConverterGUI extends javax.swing.JFrame {

    private final JFileChooser fileChooser = new JFileChooser();
    private String waiting = "c1";
    ConverterBL bl;

    public ConverterGUI() {
        initComponents();
        setSize(new Dimension(1500, 800));
        reset();
    }
    
    public void reset() {
        bl = new ConverterBL();
        bl.setFile(ConverterBL.getFirstConfig().getVideo());
        waiting="c1";
        lbAction.setText("Action: set c" + (int) spC1.getValue());
        setupDefaults(bl.getConfig());
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        try {
            Config config = bl.getConfig();
            Graphics2D g2 = (Graphics2D) pnCanvas.getGraphics();
            g2.setColor(Color.gray);
            BufferedImage bufferedImage = bl.getCurrentFrame();
            if (bufferedImage == null) {
                return;
            }
            g2.drawImage(bufferedImage, 0, 0, (int) (bufferedImage.getWidth() * config.getScale()), (int) (bufferedImage.getHeight() * config.getScale()), this);

            if (bl.getState() != ConverterBL.RUNNING) {
                for (NoteListener nl : bl.getNoteListeners()) {
                    g2.setColor(Color.orange);
                    g2.fillRect((int) (nl.getPosX() * config.getScale() - 2), (int) (nl.getPosY() * config.getScale() - 2), 4, 4);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupDefaults(Config config) {
        sliderScale.setValue((int) (config.getScale() * 100));
        sliderColorTolerance.setValue(config.getColorTolerance());
        sliderYDistance.setValue(config.getBlackWhiteVerticalSpacing());
        spC1.setValue(config.getC1Idx());
        spC2.setValue(config.getC2Idx());
        spBPM.setValue(config.getBpm());
        spPPQ.setValue(config.getPpq());
        spStartFrame.setValue(config.getStartFrame());
        spFirstFrameOfSong.setValue(config.getFirstFrameOfSong());
        spEndFrame.setValue(config.getEndFrame());
    }
    
    /**
     * This method is called from within the constructor to initialise the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pnCanvas = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        btSetC1 = new javax.swing.JButton();
        btSetC2 = new javax.swing.JButton();
        btStart = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        spBPM = new javax.swing.JSpinner();
        spC1 = new javax.swing.JSpinner();
        spC2 = new javax.swing.JSpinner();
        btChooseFile = new javax.swing.JButton();
        btFix = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        spStartFrame = new javax.swing.JSpinner();
        lbAction = new javax.swing.JLabel();
        spOffC1 = new javax.swing.JSpinner();
        spOffC2 = new javax.swing.JSpinner();
        sliderFPS = new javax.swing.JSlider();
        jLabel3 = new javax.swing.JLabel();
        sliderScale = new javax.swing.JSlider();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        spPPQ = new javax.swing.JSpinner();
        jLabel6 = new javax.swing.JLabel();
        cbSingleVoice = new javax.swing.JCheckBox();
        sliderColorTolerance = new javax.swing.JSlider();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        cbStaccatoPadding = new javax.swing.JComboBox<>();
        sliderYDistance = new javax.swing.JSlider();
        jLabel9 = new javax.swing.JLabel();
        btConfirmBaseColors = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        spFirstFrameOfSong = new javax.swing.JSpinner();
        btSetAsFirstFrame = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        spEndFrame = new javax.swing.JSpinner();
        tfAutoAdjustBeat = new javax.swing.JTextField();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        menuGoTo = new javax.swing.JMenu();
        miTime = new javax.swing.JMenuItem();
        miBar = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        pnCanvas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pnCanvasMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                pnCanvasMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                pnCanvasMouseExited(evt);
            }
        });
        pnCanvas.setLayout(new java.awt.GridLayout(1, 0));
        getContentPane().add(pnCanvas, java.awt.BorderLayout.CENTER);

        jPanel1.setMinimumSize(new java.awt.Dimension(200, 132));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        btSetC1.setText("setC");
        btSetC1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        btSetC1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSetC1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(btSetC1, gridBagConstraints);

        btSetC2.setText("setC");
        btSetC2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        btSetC2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSetC2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(btSetC2, gridBagConstraints);

        btStart.setText("start");
        btStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btStartActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(btStart, gridBagConstraints);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel1.setText("bpm:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel1.add(jLabel1, gridBagConstraints);

        spBPM.setModel(new javax.swing.SpinnerNumberModel(150, 1, null, 1));
        spBPM.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spBPMStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 2.0;
        jPanel1.add(spBPM, gridBagConstraints);

        spC1.setModel(new javax.swing.SpinnerNumberModel(2, 0, 8, 1));
        spC1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spC1StateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel1.add(spC1, gridBagConstraints);

        spC2.setModel(new javax.swing.SpinnerNumberModel(8, 0, 8, 1));
        spC2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spC2StateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel1.add(spC2, gridBagConstraints);

        btChooseFile.setText("Choose Video");
        btChooseFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btChooseFileActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel1.add(btChooseFile, gridBagConstraints);

        btFix.setText("fix");
        btFix.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btFixActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel1.add(btFix, gridBagConstraints);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel2.setText("first frame of song");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel1.add(jLabel2, gridBagConstraints);

        spStartFrame.setModel(new javax.swing.SpinnerNumberModel(100, null, null, 1));
        spStartFrame.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spStartFrameStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(spStartFrame, gridBagConstraints);

        lbAction.setText("Action: nothing");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 21;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel1.add(lbAction, gridBagConstraints);

        spOffC1.setModel(new javax.swing.SpinnerNumberModel(0, 0, 7, 1));
        spOffC1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spOffC1StateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel1.add(spOffC1, gridBagConstraints);

        spOffC2.setModel(new javax.swing.SpinnerNumberModel(0, 0, 7, 1));
        spOffC2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spOffC2StateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel1.add(spOffC2, gridBagConstraints);

        sliderFPS.setMaximum(20);
        sliderFPS.setMinimum(1);
        sliderFPS.setValue(15);
        sliderFPS.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderFPSStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel1.add(sliderFPS, gridBagConstraints);

        jLabel3.setText("playback FPS");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel1.add(jLabel3, gridBagConstraints);

        sliderScale.setMaximum(150);
        sliderScale.setMinimum(20);
        sliderScale.setValue(100);
        sliderScale.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderScaleStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel1.add(sliderScale, gridBagConstraints);

        jLabel4.setText("Scale");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel1.add(jLabel4, gridBagConstraints);

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel5.setText("ppq (rec: 2,4,12)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(jLabel5, gridBagConstraints);

        spPPQ.setModel(new javax.swing.SpinnerNumberModel(4, 1, 120, 1));
        spPPQ.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spPPQStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(spPPQ, gridBagConstraints);

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel6.setText("auto adjust beat limit");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel1.add(jLabel6, gridBagConstraints);

        cbSingleVoice.setText("Single Voice");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 19;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel1.add(cbSingleVoice, gridBagConstraints);

        sliderColorTolerance.setMaximum(120);
        sliderColorTolerance.setMinimum(40);
        sliderColorTolerance.setValue(80);
        sliderColorTolerance.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderColorToleranceStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel1.add(sliderColorTolerance, gridBagConstraints);

        jLabel7.setText("Color Tolerance");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel1.add(jLabel7, gridBagConstraints);

        jLabel8.setText("staccato padding");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 20;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(jLabel8, gridBagConstraints);

        cbStaccatoPadding.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0", "0.25", "0.5", "1", "2", "4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 20;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(cbStaccatoPadding, gridBagConstraints);

        sliderYDistance.setMaximum(150);
        sliderYDistance.setMinimum(5);
        sliderYDistance.setValue(40);
        sliderYDistance.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderYDistanceStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel1.add(sliderYDistance, gridBagConstraints);

        jLabel9.setText("y distance notelisteners");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel1.add(jLabel9, gridBagConstraints);

        btConfirmBaseColors.setText("confirm Base Colors");
        btConfirmBaseColors.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btConfirmBaseColorsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel1.add(btConfirmBaseColors, gridBagConstraints);

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel10.setText("start frame");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel1.add(jLabel10, gridBagConstraints);

        spFirstFrameOfSong.setModel(new javax.swing.SpinnerNumberModel(100, null, null, 1));
        spFirstFrameOfSong.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spFirstFrameOfSongStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(spFirstFrameOfSong, gridBagConstraints);

        btSetAsFirstFrame.setText("set as first frame");
        btSetAsFirstFrame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSetAsFirstFrameActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel1.add(btSetAsFirstFrame, gridBagConstraints);

        jLabel11.setText("end frame");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel1.add(jLabel11, gridBagConstraints);

        spEndFrame.setModel(new javax.swing.SpinnerNumberModel(100, null, null, 1));
        spEndFrame.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spEndFrameStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(spEndFrame, gridBagConstraints);

        tfAutoAdjustBeat.setText("0.2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel1.add(tfAutoAdjustBeat, gridBagConstraints);

        getContentPane().add(jPanel1, java.awt.BorderLayout.EAST);

        jMenu1.setText("Calculate");

        jMenuItem1.setText("BPM");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        menuGoTo.setText("Go To");

        miTime.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.CTRL_MASK));
        miTime.setText("Time");
        miTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miTimeActionPerformed(evt);
            }
        });
        menuGoTo.add(miTime);

        miBar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.CTRL_MASK));
        miBar.setText("Bar");
        miBar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miBarActionPerformed(evt);
            }
        });
        menuGoTo.add(miBar);

        jMenuBar1.add(menuGoTo);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btSetC1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSetC1ActionPerformed
        waiting = "c1";
        lbAction.setText("Action: set c" + (int) spC1.getValue());
    }//GEN-LAST:event_btSetC1ActionPerformed

    private void pnCanvasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnCanvasMouseClicked
        double scaledX = evt.getX() / bl.getConfig().getScale();
        double scaledY = evt.getY() / bl.getConfig().getScale();
        if (waiting.equals("c1")) {
            bl.getConfig().setC1x((int) scaledX);
            bl.getConfig().setC12y((int) scaledY);
            bl.calculateNoteListenersIfSet();
            waiting = "c2";
            lbAction.setText("Action: set c" + (int) spC2.getValue());
        } else if (waiting.equals("c2")) {
            bl.getConfig().setC2x((int) scaledX);
            bl.getConfig().setC12y((int) scaledY);
            bl.calculateNoteListenersIfSet();
            waiting = "fix";
            lbAction.setText("Action: fix");
        } else if (waiting.equals("fix")) {
            bl.fix(scaledX, scaledY);
        }
        repaint();
    }//GEN-LAST:event_pnCanvasMouseClicked

    private void btSetC2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSetC2ActionPerformed
        waiting = "c2";
        lbAction.setText("Action: set c" + (int) spC2.getValue());
    }//GEN-LAST:event_btSetC2ActionPerformed

    private void btStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btStartActionPerformed
        if (bl.getState() == ConverterBL.WAITING_TO_START) {
            try {
                double staccatopadding = Double.parseDouble(cbStaccatoPadding.getSelectedItem().toString());
                bl.convert(cbSingleVoice.isSelected(), staccatopadding, Double.parseDouble(tfAutoAdjustBeat.getText()));
                btStart.setText("Stop");
                Thread t = new Thread(() -> {
                    while (bl.getState() == ConverterBL.RUNNING) {
                        repaint();
                        try {
                            Thread.sleep(1000 / sliderFPS.getValue());
                        } catch (InterruptedException ex) {}
                    }
                    btStart.setText("save");
                });
                t.start();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Converting has failed");
                ex.printStackTrace();
            }
        } else if (bl.getState() == ConverterBL.RUNNING) {
            bl.setState(ConverterBL.WAITING_FOR_SETTINGS);
            btStart.setText("Save");
        } else if (bl.getState() == ConverterBL.WAITING_FOR_SETTINGS) {
            int bpm = (int) spBPM.getValue();
            int ppq = (int) spPPQ.getValue();
            if(bpm < 1 || ppq < 1) {
                JOptionPane.showMessageDialog(this, "bpm or ppq invalid");
                return;
            }
            bl.configCompleted();
        }

    }//GEN-LAST:event_btStartActionPerformed

    private void btChooseFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btChooseFileActionPerformed
        bl.saveDataToConfig();
        fileChooser.setCurrentDirectory(new File("C:\\Users\\Matthias\\OneDrive - HTBLA Kaindorf\\Klavier"));
        int returnVal = fileChooser.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            bl = new ConverterBL();
            bl.setFile(fileChooser.getSelectedFile());
            setupDefaults(bl.getConfig());
            repaint();
        }
    }//GEN-LAST:event_btChooseFileActionPerformed

    private void btFixActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btFixActionPerformed
        waiting = "fix";
    }//GEN-LAST:event_btFixActionPerformed

    private void pnCanvasMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnCanvasMouseEntered
        bl.paused = false;
    }//GEN-LAST:event_pnCanvasMouseEntered

    private void pnCanvasMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnCanvasMouseExited
        bl.paused = true;
    }//GEN-LAST:event_pnCanvasMouseExited

    private void spStartFrameStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spStartFrameStateChanged
        bl.getConfig().setStartFrame((int) spStartFrame.getValue());
        bl.updateCurrentFrame();
        repaint();
    }//GEN-LAST:event_spStartFrameStateChanged

    private void sliderFPSStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderFPSStateChanged
    }//GEN-LAST:event_sliderFPSStateChanged

    private void sliderScaleStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderScaleStateChanged
        bl.getConfig().setScale(sliderScale.getValue() / 100.0);
        repaint();
    }//GEN-LAST:event_sliderScaleStateChanged

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        bl.saveDataToConfig();
        bl.saveDefaults();
    }//GEN-LAST:event_formWindowClosing

    private void sliderColorToleranceStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderColorToleranceStateChanged
        bl.getConfig().setColorTolerance(sliderColorTolerance.getValue());
    }//GEN-LAST:event_sliderColorToleranceStateChanged

    private void sliderYDistanceStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderYDistanceStateChanged
        bl.getConfig().setBlackWhiteVerticalSpacing(sliderYDistance.getValue());
        bl.blackWhiteVerticalSpacingUpdated();
        repaint();
    }//GEN-LAST:event_sliderYDistanceStateChanged

    private void btConfirmBaseColorsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btConfirmBaseColorsActionPerformed
        ConfirmNotelistenerBaseColors gui = new ConfirmNotelistenerBaseColors(this, true, bl.getNoteListeners());
        gui.setVisible(true);
    }//GEN-LAST:event_btConfirmBaseColorsActionPerformed

    private void miTimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miTimeActionPerformed
        TimeInputDialog dlg = new TimeInputDialog(this, true);
        dlg.setVisible(true);
        if(dlg.wasSuccessful()) {
            int result = (int) (dlg.getSeconds() * bl.getConfig().getFPS());
            String[] options = new String[] {"Start Frame", "End Frame"};
            int selected = JOptionPane.showOptionDialog(this, "Which value should be filled", "result frame: " + result, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
            if(selected == 0) {
                spStartFrame.setValue(result);
            } else if (selected == 1){
                spEndFrame.setValue(result);
            }
        }
    }//GEN-LAST:event_miTimeActionPerformed

    private void miBarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miBarActionPerformed
        BeatInputDialog dlg = new BeatInputDialog(this, true);
        dlg.setVisible(true);
        if(dlg.wasSuccessful()) {
            double bpm = (int) spBPM.getValue();
            double desiredBeat = dlg.getBeat();
            double secondsTilBeat = desiredBeat / bpm * 60.0;
            int framesTilBeat = (int) (secondsTilBeat * bl.getConfig().getFPS());
            int baseFrame = (int) spFirstFrameOfSong.getValue();
            int result = baseFrame + framesTilBeat;

            String[] options = new String[] {"Start Frame", "End Frame"};
            int selected = JOptionPane.showOptionDialog(this, "Which value should be filled", "result frame: " + result, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
            if(selected == 0) {
                spStartFrame.setValue(result);
            } else if (selected == 1){
                spEndFrame.setValue(result);
            }
        }
    }//GEN-LAST:event_miBarActionPerformed

    private void spFirstFrameOfSongStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spFirstFrameOfSongStateChanged
        bl.getConfig().setFirstFrameOfSong((int) spFirstFrameOfSong.getValue());
    }//GEN-LAST:event_spFirstFrameOfSongStateChanged

    private void btSetAsFirstFrameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSetAsFirstFrameActionPerformed
        spFirstFrameOfSong.setValue(spStartFrame.getValue());
    }//GEN-LAST:event_btSetAsFirstFrameActionPerformed

    private void spC1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spC1StateChanged
        bl.getConfig().setC1Idx((int) spC1.getValue());
    }//GEN-LAST:event_spC1StateChanged

    private void spOffC1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spOffC1StateChanged
        bl.getConfig().setOffLeft((int) spOffC1.getValue());
    }//GEN-LAST:event_spOffC1StateChanged

    private void spC2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spC2StateChanged
        bl.getConfig().setC2Idx((int) spC2.getValue());
    }//GEN-LAST:event_spC2StateChanged

    private void spOffC2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spOffC2StateChanged
        bl.getConfig().setOffRight((int) spOffC2.getValue());
    }//GEN-LAST:event_spOffC2StateChanged

    private void spBPMStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spBPMStateChanged
        bl.getConfig().setBpm((int) spBPM.getValue());
    }//GEN-LAST:event_spBPMStateChanged

    private void spPPQStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spPPQStateChanged
        bl.getConfig().setPpq((int) spPPQ.getValue());
    }//GEN-LAST:event_spPPQStateChanged

    private void spEndFrameStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spEndFrameStateChanged
        bl.getConfig().setEndFrame((int) spEndFrame.getValue());
    }//GEN-LAST:event_spEndFrameStateChanged

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        BeatInputDialog dlg = new BeatInputDialog(this, true);
        dlg.setVisible(true);
        if(dlg.wasSuccessful()) {
            double beats = dlg.getBeat();
            double durFrames = bl.getConfig().getStartFrame() - bl.getConfig().getFirstFrameOfSong();
            double fps = bl.getConfig().getFPS();
            double bpm = beats / durFrames * fps * 60.0;
            JOptionPane.showMessageDialog(this, String.format("Precise bpm: %.3f", bpm));
            spBPM.setValue((int) Math.round(bpm));
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ConverterGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ConverterGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ConverterGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ConverterGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new ConverterGUI().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btChooseFile;
    private javax.swing.JButton btConfirmBaseColors;
    private javax.swing.JButton btFix;
    private javax.swing.JButton btSetAsFirstFrame;
    private javax.swing.JButton btSetC1;
    private javax.swing.JButton btSetC2;
    private javax.swing.JButton btStart;
    private javax.swing.JCheckBox cbSingleVoice;
    private javax.swing.JComboBox<String> cbStaccatoPadding;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lbAction;
    private javax.swing.JMenu menuGoTo;
    private javax.swing.JMenuItem miBar;
    private javax.swing.JMenuItem miTime;
    private javax.swing.JPanel pnCanvas;
    private javax.swing.JSlider sliderColorTolerance;
    private javax.swing.JSlider sliderFPS;
    private javax.swing.JSlider sliderScale;
    private javax.swing.JSlider sliderYDistance;
    private javax.swing.JSpinner spBPM;
    private javax.swing.JSpinner spC1;
    private javax.swing.JSpinner spC2;
    private javax.swing.JSpinner spEndFrame;
    private javax.swing.JSpinner spFirstFrameOfSong;
    private javax.swing.JSpinner spOffC1;
    private javax.swing.JSpinner spOffC2;
    private javax.swing.JSpinner spPPQ;
    private javax.swing.JSpinner spStartFrame;
    private javax.swing.JTextField tfAutoAdjustBeat;
    // End of variables declaration//GEN-END:variables
}
