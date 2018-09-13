import org.apache.commons.math3.distribution.GammaDistribution;
import org.apache.commons.math3.distribution.LogNormalDistribution;

import java.util.*;
import java.util.Random;


public class Simulation {

    private static Integer DELTA = 10;
    private static Integer TIME = 0;
    private static Integer FINALTIME;

    private static Double BLOCKLIMIT = 1000000D;
    private static Double EMPTYBLOCK = 0D;
    private static Integer MAXFEE = 0;
    private static Double BLOCK = 0D;
    private static Integer QUEUES = 0;

    private static Random RANDOM = new Random();

    private static List<Integer> STLL = new ArrayList<>();
    private static List<Integer> STS = new ArrayList<>();
    private static List<Integer> NT = new ArrayList<>();

    private static Double AVERAGETXSIZE;

    private static HttpClient HTTPCLIENT = new HttpClient();


    private static double SCALEGAMMA = 12.121;
    private static double SHAPEGAMMA = 0.20344;
    private static GammaDistribution TXARRDISTRIBUTION = new GammaDistribution(SCALEGAMMA, SHAPEGAMMA);

    private static double SCALELOGNORMAL = 0.71902;
    private static double SHAPELOGNORMAL = 8.5645;
    private static LogNormalDistribution FEEDISTRIBUTION = new LogNormalDistribution(SCALELOGNORMAL, SHAPELOGNORMAL);

    private static  List<Queue<Double>> queues = new ArrayList<>();

    public void run(Integer tf, Integer queuesLimit) {

        FINALTIME = tf;
        QUEUES = queuesLimit;
        MAXFEE = HTTPCLIENT.getMaxFee();
        AVERAGETXSIZE = HTTPCLIENT.getAverageSizeTx();
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

    }

    private void emptySystem() {

        while(!areQueuesEmpty()) {
            processExits();
        }

    }

    private boolean areQueuesEmpty() {
        return queues.stream().allMatch(Collection::isEmpty);
    }

    private void printResult() {

        System.out.println("--------------RESULTADOS-----------------");

        for(int N = 0; N < QUEUES; N++) {
            Integer PPS = STS.get(N) - STLL.get(N) / NT.get(N);
            System.out.println("El promedio de permanencia en la cola " + N + " es de: " + PPS);
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
        return (int) TXARRDISTRIBUTION.inverseCumulativeProbability(random()) * 1000;
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

}
