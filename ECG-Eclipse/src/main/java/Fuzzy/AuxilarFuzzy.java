package Fuzzy;

import java.util.ArrayList;
import java.util.List;

import Clases.*;

public class AuxilarFuzzy {

	public final List<Onda_P> p = new ArrayList<>();
	public final List<Onda_Q> q = new ArrayList<>();
	public final List<Onda_R> r = new ArrayList<>();
	public final List<Onda_S> s = new ArrayList<>();
	public final List<Onda_T> t = new ArrayList<>();

	public static AuxilarFuzzy clasifica(List<Onda> ondas) {
		AuxilarFuzzy ws = new AuxilarFuzzy();

		for (Onda o : ondas) {
			if (o instanceof Onda_P) {
				ws.p.add((Onda_P) o);
			} else if (o instanceof Onda_Q) {
				ws.q.add((Onda_Q) o);
			} else if (o instanceof Onda_R) {
				ws.r.add((Onda_R) o);
			} else if (o instanceof Onda_S) {
				ws.s.add((Onda_S) o);
			} else if (o instanceof Onda_T) {
				ws.t.add((Onda_T) o);
			}
		}

		return ws;
	}

	public static List<CicloECG> identificarCiclos(List<Onda_P> ondasP, List<Onda_Q> ondasQ, List<Onda_R> ondasR,
			List<Onda_S> ondasS, List<Onda_T> ondasT) {
		List<CicloECG> ciclos = new ArrayList<>();

		for (Onda_Q q : ondasQ) {
			CicloECG ciclo = new CicloECG();
			ciclo.q = q;

			int tiempoQ = q.getStart();

			for (Onda_P p : ondasP) {
				if (p.getStart() < tiempoQ && (tiempoQ - p.getStart()) < 200) {
					ciclo.p = p;
				}
			}

			for (Onda_R r : ondasR) {
				if (r.getStart() > q.getFin() && r.getStart() < q.getFin() + 100) {
					ciclo.r = r;
					break;
				}
			}

			int referenciaS = (ciclo.r != null) ? ciclo.r.getFin() : q.getFin();
			for (Onda_S s : ondasS) {
				if (s.getStart() > referenciaS && s.getStart() < referenciaS + 100) {
					ciclo.s = s;
					break;
				}
			}

			int tiempoReferencia = (ciclo.s != null) ? ciclo.s.getFin() : q.getFin();
			for (Onda_T t : ondasT) {
				if (t.getStart() > tiempoReferencia && t.getStart() < tiempoReferencia + 500) {
					ciclo.t = t;
					break;
				}
			}

			if (ciclo.s != null && ciclo.t != null) {
				ciclos.add(ciclo);
			}
		}

		return ciclos;
	}

	// METODOS AUXILIARES

	public static int calcularRitmo(List<Onda_Q> ondasQ) {
		if (ondasQ.size() < 2)
			return 0;

		int primerQ = ondasQ.get(0).getStart();
		int ultimoQ = ondasQ.get(ondasQ.size() - 1).getStart();
		int duracion = ultimoQ - primerQ;

		if (duracion == 0)
			return 0;

		return (60000 * (ondasQ.size() - 1)) / duracion;
	}

	public static double calcularAmplitudST(CicloECG ciclo) {
		double peakS = ciclo.s.getPeak();
		double peakT = ciclo.t.getPeak();

		if (peakS < 0 && peakT < 0) {
			return (peakS + peakT) / 2.0;
		}

		if (peakT < -5 && peakS < 0) {
			return Math.min(peakS, peakT / 2.0);
		}

		return (peakS + peakT) / 2.0;
	}

	public static String interpretarRitmo(double nivelBradi, double nivelTaqui, double estadoSalud) {
		if (estadoSalud < 0.15)
			return "RITMO NORMAL";

		if (nivelBradi > 0.3) {
			if (nivelBradi >= 0.7)
				return "BRADICARDIA SEVERA";
			if (nivelBradi >= 0.4)
				return "BRADICARDIA MODERADA";
			return "BRADICARDIA LEVE";
		}

		if (nivelTaqui > 0.3) {
			if (nivelTaqui >= 0.7)
				return "TAQUICARDIA SEVERA";
			if (nivelTaqui >= 0.4)
				return "TAQUICARDIA MODERADA";
			return "TAQUICARDIA LEVE";
		}

		return "RITMO LIMITE";
	}
}
