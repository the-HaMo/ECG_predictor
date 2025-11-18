package Clases;

import java.util.HashSet;
import java.util.Set;

public class Diagnostico_Inferido {

	 private Set<Diagnostico> resultados = new HashSet<>();

	public Diagnostico_Inferido(Diagnostico resultado) {
		this.resultados.add(resultado);
	}
	
	public Set<Diagnostico> getResultados() {
        return new HashSet<>(resultados);
    }
	
	public void addDiagnostico(Diagnostico diagnostico) {
		this.resultados.add(diagnostico);
	}
	
	@Override
    public String toString() {
        return "Diagnóstico(s) inferido(s): " + resultados;
    }
	
}
