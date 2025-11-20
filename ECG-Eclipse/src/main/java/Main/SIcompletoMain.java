package Main;

import Clases.*;
import Parser.InputParser;
import Parser.OutputParser;
import java.io.*;
import java.util.*;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

public class SIcompletoMain {
    
    public static void main(String[] args) throws Exception {
        
        String dirEnt = "C:\\Users\\Usuario\\OneDrive\\Escritorio\\DSIN\\Electrocardiograma\\ECG_predictor\\ECG-Eclipse\\inputs";
        String dirSal = "C:\\Users\\Usuario\\OneDrive\\Escritorio\\DSIN\\Electrocardiograma\\ECG_predictor\\ECG-Eclipse\\salida";

        try {
            // Inicializar Drools
            KieServices ks = KieServices.Factory.get();
            KieContainer kcontainer = ks.getKieClasspathContainer();
            
            // Si hay argumentos, reescribir valores
            if (args.length >= 1) dirEnt = args[0];
            if (args.length >= 2) dirSal = args[1];
            
            File inputDir = new File(dirEnt);
            File[] archivos = inputDir.listFiles((d, name) -> name.endsWith(".ecg"));
            
            // Crear directorio de salida
            new File(dirSal).mkdirs();
            
            // Mapa para archivo consolidado
            Map<String, List<Diagnostico_Inferido>> todosLosResultados = new LinkedHashMap<>();
            
            if (archivos == null || archivos.length == 0) {
                System.out.println("No se encontraron archivos .ecg en: " + dirEnt);
                return;
            }

            System.out.println("PROCESANDO " + archivos.length + " ARCHIVOS ECG");

            // Procesar cada archivo
            for (File file : archivos) {
                System.out.println("Procesando: " + file.getName());
                
                // Crear NUEVA sesión para CADA archivo
                KieSession kSession = kcontainer.newKieSession("ksession-Rules-dsi");
                
                // Parsear ondas
                List<Onda> ondas = InputParser.parseFile(file);
                System.out.println("  Ondas encontradas: " + ondas.size());
                
                // Insertar ondas en la sesión
                for (Onda onda : ondas) {
                    kSession.insert(onda);
                }
                
                // Ejecutar reglas
                kSession.getAgenda().getAgendaGroup("inferencia").setFocus();
                kSession.fireAllRules();
                
                kSession.getAgenda().getAgendaGroup("diagnosticos").setFocus();
                kSession.fireAllRules();

                kSession.getAgenda().getAgendaGroup("report").setFocus();
                kSession.fireAllRules();

              
                // Recolectar diagnósticos 
                List<Diagnostico_Inferido> diagnosticos = new ArrayList<>();
                
                for (Object obj : kSession.getObjects()) {
                    if (obj instanceof Diagnostico_Inferido) 
                        diagnosticos.add((Diagnostico_Inferido) obj);
                }
                
                // Nombre del fichero sin extensión
                String nombreFichero = file.getName().replace(".ecg", "");
                
                // Guardar para archivo consolidado
                todosLosResultados.put(nombreFichero, diagnosticos);
                
                // **GENERAR ARCHIVO INDIVIDUAL**
                OutputParser.escribirSalidaIndividual(nombreFichero, diagnosticos, dirSal);
                System.out.println("Archivo generado: " + nombreFichero + ".salida.txt");
                
                // Limpiar sesión
                kSession.dispose();
                System.out.println();
            }
            
            // **GENERAR ARCHIVO CONSOLIDADO**
            System.out.println("GENERANDO ARCHIVO CONSOLIDADO");
            OutputParser.escribirSalidaUnica(todosLosResultados, dirSal, "todo.salida.txt");
            System.out.println("Archivo consolidado generado: todo.salida.txt\n");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
