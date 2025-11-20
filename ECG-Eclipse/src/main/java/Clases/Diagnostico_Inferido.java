package Clases;

import java.util.HashSet;
import java.util.Set;

public class Diagnostico_Inferido {

	 private Set<Diagnostico> resultados = new HashSet<>();
	 private String outline;

	public Diagnostico_Inferido(Diagnostico resultado, String outline) {
		this.resultados.add(resultado);
		this.outline = outline;
	}
	
	public Set<Diagnostico> getResultados() {
        return new HashSet<>(resultados);
    }
	
	public void addDiagnostico(Diagnostico diagnostico) {
		this.resultados.add(diagnostico);
	}
	

	public String getOutline() {
		return outline;
	}

	public void setOutline(String outline) {
		this.outline = outline;
	}
	
	   public String toString() {
	        return "Diagnóstico(s) inferido(s): " + resultados + " causa: " + outline;
	    }
	
}
