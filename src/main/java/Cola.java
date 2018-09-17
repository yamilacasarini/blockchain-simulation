public class Cola {

    private Double time;
    private Double costo;
    Integer cantidadOptima = 0;

    public Cola(Double costoMinimo,Double costoMaximo, Double time){
        this.costo = costoMinimo;
        this.time = time;
    }

    public Integer getMaxSize(Transact tx){
        if(tx.tiempoMinimo < time){
            return 0;
        }else{
            System.out.println(Integer.valueOf((int) (tx.fee/costo)));
            return Integer.valueOf((int) (tx.fee/costo));

        }
    }


    public Integer getCantidadOptima() {return this.cantidadOptima;}
    public Double getTime(){return this.time;}
    public Double getCosto(){return this.costo;}
    public void setCantidadOptima(Integer cantidadOptima){this.cantidadOptima = cantidadOptima;}
}
