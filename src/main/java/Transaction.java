public class Transaction {

    private Double size;
    private Double fee;

    public Transaction(Double size, Double fee) {
        this.size = size;
        this.fee = fee;
    }

    public Double getSize() {
        return size;
    }

    public void setSize(Double size) {
        this.size = size;
    }

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }

}
