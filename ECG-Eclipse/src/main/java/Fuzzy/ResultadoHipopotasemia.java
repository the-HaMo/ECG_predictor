package Fuzzy;

public class ResultadoHipopotasemia {
    private double tpeak;
    private double stamp;
    private double probabilidad;
    
    public ResultadoHipopotasemia(double tpeak, double stamp, double probabilidad) {
        this.tpeak = tpeak;
        this.stamp = stamp;
        this.probabilidad = probabilidad;
    }
    
    public String getDiagnostico() {
        if (probabilidad >= 0.9) return "HIPOPOTASEMIA MUY ALTA";
        if (probabilidad >= 0.7) return "HIPOPOTASEMIA ALTA";
        if (probabilidad >= 0.45) return "HIPOPOTASEMIA MEDIA";
        if (probabilidad >= 0.15) return "HIPOPOTASEMIA BAJA";
        return "NO DETECTADA";
    }
    
    @Override
    public String toString() {
        return String.format(
            "T=%.2f mV, ST=%.2f mV | Prob: %.2f | %s",
            tpeak, stamp, probabilidad, getDiagnostico()
        );
    }
    
    public double getProbabilidad() { return probabilidad; }
}
