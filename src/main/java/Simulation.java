import org.apache.commons.math3.distribution.GammaDistribution;
import org.apache.commons.math3.distribution.LogNormalDistribution;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.Random;

import static com.sun.xml.internal.ws.spi.db.BindingContextFactory.LOGGER;


public class Simulation {


    private static Integer DELTA = 10;
    private static Integer TIME = 0;
    private static Integer FINALTIME;

    private static List<Double> PPS = new ArrayList<>();
    private static List<Double> POT = new ArrayList<>();

    private static Double BLOCKLIMIT =  1000000D;
    private static Double EMPTYBLOCK = 0D;
    private static Integer MAXFEE = 0;
    private static Double BLOCK = 0D;
    private static Integer QUEUES = 0;

    private static Random RANDOM = new Random();

    private static List<Integer> STLL = new ArrayList<>();
    private static List<Integer> STS = new ArrayList<>();
    private static List<Integer> NT = new ArrayList<>();
    private static Integer NTOTAL = 0;

    private static Double AVERAGETXSIZE;

    private static HttpClient HTTPCLIENT = new HttpClient();


    private static double SCALEGAMMA = 16.96;
    private static double SHAPEGAMMA = 0.2344;
    private static GammaDistribution TXARRDISTRIBUTION = new GammaDistribution(SCALEGAMMA, SHAPEGAMMA);

    private static double SCALELOGNORMAL = 0.71902;
    private static double SHAPELOGNORMAL = 8.5645;
    private static LogNormalDistribution FEEDISTRIBUTION = new LogNormalDistribution(SCALELOGNORMAL, SHAPELOGNORMAL);

    private static List<Queue<Double>> queues = new ArrayList<>();
    private static BufferedWriter writer;
    private static List<Cola> listaDeColas = new ArrayList<>();
    private static List<Double> listaDeFees = new ArrayList<>();
    private static Transact tx;
    private static Simplex simplex;


    public void run(Integer tf, Integer queuesLimit) throws IOException {

        initializeFee();

        writer = new BufferedWriter(new FileWriter("BitcoinStats.xls"));

        FINALTIME = tf;
        QUEUES = queuesLimit;
        AVERAGETXSIZE = HTTPCLIENT.getAverageSizeTx();
        MAXFEE = (HTTPCLIENT.getMaxFee() / 1024) * AVERAGETXSIZE.intValue();
        RANDOM = new Random();


        for (int N = 0; N < queuesLimit; N++) {
            Queue queue = new LinkedList<String>();
            queues.add(queue);

            STLL.add(0);
            STS.add(0);
            NT.add(0);
        }


        while (TIME < FINALTIME) {

            System.out.println("TIME: " + TIME);

            TIME += DELTA;
            BLOCK = 0D;

            processArrival();

            processExits();

            countEmptyBlock();
        }

        emptySystem();
        printResult();

        printAnswer();

        for (int i = 0;i<QUEUES;i++){

            Double costoMinimo = Double.valueOf(i*(MAXFEE/QUEUES));
            Cola cola = new Cola(costoMinimo,1D,PPS.get(i));
            listaDeColas.add(i,cola);
        }

        tx = new Transact(7,60D,10000D);

        Simplex.configuracionOptima(listaDeColas,tx);

        for (Cola cola:listaDeColas){
            System.out.println("cantidadOptima"+ cola.cantidadOptima);
        }

    }

    private void emptySystem() {

        while(!areQueuesEmpty()) {
            BLOCK = 0D;
            processExits();
        }

    }

    private boolean areQueuesEmpty() {
        return queues.stream().allMatch(Collection::isEmpty);
    }

    private void printResult() {

        System.out.println("--------------RESULTADOS-----------------");

        for(int N = 0; N < QUEUES; N++) {
            if(NT.get(N) != 0) {
                Integer PPS = STS.get(N) - STLL.get(N) / NT.get(N);
                System.out.println("El promedio de permanencia en la cola " + N + " es de: " + PPS);
            } else {
                System.out.println("La cola " + N + " no tuvo transacciones");
            }
        }

        System.out.println("El bloque no estuvo lleno al 100% " + EMPTYBLOCK + " veces");

    }

    void processArrival() {

        Integer transactions = txArr();

        for(int N = 0; N<transactions; N++) {
            Integer index = putInQueue(MAXFEE/QUEUES);
            STLL.set(index, STLL.get(index) + TIME);
        }

    }

    private Integer putInQueue(Integer queueDelta) {
        Integer queuePosition = 0;
        Integer index = -1;

        while( feeTx() >= queuePosition ) {
            queuePosition += queueDelta;
            index ++;
        }

        if(index >= queues.size()) {
            index = queues.size()- 1;
        }

        queues.get(index).add(1D);

        return index;

    }

    private static Double feeTx() {
        return FEEDISTRIBUTION.inverseCumulativeProbability(random());
    }

    private Double sizeTx() {
        return AVERAGETXSIZE;
    }

    private Integer txArr() {
        return (int) TXARRDISTRIBUTION.inverseCumulativeProbability(random()) * 600;
    }

    private void processExits() {

        for(int N = 0; N<QUEUES; N++) {
            if(queues.get(N).size() > 0 && BLOCK < BLOCKLIMIT) {
                emptyQueue(queues.get(N), BLOCKLIMIT - BLOCK, N);
            }
        }
    }

    private void emptyQueue(Queue<Double> transactions, Double blockFreeSpace,  Integer index) {

        while( blockFreeSpace > 0 ) {

            if(transactions.peek() != null && sizeTx() <= blockFreeSpace) {
            transactions.remove();
            BLOCK += sizeTx();
            blockFreeSpace -= sizeTx();
            STS.set(index, STS.get(index) + TIME);
            NT.set(index, NT.get(index) + 1);
            NTOTAL += 1;
            }
            else {
                break;
            }

        }

    }

    private static double random() {

        double rangeMin = 0D;
        double rangeMax = 1D;
        return rangeMin + (rangeMax - rangeMin) * RANDOM.nextDouble();

    }

    private void countEmptyBlock() {
        if(BLOCK < BLOCKLIMIT) {
            EMPTYBLOCK++;
        }
    }


    public static void printAnswer() throws IOException {


        initialize(PPS,0,QUEUES);
        initialize(POT,0,QUEUES);

       // Integer NTLineTotal = Arrays.asList(NTLine).stream().mapToInt(Integer::intValue).sum();

        // Calculate the results, average of waiting team for each line
        for (int i = 0; i < QUEUES; i++)
            if(NT.get(i)!=0) {
                PPS.set(i,Double.valueOf( STS.get(i) - STLL.get(i) / NT.get(i)));
            }else{
                PPS.set(i, 0D);
            }

        writer.write("line: "+"\t"+"PPS[i]"+"\t"+"% OF TRANSACTIONS[i]"+"\n");
        // Calculate the results, percentage of processed transactions in that line on total transactions.
        for (int i = 0; i < QUEUES; i++) {
            if (NT.get(i) != 0){
                POT.set(i, (Double.valueOf(NT.get(i)) * 100) / NTOTAL);
                POT.set(i, POT.get(i) * 100 / 100);
            }else{
                POT.set(i, null);
            }
            writer.write((i+1)+"\t"+String.valueOf(PPS.get(i))+"\t"+ POT.get(i) +"\n");
        }
        LOGGER.info("SIMULATION NUMBER: " + 1 + " | LINES: "+ QUEUES + " | FINAL TIME: "+ FINALTIME );
        for (int i = 0; i < QUEUES; i++) {
            LOGGER.info("Waiting time in the line:" + (i + 1) + " = " + PPS.get(i) + "\n" + "Percentage of transactions in " +
                    "the line " + (i + 1) + " of the total: " + POT.get(i));
        }

        writer.close();

    }

    private static void initialize(List lista,int valorInicial,int cantidad){
        for (int i = 0; i < cantidad; i++){
            lista.add(i, valorInicial);
            
        }
    }

    private static void initializeFee(){
        for(int i = 0; i < QUEUES; i++){
            listaDeFees.add(i, (double) (i*(MAXFEE/QUEUES)));
        }
    }


}
