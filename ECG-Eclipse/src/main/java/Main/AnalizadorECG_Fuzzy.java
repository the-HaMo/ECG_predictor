package Main;

import Clases.*;
import Fuzzy.AuxilarFuzzy;
import Parser.InputParser;
import java.io.*;
import java.util.*;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;

public class AnalizadorECG_Fuzzy {
    
    private static FIS fis;
    
    public static void main(String[] args) throws Exception {
        
        String dirEnt = "inputs";
        String dirSal = "salidaFuzzy";
        
        if (args.length >= 1) dirEnt = args[0];
        if (args.length >= 2) dirSal = args[1];
        
        System.out.println("Directorio entrada: " + new File(dirEnt).getAbsolutePath());
        System.out.println("Directorio salida: " + new File(dirSal).getAbsolutePath());
        
        System.out.println("\nCargando sistema fuzzy...");
        fis = FIS.load("src/main/resources/Reglas/diagnostico_ecg.fcl", true);
        
        if (fis == null) {
            System.err.println("ERROR: No se pudo cargar el sistema fuzzy");
            return;
        }
        System.out.println("Sistema fuzzy cargado correctamente\n");
        
        new File(dirSal).mkdirs();
        
        File inputDir = new File(dirEnt);
        File[] archivos = inputDir.listFiles((d, name) -> name.endsWith(".ecg"));
        
        if (archivos == null || archivos.length == 0) {
            System.out.println("No se encontraron archivos .ecg en: " + dirEnt);
            return;
        }
        
        System.out.println("PROCESANDO " + archivos.length + " ARCHIVOS ECG CON LOGICA DIFUSA\n");
        
        for (File file : archivos) {
            System.out.println("Procesando: " + file.getName());
            
            try {
                List<Onda> ondas = InputParser.parseFile(file);
                System.out.println("Ondas encontradas: " + ondas.size());
                
                AuxilarFuzzy clOndas = AuxilarFuzzy.clasifica(ondas); 
                
                System.out.println(
                	    "Ondas P: " + clOndas.p.size() +
                	    ", Q: " + clOndas.q.size() + 
                	    ", R: " + clOndas.r.size() +
                	    ", S: " + clOndas.s.size() +
                	    ", T: " + clOndas.t.size()
                	);
              
                // Identificar ciclos completos
                List<CicloECG> ciclos = AuxilarFuzzy.identificarCiclos(clOndas.p, clOndas.q, clOndas.r, clOndas.s, clOndas.t);
                System.out.println("Ciclos completos identificados: " + ciclos.size());
                
                // CREAR CARPETA INDIVIDUAL PARA ESTE ARCHIVO
                String nombreBase = file.getName().replace(".ecg", "");
                String carpetaSalida = nombreBase + "_salida";
                File dirArchivoSalida = new File(dirSal, carpetaSalida);
                dirArchivoSalida.mkdirs();
                
                String nombreTxt = nombreBase + "_diagnostico.txt";
                BufferedWriter bw = new BufferedWriter(new FileWriter(new File(dirArchivoSalida, nombreTxt)));
                
                bw.write("DIAGNOSTICO ECG CON LOGICA DIFUSA\n");
                bw.write("Archivo analizado: " + file.getName() + "\n");
                bw.write("Fecha: " + new java.util.Date() + "\n");
                bw.write("Ciclos analizados: " + ciclos.size() + "\n");
                
                boolean enfermedadDetectada = false;
                List<String> diagnosticosPositivos = new ArrayList<>();
                
                // ANALIZAR RITMO CARDIACO
                int ritmo = AuxilarFuzzy.calcularRitmo(clOndas.q);
                
                bw.write("1. ANALISIS DE RITMO CARDIACO\n");
                bw.write("Se calcula la frecuencia cardiaca a partir de los intervalos entre ondas Q,\n");
                bw.write("detectando la posible presencia de bradicardia (disminucion) o taquicardia (aumento).\n\n");
                
                if (ritmo > 0) {
                    System.out.println("\n[RITMO] Calculado: " + ritmo + " pul/min");
                    bw.write("Ritmo cardiaco calculado: " + ritmo + " pul/min\n");
                    
                    FunctionBlock fb = fis.getFunctionBlock("Analisis_Ritmo_Cardiaco");
                    if (fb != null) {
                        fb.setVariable("ritmo", ritmo);
                        fb.evaluate();
                        
                        double nivelBradi = fb.getVariable("nivel_bradicardia").getValue();
                        double nivelTaqui = fb.getVariable("nivel_taquicardia").getValue();
                        double estadoSalud = fb.getVariable("estado_salud_ritmo").getValue();
                        
                        bw.write("Resultados fuzzy:\n");
                        bw.write("- Nivel bradicardia: " + String.format("%.3f", nivelBradi) + "\n");
                        bw.write("- Nivel taquicardia: " + String.format("%.3f", nivelTaqui) + "\n");
                        bw.write("- Estado salud ritmo: " + String.format("%.3f", estadoSalud) + "\n\n");
                        
                        if (estadoSalud >= 0.15) {
                            enfermedadDetectada = true;
                            
                            String diagnostico;
                            if (nivelBradi > nivelTaqui) {
                                String severidad = interpretarSeveridad(nivelBradi);
                                diagnostico = "BRADICARDIA " + severidad;
                            } else {
                                String severidad = interpretarSeveridad(nivelTaqui);
                                diagnostico = "TAQUICARDIA " + severidad;
                            }
                            
                            diagnosticosPositivos.add(diagnostico);
                            
                            bw.write("Interpretacion:\n");
                            bw.write("El ritmo se encuentra fuera de la zona fisiologica considerada normal.\n");
                            bw.write("Se detecta " + diagnostico.toLowerCase() + " segun el analisis fuzzy.\n\n");
                            bw.write("DIAGNOSTICO: " + diagnostico + "\n");
                            bw.write("Justificacion: Ritmo=" + ritmo + " pul/min, Estado_salud=" + String.format("%.3f", estadoSalud) + " (>=0.15)\n\n");
                            
                            System.out.println("DETECTADO: " + diagnostico);
                            
                            if (nivelBradi > nivelTaqui) {
                                JFuzzyChart.get().chart(fb.getVariable("nivel_bradicardia"), 
                                                        fb.getVariable("nivel_bradicardia").getDefuzzifier(), true);
                            } else {
                                JFuzzyChart.get().chart(fb.getVariable("nivel_taquicardia"), 
                                                        fb.getVariable("nivel_taquicardia").getDefuzzifier(), true);
                            }
                        } else {
                            bw.write("Interpretacion:\n");
                            bw.write("El ritmo esta dentro del rango normal. El sistema fuzzy descarta\n");
                            bw.write("alteraciones significativas en el ritmo cardiaco.\n\n");
                            bw.write("DIAGNOSTICO: RITMO NORMAL\n\n");
                        }
                    }
                } else {
                    bw.write("No se pudo calcular (insuficientes ondas Q)\n\n");
                }
                
                //ANALIZAR ISQUEMIA
                bw.write("2. ANALISIS DE ISQUEMIA CORONARIA\n");
                bw.write("Se analiza si los ciclos evaluados presentan inversion de onda T y descenso\n");
                bw.write("del segmento ST, configuraciones tipicas de isquemia miocardica.\n\n");
                
                if (!ciclos.isEmpty()) {
                    System.out.println("\nISQUEMIA: Analizando ciclos completos...");
                    
                    boolean isquemiaDetectada = false;
                    FunctionBlock fbIsq = fis.getFunctionBlock("ECG_Isquemia_Coronaria");
                    
                    if (fbIsq != null) {
                        
                        for (int i = 0; i < Math.min(3, ciclos.size()); i++) {
                            CicloECG ciclo = ciclos.get(i);
                            double stamp = AuxilarFuzzy.calcularAmplitudST(ciclo);
                            double duracionST = ciclo.t.getStart() - ciclo.s.getFin();
                            double tpeak = ciclo.t.getPeak();
                            
                            if (tpeak > -12 && tpeak < -5 && stamp > -6 && stamp < -1) {
                                
                                fbIsq.setVariable("tpeak", tpeak);
                                fbIsq.setVariable("stamp", stamp);
                                fbIsq.setVariable("duracion_st", duracionST);
                                fbIsq.evaluate();
                                
                                double riesgo = fbIsq.getVariable("riesgo_isquemia_coronaria").getValue();
                                
                                if (riesgo >= 0.3 && !isquemiaDetectada) {
                                    isquemiaDetectada = true;
                                    enfermedadDetectada = true;
                                    
                                    String severidad = interpretarSeveridad(riesgo);
                                    String diagnostico = "ISQUEMIA CORONARIA " + severidad;
                                    diagnosticosPositivos.add(diagnostico);
                                    
                                    bw.write("Resultados por ciclo:\n");
                                    bw.write("Ciclo " + (i+1) + ": T peak=" + String.format("%.2f", tpeak) + 
                                            " mV, ST=" + String.format("%.2f", stamp) + " mV, Duracion ST=" + 
                                            String.format("%.0f", duracionST) + " ms\n");
                                    bw.write("               Riesgo isquemia: " + String.format("%.3f", riesgo) + "\n");
                                    
                                    bw.write("\nInterpretacion:\n");
                                    bw.write("Se detecta inversion de onda T y descenso relevante de ST en ciclo " + (i+1) + ".\n");
                                    bw.write("El riesgo fuzzy de isquemia es " + severidad.toLowerCase() + ".\n\n");
                                    bw.write("DIAGNOSTICO: " + diagnostico + "\n");
                                    bw.write("Justificacion: T_peak=" + String.format("%.2f", tpeak) + " mV (entre -12 y -5), ST_amp=" + String.format("%.2f", stamp) + " mV (entre -6 y -1), Riesgo=" + String.format("%.3f", riesgo) + "\n\n");
                                    
                                    System.out.println("DETECTADO: " + diagnostico);
                                    
                                    JFuzzyChart.get().chart(fbIsq.getVariable("riesgo_isquemia_coronaria"), 
                                                            fbIsq.getVariable("riesgo_isquemia_coronaria").getDefuzzifier(), true);
                                }
                            }
                        }
                        
                        if (!isquemiaDetectada) {
                            bw.write("Interpretacion:\n");
                            bw.write("Ningun ciclo presenta simultaneamente los hallazgos tipicos de isquemia.\n");
                            bw.write("El diagnostico fuzzy es negativo.\n\n");
                            bw.write("DIAGNOSTICO: No se detecto isquemia coronaria\n\n");
                        }
                    }
                } else {
                    bw.write("No hay ciclos completos para analizar.\n\n");
                }
                
                //ANALIZAR HIPOPOTASEMIA
                bw.write("3. ANALISIS DE HIPOPOTASEMIA\n");
                bw.write("Se busca onda T muy invertida (< -12 mV) acompanada de ST muy descendido (< -0.5 mV),\n");
                bw.write("patron tipico en hipopotasemia.\n\n");
                
                if (!ciclos.isEmpty()) {
                    boolean hipopotasemiaDetectada = false;
                    FunctionBlock fbHipo = fis.getFunctionBlock("ECG_Hipopotasemia");
                    
                    if (fbHipo != null) {
                        for (int i = 0; i < ciclos.size(); i++) {
                            CicloECG ciclo = ciclos.get(i);
                            double stamp = AuxilarFuzzy.calcularAmplitudST(ciclo);
                            double tpeak = ciclo.t.getPeak();
                            
                           
                            if (tpeak < -12 && stamp < -0.5) {
                                
                                fbHipo.setVariable("tpeak", tpeak);
                                fbHipo.setVariable("stamp", stamp);
                                fbHipo.evaluate();
                                
                                double prob = fbHipo.getVariable("probabilidad_hipopotasemia").getValue();
                                
                                if (prob >= 0.3 && !hipopotasemiaDetectada) {
                                    hipopotasemiaDetectada = true;
                                    enfermedadDetectada = true;
                                    
                                    String severidad = interpretarSeveridad(prob);
                                    String diagnostico = "HIPOPOTASEMIA " + severidad;
                                    diagnosticosPositivos.add(diagnostico);
                                    
                                    bw.write("Resultados por ciclo:\n");
                                    bw.write("   Ciclo " + (i+1) + ": T=" + String.format("%.2f", tpeak) + 
                                            " mV, ST=" + String.format("%.2f", stamp) + " mV\n");
                                    bw.write("              Probabilidad: " + String.format("%.3f", prob) + "\n");
                                    
                                    bw.write("\nInterpretacion:\n");
                                    bw.write("Se detecta inversion extrema de onda T y ST muy descendido.\n");
                                    bw.write("La probabilidad fuzzy de hipopotasemia es " + severidad.toLowerCase() + ".\n\n");
                                    bw.write("DIAGNOSTICO: " + diagnostico + "\n");
                                    bw.write("Justificacion: T_peak=" + String.format("%.2f", tpeak) + " mV (<-12), ST_amp=" + String.format("%.2f", stamp) + " mV (<-0.5), Probabilidad=" + String.format("%.3f", prob) + "\n\n");
                                    
                                    System.out.println("DETECTADO: " + diagnostico);
                                    
                                    JFuzzyChart.get().chart(fbHipo.getVariable("probabilidad_hipopotasemia"), 
                                                            fbHipo.getVariable("probabilidad_hipopotasemia").getDefuzzifier(), true);
                                }
                            }
                        }
                        
                        if (!hipopotasemiaDetectada) {
                            bw.write("Interpretacion:\n");
                            bw.write("No se encuentra ningun ciclo con T < -12 mV y ST < -0.5 mV.\n");
                            bw.write("Todos los indicadores de hipopotasemia estan ausentes.\n\n");
                            bw.write("DIAGNOSTICO: No se detecto hipopotasemia\n\n");
                        }
                    }
                } else {
                    bw.write("No hay ciclos completos para analizar.\n\n");
                }
                
                //ANALIZAR INFARTO AGUDO DE MIOCARDIO
                bw.write("4. ANALISIS DE INFARTO AGUDO DE MIOCARDIO\n");
                bw.write("Se evalua la elevacion significativa del segmento ST (>0.1 mV) junto con\n");
                bw.write("valores elevados de onda T (>0.6 mV), patron tipico de infarto agudo.\n\n");
                
                if (!ciclos.isEmpty()) {
                    boolean infartoDetectado = false;
                    FunctionBlock fbIAM = fis.getFunctionBlock("ECG_infarto_agudo_de_miocardio");
                    
                    if (fbIAM != null) {
                        for (int i = 0; i < Math.min(3, ciclos.size()); i++) {
                            CicloECG ciclo = ciclos.get(i);
                            double stamp = AuxilarFuzzy.calcularAmplitudST(ciclo);
                            double tpeak = ciclo.t.getPeak();
                            
                          
                            if (stamp > 0.1 && tpeak > 0.6) {
                                
                                fbIAM.setVariable("stamp", stamp);
                                fbIAM.setVariable("tpeak", tpeak);
                                fbIAM.evaluate();
                                
                                net.sourceforge.jFuzzyLogic.rule.Variable varRiesgo = fbIAM.getVariable("riesgo_infarto_de_miocardio");
                                
                                if (varRiesgo != null) {
                                    double riesgo = varRiesgo.getValue();
                                    
                                    if (riesgo >= 0.3 && !infartoDetectado) {
                                        infartoDetectado = true;
                                        enfermedadDetectada = true;
                                        
                                        String severidad = interpretarSeveridad(riesgo);
                                        String diagnostico = "INFARTO AGUDO DE MIOCARDIO " + severidad;
                                        diagnosticosPositivos.add(diagnostico);
                                        
                                        bw.write("Resultados por ciclo:\n");
                                        bw.write("   Ciclo " + (i+1) + ": ST=" + String.format("%.2f", stamp) + 
                                                " mV, T=" + String.format("%.2f", tpeak) + " mV\n");
                                        bw.write("              Riesgo IAM: " + String.format("%.3f", riesgo) + "\n");
                                        
                                        bw.write("\nInterpretacion:\n");
                                        bw.write("Se detecta elevacion significativa del segmento ST.\n");
                                        bw.write("El riesgo fuzzy de infarto de miocardio agudo es " + severidad.toLowerCase() + ".\n\n");
                                        bw.write("DIAGNOSTICO: " + diagnostico + "\n");
                                        bw.write("Justificacion: ST_amp=" + String.format("%.2f", stamp) + " mV (>0.1), T_peak=" + String.format("%.2f", tpeak) + " mV (>0.6), Riesgo_IAM=" + String.format("%.3f", riesgo) + "\n\n");
                                        
                                        System.out.println("DETECTADO: " + diagnostico);
                                        
                                        JFuzzyChart.get().chart(varRiesgo, varRiesgo.getDefuzzifier(), true);
                                    }
                                }
                            }
                        }
                        
                        if (!infartoDetectado) {
                            bw.write("Interpretacion:\n");
                            bw.write("Ningun ciclo supera los umbrales de elevacion de ST (>0.1) y T (>0.6).\n");
                            bw.write("El diagnostico fuzzy de infarto de miocardio agudo es negativo.\n\n");
                            bw.write("DIAGNOSTICO: No se detecto infarto agudo de miocardio\n\n");
                        }
                    }
                } else {
                    bw.write("No hay ciclos completos para analizar.\n\n");
                }
                
                //ANALIZAR HIPOCALCEMIA
                bw.write("5. ANALISIS DE HIPOCALCEMIA\n");
                bw.write("Se evalua la duracion del intervalo QT en cada ciclo.\n");
                bw.write("Si QT > 440 ms, la probabilidad de hipocalcemia es alta.\n\n");
                
                if (!ciclos.isEmpty()) {
                    
                    boolean hipocalcemiaDetectada = false;
                    FunctionBlock fbHipoca = fis.getFunctionBlock("ECG_Hipocalcemia");
                    
                    if (fbHipoca != null) {
                        
                        for (int i = 0; i < Math.min(5, ciclos.size()); i++) {
                            CicloECG ciclo = ciclos.get(i);
                            double duracionQT = ciclo.t.getFin() - ciclo.q.getStart();
                            
                            
                            if (duracionQT > 440) {
                                
                                fbHipoca.setVariable("duracion_qt", duracionQT);
                                fbHipoca.evaluate();
                                
                                double prob = fbHipoca.getVariable("probabilidad_hipocalcemia").getValue();
                                
                                if (prob >= 0.3 && !hipocalcemiaDetectada) {
                                    hipocalcemiaDetectada = true;
                                    enfermedadDetectada = true;
                                    
                                    String severidad = interpretarSeveridad(prob);
                                    String diagnostico = "HIPOCALCEMIA " + severidad;
                                    diagnosticosPositivos.add(diagnostico);
                                    
                                    System.out.println("DETECTADO: " + diagnostico + " - QT: " + String.format("%.0f", duracionQT) + " ms");
                                    
                                    bw.write("Resultados por ciclo:\n");
                                    bw.write("     Ciclo " + (i+1) + ": QT=" + String.format("%.0f", duracionQT) + 
                                            " ms, Probabilidad=" + String.format("%.3f", prob) + "\n");
                                    
                                    bw.write("\nInterpretacion:\n");
                                    bw.write("Se detecta prolongacion del intervalo QT (> 440 ms).\n");
                                    bw.write("La probabilidad fuzzy de hipocalcemia es " + severidad.toLowerCase() + ".\n\n");
                                    bw.write("DIAGNOSTICO: " + diagnostico + "\n");
                                    bw.write("Justificacion: Duracion_QT=" + String.format("%.0f", duracionQT) + " ms (>440), Probabilidad=" + String.format("%.3f", prob) + "\n\n");
                                    
                                    JFuzzyChart.get().chart(fbHipoca.getVariable("probabilidad_hipocalcemia"), 
                                                            fbHipoca.getVariable("probabilidad_hipocalcemia").getDefuzzifier(), true);
                                }
                            }
                        }
                        
                        if (!hipocalcemiaDetectada) {
                            bw.write("Interpretacion:\n");
                            bw.write("Todos los intervalos QT estan dentro de valores normales (<= 440 ms).\n");
                            bw.write("La logica difusa asigna probabilidad muy baja a hipocalcemia.\n\n");
                            bw.write("DIAGNOSTICO: No se detecto hipocalcemia\n\n");
                            System.out.println("No se detecto hipocalcemia");
                        }
                    }
                } else {
                    bw.write("No hay ciclos completos para analizar.\n\n");
                }
                
                //ANALIZAR PVC
                bw.write("6. ANALISIS DE CONTRACCION VENTRICULAR PREMATURA (PVC)\n");
                bw.write("Se mide la duracion del QRS y la presencia de onda P.\n");
                bw.write("Un QRS corto (<90 ms) sin P previa eleva la probabilidad de PVC.\n\n");
                
                if (!ciclos.isEmpty()) {
                    boolean pvcDetectada = false;
                    FunctionBlock fbPVC = fis.getFunctionBlock("ECG_PVC");
                    
                    if (fbPVC != null) {
                        
                        for (int i = 0; i < Math.min(3, ciclos.size()); i++) {
                            CicloECG ciclo = ciclos.get(i);
                            double duracionQRS = ciclo.s.getFin() - ciclo.q.getStart();
                            double ausenciaP = (ciclo.p == null) ? 1.0 : 0.0;
                            
                            
                            if (duracionQRS < 90) {
                                
                                fbPVC.setVariable("duracion_qrs", duracionQRS);
                                fbPVC.setVariable("ausencia_p", ausenciaP);
                                fbPVC.evaluate();
                                
                                double prob = fbPVC.getVariable("probabilidad_pvc").getValue();
                                
                                if (prob >= 0.3 && !pvcDetectada) {
                                    pvcDetectada = true;
                                    enfermedadDetectada = true;
                                    
                                    String severidad = interpretarSeveridad(prob);
                                    String diagnostico = "PVC " + severidad;
                                    diagnosticosPositivos.add(diagnostico);
                                    
                                    System.out.println("DETECTADO: " + diagnostico + " - Prob: " + String.format("%.3f", prob));
                                    
                                    bw.write("Resultados por ciclo:\n");
                                    bw.write("Ciclo " + (i+1) + ": Duracion QRS=" + String.format("%.0f", duracionQRS) + 
                                            " ms, Ausencia P=" + (ausenciaP == 1.0 ? "Si" : "No") + "\n");
                                    bw.write("Probabilidad PVC: " + String.format("%.3f", prob) + "\n");
                                    
                                    bw.write("\nInterpretacion:\n");
                                    bw.write("Se detecta QRS corto (<90 ms) compatible con contraccion prematura.\n");
                                    bw.write("La probabilidad fuzzy de PVC es " + severidad.toLowerCase() + ".\n\n");
                                    bw.write("DIAGNOSTICO: " + diagnostico + "\n");
                                    bw.write("Justificacion: Duracion_QRS=" + String.format("%.0f", duracionQRS) + " ms (<90), Ausencia_P=" + (ausenciaP == 1.0 ? "Si" : "No") + ", Probabilidad=" + String.format("%.3f", prob) + "\n\n");
                                    
                                    JFuzzyChart.get().chart(fbPVC.getVariable("probabilidad_pvc"), 
                                                            fbPVC.getVariable("probabilidad_pvc").getDefuzzifier(), true);
                                }
                            }
                        }
                        
                        if (!pvcDetectada) {
                            bw.write("Interpretacion:\n");
                            bw.write("Todos los valores se encuentran dentro del rango normal (QRS >= 90 ms).\n");
                            bw.write("Se descarta PVC.\n\n");
                            bw.write("DIAGNOSTICO: No se detecto PVC\n\n");
                        }
                    }
                } else {
                    bw.write("No hay ciclos completos para analizar.\n\n");
                }
                
                // RESUMEN FINAL
                bw.write("\nRESUMEN DEL DIAGNOSTICO\n");
                
                if (!enfermedadDetectada) {
                    bw.write("DIAGNOSTICO FINAL: PACIENTE SANO\n\n");
                    bw.write("No se han detectado alteraciones relevantes en el ECG analizado.\n");
                    bw.write("El paciente puede considerarse sano desde el punto de vista electrocardiografico.\n");
                    System.out.println("\n[RESUMEN] Paciente sano");
                } else {
                    bw.write("DIAGNOSTICO FINAL: PATOLOGIAS DETECTADAS\n\n");
                    bw.write("Se han detectado las siguientes patologias:\n");
                    for (String diag : diagnosticosPositivos) {
                        bw.write("  - " + diag + "\n");
                    }
                    bw.write("\nGraficas mostradas en pantalla durante el analisis.\n");
                    System.out.println("\n[RESUMEN] Patologias detectadas: " + diagnosticosPositivos.size());
                }
                
                bw.close();
                
                System.out.println("Archivo generado: " + nombreTxt);
                System.out.println("Carpeta: " + dirArchivoSalida.getAbsolutePath());
                System.out.println();
                
            } catch (Exception e) {
                System.err.println("Error procesando " + file.getName() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    // INTERPRETAR SEVERIDAD (UNIVERSAL)
    private static String interpretarSeveridad(double valor) {
        if (valor >= 0.9) return "MUY GRAVE";
        if (valor >= 0.7) return "GRAVE";
        if (valor >= 0.45) return "MODERADA";
        if (valor >= 0.3) return "LEVE";
        return "AUSENTE";
    }
}
