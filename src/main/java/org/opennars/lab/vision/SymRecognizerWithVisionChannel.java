/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package org.opennars.lab.vision;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import org.opennars.gui.NARSwing;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import org.opennars.main.Nar;
import org.opennars.main.Parameters;
import org.opennars.entity.Sentence;
import org.opennars.io.events.AnswerHandler;
import org.opennars.io.Narsese;
import org.opennars.language.SetInt;
import org.opennars.language.Term;
import org.opennars.plugin.perception.VisionChannel;


public class SymRecognizerWithVisionChannel extends javax.swing.JFrame {

    private Image fitimage(Image img , int w , int h)
    {
        BufferedImage resizedimage = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = resizedimage.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(img, 0, 0,w,h,null);
        g2.dispose();
        return resizedimage;
    }
    
    static int SIZE = 10;
    private void canvasMousePressed(MouseEvent evt) {
        int X = evt.getX()/(jLabel1.getWidth() / SIZE);
        int Y = evt.getY()/(jLabel1.getHeight() / SIZE);
        
        int RAD = 0;
        for(int x=X-RAD;x<X+RAD+1;x++) {
            for(int y=Y-RAD;y<Y+RAD+1;y++) {
                if(x<0 || y<0 || x>=SIZE || y>=SIZE) {
                    continue;
                }
                float dx = Math.abs(X-x);
                float dy = Math.abs(Y-y);
                float distance = (float) Math.sqrt(dx*dx+dy*dy);
                //distance*=distance;
                float maxDistance = (float) Math.sqrt(Math.pow(Math.abs(X-RAD - X),2)+
                                            Math.pow(Math.abs(Y-RAD - Y),2));
                //maxDistance*=maxDistance;
                float R = 255.0f;// - 255.0f*(distance / maxDistance);
                Color col1 = new Color(canvasIMG.getRGB(x, y));
                R*=0.4;
                R+=col1.getRed();
                if(R > 255) {
                    R = 255;
                }
                if(R < 0) {
                    R = 0;
                }

                canvasIMG.setRGB(x,y, new Color((int)R,(int)R,(int)R).getRGB());
            }
        }
        //canvasIMG.setRGB(X, Y, new Color(255,0,0).getRGB());
        jLabel1.setIcon(new ImageIcon(fitimage(canvasIMG,jLabel1.getWidth(), jLabel1.getHeight())));
        jLabel1.repaint();
    }
    
    public BufferedImage canvasIMG;
    public BufferedImage exampleIMG;
    
    public SymRecognizerWithVisionChannel() {
        initComponents();
        invar.setSelected(true);
        jButton3.setForeground(Color.RED);
        exampleIMG = new BufferedImage(SIZE*10,SIZE*10,BufferedImage.TYPE_INT_RGB);
        canvasIMG = new BufferedImage(SIZE,SIZE,BufferedImage.TYPE_INT_RGB);
        //JLabel picLabel = new JLabel(new ImageIcon(canvasIMG));
        jLabel1.setIcon(new ImageIcon(fitimage(canvasIMG,jLabel1.getWidth(), jLabel1.getHeight())));
        estimate.setIcon(new ImageIcon(fitimage(exampleIMG,estimate.getWidth(), estimate.getHeight())));
        jLabel1.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                canvasMousePressed(evt);
            }
            public void mouseReleased(MouseEvent evt) {
                canvasMousePressed(evt);
            }  
        });
        jLabel1.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                canvasMousePressed(e);
            }
            @Override public void mouseMoved(MouseEvent e) {}
        });
       // this.jPanel1.setBackground(Color.GRAY);
       this.getContentPane().setBackground(Color.DARK_GRAY);
       jButton2.setBackground(Color.BLACK);
        jButton3.setBackground(Color.BLACK);
         addPatternButton.setBackground(Color.BLACK);
       this.setTitle("Unsupervised recognition GUI");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        inputPanel = new javax.swing.JTextPane();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        inputPanel2 = new javax.swing.JTextPane();
        estimate = new javax.swing.JLabel();
        addPatternButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        invar = new javax.swing.JCheckBox();
        invar1 = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(102, 204, 0));

        jLabel1.setBackground(new java.awt.Color(255, 51, 51));
        jLabel1.setText("jLabel1");

        jButton2.setText("Clear");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setActionCommand("Determine most similar saved pattern");
        jButton3.setLabel("Determine most similar saved pattern");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jScrollPane1.setViewportView(inputPanel);

        jLabel2.setText("Saved patterns");

        jScrollPane3.setViewportView(inputPanel2);

        estimate.setBackground(new java.awt.Color(255, 51, 51));
        estimate.setText("jLabel1");

        addPatternButton.setText("Save pattern");
        addPatternButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addPatternButtonActionPerformed(evt);
            }
        });

        jLabel3.setText("Input");

        jLabel4.setText("Input");

        jLabel5.setText("Question");

        invar.setActionCommand("Assume translation invariance");
        invar.setLabel("Assume translation invariance");

        invar1.setText("Show reasoner GUI");
        invar1.setActionCommand("Assume translation invariance");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1)
                    .addComponent(jScrollPane3)
                    .addComponent(jLabel5)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(estimate, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addPatternButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(invar1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(invar)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(estimate, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(addPatternButton)
                    .addComponent(jButton3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(invar)
                    .addComponent(invar1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        canvasIMG = new BufferedImage(SIZE,SIZE,BufferedImage.TYPE_INT_RGB);
        jLabel1.setIcon(new ImageIcon(fitimage(canvasIMG,jLabel1.getWidth(), jLabel1.getHeight())));
        inputPanel.setText("");
        inputPanel2.setText("");
        resetDetection();
    }//GEN-LAST:event_jButton2ActionPerformed

    public void clear() {
        int max_per_row = exampleIMG.getWidth()/(scale_palette*SIZE);
        for(int k=0;k<max_per_row;k+=1) {
            for(int j=0;;j+=1) {
                boolean break3 = false;
                for(int x=0;x<SIZE*scale_palette;x+=1) {
                    for(int y=0;y<SIZE*scale_palette;y+=1) {
                        int Y = y+(3*j+1)*scale_palette*SIZE;
                        if(Y >= exampleIMG.getHeight()) {
                            break3=true;break;
                        }
                        exampleIMG.setRGB(x+k*scale_palette*SIZE, Y, 
                                Color.BLACK.getRGB());
                    }
                }
                if(break3){break;}
            }
        }
        estimate.setIcon(new ImageIcon(fitimage(exampleIMG,estimate.getWidth(), estimate.getHeight())));
        estimate.repaint();
    }
    
    public void resetDetection() {
        clear();
        for(AnswerHandler ans : q) {
            ans.off();
        }
        q = new ArrayList<AnswerHandler>();
        if(nar != null) {
            nar.stop();
            nar.reset();
            if(gui != null)
                gui.mainWindow.setVisible(false);
            nar = null;
            gui = null;
        }
        
    }
    
    static {
        //Parameters.SEQUENCE_BAG_ATTEMPTS = 10000;
        //Parameters.SEQUENCE_BAG_LEVELS = 1000;
        //Parameters.SEQUENCE_BAG_SIZE=10000;
    }
    
    int sensor_W = SIZE;
    int sensor_H = SIZE;
    static String LABEL = "WHITE";
    Nar nar = null;
    NARSwing gui = null;
    ArrayList<AnswerHandler> q = new ArrayList<AnswerHandler>();
    int scale_palette=2;
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        resetDetection();
        StringBuilder build = new StringBuilder();
        StringBuilder build2 = new StringBuilder();
        build2.append("<(&|,");
        for(int x=0;x<SIZE;x+=1) {
            for(int y=0;y<SIZE;y+=1) {
                int used_X = x;
                int used_Y = y;
                float USED_X = 2.0f*(used_X / (float) (sensor_W-1))-1.0f;
                float USED_Y = 2.0f*(used_Y / (float) (sensor_H-1))-1.0f;
                Color col1 = new Color(canvasIMG.getRGB(x, y));
                
                float col = ((float)col1.getRed()) / 255.0f;
                //if(col > 0.0) { 
                    float freq = 0.5f+(col - 0.5f);
                    if(invar.isSelected()) {
                        build.append("<{p"+exid+"["+String.valueOf(USED_X)+","+String.valueOf(USED_Y)+"]} --> ["+LABEL+"]>. %"+String.valueOf(freq)+"%");
                        build.append("\n");
                    } else {
                        build.append("<{p"+exid+"["+String.valueOf(USED_X)+","+String.valueOf(USED_Y)+"]} --> ["+LABEL+"]>. %"+String.valueOf(freq)+"%");
                        build.append("\n");
                    }
                //}
            }
        }
        if(evt == null) {
            build.append("<{p"+exid+"} --> (*,example"+exid+")>.");
        }
        inputPanel.setText(build.toString());
        
        if(evt == null) {
            additional[exid]=build.toString();
        }
        else {
            
            nar = new Nar(); //add vision channel for [on] property
            //to the nar instance, and nar is also the "next higher" sensory channel
            //to report the results to
            Term label = SetInt.make(new Term(LABEL));
            nar.addPlugin(new VisionChannel(label, nar, nar, sensor_H, sensor_W, sensor_W*sensor_H));
            
            if(invar1.isSelected()) {
                gui = new NARSwing(nar);
            }
            
            int u = 0;
            inputPanel2.setText("");
            //for(String s : questions) {
            String s = "<{p"+exid+"} --> (*,?what)>?"; {
                if(s!=null) {
                    AnswerHandler cur = new AnswerHandler() {
                        @Override
                        public void onSolution(Sentence belief) {
                            //System.out.println("solution: " + belief);
                            System.out.println(belief);
                            float howconf = belief.truth.getConfidence();
                            if(howconf >= 0.001f) { //only mark if above 0.1 confidence
                                //also mark image:
                                int maxu = Integer.valueOf(belief.getTerm().toString().split("example")[1].split("\\)")[0]);
                                clear();
                                for(int x=0;x<SIZE*scale_palette;x+=1) {
                                    for(int y=0;y<SIZE*scale_palette;y+=1) {
                                        Color col = new Color(canvasIMG.getRGB(x/scale_palette, y/scale_palette));
                                        int k = getK[maxu];
                                        int j = getJ[maxu];
                                        exampleIMG.setRGB(x+k*scale_palette*SIZE, y+(3*j+1)*scale_palette*SIZE, 
                                                new Color(col.getRed(),0,0).getRGB());
                                    }
                                }
                                estimate.setIcon(new ImageIcon(fitimage(exampleIMG,estimate.getWidth(), estimate.getHeight())));
                                estimate.repaint();
                            }
                        }
                    };
                    q.add(cur);
                    try {
                        for(int h=0;h<exid;h++) {
                            inputPanel2.setText(inputPanel2.getText()+additional[h]+"\n");
                            nar.addInput(additional[h]);
                        }
                        
                        inputPanel2.setText(inputPanel2.getText()+s+"\n");
                        nar.ask(s.substring(0,s.length()-1), cur);
                    } catch (Narsese.InvalidInputException ex) {
                        Logger.getLogger(SymRecognizerWithVisionChannel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                u++;
            }
            nar.param.noiseLevel.set(0);
            nar.addInput(inputPanel.getText());
            nar.addInput(s);
            nar.start(0);
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    int maxExamples = 50;
    //String[] questions = new String[maxExamples];
    String question ="<{?what} --> [observed]>?";
    String[] additional = new String[maxExamples];
    int k =0;
    int j=0;
    int exid=0;
    int[] getJ= new int[maxExamples];
    int[] getK= new int[maxExamples];
    private void addPatternButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addPatternButtonActionPerformed
        invar.setEnabled(false);
        for(int x=0;x<SIZE*scale_palette;x+=1) {
            for(int y=0;y<SIZE*scale_palette;y+=1) {
                Color col = new Color(canvasIMG.getRGB(x/scale_palette, y/scale_palette));
                exampleIMG.setRGB(x+k*scale_palette*SIZE, y+3*j*scale_palette*SIZE, 
                        col.getRGB());
            }
        }
        estimate.setIcon(new ImageIcon(fitimage(exampleIMG,estimate.getWidth(), estimate.getHeight())));
        estimate.repaint();
        jButton3ActionPerformed(null);
        int max_per_row = exampleIMG.getWidth()/(scale_palette*SIZE);
        int index=max_per_row*j+k;
        //questions[index]=inputPanel2.getText();
        getJ[index]=j;
        getK[index]=k;
        k+=1;
        if(k >= max_per_row) {
            j++;
            k=0;
        }
        exid++;
    }//GEN-LAST:event_addPatternButtonActionPerformed


    public static void main(String args[]) {
        NARSwing.themeInvert();
        Parameters.DURATION = 1000;
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SymRecognizerWithVisionChannel().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addPatternButton;
    private javax.swing.JLabel estimate;
    private javax.swing.JTextPane inputPanel;
    private javax.swing.JTextPane inputPanel2;
    private javax.swing.JCheckBox invar;
    private javax.swing.JCheckBox invar1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    // End of variables declaration//GEN-END:variables

    
}
