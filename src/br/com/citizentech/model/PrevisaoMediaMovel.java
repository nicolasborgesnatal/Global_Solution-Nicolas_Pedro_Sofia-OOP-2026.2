package br.com.citizentech.model;

import java.util.List;

// Prevê a próxima leitura pela média das últimas N leituras
public class PrevisaoMediaMovel extends EstrategiaPrevisao {

    private int tamanhoJanela;

    public PrevisaoMediaMovel(int tamanhoJanela) {
        // A janela é também o mínimo de leituras para prever
        super("Média Móvel", tamanhoJanela);
        this.tamanhoJanela = this.getLeiturasMinimas();
    }

    @Override
    public double prever(List<Double> leituras) {
        if (!this.podePrever(leituras)) {
            return SEM_PREVISAO;
        }
        int fim = leituras.size();
        int inicio = fim - this.tamanhoJanela;
        return this.arredondar(this.calcularMedia(leituras, inicio, fim));
    }

    @Override
    public String explicar(List<Double> leituras) {
        if (!this.podePrever(leituras)) {
            return this.getMotivoSemPrevisao(leituras);
        }
        int fim = leituras.size();
        return "Últimas " + this.tamanhoJanela + " leituras: " + leituras.subList(fim - this.tamanhoJanela, fim)
                + " | A próxima deve ficar perto da média: " + this.prever(leituras);
    }

    @Override
    public String getDescricao() {
        return super.getDescricao() + " | janela das últimas " + this.tamanhoJanela + " leituras";
    }

    public int getTamanhoJanela() {
        return this.tamanhoJanela;
    }
}