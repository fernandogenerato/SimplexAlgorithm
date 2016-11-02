package simplex;

import java.util.ArrayList;

public class Restricao {

    private Double limite;
    private ArrayList<Double> variaveis = new ArrayList<Double>();

    public void addVariavel(Double valor) {
        this.variaveis.add(valor);
    }

    public Double getLista(int indicie) {
        return this.variaveis.get(indicie);
    }

    public void setLimite(Double valor) {
        this.limite = valor;
    }

    public Double getLimite() {
        return this.limite;
    }
}
