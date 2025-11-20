package Fuzzy;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;

public class EvaluadorFuzzy {
    
    private static FIS fis;
    
    static {
        // Cargar sistema fuzzy
        String ruta = "src/main/resources/Reglas/diagnostico_ecg.fcl";
        fis = FIS.load(ruta, true);
        
        if (fis == null) {
            System.err.println("ERROR: No se pudo cargar " + ruta);
        } else {
            System.out.println("Sistema fuzzy cargado correctamente");
        }
    }
    
    // RITMO CARDIACO
    public static ResultadoRitmo evaluarRitmo(int ritmo) {
        if (fis == null) return null;
        
        FunctionBlock fb = fis.getFunctionBlock("Analisis_Ritmo_Cardiaco");
        if (fb == null) return null;
        
        fb.setVariable("ritmo", ritmo);
        fb.evaluate();
        
        double nivelBradi = fb.getVariable("nivel_bradicardia").getValue();
        double nivelTaqui = fb.getVariable("nivel_taquicardia").getValue();
        double estadoSalud = fb.getVariable("estado_salud_ritmo").getValue();
        
        return new ResultadoRitmo(ritmo, nivelBradi, nivelTaqui, estadoSalud);
    }
    
    // ISQUEMIA
    public static ResultadoIsquemia evaluarIsquemia(double tpeak, double stamp, double duracionST) {
        if (fis == null) return null;
        
        FunctionBlock fb = fis.getFunctionBlock("ECG_Isquemia");
        if (fb == null) return null;
        
        fb.setVariable("tpeak", tpeak);
        fb.setVariable("stamp", stamp);
        fb.setVariable("duracion_st", duracionST);
        fb.evaluate();
        
        double riesgo = fb.getVariable("riesgo_isquemia").getValue();
        
        return new ResultadoIsquemia(tpeak, stamp, riesgo);
    }
    
    // HIPOPOTASEMIA
    public static ResultadoHipopotasemia evaluarHipopotasemia(double tpeak, double stamp) {
        if (fis == null) return null;
        
        FunctionBlock fb = fis.getFunctionBlock("ECG_Hipopotasemia");
        if (fb == null) return null;
        
        fb.setVariable("tpeak", tpeak);
        fb.setVariable("stamp", stamp);
        fb.evaluate();
        
        double prob = fb.getVariable("probabilidad_hipopotasemia").getValue();
        
        return new ResultadoHipopotasemia(tpeak, stamp, prob);
    }
    
    // INFARTO
    public static ResultadoInfarto evaluarInfarto(double stamp, double tpeak) {
        if (fis == null) return null;
        
        FunctionBlock fb = fis.getFunctionBlock("ECG_Infarto");
        if (fb == null) return null;
        
        fb.setVariable("stamp", stamp);
        fb.setVariable("tpeak", tpeak);
        fb.evaluate();
        
        double riesgo = fb.getVariable("riesgo_infarto").getValue();
        
        return new ResultadoInfarto(stamp, tpeak, riesgo);
    }
    
    // HIPOCALCEMIA
    public static ResultadoHipocalcemia evaluarHipocalcemia(double duracionQT) {
        if (fis == null) return null;
        
        FunctionBlock fb = fis.getFunctionBlock("ECG_Hipocalcemia");
        if (fb == null) return null;
        
        fb.setVariable("duracion_qt", duracionQT);
        fb.evaluate();
        
        double prob = fb.getVariable("probabilidad_hipocalcemia").getValue();
        
        return new ResultadoHipocalcemia(duracionQT, prob);
    }
    
    // PVC
    public static ResultadoPVC evaluarPVC(double duracionQRS, double ausenciaP) {
        if (fis == null) return null;
        
        FunctionBlock fb = fis.getFunctionBlock("ECG_PVC");
        if (fb == null) return null;
        
        fb.setVariable("duracion_qrs", duracionQRS);
        fb.setVariable("ausencia_p", ausenciaP);
        fb.evaluate();
        
        double prob = fb.getVariable("probabilidad_pvc").getValue();
        
        return new ResultadoPVC(duracionQRS, ausenciaP, prob);
    }
}
