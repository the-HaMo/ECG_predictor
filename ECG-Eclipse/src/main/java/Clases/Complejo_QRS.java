package Clases;

public class Complejo_QRS {

	private int duracion;
	private Onda_Q q;
	private Onda_R r;
	private Onda_S s;
	
	public Complejo_QRS( Onda_Q q, Onda_R r, Onda_S s, int duracion) {
		this.duracion = duracion;
		this.q = q;
		this.r = r;
		this.s = s;
	}

	public int getDuracion() {
		return duracion;
	}

	public void setDuracion(int duracion) {
		this.duracion = duracion;
	}

	public Onda_Q getQ() {
		return q;
	}

	public void setQ(Onda_Q q) {
		this.q = q;
	}

	public Onda_R getR() {
		return r;
	}

	public void setR(Onda_R r) {
		this.r = r;
	}

	public Onda_S getS() {
		return s;
	}

	public void setS(Onda_S s) {
		this.s = s;
	}
	
	


}
