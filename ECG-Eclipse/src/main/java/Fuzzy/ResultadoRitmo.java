package Fuzzy;

public class ResultadoRitmo {
    private double ritmo;
    private double nivelBradicardia;
    private double nivelTaquicardia;
    private double estadoSalud;
    
    public ResultadoRitmo(double ritmo, double nivelBradicardia, 
                          double nivelTaquicardia, double estadoSalud) {
        this.ritmo = ritmo;
        this.nivelBradicardia = nivelBradicardia;
        this.nivelTaquicardia = nivelTaquicardia;
        this.estadoSalud = estadoSalud;
    }
    
    public String getDiagnostico() {
        if (estadoSalud < 0.15) {
            return "SANO";
        }
        
        if (nivelBradicardia > 0.3) {
            if (nivelBradicardia >= 0.7) return "BRADICARDIA SEVERA";
            if (nivelBradicardia >= 0.4) return "BRADICARDIA MODERADA";
            return "BRADICARDIA LEVE";
        }
        
        if (nivelTaquicardia > 0.3) {
            if (nivelTaquicardia >= 0.7) return "TAQUICARDIA SEVERA";
            if (nivelTaquicardia >= 0.4) return "TAQUICARDIA MODERADA";
            return "TAQUICARDIA LEVE";
        }
        
        return "RITMO LIMITE";
    }
    
    @Override
    public String toString() {
        return String.format(
            "Ritmo: %.0f pul/min | Severidad: %.2f | %s", 
            ritmo, Math.max(nivelBradicardia, nivelTaquicardia), getDiagnostico()
        );
    }
    
    // Getters
    public double getRitmo() { return ritmo; }
    public double getNivelBradicardia() { return nivelBradicardia; }
    public double getNivelTaquicardia() { return nivelTaquicardia; }
    public double getEstadoSalud() { return estadoSalud; }
}
