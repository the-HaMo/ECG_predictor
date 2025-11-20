package Main;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.rule.Variable;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;

public class TestFuzzy {
    
    private static FIS fis;
    
    public static void main(String[] args) {

        System.out.println("========================================");
        System.out.println("SISTEMA DE PRUEBA FUZZY - TODAS LAS PATOLOGIAS");
        System.out.println("========================================\n");
        
        // Cargar sistema fuzzy
        String fileName = "src/main/resources/Reglas/diagnostico_ecg.fcl";
        fis = FIS.load(fileName, true);

        if (fis == null) {
            System.err.println("ERROR: No se pudo cargar el archivo FCL");
            return;
        }
        
        System.out.println("Sistema fuzzy cargado correctamente\n");
        
        // Menú de opciones
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        
        while (true) {
            System.out.println("\n========== MENU DE PRUEBAS ==========");
            System.out.println("1. Analisis de Ritmo Cardiaco (Taquicardia/Bradicardia)");
            System.out.println("2. Isquemia Coronaria");
            System.out.println("3. Hipopotasemia");
            System.out.println("4. Infarto Agudo de Miocardio");
            System.out.println("5. Hipocalcemia");
            System.out.println("6. Contraccion Ventricular Prematura (PVC)");
            System.out.println("7. Probar TODAS las patologias");
            System.out.println("0. Salir");
            System.out.print("\nSelecciona una opcion: ");
            
            int opcion = scanner.nextInt();
            
            if (opcion == 0) {
                System.out.println("Saliendo...");
                break;
            }
            
            switch (opcion) {
                case 1: probarRitmoCardiaco(); break;
                case 2: probarIsquemia(); break;
                case 3: probarHipopotasemia(); break;
                case 4: probarInfarto(); break;
                case 5: probarHipocalcemia(); break;
                case 6: probarPVC(); break;
                case 7: probarTodas(); break;
                default: System.out.println("Opcion invalida");
            }
        }
        
        scanner.close();
    }
    
    // ========== 1. RITMO CARDIACO ==========
    private static void probarRitmoCardiaco() {
        System.out.println("\n========== ANALISIS DE RITMO CARDIACO ==========");
        
        // Casos de prueba
        int[] casos = {45, 58, 75, 102, 125, 150};
        String[] descripciones = {
            "Bradicardia moderada",
            "Ritmo limite (bradileve)",
            "Ritmo normal",
            "Ritmo limite (taquileve)", 
            "Taquicardia moderada",
            "Taquicardia severa"
        };
        
        FunctionBlock fb = fis.getFunctionBlock("Analisis_Ritmo_Cardiaco");
        if (fb == null) {
            System.err.println("ERROR: FUNCTION_BLOCK no encontrado");
            return;
        }
        
        for (int i = 0; i < casos.length; i++) {
            fb.setVariable("ritmo", casos[i]);
            fb.evaluate();
            
            double nivelBradi = fb.getVariable("nivel_bradicardia").getValue();
            double nivelTaqui = fb.getVariable("nivel_taquicardia").getValue();
            double estadoSalud = fb.getVariable("estado_salud_ritmo").getValue();
            
            System.out.println("\n[" + descripciones[i] + "] Ritmo = " + casos[i] + " pul/min");
            System.out.println("  - Nivel bradicardia: " + String.format("%.3f", nivelBradi));
            System.out.println("  - Nivel taquicardia: " + String.format("%.3f", nivelTaqui));
            System.out.println("  - Estado salud: " + String.format("%.3f", estadoSalud));
            System.out.println("  - Diagnostico: " + interpretarRitmo(nivelBradi, nivelTaqui, estadoSalud));
        }
        
        // Mostrar graficos del ultimo caso
        System.out.println("\nMostrando graficos del ultimo caso...");
        JFuzzyChart.get().chart(fb.getVariable("ritmo"), true);
        JFuzzyChart.get().chart(fb.getVariable("nivel_bradicardia"), 
                                fb.getVariable("nivel_bradicardia").getDefuzzifier(), true);
        JFuzzyChart.get().chart(fb.getVariable("nivel_taquicardia"), 
                                fb.getVariable("nivel_taquicardia").getDefuzzifier(), true);
    }
    
    // ========== 2. ISQUEMIA CORONARIA ==========
    private static void probarIsquemia() {
        System.out.println("\n========== ISQUEMIA CORONARIA ==========");
        
        // Casos de prueba
        double[][] casos = {
            {-2, -1, 100},   // Isquemia leve
            {-5, -2, 110},   // Isquemia moderada
            {-8, -3, 120},   // Isquemia alta
            {-12, -6, 150},  // Isquemia muy alta
            {0, 0, 100}      // Normal
        };
        String[] descripciones = {
            "Isquemia leve",
            "Isquemia moderada",
            "Isquemia alta",
            "Isquemia muy alta",
            "Normal (sin isquemia)"
        };
        
        FunctionBlock fb = fis.getFunctionBlock("ECG_Isquemia");
        if (fb == null) {
            System.err.println("ERROR: FUNCTION_BLOCK no encontrado");
            return;
        }
        
        for (int i = 0; i < casos.length; i++) {
            fb.setVariable("tpeak", casos[i][0]);
            fb.setVariable("stamp", casos[i][1]);
            fb.setVariable("duracion_st", casos[i][2]);
            fb.evaluate();
            
            double riesgo = fb.getVariable("riesgo_isquemia").getValue();
            
            System.out.println("\n[" + descripciones[i] + "]");
            System.out.println("  T peak = " + casos[i][0] + " mV, ST amp = " + casos[i][1] + " mV");
            System.out.println("  Riesgo isquemia: " + String.format("%.3f", riesgo));
            System.out.println("  Interpretacion: " + interpretarRiesgo(riesgo, "isquemia"));
        }
        
        // Graficos
        System.out.println("\nMostrando graficos...");
        JFuzzyChart.get().chart(fb.getVariable("tpeak"), true);
        JFuzzyChart.get().chart(fb.getVariable("stamp"), true);
        JFuzzyChart.get().chart(fb.getVariable("riesgo_isquemia"), 
                                fb.getVariable("riesgo_isquemia").getDefuzzifier(), true);
    }
    
    // ========== 3. HIPOPOTASEMIA ==========
    private static void probarHipopotasemia() {
        System.out.println("\n========== HIPOPOTASEMIA ==========");
        
        double[][] casos = {
            {-8, -2},    // Hipopotasemia baja
            {-15, -4},   // Hipopotasemia moderada
            {-20, -8},   // Hipopotasemia alta
            {-25, -10},  // Hipopotasemia muy alta
            {0, 0}       // Normal
        };
        String[] descripciones = {
            "Hipopotasemia baja",
            "Hipopotasemia moderada",
            "Hipopotasemia alta",
            "Hipopotasemia muy alta",
            "Normal"
        };
        
        FunctionBlock fb = fis.getFunctionBlock("ECG_Hipopotasemia");
        if (fb == null) {
            System.err.println("ERROR: FUNCTION_BLOCK no encontrado");
            return;
        }
        
        for (int i = 0; i < casos.length; i++) {
            fb.setVariable("tpeak", casos[i][0]);
            fb.setVariable("stamp", casos[i][1]);
            fb.evaluate();
            
            double prob = fb.getVariable("probabilidad_hipopotasemia").getValue();
            
            System.out.println("\n[" + descripciones[i] + "]");
            System.out.println("  T peak = " + casos[i][0] + " mV, ST amp = " + casos[i][1] + " mV");
            System.out.println("  Probabilidad: " + String.format("%.3f", prob));
            System.out.println("  Interpretacion: " + interpretarRiesgo(prob, "hipopotasemia"));
        }
        
        // Graficos
        System.out.println("\nMostrando graficos...");
        JFuzzyChart.get().chart(fb.getVariable("tpeak"), true);
        JFuzzyChart.get().chart(fb.getVariable("stamp"), true);
        JFuzzyChart.get().chart(fb.getVariable("probabilidad_hipopotasemia"), 
                                fb.getVariable("probabilidad_hipopotasemia").getDefuzzifier(), true);
    }
    
    // ========== 4. INFARTO AGUDO DE MIOCARDIO ==========
    private static void probarInfarto() {
        System.out.println("\n========== INFARTO AGUDO DE MIOCARDIO ==========");
        
        double[][] casos = {
            {0.2, 0.3},   // IAM leve
            {0.6, 0.7},   // IAM moderado
            {1.0, 1.0},   // IAM alto
            {1.5, 1.2},   // IAM muy alto
            {0, 0}        // Normal
        };
        String[] descripciones = {
            "IAM leve (ST ligeramente elevado)",
            "IAM moderado (ST elevado)",
            "IAM alto (ST muy elevado)",
            "IAM muy alto (ST extremadamente elevado)",
            "Normal"
        };
        
        FunctionBlock fb = fis.getFunctionBlock("ECG_Infarto");
        if (fb == null) {
            System.err.println("ERROR: FUNCTION_BLOCK no encontrado");
            return;
        }
        
        for (int i = 0; i < casos.length; i++) {
            fb.setVariable("stamp", casos[i][0]);
            fb.setVariable("tpeak", casos[i][1]);
            fb.evaluate();
            
            double riesgo = fb.getVariable("riesgo_infarto").getValue();
            
            System.out.println("\n[" + descripciones[i] + "]");
            System.out.println("  ST amp = " + casos[i][0] + " mV, T peak = " + casos[i][1] + " mV");
            System.out.println("  Riesgo IAM: " + String.format("%.3f", riesgo));
            System.out.println("  Interpretacion: " + interpretarRiesgo(riesgo, "infarto"));
        }
        
        // Graficos
        System.out.println("\nMostrando graficos...");
        JFuzzyChart.get().chart(fb.getVariable("stamp"), true);
        JFuzzyChart.get().chart(fb.getVariable("tpeak"), true);
        JFuzzyChart.get().chart(fb.getVariable("riesgo_infarto"), 
                                fb.getVariable("riesgo_infarto").getDefuzzifier(), true);
    }
    
    // ========== 5. HIPOCALCEMIA ==========
    private static void probarHipocalcemia() {
        System.out.println("\n========== HIPOCALCEMIA ==========");
        
        double[] casos = {410, 450, 480, 520, 560};
        String[] descripciones = {
            "Normal",
            "QT ligeramente prolongado",
            "QT prolongado",
            "QT muy prolongado",
            "QT extremadamente prolongado"
        };
        
        FunctionBlock fb = fis.getFunctionBlock("ECG_Hipocalcemia");
        if (fb == null) {
            System.err.println("ERROR: FUNCTION_BLOCK no encontrado");
            return;
        }
        
        for (int i = 0; i < casos.length; i++) {
            fb.setVariable("duracion_qt", casos[i]);
            fb.evaluate();
            
            double prob = fb.getVariable("probabilidad_hipocalcemia").getValue();
            
            System.out.println("\n[" + descripciones[i] + "] QT = " + casos[i] + " ms");
            System.out.println("  Probabilidad hipocalcemia: " + String.format("%.3f", prob));
            System.out.println("  Interpretacion: " + interpretarRiesgo(prob, "hipocalcemia"));
        }
        
        // Graficos
        System.out.println("\nMostrando graficos...");
        JFuzzyChart.get().chart(fb.getVariable("duracion_qt"), true);
        JFuzzyChart.get().chart(fb.getVariable("probabilidad_hipocalcemia"), 
                                fb.getVariable("probabilidad_hipocalcemia").getDefuzzifier(), true);
    }
    
    // ========== 6. PVC ==========
    private static void probarPVC() {
        System.out.println("\n========== CONTRACCION VENTRICULAR PREMATURA (PVC) ==========");
        
        double[][] casos = {
            {100, 0},    // QRS normal con P
            {130, 0.5},  // QRS ancho, P dudosa
            {150, 1.0},  // QRS ancho sin P
            {180, 1.0},  // QRS muy ancho sin P
            {200, 1.0}   // QRS extremo sin P
        };
        String[] descripciones = {
            "Normal (QRS normal con P)",
            "PVC posible (QRS ancho, P dudosa)",
            "PVC moderada (QRS ancho sin P)",
            "PVC alta (QRS muy ancho sin P)",
            "PVC muy alta (QRS extremo sin P)"
        };
        
        FunctionBlock fb = fis.getFunctionBlock("ECG_PVC");
        if (fb == null) {
            System.err.println("ERROR: FUNCTION_BLOCK no encontrado");
            return;
        }
        
        for (int i = 0; i < casos.length; i++) {
            fb.setVariable("duracion_qrs", casos[i][0]);
            fb.setVariable("ausencia_p", casos[i][1]);
            fb.evaluate();
            
            double prob = fb.getVariable("probabilidad_pvc").getValue();
            
            System.out.println("\n[" + descripciones[i] + "]");
            System.out.println("  QRS = " + casos[i][0] + " ms, Ausencia P = " + casos[i][1]);
            System.out.println("  Probabilidad PVC: " + String.format("%.3f", prob));
            System.out.println("  Interpretacion: " + interpretarRiesgo(prob, "pvc"));
        }
        
        // Graficos
        System.out.println("\nMostrando graficos...");
        JFuzzyChart.get().chart(fb.getVariable("duracion_qrs"), true);
        JFuzzyChart.get().chart(fb.getVariable("ausencia_p"), true);
        JFuzzyChart.get().chart(fb.getVariable("probabilidad_pvc"), 
                                fb.getVariable("probabilidad_pvc").getDefuzzifier(), true);
    }
    
    // ========== 7. PROBAR TODAS ==========
    private static void probarTodas() {
        System.out.println("\n========== PRUEBA COMPLETA DE TODAS LAS PATOLOGIAS ==========\n");
        
        probarRitmoCardiaco();
        esperarTecla();
        
        probarIsquemia();
        esperarTecla();
        
        probarHipopotasemia();
        esperarTecla();
        
        probarInfarto();
        esperarTecla();
        
        probarHipocalcemia();
        esperarTecla();
        
        probarPVC();
        
        System.out.println("\n========== PRUEBA COMPLETA FINALIZADA ==========");
    }
    
    // ========== METODOS AUXILIARES ==========
    
    private static String interpretarRitmo(double nivelBradi, double nivelTaqui, double estadoSalud) {
        if (estadoSalud < 0.15) return "SANO";
        
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
    
    private static String interpretarRiesgo(double valor, String tipo) {
        if (valor >= 0.9) return "MUY ALTO - " + tipo + " severa";
        if (valor >= 0.7) return "ALTO - " + tipo + " significativa";
        if (valor >= 0.45) return "MEDIO - " + tipo + " moderada";
        if (valor >= 0.15) return "BAJO - " + tipo + " leve";
        return "MUY BAJO - No " + tipo;
    }
    
    private static void esperarTecla() {
        System.out.print("\nPresiona ENTER para continuar...");
        try {
            System.in.read();
        } catch (Exception e) {}
    }
}
