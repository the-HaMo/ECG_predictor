package Parser;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import Clases.Onda;
import Clases.Onda_P;
import Clases.Onda_Q;
import Clases.Onda_R;
import Clases.Onda_S;
import Clases.Onda_T;

public class InputParser {

    // Patrón para líneas tipo: P(0,100,0.33)
	 private static final Pattern WAVE_PATTERN = Pattern.compile(
		        "(P|Q|R|S|T)\\((\\d+),(\\d+),(-?\\d+(?:\\.\\d+)?(?:[Ee][-+]?\\d+)?)\\)"
		    );

    public static List<Onda> parseFile(File file) throws IOException {
        List<Onda> ondas = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();

                // Ignorar líneas vacías o de cabecera
                if (line.isEmpty() || line.startsWith("#")) continue;

                Matcher m = WAVE_PATTERN.matcher(line);
                if (m.matches()) {
                    String tipo = m.group(1);
                    int inicio = Integer.parseInt(m.group(2));
                    int fin = Integer.parseInt(m.group(3));
                    double pico = Double.parseDouble(m.group(4));

                    switch (tipo) {
                        case "P": ondas.add(new Onda_P(inicio, fin, pico)); break;
                        case "Q": ondas.add(new Onda_Q(inicio, fin, pico)); break;
                        case "R": ondas.add(new Onda_R(inicio, fin, pico)); break;
                        case "S": ondas.add(new Onda_S(inicio, fin, pico)); break;
                        case "T": ondas.add(new Onda_T(inicio, fin, pico)); break;
                    }
                } else {
                    System.err.println("Línea ignorada (no válida): " + line);
                }
            }
        }

        return ondas;
    }
}
