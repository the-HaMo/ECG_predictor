package Clases;

public abstract class Diagnostico_Inferido {

	 private Diagnostico resultado;

	public Diagnostico_Inferido(Diagnostico resultado) {
		super();
		this.resultado = resultado;
	}

	public Diagnostico getResultado() {
		return resultado;
	}

	public void setResultado(Diagnostico resultado) {
		this.resultado = resultado;
	}
	 
}
