package Main;
import Clases.*;
import Parser.InputParser;

import java.io.*;
import java.util.*;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

public class LectorFicheroPrueba {
	
	public static void main(String[] args) throws Exception {
	    
		String dirEnt = "C:\\Users\\Usuario\\OneDrive\\Escritorio\\DSIN\\Electrocardiograma\\ECG_predictor\\inputs";
        String dirSal = "C:\\Users\\Usuario\\OneDrive\\Escritorio\\DSIN\\ficherosSalida";

        try {
            // Inicializar Drools
            KieServices ks = KieServices.Factory.get();
            KieContainer kcontainer = ks.getKieClasspathContainer();
            
            // Si hay argumentos, reescribir valores
            if (args.length >= 1) dirEnt = args[0];
            if (args.length >= 2) dirSal = args[1];

            File inputDir = new File(dirEnt);
            File[] archivos = inputDir.listFiles((d, name) -> name.endsWith(".ecg"));
            
            if (archivos == null || archivos.length == 0) {
                System.out.println("No se encontraron archivos .ecg");
                return;
            }

            for (File file : archivos) {
                System.out.println("=== Procesando: " + file.getName() + " ===");
                
                // Crear NUEVA sesión para CADA archivo
                KieSession kSession = kcontainer.newKieSession("ksession-Rules-dsi");
                List<Onda> ondas = InputParser.parseFile(file);
                System.out.println("Ondas encontradas: " + ondas.size());
                
                for (Onda onda : ondas) {
                    kSession.insert(onda);
                }
                // Ejecutar reglas
                System.out.println("Ejecutando reglas Drools...(Solo Imprime Ondas)");
                int ejec = kSession.fireAllRules();
                System.out.println("Reglas ejecutadas: " + ejec);
                
                // Limpiar para el próximo archivo
                kSession.dispose();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
