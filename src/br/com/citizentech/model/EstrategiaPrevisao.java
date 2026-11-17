package br.com.citizentech.model;

import java.util.List;

// Molde de toda forma de prever a próxima leitura de um sensor
public abstract class EstrategiaPrevisao {

    // Valor devolvido quando não há leituras suficientes para prever
    public static final double SEM_PREVISAO = -1.0;

    private static final int LEITURAS_MINIMAS_ACEITAS = 2;
    private static final int LEITURAS_MINIMAS_PADRAO = 5;

    private String nome;
    private int leiturasMinimas;

    public EstrategiaPrevisao(String nome, int leiturasMinimas) {
        this.nome = nome;
        this.setLeiturasMinimas(leiturasMinimas);
    }

    // Cada estratégia faz a sua própria conta
    public abstract double prever(List<Double> leituras);

    // Cada estratégia explica o que a sua previsão significa
    public abstract String explicar(List<Double> leituras);

    // Com poucas leituras o sistema se cala em vez de chutar
    public boolean podePrever(List<Double> leituras) {
        return leituras != null && leituras.size() >= this.leiturasMinimas;
    }

    public String getMotivoSemPrevisao(List<Double> leituras) {
        int quantidade = (leituras == null) ? 0 : leituras.size();
        return this.nome + " precisa de " + this.leiturasMinimas + " leituras e só há " + quantidade
                + ". Sem previsão por enquanto.";
    }

    public String getDescricao() {
        return this.nome + " (mínimo de " + this.leiturasMinimas + " leituras)";
    }

    // Média de um trecho da lista: [inicio, fim)
    protected double calcularMedia(List<Double> leituras, int inicio, int fim) {
        double soma = 0;
        for (int i = inicio; i < fim; i++) {
            soma += leituras.get(i);
        }
        return soma / (fim - inicio);
    }

    protected double arredondar(double valor) {
        return Math.round(valor * 100) / 100.0;
    }

    public String getNome() {
        return this.nome;
    }

    public int getLeiturasMinimas() {
        return this.leiturasMinimas;
    }

    private void setLeiturasMinimas(int leiturasMinimas) {
        if (leiturasMinimas < LEITURAS_MINIMAS_ACEITAS) {
            System.out.println("Erro: uma previsão precisa de pelo menos " + LEITURAS_MINIMAS_ACEITAS
                    + " leituras. Usando " + LEITURAS_MINIMAS_PADRAO + ".");
            this.leiturasMinimas = LEITURAS_MINIMAS_PADRAO;
            return;
        }
        this.leiturasMinimas = leiturasMinimas;
    }
}