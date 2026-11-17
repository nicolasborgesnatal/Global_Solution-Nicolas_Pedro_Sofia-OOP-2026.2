package br.com.citizentech.model;

// Pluviômetro: mede a intensidade da chuva em mm/h
public class SensorChuva extends ComponenteTerritorial implements Sensor {

    private static final double CHANCE_FALHA = 0.05;
    private static final int CAPACIDADE_HISTORICO = 48;

    private HistoricoLeituras historico;
    private boolean ultimaLeituraFalhou;

    public SensorChuva(String id, String nome, String regiao) {
        super(id, nome, regiao);
        this.historico = new HistoricoLeituras(CAPACIDADE_HISTORICO);
        this.ultimaLeituraFalhou = false;
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
        double valor = this.simularIntensidade();
        this.ultimaLeituraFalhou = false;
        this.historico.registrar(valor);
        return valor;
    }

    // Maioria das horas com chuva fraca, poucas com temporal
    private double simularIntensidade() {
        double sorteio = Math.random();
        double valor;
        if (sorteio < 0.6) {
            valor = Math.random() * 10;
        } else if (sorteio < 0.9) {
            valor = 10 + Math.random() * 20;
        } else {
            valor = 30 + Math.random() * 40;
        }
        return Math.round(valor * 10) / 10.0;
    }

    @Override
    public boolean verificarFuncionamento() {
        return this.isAtivo() && !this.ultimaLeituraFalhou;
    }

    @Override
    public String getTipo() {
        return TIPO_CHUVA;
    }

    @Override
    public String getUnidade() {
        return "mm/h";
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
            return this.getNome() + " DESATIVADO: sem leituras de chuva.";
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