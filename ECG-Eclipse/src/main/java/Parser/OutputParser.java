package Parser;

import Clases.Diagnostico_Inferido;
import Clases.Analisis_Señal; 
import Clases.Diagnostico;

import java.io.*;
import java.util.*;

public class OutputParser {


    public static void escribirSalidaIndividual(
            String nombreFichero, 
            List<Diagnostico_Inferido> diagnosticos,
            Analisis_Señal analisisSenal,
            String dirSalida
    ) throws IOException {
        
        String nombreArchivoSalida = nombreFichero + ".salida.txt";
        File archivoSalida = new File(dirSalida, nombreArchivoSalida);
        
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivoSalida))) {
            
            // Cabecera

            bw.write("DIAGNOSTICO ECG\n");
            bw.write("Archivo: " + nombreFichero + ".ecg\n");
            
            // Seccion 1: Analisis del ritmo cardiaco
            if (analisisSenal != null) {
                bw.write("ANALISIS DEL RITMO CARDIACO:\n");
                bw.write("  - Ritmo cardiaco: " + analisisSenal.getRitmo_cardiaco() + " pul/min\n");
                bw.write("  - Numero de ciclos: " + analisisSenal.getNumciclos() + "\n");
                
                // Diagnostico del ritmo
                int ritmo = analisisSenal.getRitmo_cardiaco();
                if (ritmo > 100) {
                    bw.write("  - Diagnostico: Taquicardia Sinusal\n");
                    bw.write("    Justificacion: " + Diagnostico.TAQUICARDIA_SINUSAL.getDescripcion() + "\n");
                } else if (ritmo < 60) {
                    bw.write("  - Diagnostico: Bradicardia Sinusal\n");
                    bw.write("    Justificacion: " + Diagnostico.BRADICARDIA_SINUSAL.getDescripcion() + "\n");
                } else {
                    bw.write("  - Diagnostico: Ritmo Normal\n");
                }
                bw.write("\n");
            }
            
            // Seccion 2: Diagnosticos detectados (CON JUSTIFICACIONES)
            bw.write("DIAGNOSTICOS DETECTADOS:\n");
          
            if (diagnosticos.isEmpty()) {
                bw.write("\n  [*] Paciente Sano\n");
                bw.write("      Justificacion: " + Diagnostico.SANO.getDescripcion() + "\n\n");
            } else {
                Set<Diagnostico> diagnosticosUnicos = new HashSet<>();
                int contador = 1;
                
                for (Diagnostico_Inferido di : diagnosticos) {
                    for (Diagnostico d : di.getResultados()) {
                        if (diagnosticosUnicos.add(d)) {
                            // Nombre del diagnostico
                            bw.write("\n  [" + contador + "] " + formatearDiagnostico(d) + "\n");
                            
                            // Justificacion tecnica (NUEVO)
                            bw.write("      Justificacion: " + d.getDescripcion() + "\n");
                            
                            contador++;
                        }
                    }
                }
                bw.write("\n");
            }
        }
    }
    

    public static void escribirSalidaUnica(
            Map<String, List<Diagnostico_Inferido>> todosLosResultados,
            String dirSalida,
            String nombreArchivo
    ) throws IOException {
        
        File archivoSalida = new File(dirSalida, nombreArchivo);
        
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivoSalida))) {
            
            // Cabecera
            bw.write("RESUMEN DE DIAGNOSTICOS - TODOS LOS ARCHIVOS\n");
            
            // Iterar por cada archivo procesado
            for (Map.Entry<String, List<Diagnostico_Inferido>> entry : todosLosResultados.entrySet()) {
                String nombreFichero = entry.getKey();
                List<Diagnostico_Inferido> diagnosticos = entry.getValue();
                
                bw.write("--- Archivo: " + nombreFichero + ".ecg ---\n");
                bw.write("Diagnosticos: ");
                
                if (diagnosticos.isEmpty()) {
                    bw.write("Paciente Sano");
                } else {
                    Set<Diagnostico> diagnosticosUnicos = new HashSet<>();
                    List<String> listaDiagnosticos = new ArrayList<>();
                    
                    for (Diagnostico_Inferido di : diagnosticos) {
                        for (Diagnostico d : di.getResultados()) {
                            if (diagnosticosUnicos.add(d)) {
                                listaDiagnosticos.add(formatearDiagnostico(d));
                            }
                        }
                    }
                    
                    bw.write(String.join(", ", listaDiagnosticos));
                }
                
                bw.write("\n\n");
            }
            
           
            bw.write("Total de archivos procesados: " + todosLosResultados.size() + "\n");
        }
        
        // Mostrar por consol
        System.out.println("CONTENIDO DE " + nombreArchivo);
        mostrarArchivoEnConsola(archivoSalida);
    }
    
    private static void mostrarArchivoEnConsola(File archivo) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                System.out.println(linea);
            }
        }
    }
    

    private static String formatearDiagnostico(Diagnostico d) {
        String nombre = d.name().replace("_", " ");
        String[] palabras = nombre.toLowerCase().split(" ");
        StringBuilder resultado = new StringBuilder();
        for (String palabra : palabras) {
            if (!palabra.isEmpty()) {
                resultado.append(Character.toUpperCase(palabra.charAt(0)))
                         .append(palabra.substring(1))
                         .append(" ");
            }
        }
        return resultado.toString().trim();
    }
}
