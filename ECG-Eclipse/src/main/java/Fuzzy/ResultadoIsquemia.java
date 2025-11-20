package Fuzzy;

public class ResultadoIsquemia {
    private double tpeak;
    private double stamp;
    private double riesgoIsquemia;
    
    public ResultadoIsquemia(double tpeak, double stamp, double riesgoIsquemia) {
        this.tpeak = tpeak;
        this.stamp = stamp;
        this.riesgoIsquemia = riesgoIsquemia;
    }
    
    public String getDiagnostico() {
        if (riesgoIsquemia >= 0.85) return "ISQUEMIA MUY ALTA";
        if (riesgoIsquemia >= 0.6) return "ISQUEMIA ALTA";
        if (riesgoIsquemia >= 0.35) return "ISQUEMIA MEDIA";
        if (riesgoIsquemia >= 0.1) return "ISQUEMIA BAJA";
        return "NO DETECTADA";
    }
    
    @Override
    public String toString() {
        return String.format(
            "T=%.2f mV, ST=%.2f mV | Riesgo: %.2f | %s",
            tpeak, stamp, riesgoIsquemia, getDiagnostico()
        );
    }
    
    public double getRiesgoIsquemia() { return riesgoIsquemia; }
}
