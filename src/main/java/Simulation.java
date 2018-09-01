import org.apache.commons.math3.distribution.GammaDistribution;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public class Simulation {

    private static Integer DELTA = 10;
    private static Integer TIME = 0;
    private static Integer FINALTIME;

    private static Double BLOCKLIMIT = 0D;
    private static Double EMPTYBLOCK = 0D;
    private static Integer MAXFEE = 0;
    private static Double BLOCK = 0D;
    private static Integer QUEUES = 0;

    private static List<Integer> STLL = new LinkedList<>();
    private static List<Integer> STS = new LinkedList<>();
    private static List<Integer> NT = new LinkedList<>();

    private static HttpClient HTTPCLIENT = new HttpClient();


    private static double ALPHAGAMMA = 12.121;
    private static double BETAGAMMA = 0.20344;
    private static GammaDistribution TXARRDISTRIBUTION = new GammaDistribution(ALPHAGAMMA, BETAGAMMA);

    private static List<Queue<Transaction>> queues = new LinkedList<>();


    public void run(Integer tf, Integer queues, Double blockLimit) {

        FINALTIME = tf;
        BLOCKLIMIT = blockLimit;
        QUEUES = queues;

        while (TIME < FINALTIME) {

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

            Transaction tx = new Transaction(sizeTx(), feeTx());
            putInQueue(tx, MAXFEE/QUEUES);
            STLL.add(MAXFEE/QUEUES, STLL.get(MAXFEE/QUEUES) + TIME);

        }

    }

    private void putInQueue(Transaction tx, Integer queueDelta) {
        Integer queuePosition = 0;

        while( tx.getFee() > queuePosition ) {
            queuePosition += queueDelta;
        }

        queues.get(queuePosition/queueDelta).add(tx);

    }

    private Double feeTx() {
        return 0D;
    }

    private Double sizeTx() {
        return HTTPCLIENT.getAverageSizeTx();
    }

    private Integer txArr() {
        return 0;
    }

    private void processExits() {

        for(int N = 0; N<QUEUES; N++) {
            if(queues.get(N).size() > 0 && BLOCK < BLOCKLIMIT) {
                emptyQueue(queues.get(N), BLOCKLIMIT - BLOCK, N);
            }
        }
    }

    private void emptyQueue(Queue<Transaction> transactions, Double blockFreeSpace,  Integer index) {

        while( blockFreeSpace > 0 ) {

            if(transactions.peek() != null && transactions.peek().getSize() <= blockFreeSpace) {
            Transaction tx = transactions.remove();
            BLOCK = tx.getSize();
            blockFreeSpace -= tx.getSize();
            STS.add(index, STS.get(index) + TIME);
            NT.add(index, NT.get(index) + 1);
            }
            else {
                break;
            }

        }

    }

    private void countEmptyBlock() {
        if(BLOCK < BLOCKLIMIT) {
            EMPTYBLOCK++;
        }
    }

}
