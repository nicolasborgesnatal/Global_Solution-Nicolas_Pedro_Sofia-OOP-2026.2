package br.com.citizentech.model;

// Régua eletrônica: mede o nível do córrego em metros
public class SensorNivelRio extends ComponenteTerritorial implements Sensor {

    private static final double CHANCE_FALHA = 0.05;
    private static final int CAPACIDADE_HISTORICO = 48;
    private static final double NIVEL_MINIMO = 0.5;
    private static final double NIVEL_MAXIMO = 6.0;
    private static final double NIVEL_INICIAL = 2.0;
    private static final double CHANCE_INICIAR_CHEIA = 0.06;
    private static final double CHANCE_TERMINAR_CHEIA = 0.2;
    private static final double FATOR_RETORNO = 0.15;

    private HistoricoLeituras historico;
    private boolean ultimaLeituraFalhou;
    private double nivelAtual;
    private boolean emCheia;

    public SensorNivelRio(String id, String nome, String regiao) {
        super(id, nome, regiao);
        this.historico = new HistoricoLeituras(CAPACIDADE_HISTORICO);
        this.ultimaLeituraFalhou = false;
        this.nivelAtual = NIVEL_INICIAL;
        this.emCheia = false;
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
        this.historico.registrar(this.nivelAtual);
        return this.nivelAtual;
    }

    // Na cheia o rio sobe; fora dela volta aos poucos ao nível normal
    private void simularVariacao() {
        this.atualizarCheia();
        double variacao;
        if (this.emCheia) {
            variacao = 0.1 + Math.random() * 0.4;
        } else {
            variacao = (NIVEL_INICIAL - this.nivelAtual) * FATOR_RETORNO - 0.15 + Math.random() * 0.3;
        }
        double novoNivel = this.nivelAtual + variacao;
        if (novoNivel < NIVEL_MINIMO) {
            novoNivel = NIVEL_MINIMO;
        }
        if (novoNivel > NIVEL_MAXIMO) {
            novoNivel = NIVEL_MAXIMO;
        }
        this.nivelAtual = Math.round(novoNivel * 100) / 100.0;
    }

    // Sorteia se a cheia começa ou termina nesta hora
    private void atualizarCheia() {
        if (this.emCheia) {
            this.emCheia = Math.random() >= CHANCE_TERMINAR_CHEIA;
        } else {
            this.emCheia = Math.random() < CHANCE_INICIAR_CHEIA;
        }
    }

    @Override
    public boolean verificarFuncionamento() {
        return this.isAtivo() && !this.ultimaLeituraFalhou;
    }

    @Override
    public String getTipo() {
        return TIPO_NIVEL_RIO;
    }

    @Override
    public String getUnidade() {
        return "m";
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
            return this.getNome() + " DESATIVADO: sem leituras do nível do rio.";
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