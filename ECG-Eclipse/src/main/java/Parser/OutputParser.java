package Parser;

import java.io.*;
import java.util.*;
import Clases.Diagnostico_Inferido;
import Clases.Diagnostico;

public class OutputParser {
    public static void escribirSalidaUnica(Map<String, List<Diagnostico_Inferido>> resultados, 
    		String directorioSalida, String nombreArchivo) throws IOException {
        
        new File(directorioSalida).mkdirs();
        File archivoSalida = new File(directorioSalida, nombreArchivo);
        
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivoSalida))) {
            bw.write("SISTEMA DE DIAGNÓSTICO AUTOMÁTICO DE ECG\n");
            bw.write("\n");
            
            int contadorFicheros = 0;
            
            for (Map.Entry<String, List<Diagnostico_Inferido>> entry : resultados.entrySet()) {
                contadorFicheros++;
                String nombreFichero = entry.getKey();
                List<Diagnostico_Inferido> diagnosticos = entry.getValue();
                
                bw.write("\n");
                bw.write("Fichero: " + nombreFichero + ".ecg\n");
                
                if (diagnosticos.isEmpty()) {
                    bw.write("Diagnóstico: Sin diagnóstico detectado\n");
                } else {
                    bw.write("Diagnóstico(s):\n");
                    for (Diagnostico_Inferido diag : diagnosticos) {
                        bw.write("  - " + diag.getResultados().toString() + "\n");
                    }
                }
                
                bw.write("\n");
            }
            
            bw.write("Total de ficheros procesados: " + contadorFicheros + "\n");
        }
        
        System.out.println("\n Archivo de salida generado: " + archivoSalida.getAbsolutePath());
    }
    
    public static void escribirSalidaUnica(Map<String, List<Diagnostico_Inferido>> resultados, 
                                            String directorioSalida) throws IOException {
        escribirSalidaUnica(resultados, directorioSalida, "resultados.txt");
    }
}
