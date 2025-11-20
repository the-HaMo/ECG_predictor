package Main;

import Clases.*;
import Parser.InputParser;
import java.io.*;
import java.util.*;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;

public class AnalizadorECG_Fuzzy {
    
    private static FIS fis;
    
    public static void main(String[] args) throws Exception {
        
        System.out.println("Directorio de trabajo: " + System.getProperty("user.dir"));
        
        String dirEnt = "inputs";
        String dirSal = "salidaFuzzy";
        
        if (args.length >= 1) dirEnt = args[0];
        if (args.length >= 2) dirSal = args[1];
        
        System.out.println("Directorio entrada: " + new File(dirEnt).getAbsolutePath());
        System.out.println("Directorio salida: " + new File(dirSal).getAbsolutePath());
        System.out.println();
        
        System.out.println("Cargando sistema fuzzy...");
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
            System.out.println("----------Procesando: " + file.getName() + "----------");
            
            try {
                List<Onda> ondas = InputParser.parseFile(file);
                System.out.println("Ondas encontradas: " + ondas.size());
                
                // ========== EXTRAER TODAS LAS ONDAS ==========
                List<Onda_P> ondasP = new ArrayList<>();
                List<Onda_Q> ondasQ = new ArrayList<>();
                List<Onda_R> ondasR = new ArrayList<>();
                List<Onda_S> ondasS = new ArrayList<>();
                List<Onda_T> ondasT = new ArrayList<>();
                
                for (Onda o : ondas) {
                    if (o instanceof Onda_P) ondasP.add((Onda_P) o);
                    else if (o instanceof Onda_Q) ondasQ.add((Onda_Q) o);
                    else if (o instanceof Onda_R) ondasR.add((Onda_R) o);
                    else if (o instanceof Onda_S) ondasS.add((Onda_S) o);
                    else if (o instanceof Onda_T) ondasT.add((Onda_T) o);
                }
                
                System.out.println("Ondas P: " + ondasP.size() + ", Q: " + ondasQ.size() + 
                                 ", R: " + ondasR.size() + ", S: " + ondasS.size() + 
                                 ", T: " + ondasT.size());
                
                // Identificar ciclos completos
                List<CicloECG> ciclos = identificarCiclos(ondasP, ondasQ, ondasR, ondasS, ondasT);
                System.out.println("Ciclos completos identificados: " + ciclos.size());
                
                String nombreBase = file.getName().replace(".ecg", "");
                String nombreSalida = nombreBase + "_salida_fuzzy.txt";
                
                BufferedWriter bw = new BufferedWriter(new FileWriter(new File(dirSal, nombreSalida)));
                

                bw.write("DIAGNOSTICO ECG CON LOGICA DIFUSA\n");
                bw.write("Archivo analizado: " + file.getName() + "\n");
                bw.write("Fecha: " + new java.util.Date() + "\n");
                bw.write("Ciclos analizados: " + ciclos.size() + "\n");
                
                boolean enfermedadDetectada = false;
                List<String> graficasGeneradas = new ArrayList<>();
                
                // ========== 1. ANALIZAR RITMO CARDIACO ==========
                int ritmo = calcularRitmo(ondasQ);
                
                if (ritmo > 0) {
                    System.out.println("\n[RITMO] Calculado: " + ritmo + " pul/min");
                    bw.write("1. ANALISIS DE RITMO CARDIACO\n");
                    bw.write("   Ritmo cardiaco: " + ritmo + " pul/min\n");
                    
                    FunctionBlock fb = fis.getFunctionBlock("Analisis_Ritmo_Cardiaco");
                    if (fb != null) {
                        fb.setVariable("ritmo", ritmo);
                        fb.evaluate();
                        
                        double nivelBradi = fb.getVariable("nivel_bradicardia").getValue();
                        double nivelTaqui = fb.getVariable("nivel_taquicardia").getValue();
                        double estadoSalud = fb.getVariable("estado_salud_ritmo").getValue();
                        
                        bw.write("- Nivel bradicardia: " + String.format("%.3f", nivelBradi) + "\n");
                        bw.write("- Nivel taquicardia: " + String.format("%.3f", nivelTaqui) + "\n");
                        bw.write("- Estado salud: " + String.format("%.3f", estadoSalud) + "\n");
                        
                        String diagnostico = interpretarRitmo(nivelBradi, nivelTaqui, estadoSalud);
                        bw.write("- DIAGNOSTICO: " + diagnostico + "\n");
                        
                        if (estadoSalud >= 0.15) {
                            enfermedadDetectada = true;
                            System.out.println("DETECTADO: " + diagnostico);
                            
                            if (nivelBradi > nivelTaqui) {
                                JFuzzyChart.get().chart(fb.getVariable("nivel_bradicardia"), 
                                                        fb.getVariable("nivel_bradicardia").getDefuzzifier(), true);
                                graficasGeneradas.add("nivel_bradicardia");
                                bw.write("GRAFICA: nivel_bradicardia\n");
                            } else {
                                JFuzzyChart.get().chart(fb.getVariable("nivel_taquicardia"), 
                                                        fb.getVariable("nivel_taquicardia").getDefuzzifier(), true);
                                graficasGeneradas.add("nivel_taquicardia");
                                bw.write("GRAFICA: nivel_taquicardia\n");
                            }
                        } else {
                            System.out.println("Ritmo normal");
                        }
                        bw.write("\n");
                    }
                }
                
                // ========== 2. ANALIZAR ISQUEMIA (con ciclos completos) ==========
                if (!ciclos.isEmpty()) {
                    System.out.println("\nISQUEMIA: Analizando ciclos completos...");
                    bw.write("2. ANALISIS DE ISQUEMIA CORONARIA\n");
                    
                    boolean isquemiaDetectada = false;
                    FunctionBlock fbIsq = fis.getFunctionBlock("ECG_Isquemia");
                    
                    if (fbIsq != null) {
                        for (int i = 0; i < Math.min(3, ciclos.size()); i++) {
                            CicloECG ciclo = ciclos.get(i);
                            
                            // Calcular ST real (diferencia entre fin de S e inicio de T)
                            double stamp = calcularAmplitudST(ciclo);
                            double duracionST = ciclo.t.getStart() - ciclo.s.getFin();
                            
                            fbIsq.setVariable("tpeak", ciclo.t.getPeak());
                            fbIsq.setVariable("stamp", stamp);
                            fbIsq.setVariable("duracion_st", duracionST);
                            fbIsq.evaluate();
                            
                            double riesgo = fbIsq.getVariable("riesgo_isquemia").getValue();
                            
                            bw.write("Ciclo " + (i+1) + ":\n");
                            bw.write("T peak: " + String.format("%.2f", ciclo.t.getPeak()) + " mV\n");
                            bw.write("ST amplitud: " + String.format("%.2f", stamp) + " mV\n");
                            bw.write("ST duracion: " + String.format("%.0f", duracionST) + " ms\n");
                            bw.write("Riesgo isquemia: " + String.format("%.3f", riesgo) + "\n");
                            
                            if (riesgo >= 0.6 && !isquemiaDetectada) {
                                isquemiaDetectada = true;
                                enfermedadDetectada = true;
                                
                                System.out.println("  [DETECTADO] Isquemia - Riesgo: " + String.format("%.3f", riesgo));
                                JFuzzyChart.get().chart(fbIsq.getVariable("riesgo_isquemia"), 
                                                        fbIsq.getVariable("riesgo_isquemia").getDefuzzifier(), true);
                                graficasGeneradas.add("riesgo_isquemia");
                                bw.write("     DIAGNOSTICO: ISQUEMIA CORONARIA DETECTADA\n");
                                bw.write("     [GRAFICA: riesgo_isquemia]\n");
                            }
                        }
                        
                        if (!isquemiaDetectada) {
                            System.out.println("  [OK] No se detecto isquemia");
                            bw.write("   DIAGNOSTICO: No se detecto isquemia\n");
                        }
                    }
                    bw.write("\n");
                }
                
                // ========== 3. ANALIZAR HIPOPOTASEMIA ==========
                if (!ciclos.isEmpty()) {
                    System.out.println("\n[HIPOPOTASEMIA] Analizando...");
                    bw.write("3. ANALISIS DE HIPOPOTASEMIA\n");
                    
                    boolean hipopotasemiaDetectada = false;
                    FunctionBlock fbHipo = fis.getFunctionBlock("ECG_Hipopotasemia");
                    
                    if (fbHipo != null) {
                        for (int i = 0; i < Math.min(3, ciclos.size()); i++) {
                            CicloECG ciclo = ciclos.get(i);
                            
                            if (ciclo.t.getPeak() < -10) {
                                double stamp = calcularAmplitudST(ciclo);
                                
                                fbHipo.setVariable("tpeak", ciclo.t.getPeak());
                                fbHipo.setVariable("stamp", stamp);
                                fbHipo.evaluate();
                                
                                double prob = fbHipo.getVariable("probabilidad_hipopotasemia").getValue();
                                
                                bw.write("   Ciclo " + (i+1) + ": T=" + String.format("%.2f", ciclo.t.getPeak()) + 
                                        " mV, Prob=" + String.format("%.3f", prob) + "\n");
                                
                                if (prob >= 0.7 && !hipopotasemiaDetectada) {
                                    hipopotasemiaDetectada = true;
                                    enfermedadDetectada = true;
                                    
                                    System.out.println("  [DETECTADO] Hipopotasemia");
                                    JFuzzyChart.get().chart(fbHipo.getVariable("probabilidad_hipopotasemia"), 
                                                            fbHipo.getVariable("probabilidad_hipopotasemia").getDefuzzifier(), true);
                                    graficasGeneradas.add("probabilidad_hipopotasemia");
                                    bw.write("     DIAGNOSTICO: HIPOPOTASEMIA DETECTADA\n");
                                    bw.write("     [GRAFICA: probabilidad_hipopotasemia]\n");
                                }
                            }
                        }
                        
                        if (!hipopotasemiaDetectada) {
                            System.out.println("  [OK] No se detecto hipopotasemia");
                            bw.write("   DIAGNOSTICO: No se detecto hipopotasemia\n");
                        }
                    }
                    bw.write("\n");
                }
                
                // ========== 4. ANALIZAR INFARTO ==========
                if (!ciclos.isEmpty()) {
                    System.out.println("\n[INFARTO] Analizando...");
                    bw.write("4. ANALISIS DE INFARTO AGUDO DE MIOCARDIO\n");
                    
                    boolean infartoDetectado = false;
                    FunctionBlock fbIAM = fis.getFunctionBlock("ECG_Infarto");
                    
                    if (fbIAM != null) {
                        for (int i = 0; i < Math.min(3, ciclos.size()); i++) {
                            CicloECG ciclo = ciclos.get(i);
                            double stamp = calcularAmplitudST(ciclo);
                            
                            if (stamp > 0.1) {
                                fbIAM.setVariable("stamp", stamp);
                                fbIAM.setVariable("tpeak", ciclo.t.getPeak());
                                fbIAM.evaluate();
                                
                                double riesgo = fbIAM.getVariable("riesgo_infarto").getValue();
                                
                                bw.write("   Ciclo " + (i+1) + ": ST=" + String.format("%.2f", stamp) + 
                                        " mV, Riesgo=" + String.format("%.3f", riesgo) + "\n");
                                
                                if (riesgo >= 0.7 && !infartoDetectado) {
                                    infartoDetectado = true;
                                    enfermedadDetectada = true;
                                    
                                    System.out.println("  [DETECTADO] Infarto Agudo");
                                    JFuzzyChart.get().chart(fbIAM.getVariable("riesgo_infarto"), 
                                                            fbIAM.getVariable("riesgo_infarto").getDefuzzifier(), true);
                                    graficasGeneradas.add("riesgo_infarto");
                                    bw.write("     DIAGNOSTICO: INFARTO AGUDO DETECTADO\n");
                                    bw.write("     [GRAFICA: riesgo_infarto]\n");
                                }
                            }
                        }
                        
                        if (!infartoDetectado) {
                            System.out.println("  [OK] No se detecto infarto");
                            bw.write("   DIAGNOSTICO: No se detecto infarto\n");
                        }
                    }
                    bw.write("\n");
                }
                
                // ========== 5. ANALIZAR HIPOCALCEMIA ==========
                if (!ciclos.isEmpty()) {
                    System.out.println("\n[HIPOCALCEMIA] Analizando intervalo QT...");
                    bw.write("5. ANALISIS DE HIPOCALCEMIA\n");
                    
                    boolean hipocalcemiaDetectada = false;
                    FunctionBlock fbHipoca = fis.getFunctionBlock("ECG_Hipocalcemia");
                    
                    if (fbHipoca != null) {
                        for (int i = 0; i < Math.min(3, ciclos.size()); i++) {
                            CicloECG ciclo = ciclos.get(i);
                            double duracionQT = ciclo.t.getFin() - ciclo.q.getStart();
                            
                            fbHipoca.setVariable("duracion_qt", duracionQT);
                            fbHipoca.evaluate();
                            
                            double prob = fbHipoca.getVariable("probabilidad_hipocalcemia").getValue();
                            
                            bw.write("   Ciclo " + (i+1) + ": QT=" + String.format("%.0f", duracionQT) + 
                                    " ms, Prob=" + String.format("%.3f", prob) + "\n");
                            
                            if (prob >= 0.75 && !hipocalcemiaDetectada) {
                                hipocalcemiaDetectada = true;
                                enfermedadDetectada = true;
                                
                                System.out.println("  [DETECTADO] Hipocalcemia");
                                JFuzzyChart.get().chart(fbHipoca.getVariable("probabilidad_hipocalcemia"), 
                                                        fbHipoca.getVariable("probabilidad_hipocalcemia").getDefuzzifier(), true);
                                graficasGeneradas.add("probabilidad_hipocalcemia");
                                bw.write("     DIAGNOSTICO: HIPOCALCEMIA DETECTADA\n");
                                bw.write("     [GRAFICA: probabilidad_hipocalcemia]\n");
                            }
                        }
                        
                        if (!hipocalcemiaDetectada) {
                            System.out.println("  [OK] No se detecto hipocalcemia");
                            bw.write("   DIAGNOSTICO: No se detecto hipocalcemia\n");
                        }
                    }
                    bw.write("\n");
                }
             // ========== 6. ANALIZAR CONTRACCION VENTRICULAR PREMATURA (PVC) ==========
                if (!ciclos.isEmpty()) {
                    System.out.println("\n[PVC] Analizando contraccion ventricular prematura...");
                    bw.write("6. ANALISIS DE CONTRACCION VENTRICULAR PREMATURA (PVC)\n");
                    
                    boolean pvcDetectada = false;
                    FunctionBlock fbPVC = fis.getFunctionBlock("ECG_PVC");
                    
                    if (fbPVC != null) {
                        for (int i = 0; i < Math.min(3, ciclos.size()); i++) {
                            CicloECG ciclo = ciclos.get(i);
                            
                            // Calcular duración del QRS (Q inicio -> S fin)
                            double duracionQRS = ciclo.s.getFin() - ciclo.q.getStart();
                            
                            // Verificar ausencia de onda P (1 = ausente, 0 = presente)
                            double ausenciaP = (ciclo.p == null) ? 1.0 : 0.0;
                            
                            fbPVC.setVariable("duracion_qrs", duracionQRS);
                            fbPVC.setVariable("ausencia_p", ausenciaP);
                            fbPVC.evaluate();
                            
                            double prob = fbPVC.getVariable("probabilidad_pvc").getValue();
                            
                            bw.write("   Ciclo " + (i+1) + ":\n");
                            bw.write("     Duracion QRS: " + String.format("%.0f", duracionQRS) + " ms\n");
                            bw.write("     Ausencia P: " + (ausenciaP == 1.0 ? "Si" : "No") + "\n");
                            bw.write("     Probabilidad PVC: " + String.format("%.3f", prob) + "\n");
                            
                            // Mostrar gráfico si prob >= 0.7
                            if (prob >= 0.7 && !pvcDetectada) {
                                pvcDetectada = true;
                                enfermedadDetectada = true;
                                
                                System.out.println("  [DETECTADO] PVC - Prob: " + String.format("%.3f", prob));
                                System.out.println("  QRS=" + String.format("%.0f", duracionQRS) + 
                                                  " ms (< 90 ms = prematuro)");
                                
                                JFuzzyChart.get().chart(fbPVC.getVariable("probabilidad_pvc"), 
                                                        fbPVC.getVariable("probabilidad_pvc").getDefuzzifier(), true);
                                graficasGeneradas.add("probabilidad_pvc");
                                bw.write("     DIAGNOSTICO: PVC DETECTADA\n");
                                bw.write("     [GRAFICA: probabilidad_pvc]\n");
                            }
                        }
                        
                        if (!pvcDetectada) {
                            System.out.println("  [OK] No se detecto PVC");
                            bw.write("   DIAGNOSTICO: No se detecto PVC\n");
                        }
                    }
                    bw.write("\n");
                }

                
                // ========== RESUMEN FINAL ==========
                bw.write("========================================\n");
                bw.write("RESUMEN DEL DIAGNOSTICO\n");
                bw.write("========================================\n");
                
                if (!enfermedadDetectada) {
                    bw.write("DIAGNOSTICO FINAL: PACIENTE SANO\n");
                    System.out.println("\n[RESUMEN] Paciente sano");
                } else {
                    bw.write("DIAGNOSTICO FINAL: PATOLOGIAS DETECTADAS\n\n");
                    bw.write("Graficas generadas: " + graficasGeneradas.size() + "\n");
                    for (String grafica : graficasGeneradas) {
                        bw.write("  - " + grafica + "\n");
                    }
                    System.out.println("\n[RESUMEN] Patologias detectadas");
                }
                
                bw.write("========================================\n");
                bw.close();
                
                System.out.println("Archivo generado: " + nombreSalida);
                System.out.println();
                
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        System.out.println("========================================");
        System.out.println("PROCESAMIENTO COMPLETADO");
        System.out.println("========================================");
    }
    
    // ========== METODOS AUXILIARES ==========
    
    /**
     * Calcular ritmo cardiaco
     */
    private static int calcularRitmo(List<Onda_Q> ondasQ) {
        if (ondasQ.size() < 2) return 0;
        
        int primerQ = ondasQ.get(0).getStart();
        int ultimoQ = ondasQ.get(ondasQ.size() - 1).getStart();
        int duracion = ultimoQ - primerQ;
        
        if (duracion == 0) return 0;
        
        return (60000 * (ondasQ.size() - 1)) / duracion;
    }
    
    /**
     * Identificar ciclos completos P-Q-R-S-T
     */
    private static List<CicloECG> identificarCiclos(List<Onda_P> ondasP, List<Onda_Q> ondasQ,
                                                      List<Onda_R> ondasR, List<Onda_S> ondasS,
                                                      List<Onda_T> ondasT) {
        List<CicloECG> ciclos = new ArrayList<>();
        
        // Simplificado: asumir que las ondas están en orden
        int numCiclos = Math.min(Math.min(ondasQ.size(), ondasS.size()), ondasT.size());
        
        for (int i = 0; i < numCiclos; i++) {
            CicloECG ciclo = new CicloECG();
            ciclo.q = ondasQ.get(i);
            ciclo.s = ondasS.get(i);
            ciclo.t = ondasT.get(i);
            
            if (i < ondasP.size()) ciclo.p = ondasP.get(i);
            if (i < ondasR.size()) ciclo.r = ondasR.get(i);
            
            ciclos.add(ciclo);
        }
        
        return ciclos;
    }
    
    /**
     * Calcular amplitud del segmento ST
     * ST = nivel entre fin de S e inicio de T
     */
    private static double calcularAmplitudST(CicloECG ciclo) {
        // Amplitud aproximada del ST como promedio entre picos de S y T
        // (En un ECG real, ST es el segmento plano entre S y T)
        return (ciclo.s.getPeak() + ciclo.t.getPeak()) / 2.0;
    }
    
    /**
     * Interpretar ritmo
     */
    private static String interpretarRitmo(double nivelBradi, double nivelTaqui, double estadoSalud) {
        if (estadoSalud < 0.15) return "RITMO NORMAL";
        
        if (nivelBradi > 0.3) {
            if (nivelBradi >= 0.7) return "BRADICARDIA SEVERA";
            if (nivelBradi >= 0.4) return "BRADICARDIA MODERADA";
            return "BRADICARDIA LEVE";
        }
        
        if (nivelTaqui > 0.3) {
            if (nivelTaqui >= 0.7) return "TAQUICARDIA SEVERA";
            if (nivelTaqui >= 0.4) return "TAQUICARDIA MODERADA";
            return "TAQUICARDIA LEVE";
        }
        
        return "RITMO LIMITE";
    }
    
    // Clase interna para representar un ciclo completo
    static class CicloECG {
        Onda_P p;
        Onda_Q q;
        Onda_R r;
        Onda_S s;
        Onda_T t;
    }
}
