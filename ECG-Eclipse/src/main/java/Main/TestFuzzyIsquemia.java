package Main;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.rule.Variable;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;

public class TestFuzzyIsquemia {
    public static void main(String[] args) {

        String fileName = "src/main/resources/Reglas/control.fcl";
        FIS fis = FIS.load(fileName, true);

        if (fis == null) {
            System.err.println("Error al cargar archivo FCL");
            return;
        }

        // Valores de ejemplo
        fis.setVariable("tpeak", -8); 
        fis.setVariable("stamp", -3);

        // Evaluar reglas
        fis.evaluate();

        // Obtener variable de salida
        Variable riesgo = fis.getVariable("riesgo_isquemia");
        System.out.println("Riesgo de isquemia = " + riesgo.getValue());
        System.out.println(riesgo.toString());

        // Mostrar gráficos de la variable de salida
        JFuzzyChart.get().chart(riesgo, riesgo.getDefuzzifier(), true);

        // Opcional: mostrar gráficos de las variables de entrada
        JFuzzyChart.get().chart(fis.getVariable("tpeak"), true);
        JFuzzyChart.get().chart(fis.getVariable("stamp"), true);
    }
}
