package Clases;

public abstract class Segmento {

	private int inicio;
	private int fin;
	private int duracion;
	private double amplitud;
	
	
	public Segmento(int inicio, int fin, double ampl,int duracion) {
		super();
		this.inicio = inicio;
		this.fin = fin;
		this.amplitud=ampl;
		this.duracion = duracion;
	}


	public int getInicio() {
		return inicio;
	}
	
	public double getAmplitud() {
		return amplitud;
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
