// Archivo: src/com/tuproyecto/Main.java
package com.tuproyecto;

import javax.swing.SwingUtilities;

public class Main {
    
    public static void main(String[] args) {
        // Ejecuta la creación de la GUI en el hilo correcto de Swing
        SwingUtilities.invokeLater(() -> {
            TranscriptorView view = new TranscriptorView();
            view.setVisible(true);
        });
    }
}