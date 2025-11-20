package Fuzzy;

public class ResultadoHipocalcemia {
    private double duracionQT;
    private double probabilidad;
    
    public ResultadoHipocalcemia(double duracionQT, double probabilidad) {
        this.duracionQT = duracionQT;
        this.probabilidad = probabilidad;
    }
    
    public String getDiagnostico() {
        if (probabilidad >= 0.95) return "HIPOCALCEMIA MUY ALTA";
        if (probabilidad >= 0.75) return "HIPOCALCEMIA ALTA";
        if (probabilidad >= 0.45) return "HIPOCALCEMIA MEDIA";
        if (probabilidad >= 0.15) return "HIPOCALCEMIA BAJA";
        return "NO DETECTADA";
    }
    
    @Override
    public String toString() {
        return String.format(
            "QT=%.0f ms | Prob: %.2f | %s",
            duracionQT, probabilidad, getDiagnostico()
        );
    }
    
    public double getProbabilidad() { return probabilidad; }
}
