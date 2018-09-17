import java.io.IOException;
import java.util.Scanner;

public class App {

    public static void main(String [] args) throws IOException {
        System.out.println("Bienvenido");
        System.out.println("Ingrese la cantidad de colas: ");
        Integer tf =  Integer.valueOf(new Scanner(System.in).nextLine());
        System.out.println("Ingrese el tiempo final (en minutos): ");
        Integer queues =  Integer.valueOf(new Scanner(System.in).nextLine());;
        Simulation simulation = new Simulation();

        simulation.run(queues, tf);
    }


}
