package Clases;

public abstract class Onda {

	private int start;
	private int fin;
	private double peak;
	
	
	public Onda(int start, int fin, double peak) {
		this.start = start;
		this.fin = fin;
		this.peak = peak;
	}


	public int getStart() {
		return start;
	}


	public void setStart(int start) {
		this.start = start;
	}


	public int getFin() {
		return fin;
	}


	public void setEnd(int fin) {
		this.fin = fin;
	}


	public double getPeak() {
		return peak;
	}


	public void setPeak(double peak) {
		this.peak = peak;
	}


	@Override
	public String toString() {
		return getClass().getSimpleName() + " [start=" + start + ", end=" + fin + ", peak=" + peak + "]";
	}
	
	
	
	
}
