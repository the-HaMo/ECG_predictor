package Main;
import Clases.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
public class LectorFicheroPrueba {


	    public static void main(String[] args) {
	    	
			KieServices ks=KieServices.Factory.get();
			KieContainer kContainer=ks.getKieClasspathContainer();
			
			KieSession kSession=kContainer.newKieSession("ksession-Rules-dsi");
			
	        String rutaArchivo = "ecg.txt"; // cambia según tu ruta
	        List<Onda> ondas = new ArrayList<>();
	        String patron = "([PQRST])\\((\\d+),(\\d+),([-\\d\\.E]+)\\)";
	        Pattern regex = Pattern.compile(patron);

	        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
	            String linea;
	            while ((linea = br.readLine()) != null) {
	                linea = linea.trim();
	                if (linea.isEmpty() || linea.startsWith("#")) continue;

	                Matcher m = regex.matcher(linea);
	                if (m.find()) {
	                    String tipo = m.group(1);
	                    int inicio = Integer.parseInt(m.group(2));
	                    int fin = Integer.parseInt(m.group(3));
	                    double amp = Double.parseDouble(m.group(4));

	                    Onda o=null;
	                    if (tipo.equals("P")) {
	                        o = new Onda_P(inicio, fin, amp);
	                    } else if (tipo.equals("Q")) {
	                        o = new Onda_Q(inicio, fin, amp);
	                    } else if (tipo.equals("R")) {
	                        o = new Onda_R(inicio, fin, amp);
	                    } else if (tipo.equals("S")) {
	                        o = new Onda_S(inicio, fin, amp);
	                    } else if (tipo.equals("T")) {
	                        o = new Onda_T(inicio, fin, amp);
	                    }
	                    if(o!=null) {
	                    	ondas.add(o);
	                    	kSession.insert(o);
	                    }
	                }
	            }

	            System.out.println("Ondas leídas:");
	            ondas.forEach(System.out::println);

	            kSession.fireAllRules();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

}
