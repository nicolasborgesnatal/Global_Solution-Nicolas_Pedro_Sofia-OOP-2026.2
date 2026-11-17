package br.com.citizentech.model;

import java.util.ArrayList;
import java.util.List;

// Guarda as últimas leituras válidas de um sensor
public class HistoricoLeituras {

    private static final int CAPACIDADE_MINIMA = 5;
    private static final int CAPACIDADE_PADRAO = 48;

    private ArrayList<Double> leituras;
    private int capacidadeMaxima;

    public HistoricoLeituras(int capacidadeMaxima) {
        this.leituras = new ArrayList<>();
        this.setCapacidadeMaxima(capacidadeMaxima);
    }

    public void registrar(double valor) {
        this.leituras.add(valor);
        // Descarta a leitura mais antiga quando o histórico enche
        if (this.leituras.size() > this.capacidadeMaxima) {
            this.leituras.remove(0);
        }
    }

    // Devolve uma cópia: quem recebe não consegue alterar o histórico original
    public List<Double> getLeituras() {
        return new ArrayList<>(this.leituras);
    }

    public double getUltima() {
        if (this.estaVazio()) {
            return 0.0;
        }
        return this.leituras.get(this.leituras.size() - 1);
    }

    public int getQuantidade() {
        return this.leituras.size();
    }

    public boolean estaVazio() {
        return this.leituras.isEmpty();
    }

    public int getCapacidadeMaxima() {
        return this.capacidadeMaxima;
    }

    private void setCapacidadeMaxima(int capacidadeMaxima) {
        if (capacidadeMaxima < CAPACIDADE_MINIMA) {
            System.out.println("Erro: o histórico precisa guardar pelo menos " + CAPACIDADE_MINIMA
                    + " leituras. Usando " + CAPACIDADE_PADRAO + ".");
            this.capacidadeMaxima = CAPACIDADE_PADRAO;
            return;
        }
        this.capacidadeMaxima = capacidadeMaxima;
    }
}