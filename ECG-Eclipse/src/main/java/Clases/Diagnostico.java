package Clases;

public enum Diagnostico {
    
    SANO(
        "Patron ECG normal. Ritmo sinusal regular, ondas y segmentos dentro de valores fisiologicos normales"
    ),
    
    TAQUICARDIA_SINUSAL(
        "Frecuencia cardiaca > 100 pul/min. "
    ),
    
    BRADICARDIA_SINUSAL(
        "Frecuencia cardiaca < 60 pul/min. "
    ),
    
    ISQUEMIA_CORONARIA(
        "Onda T invertida (peak < 0 mV) y segmento ST descendido. "
    ),
    
    HIPOPOTASEMIA(
        "Onda T muy invertida (peak < -12 mV) con segmento ST profundamente descendido (< -0.5 mV). "
    ),
    
    INFARTO_AGUDO_MIOCARDIO(
        "Segmento ST elevado (> 0.1 mV) con onda T muy alta (peak > 0.6 mV). "
    ),
    
    HIPOCALCEMIA(
        "Intervalo QT prolongado (> 440 ms) con segmento ST acortado (< 80 ms). "
    ),
    
    CONTRACCION_VENTRICULAR_PREMATURA(
        "Complejo QRS ancho (> 120 ms) sin onda P previa y prematuro (RR < 80% del intervalo medio). "
    ),
    
    FLUTTER_AURICULAR(
        "Ondas P en diente de sierra con frecuencia auricular 250-350 lpm. "
    );
    
    private final String descripcion;
    
    Diagnostico(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}
