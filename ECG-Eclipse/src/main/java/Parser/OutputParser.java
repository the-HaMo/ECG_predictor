package Parser;

import Clases.Diagnostico_Inferido;

import java.io.*;
import java.util.*;

public class OutputParser {

	public static void escribirSalidaIndividual(String nombreFichero,List<Diagnostico_Inferido> diagnosticos, String dirSalida) throws IOException {

	    String nombreArchivoSalida = nombreFichero + ".salida.txt";
	    File archivoSalida = new File(dirSalida, nombreArchivoSalida);

	    try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivoSalida))) {

	        // Cabecera
	        bw.write("DIAGNOSTICO ECG\n");
	        bw.write("Archivo: " + nombreFichero + ".ecg\n\n");

	        // Diagnósticos inferidos directamente (sin análisis adicional)
	        bw.write("DIAGNOSTICOS INFERIDOS:\n");

	        if (diagnosticos.isEmpty()) {
	            bw.write("  - No se genero ningun diagnostico.\n");
	        } else {
	            int contador = 1;
	            for (Diagnostico_Inferido di : diagnosticos) {
	                bw.write("  [" + contador + "] " + di.getOutline() + "\n");
	                contador++;
	            }
	        }

	        bw.write("\n");
	    }
	}


	public static void escribirSalidaUnica(
	        Map<String, List<Diagnostico_Inferido>> todosLosResultados,
	        String dirSalida,
	        String nombreArchivo) throws IOException {

	    File archivoSalida = new File(dirSalida, nombreArchivo);

	    try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivoSalida))) {

	        for (Map.Entry<String, List<Diagnostico_Inferido>> entry : todosLosResultados.entrySet()) {

	            String nombreFichero = entry.getKey();
	            List<Diagnostico_Inferido> diagnosticos = entry.getValue();

	            bw.write("--- Archivo: " + nombreFichero + ".ecg ---\n");
	            bw.write("Diagnosticos:\n");

	            // Cada diagnóstico tiene un tipo y una descripción
	            for (Diagnostico_Inferido di : diagnosticos) {
	                bw.write(" - " + di.getOutline() + "\n");
	            }

	            bw.write("\n");
	        }

	        bw.write("Total de archivos procesados: " + todosLosResultados.size() + "\n");
	    }

	    System.out.println("CONTENIDO DE " + nombreArchivo);
	}
}