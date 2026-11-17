package br.com.citizentech.model;

// Registro de um alerta: qual nível, qual sensor e por quê
public class Alerta {

    public static final String NIVEL_ATENCAO = "ATENÇÃO";
    public static final String NIVEL_ALERTA = "ALERTA";
    public static final String NIVEL_CRITICO = "CRÍTICO";

    private String nivel;
    private Sensor origem;
    private String explicacao;

    public Alerta(String nivel, Sensor origem, String explicacao) {
        this.setNivel(nivel);
        this.origem = origem;
        this.explicacao = explicacao;
    }

    // O que a comunidade deve fazer em cada nível
    public String getRecomendacao() {
        if (NIVEL_CRITICO.equals(this.nivel)) {
            return "Procure um local seguro agora e siga as orientações da Defesa Civil (199).";
        }
        if (NIVEL_ALERTA.equals(this.nivel)) {
            return "Evite as margens do córrego e as áreas baixas. Prepare documentos e remédios.";
        }
        return "Acompanhe os avisos do painel. Ainda não há risco imediato.";
    }

    // Texto curto para o painel da rua
    public String getMensagemPainel() {
        return "[" + this.nivel + "] " + this.origem.getNome() + " | " + this.getRecomendacao();
    }

    public String getDescricao() {
        return "[" + this.nivel + "] " + this.origem.getNome() + " (" + this.origem.getTipo() + ")\n"
                + this.explicacao + "\nO que fazer: " + this.getRecomendacao();
    }

    public boolean isCritico() {
        return NIVEL_CRITICO.equals(this.nivel);
    }

    public String getNivel() {
        return this.nivel;
    }

    public Sensor getOrigem() {
        return this.origem;
    }

    public String getExplicacao() {
        return this.explicacao;
    }

    private void setNivel(String nivel) {
        if (NIVEL_ATENCAO.equals(nivel) || NIVEL_ALERTA.equals(nivel) || NIVEL_CRITICO.equals(nivel)) {
            this.nivel = nivel;
            return;
        }
        System.out.println("Erro: nível de alerta desconhecido (" + nivel + "). Usando " + NIVEL_ATENCAO + ".");
        this.nivel = NIVEL_ATENCAO;
    }
}