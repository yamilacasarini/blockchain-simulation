import java.util.Scanner;

public class App {

    public static void main(String [] args)
    {
        System.out.println("Bienvenido");
        System.out.println("Ingrese el tiempo final en minutos: ");
        //Integer tf =  Integer.valueOf(new Scanner(System.in).nextLine());
        System.out.println("Ingrese cantidad de colas: ");
        //Integer queues =  Integer.valueOf(new Scanner(System.in).nextLine());;
        Simulation simulation = new Simulation();

        simulation.run(60, 13);
    }


}
