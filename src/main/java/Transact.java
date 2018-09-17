import org.omg.CORBA.TRANSACTION_MODE;

public class Transact {

    Double tiempoMinimo;
    Double fee;
    Integer cantidad;

    public Transact(Integer cantidadDeTransacciones, Double tiempoMinimo, Double feeAPagar){
        this.fee = feeAPagar;
        this.cantidad = cantidadDeTransacciones;
        this.tiempoMinimo = tiempoMinimo;

    }

    public double getTiempo() {return this.tiempoMinimo;}
    public double getfee()  {return this.fee;}

}
