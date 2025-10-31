package Clases;

public class Ciclo_Cardiaco {

	private int tciclo;
	

	public Ciclo_Cardiaco(int tciclo) {
		super();
		this.tciclo = tciclo;
	}


	public int getTciclo() {
		return tciclo;
	}


	public void setTciclo(int tciclo) {
		this.tciclo = tciclo;
	}


	@Override
	public String toString() {
		return "Ciclo_Cardiaco [tciclo=" + tciclo + "]";
	}
	
	
	
}
