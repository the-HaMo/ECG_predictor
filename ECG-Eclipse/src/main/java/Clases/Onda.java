package Clases;

public abstract class Onda {

	private int start;
	private int end;
	private double peak;
	
	
	public Onda(int start, int end, double peak) {
		this.start = start;
		this.end = end;
		this.peak = peak;
	}


	public int getStart() {
		return start;
	}


	public void setStart(int start) {
		this.start = start;
	}


	public int getEnd() {
		return end;
	}


	public void setEnd(int end) {
		this.end = end;
	}


	public double getPeak() {
		return peak;
	}


	public void setPeak(double peak) {
		this.peak = peak;
	}
	
	
	
	
}
