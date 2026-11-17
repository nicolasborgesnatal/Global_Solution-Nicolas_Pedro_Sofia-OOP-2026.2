package br.com.citizentech.model;

// Sensor de umidade do solo (%): solo encharcado não absorve mais chuva
public class SensorUmidade extends ComponenteTerritorial implements Sensor {

    private static final double CHANCE_FALHA = 0.05;
    private static final int CAPACIDADE_HISTORICO = 48;
    private static final double UMIDADE_MINIMA = 20.0;
    private static final double UMIDADE_MAXIMA = 100.0;
    private static final double UMIDADE_INICIAL = 60.0;

    private HistoricoLeituras historico;
    private boolean ultimaLeituraFalhou;
    private double umidadeAtual;

    public SensorUmidade(String id, String nome, String regiao) {
        super(id, nome, regiao);
        this.historico = new HistoricoLeituras(CAPACIDADE_HISTORICO);
        this.ultimaLeituraFalhou = false;
        this.umidadeAtual = UMIDADE_INICIAL;
    }

    @Override
    public double lerValor() {
        if (!this.isAtivo()) {
            System.out.println("Erro: " + this.getNome() + " está desativado e não fez a leitura.");
            return LEITURA_INVALIDA;
        }
        if (Math.random() < CHANCE_FALHA) {
            this.ultimaLeituraFalhou = true;
            System.out.println("Falha: " + this.getNome() + " não respondeu nesta leitura.");
            return LEITURA_INVALIDA;
        }
        this.simularVariacao();
        this.ultimaLeituraFalhou = false;
        this.historico.registrar(this.umidadeAtual);
        return this.umidadeAtual;
    }

    // O solo seca devagar e encharca aos poucos
    private void simularVariacao() {
        double variacao = -3 + Math.random() * 8;
        double novaUmidade = this.umidadeAtual + variacao;
        if (novaUmidade < UMIDADE_MINIMA) {
            novaUmidade = UMIDADE_MINIMA;
        }
        if (novaUmidade > UMIDADE_MAXIMA) {
            novaUmidade = UMIDADE_MAXIMA;
        }
        this.umidadeAtual = Math.round(novaUmidade * 10) / 10.0;
    }

    @Override
    public boolean verificarFuncionamento() {
        return this.isAtivo() && !this.ultimaLeituraFalhou;
    }

    @Override
    public String getTipo() {
        return TIPO_UMIDADE;
    }

    @Override
    public String getUnidade() {
        return "%";
    }

    @Override
    public boolean ultrapassouLimite(double limite) {
        if (this.historico.estaVazio()) {
            return false;
        }
        return this.historico.getUltima() >= limite;
    }

    @Override
    public HistoricoLeituras getHistorico() {
        return this.historico;
    }

    @Override
    public String verificarStatus() {
        if (!this.isAtivo()) {
            return this.getNome() + " DESATIVADO: sem leituras de umidade do solo.";
        }
        if (this.ultimaLeituraFalhou) {
            return this.getNome() + " com FALHA na última leitura: verificar o equipamento.";
        }
        if (this.historico.estaVazio()) {
            return this.getNome() + " OK | Ainda sem leituras.";
        }
        return this.getNome() + " OK | Última leitura: " + this.historico.getUltima() + " " + this.getUnidade()
                + " | Leituras guardadas: " + this.historico.getQuantidade();
    }
}