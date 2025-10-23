package Clases;

public abstract class Intervalo {

	private int inicio;
	private int fin;
	private int duracion;
	
	public Intervalo(int inicio, int fin, int duracion) {
		super();
		this.inicio = inicio;
		this.fin = fin;
		this.duracion = duracion;
	}

	public int getInicio() {
		return inicio;
	}

	public void setInicio(int inicio) {
		this.inicio = inicio;
	}

	public int getFin() {
		return fin;
	}

	public void setFin(int fin) {
		this.fin = fin;
	}

	public int getDuracion() {
		return duracion;
	}

	public void setDuracion(int duracion) {
		this.duracion = duracion;
	}
	
	
	
}
