import org.apache.commons.math3.optim.MaxIter;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.linear.*;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Simplex {

        private static int posicion;
        private static double horasCorrientes;
        static List<LinearConstraint> restricciones = new ArrayList<LinearConstraint>();
        public List<Cola> colas = new ArrayList<Cola>();

        public static HashMap<Cola, Double> configuracionOptima(List<Cola> colas, Transact tx) {

            SimplexSolver simplex = new SimplexSolver();

            HashMap<Cola, Double> configuracionOptima = new HashMap<Cola, Double>();

            double array[] = new double[colas.size()];
            Arrays.fill(array, 1);
            LinearObjectiveFunction funcionAOptimizar = new LinearObjectiveFunction(array, 0);

            restricciones = RestriccionBuilder.getRestricciones(colas, tx);

            PointValuePair resultado = simplex.optimize(new MaxIter(100), funcionAOptimizar,
                    new LinearConstraintSet(restricciones), GoalType.MINIMIZE, new NonNegativeConstraint(true));

            double horasTotales = resultado.getValue();

            double[] arrayEnviosOptimos = resultado.getPoint();

            posicion = 0;

            colas.forEach(cola -> {
                int pesoOptimo = (int) arrayEnviosOptimos[posicion];
                cola.setCantidadOptima(pesoOptimo);
                configuracionOptima.put(colas.get(posicion), (double) pesoOptimo);
                posicion++;
            });

            return configuracionOptima;

        }
}

