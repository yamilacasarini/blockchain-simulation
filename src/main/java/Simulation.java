import java.util.*;
import info.blockchain.api.*;
import info.blockchain.api.statistics.StatisticsResponse;

public class Simulation {

    private static Integer DELTA = 10;
    private static Integer TIME = 0;
    private static Integer FINALTIME;

    private static Integer BLOCKLIMIT = 0;
    private static Integer EMPTYBLOCK = 0;
    private static Integer MAXFEE = 0;
    private static Integer BLOCK = 0;
    private static Integer QUEUES = 0;

    private static List<Integer> STLL = new LinkedList<>();
    private static List<Integer> STS = new LinkedList<>();
    private static List<Integer> NT = new LinkedList<>();

    private static List<Queue<Transaction>> queues = new LinkedList<>();

    public void run(Integer tf, Integer queues, Integer blockLimit) {

        FINALTIME = tf;
        BLOCKLIMIT = blockLimit;
        QUEUES = queues;

        while (TIME < FINALTIME) {

            TIME += DELTA;
            BLOCK = 0;

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

    private Integer feeTx() {
        return 0;
    }

    private Integer sizeTx() {
        return 0; }

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

    private void emptyQueue(Queue<Transaction> transactions, Integer blockFreeSpace,  Integer index) {

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
