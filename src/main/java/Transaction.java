public class Transaction {

    private Integer size;
    private Integer fee;

    public Transaction(Integer size, Integer fee) {
        this.size = size;
        this.fee = fee;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getFee() {
        return fee;
    }

    public void setFee(Integer fee) {
        this.fee = fee;
    }

}
