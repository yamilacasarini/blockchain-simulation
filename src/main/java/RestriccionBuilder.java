import org.apache.commons.math3.optim.linear.LinearConstraint;
import org.apache.commons.math3.optim.linear.Relationship;
import java.util.ArrayList;
import java.util.List;

public class RestriccionBuilder {
    private static int i;
    private static int posicion;
    double dineroMaxiomo;

    public static ArrayList<LinearConstraint> getRestricciones(List<Cola> cola, Transact tx){

        ArrayList<LinearConstraint> restricciones = new ArrayList<LinearConstraint>();
        ArrayList<LinearConstraint> restriccionesPesos = crearRestriccionPesos(cola,tx);
        LinearConstraint restriccionPrincipal = crearRestriccionPrincipal(cola,tx.fee);

        restricciones.addAll(restriccionesPesos);
        restricciones.add(restriccionPrincipal);
        restricciones.add(crearRestriccionPrincipal2(cola,tx));

        return restricciones;
    }


    private static LinearConstraint crearRestriccionPrincipal(List<Cola> colas, double dineroMaximo){

        i = 0;
        double coeficientes[] = new double[colas.size()];

        colas.forEach(cola -> {
            coeficientes[i]=cola.getTime();
            System.out.println(coeficientes[i]);
            i++;
        });

        LinearConstraint restriccionPrincipal = new LinearConstraint(coeficientes, Relationship.LEQ, dineroMaximo);
        System.out.println("fee max " + dineroMaximo);


        return restriccionPrincipal;
    }

    private static LinearConstraint crearRestriccionPrincipal2(List<Cola> colas, Transact tx){

        i = 0;
        double coeficientes[] = new double[colas.size()];

        colas.forEach(cola -> {
            coeficientes[i]=1;
            i++;
        });

        LinearConstraint restriccionPrincipal = new LinearConstraint(coeficientes, Relationship.EQ, tx.cantidad);


        return restriccionPrincipal;
    }



    private static ArrayList<LinearConstraint> crearRestriccionPesos(List<Cola> colas, Transact transaccion) {

        posicion = 0;
        ArrayList<LinearConstraint> restricciones = new ArrayList<LinearConstraint>();

        colas.forEach(cola -> {
           // LinearConstraint restriccionMayorA = new LinearConstraint(prepararArray(cola, posicion, colas), Relationship.GEQ, cola.getMaxSize(transaccion));
           LinearConstraint restriccionMenorA = new LinearConstraint(prepararArray(cola, posicion, colas), Relationship.LEQ, cola.getMaxSize(transaccion));
           //  restricciones.add(restriccionMenorA);
           // restricciones.add(restriccionMayorA);
            posicion++;
        });

        return restricciones;
    }


    private static double[] prepararArray(Cola cola, int posicion, List<Cola> colas) {
        double array[] = new double[colas.size()];
        array[posicion] = 1;
        return array;
    }

}
