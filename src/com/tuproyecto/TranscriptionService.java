// Archivo: src/com/tuproyecto/TranscriptionService.java
package com.tuproyecto;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class TranscriptionService {

    // Referencia a la Vista para poder actualizarla
    private final TranscriptorView view;

    public TranscriptionService(TranscriptorView view) {
        this.view = view;
    }

    /**
     * Llamado por la Vista cuando se presiona el botón "Transcribir".
     */
    public void startTranscription() {
        // 1. Obtener los datos de la Vista
        String filePath = view.getSelectedFilePath();
        String model = view.getSelectedModel();

        // 2. Validar
        if (filePath == null || filePath.isEmpty()) {
            JOptionPane.showMessageDialog(view,
                    "Por favor, selecciona un archivo primero.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 3. Preparar la GUI para el trabajo
        view.setControlsEnabled(false);
        view.setStatus("Transcribiendo con modelo '" + model + "'...");
        view.clearOutput();

        // 4. Crear y ejecutar el Worker en segundo plano
        TranscriptionWorker worker = new TranscriptionWorker(filePath, model);
        worker.execute();
    }

    /**
     * El SwingWorker ahora es una clase interna del Servicio.
     * Tiene acceso a la 'view' para publicar resultados.
     */
    private class TranscriptionWorker extends SwingWorker<Void, String> {

        private final String filePath;
        private final String model;

        public TranscriptionWorker(String filePath, String model) {
            this.filePath = filePath;
            this.model = model;
        }

        @Override
        protected Void doInBackground() throws Exception {
            
            // Usamos el lanzador 'py' para seleccionar la versión 3.12
            String[] command = {"py", "-3.12", "-m", "whisper", filePath, "--model", model};

            publish("Ejecutando: " + String.join(" ", command) + "\n");

            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    publish(line); // Envía la línea al método process()
                }
            } // 's' errante eliminada

            int exitCode = process.waitFor();
            
            // --- ¡ERROR CORREGIDO AQUÍ! ---
            // La línea anterior tenía un error de comillas. Esta es la correcta.
            publish("\n--- Proceso terminado con código: " + exitCode + " ---");
            return null;
        }

        @Override
        protected void process(List<String> chunks) {
            // Este método se ejecuta en el hilo de la GUI
            // y actualiza la vista de forma segura
            for (String line : chunks) {
                view.appendOutput(line);
            }
        }

        @Override
        protected void done() {
            // Este método se ejecuta en el hilo de la GUI cuando todo termina
            try {
                get(); // Captura excepciones de doInBackground
                view.setStatus("¡Transcripción completada!");
            } catch (Exception e) {
                view.setStatus("Error durante la transcripción.");
                view.appendOutput("\n--- ERROR ---\n");
                view.appendOutput(e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
            }
            
            // Re-activa los botones de la vista
            view.setControlsEnabled(true);
        }
    }
}