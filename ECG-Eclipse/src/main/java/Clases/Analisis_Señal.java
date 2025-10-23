package Clases;

public class Analisis_Señal {

	private int numciclos;
	private int ritmo_cardiaco;
	public Analisis_Señal(int numciclos, int ritmo_cardiaco) {
		super();
		this.numciclos = numciclos;
		this.ritmo_cardiaco = ritmo_cardiaco;
	}
	public int getNumciclos() {
		return numciclos;
	}
	public void setNumciclos(int numciclos) {
		this.numciclos = numciclos;
	}
	public int getRitmo_cardiaco() {
		return ritmo_cardiaco;
	}
	public void setRitmo_cardiaco(int ritmo_cardiaco) {
		this.ritmo_cardiaco = ritmo_cardiaco;
	}
	
	
}
