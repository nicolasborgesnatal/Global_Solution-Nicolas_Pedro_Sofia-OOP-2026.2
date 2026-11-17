package br.com.citizentech.model;

import java.util.List;

// Compara o período atual com o anterior e projeta a tendência
public class PrevisaoComparacao extends EstrategiaPrevisao {

    private int tamanhoPeriodo;

    public PrevisaoComparacao(int tamanhoPeriodo) {
        // Precisa de dois períodos completos: o atual e o anterior
        super("Comparação de Períodos", tamanhoPeriodo * 2);
        this.tamanhoPeriodo = this.getLeiturasMinimas() / 2;
    }

    @Override
    public double prever(List<Double> leituras) {
        if (!this.podePrever(leituras)) {
            return SEM_PREVISAO;
        }
        double mediaAtual = this.calcularMediaAtual(leituras);
        double mediaAnterior = this.calcularMediaAnterior(leituras);
        // Se subiu X de um período para o outro, deve subir X de novo
        double previsto = mediaAtual + (mediaAtual - mediaAnterior);
        if (previsto < 0) {
            previsto = 0;
        }
        return this.arredondar(previsto);
    }

    // Explica em texto o que a comparação mostrou
    @Override
    public String explicar(List<Double> leituras) {
        if (!this.podePrever(leituras)) {
            return this.getMotivoSemPrevisao(leituras);
        }
        double mediaAtual = this.arredondar(this.calcularMediaAtual(leituras));
        double mediaAnterior = this.arredondar(this.calcularMediaAnterior(leituras));
        String direcao;
        if (mediaAtual > mediaAnterior) {
            direcao = "SUBINDO";
        } else if (mediaAtual < mediaAnterior) {
            direcao = "DESCENDO";
        } else {
            direcao = "ESTÁVEL";
        }
        return "Período atual: " + mediaAtual + " | Período anterior: " + mediaAnterior + " | Tendência: " + direcao
                + " (" + this.calcularVariacaoPercentual(mediaAtual, mediaAnterior) + "%)";
    }

    @Override
    public String getDescricao() {
        return super.getDescricao() + " | compara as últimas " + this.tamanhoPeriodo + " leituras com as "
                + this.tamanhoPeriodo + " anteriores";
    }

    public int getTamanhoPeriodo() {
        return this.tamanhoPeriodo;
    }

    private double calcularMediaAtual(List<Double> leituras) {
        int fim = leituras.size();
        return this.calcularMedia(leituras, fim - this.tamanhoPeriodo, fim);
    }

    private double calcularMediaAnterior(List<Double> leituras) {
        int fim = leituras.size() - this.tamanhoPeriodo;
        return this.calcularMedia(leituras, fim - this.tamanhoPeriodo, fim);
    }

    private double calcularVariacaoPercentual(double atual, double anterior) {
        if (anterior == 0) {
            return 0.0;
        }
        return this.arredondar((atual - anterior) / anterior * 100);
    }
}