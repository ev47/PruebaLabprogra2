/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package prueba;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.sound.sampled.SourceDataLine;
import static javax.sound.sampled.AudioSystem.getAudioInputStream;
import static javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Port.Info;
import javax.swing.ImageIcon;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;



/**
 *
 * @author edgva
 */
public class ReproductorMusical extends javax.swing.JFrame {
      private NodoPlay playlistInicio; 
      private NodoPlay currentSong;
      private String rutaCancionActual;
      private int currentSongIndex;
      private String playlist [];
      private long pausaPosition;
      private boolean isPaused;
      private SourceDataLine line;
    /**
     * Creates new form ReproductorMusical
     */
    public ReproductorMusical() {
        initComponents();
        playlist = new String[10];
        currentSongIndex = -1;
         playlistInicio = null; 
        currentSong = null;
    }    
   /* private NodoPlay encontrarUltimoNodo() {
        NodoPlay nodoActual = playlistInicio;
        while (nodoActual.getSiguiente() != null) {
            nodoActual = nodoActual.getSiguiente();
        }
        return nodoActual;
    } */
    private void agregarCancion() {
    JFileChooser fileChooser = new JFileChooser();
    FileFilter filter = new FileNameExtensionFilter("Archivos de audio", "mp3");
    fileChooser.setFileFilter(filter);
    int result = fileChooser.showOpenDialog(this);
    if (result == JFileChooser.APPROVE_OPTION) {
        File selectedFile = fileChooser.getSelectedFile();
        if (currentSongIndex < playlist.length - 1) {
            currentSongIndex++;
            playlist[currentSongIndex] = selectedFile.getAbsolutePath();
            rutaCancionActual = selectedFile.getAbsolutePath();
            System.out.println("Ruta de la canción seleccionada: " + rutaCancionActual);
            currentSong = new NodoPlay(rutaCancionActual, selectedFile.getName(), "", ""); 
            try {
                // Bloque try-catch para manejar la excepción
                try {
                    AudioFile file = AudioFileIO.read(selectedFile);
                    Artwork artwork = file.getTag().getFirstArtwork();
                    if (artwork != null) {
                        byte[] imageData = artwork.getBinaryData();
                        ImageIcon icon = new ImageIcon(imageData);
                        Imagen.setIcon(icon);
                    } else {
                        ImageIcon defaultIcon = new ImageIcon("imagenes/musica.jpg");
                        Imagen.setIcon(defaultIcon);
                    }
                } catch (IOException | CannotReadException | TagException | InvalidAudioFrameException e) {
                    e.printStackTrace();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            System.out.println("No se pueden agregar más canciones, la lista está llena.");
        }
    } else {
        System.out.println("No se ha seleccionado ninguna canción.");
    }
    }

    private void reproducirCancion() {
        if (currentSong != null) {
        String rutaCancion = currentSong.getRuta();
        Cancion.setText("Reproduciendo canción: " + currentSong.toString());
        System.out.println("Reproduciendo canción: " + currentSong.toString());
        System.out.println("Ruta de la canción: " + rutaCancion);
        play(rutaCancion);
    } else {
        Cancion.setText("No hay Cancion para reproducir");
        System.out.println("No hay canción para reproducir.");
    }
    }

    private void play(String filePath) {
        final File file = new File(filePath);

    try (final AudioInputStream in = AudioSystem.getAudioInputStream(file)) {
        final AudioFormat outFormat = getOutFormat(in.getFormat());
        final DataLine.Info info = new DataLine.Info(SourceDataLine.class, outFormat);
        if (line != null) {
            line.open(outFormat);
            line.start();
            stream(getAudioInputStream(outFormat, in), line);
            line.drain();
            line.stop();
        }
    } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
        throw new IllegalStateException(e);
    }
    }

    private AudioFormat getOutFormat(AudioFormat inFormat) {
        final int ch = inFormat.getChannels();
        final float rate = inFormat.getSampleRate();
        return new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, rate, 16, ch, ch * 2, rate, false);
    }

    private void stream(AudioInputStream in, SourceDataLine line) throws IOException {
        final byte[] buffer = new byte[4096];
        for (int n = 0; n != -1; n = in.read(buffer, 0, buffer.length)) {
            line.write(buffer, 0, n);
        }
    }


/*
public void pausar() {
    if (line != null && line.isRunning()) {
        pausaPosition = line.getLongFramePosition();
        line.stop();
        isPaused = true;
    }
}
public void reanudar() {
    if (line != null && !line.isRunning() && isPaused) {
       // line.setFramePosition(pausaPosition);
        line.start();
        isPaused = false;
    }
}
  */  
    private void stop(){
     if (currentSong != null && line != null && line.isRunning()) {
        line.stop();
        line.flush();
        line.start();
    } else {
        Cancion.setText("No hay Cancion en Reproduccion para Detener");
        System.out.println("No hay canción en reproducción para detener.");
    }
    }
    
    private void pausa(){
         if (currentSong != null) {
        currentSong.pausar(); 
    } else {
        Cancion.setText("No hay Cancion en Reproduccion para Pausar");     
        System.out.println("No hay canción para pausar."); 
    } 
    }
    private void vol(){
     int delta = 10; 
    int currentValue = volumen.getValue();
    int newValue = currentValue + delta;
    if (currentValue==25){
        newValue=25;
    }
    newValue = Math.max(0, Math.min(100, newValue));
    volumen.setValue(newValue);
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnplay = new javax.swing.JButton();
        btnpausa = new javax.swing.JButton();
        btnstop = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        Cancion = new javax.swing.JTextArea();
        AddSong = new javax.swing.JButton();
        volumen = new javax.swing.JSlider();
        Imagen = new javax.swing.JLabel();
        Select = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnplay.setText("Play");
        btnplay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnplayActionPerformed(evt);
            }
        });

        btnpausa.setText("Pausa");
        btnpausa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnpausaActionPerformed(evt);
            }
        });

        btnstop.setText("Stop");
        btnstop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnstopActionPerformed(evt);
            }
        });

        Cancion.setColumns(20);
        Cancion.setRows(5);
        jScrollPane1.setViewportView(Cancion);

        AddSong.setText("Add Song");
        AddSong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddSongActionPerformed(evt);
            }
        });

        volumen.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                volumenMousePressed(evt);
            }
        });

        Imagen.setBackground(new java.awt.Color(255, 255, 255));

        Select.setText("Select");
        Select.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SelectActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnpausa, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(27, 27, 27)
                                .addComponent(btnplay, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(37, 37, 37)
                                .addComponent(Select)
                                .addGap(18, 18, 18)
                                .addComponent(btnstop, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(61, 61, 61)
                        .addComponent(volumen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(54, 54, 54)
                        .addComponent(AddSong))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addComponent(Imagen, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(42, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnstop)
                                .addComponent(Select))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnpausa)
                                .addComponent(btnplay)))
                        .addGap(18, 18, 18)
                        .addComponent(volumen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(AddSong)
                        .addGap(23, 23, 23)
                        .addComponent(Imagen, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(189, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnplayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnplayActionPerformed
        // TODO add your handling code here:
        reproducirCancion();
    }//GEN-LAST:event_btnplayActionPerformed

    private void btnpausaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnpausaActionPerformed
        // TODO add your handling code here:
        pausa();
    }//GEN-LAST:event_btnpausaActionPerformed

    private void btnstopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnstopActionPerformed
        // TODO add your handling code here:
        stop();
    }//GEN-LAST:event_btnstopActionPerformed

    private void AddSongActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddSongActionPerformed
        // TODO add your handling code here:
         agregarCancion();
    }//GEN-LAST:event_AddSongActionPerformed

    private void volumenMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_volumenMousePressed
        // TODO add your handling code here:
        vol();
    }//GEN-LAST:event_volumenMousePressed

    private void SelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SelectActionPerformed
        // TODO add your handling code here:
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY); 
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos de música MP3", "mp3");
        fileChooser.setFileFilter(filter);
        int result = fileChooser.showOpenDialog(this);  
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();       
            if (currentSongIndex < playlist.length - 1) {
                currentSongIndex++;
                playlist[currentSongIndex] = selectedFile.getAbsolutePath();
                rutaCancionActual = selectedFile.getAbsolutePath();
                System.out.println("Ruta de la canción seleccionada: " + rutaCancionActual);
                currentSong = new NodoPlay(rutaCancionActual, selectedFile.getName(), "", ""); 
                try {
                    try {
                        AudioFile file = AudioFileIO.read(selectedFile);
                        Artwork artwork = file.getTag().getFirstArtwork();
                        if (artwork != null) {
                            byte[] imageData = artwork.getBinaryData();
                            ImageIcon icon = new ImageIcon(imageData);
                            Imagen.setIcon(icon);
                        } else {
                            ImageIcon defaultIcon = new ImageIcon("imagenes/musica.jpg");
                            Imagen.setIcon(defaultIcon);
                        }
                    } catch (IOException | CannotReadException | TagException | InvalidAudioFrameException e) {
                        e.printStackTrace();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                System.out.println("No se pueden agregar más canciones, la lista está llena.");
            }
        } else {
            System.out.println("No se ha seleccionado ninguna canción.");
        }
    }//GEN-LAST:event_SelectActionPerformed

    /**
     * @param args the command line arguments
     */
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
            java.util.logging.Logger.getLogger(ReproductorMusical.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ReproductorMusical.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ReproductorMusical.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ReproductorMusical.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ReproductorMusical().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AddSong;
    private javax.swing.JTextArea Cancion;
    private javax.swing.JLabel Imagen;
    private javax.swing.JButton Select;
    private javax.swing.JButton btnpausa;
    private javax.swing.JButton btnplay;
    private javax.swing.JButton btnstop;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSlider volumen;
    // End of variables declaration//GEN-END:variables
}
