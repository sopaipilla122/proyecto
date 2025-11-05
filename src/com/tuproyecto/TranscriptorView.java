// Archivo: src/com/tuproyecto/TranscriptorView.java
package com.tuproyecto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class TranscriptorView extends JFrame {
    // Componentes de la Interfaz
    private final JTextArea outputArea;
    private final JButton transcribirButton;
    private final JButton selectFileButton;
    private final JComboBox<String> modelComboBox;
    private final JLabel statusLabel;
    private final JTextField filePathField;

    private String selectedFilePath;

    // Referencia al controlador/servicio
    private final TranscriptionService service;

    public TranscriptorView() {
        // --- 1. Configurar la Ventana ---
        setTitle("Transcriptor");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- 2. Crear Componentes ---
        
        // Panel Superior
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        selectFileButton = new JButton("Seleccionar Archivo...");
        filePathField = new JTextField("Ningún archivo seleccionado.");
        filePathField.setEditable(false);
        
        JPanel modelPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        modelPanel.add(new JLabel("Modelo:"));
        String[] models = {"tiny", "base", "small", "medium", "large"};
        modelComboBox = new JComboBox<>(models);
        modelComboBox.setSelectedItem("small");
        
        topPanel.add(selectFileButton, BorderLayout.WEST);
        topPanel.add(filePathField, BorderLayout.CENTER);
        topPanel.add(modelPanel, BorderLayout.EAST);

        // Panel Central
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        
        // Panel Inferior
        JPanel bottomPanel = new JPanel(new BorderLayout());
        transcribirButton = new JButton("Transcribir");
        statusLabel = new JLabel("Listo.");
        
        bottomPanel.add(transcribirButton, BorderLayout.CENTER);
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);

        // --- 3. Añadir Paneles a la Ventana ---
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(mainPanel);

        // --- 4. Crear el Servicio y Asignar Acciones ---
        // Pasa una referencia de sí misma (la Vista) al Servicio.
        this.service = new TranscriptionService(this);
        addListeners();
    }

    private void addListeners() {
        // Acción del botón "Seleccionar Archivo..."
        selectFileButton.addActionListener((ActionEvent e) -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Selecciona un archivo de audio");
            int result = fileChooser.showOpenDialog(this);
            
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                selectedFilePath = selectedFile.getAbsolutePath();
                filePathField.setText(selectedFilePath);
                statusLabel.setText("Archivo '" + selectedFile.getName() + "' seleccionado.");
            }
        });

        // Acción del botón "Transcribir"
        // Fíjate qué simple es ahora. Solo le dice al servicio "empieza".
        transcribirButton.addActionListener((ActionEvent e) -> {
            service.startTranscription();
        });
    }

    // --- Métodos "Getters" para que el Servicio obtenga datos de la Vista ---

    public String getSelectedFilePath() {
        return selectedFilePath;
    }

    public String getSelectedModel() {
        return (String) modelComboBox.getSelectedItem();
    }
    
    // --- Métodos "Setters" para que el Servicio actualice la Vista ---
    
    public void appendOutput(String text) {
        outputArea.append(text + "\n");
    }
    
    public void clearOutput() {
        outputArea.setText("");
    }
    
    public void setStatus(String text) {
        statusLabel.setText(text);
    }
    
    public void setControlsEnabled(boolean enabled) {
        transcribirButton.setEnabled(enabled);
        selectFileButton.setEnabled(enabled);
        modelComboBox.setEnabled(enabled);
    }
}