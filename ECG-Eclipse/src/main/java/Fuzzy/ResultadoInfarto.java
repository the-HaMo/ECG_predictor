package Fuzzy;

public class ResultadoInfarto {
    private double stamp;
    private double tpeak;
    private double riesgo;
    
    public ResultadoInfarto(double stamp, double tpeak, double riesgo) {
        this.stamp = stamp;
        this.tpeak = tpeak;
        this.riesgo = riesgo;
    }
    
    public String getDiagnostico() {
        if (riesgo >= 0.9) return "IAM MUY ALTO";
        if (riesgo >= 0.7) return "IAM ALTO";
        if (riesgo >= 0.4) return "IAM MEDIO";
        if (riesgo >= 0.1) return "IAM BAJO";
        return "NO DETECTADO";
    }
    
    @Override
    public String toString() {
        return String.format(
            "ST=%.2f mV, T=%.2f mV | Riesgo: %.2f | %s",
            stamp, tpeak, riesgo, getDiagnostico()
        );
    }
    
    public double getRiesgo() { return riesgo; }
}
