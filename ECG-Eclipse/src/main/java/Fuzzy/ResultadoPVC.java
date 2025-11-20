package Fuzzy;

public class ResultadoPVC {
    private double duracionQRS;
    private double ausenciaP;
    private double probabilidad;
    
    public ResultadoPVC(double duracionQRS, double ausenciaP, double probabilidad) {
        this.duracionQRS = duracionQRS;
        this.ausenciaP = ausenciaP;
        this.probabilidad = probabilidad;
    }
    
    public String getDiagnostico() {
        if (probabilidad >= 0.9) return "PVC MUY ALTA";
        if (probabilidad >= 0.7) return "PVC ALTA";
        if (probabilidad >= 0.45) return "PVC MEDIA";
        if (probabilidad >= 0.15) return "PVC BAJA";
        return "NO DETECTADA";
    }
    
    @Override
    public String toString() {
        return String.format(
            "QRS=%.0f ms, P ausente=%.1f | Prob: %.2f | %s",
            duracionQRS, ausenciaP, probabilidad, getDiagnostico()
        );
    }
    
    public double getProbabilidad() { return probabilidad; }
}
