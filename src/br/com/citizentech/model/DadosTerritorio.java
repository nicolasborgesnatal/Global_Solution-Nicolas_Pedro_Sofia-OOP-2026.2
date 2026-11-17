package br.com.citizentech.model;

// Dados do território monitorado: tudo privado e validado
public class DadosTerritorio {

    // Faixas aceitas para cada limiar de alerta
    private static final double LIMITE_CHUVA_MIN = 5.0;
    private static final double LIMITE_CHUVA_MAX = 100.0;
    private static final double LIMITE_RIO_MIN = 0.5;
    private static final double LIMITE_RIO_MAX = 10.0;
    private static final double LIMITE_UMIDADE_MIN = 30.0;
    private static final double LIMITE_UMIDADE_MAX = 100.0;
    private static final int TAMANHO_MINIMO_CODIGO = 4;

    private String nomeRegiao;
    private int populacao;
    private String codigoAcesso;
    private double limiteChuva;
    private double limiteNivelRio;
    private double limiteUmidade;

    public DadosTerritorio(String nomeRegiao, int populacao, String codigoAcesso) {
        this.nomeRegiao = "Região não informada";
        this.setNomeRegiao(nomeRegiao);
        this.setPopulacao(populacao);
        this.definirCodigoInicial(codigoAcesso);
        // Limiares padrão de alerta
        this.limiteChuva = 30.0;
        this.limiteNivelRio = 3.5;
        this.limiteUmidade = 85.0;
    }

    // ---------- Região e população (validação simples) ----------

    public String getNomeRegiao() {
        return this.nomeRegiao;
    }

    public void setNomeRegiao(String nomeRegiao) {
        if (nomeRegiao == null || nomeRegiao.trim().isEmpty()) {
            System.out.println("Erro: o nome da região não pode ficar vazio. Mantido: " + this.nomeRegiao);
            return;
        }
        this.nomeRegiao = nomeRegiao.trim();
        System.out.println("Região definida: " + this.nomeRegiao);
    }

    public int getPopulacao() {
        return this.populacao;
    }

    public void setPopulacao(int populacao) {
        if (populacao <= 0) {
            System.out.println("Erro: a população deve ser maior que zero. Mantido: " + this.populacao);
            return;
        }
        this.populacao = populacao;
        System.out.println("População definida: " + this.populacao + " habitantes.");
    }

    // ---------- Limiares (protegidos por código de acesso) ----------

    public double getLimiteChuva() {
        return this.limiteChuva;
    }

    public double getLimiteNivelRio() {
        return this.limiteNivelRio;
    }

    public double getLimiteUmidade() {
        return this.limiteUmidade;
    }

    public boolean setLimiteChuva(String codigoAcesso, double novoLimite) {
        if (!this.podeAlterarLimite(codigoAcesso, novoLimite, LIMITE_CHUVA_MIN, LIMITE_CHUVA_MAX, "chuva (mm/h)")) {
            return false;
        }
        this.limiteChuva = novoLimite;
        System.out.println("Limite de chuva alterado para " + this.limiteChuva + " mm/h.");
        return true;
    }

    public boolean setLimiteNivelRio(String codigoAcesso, double novoLimite) {
        if (!this.podeAlterarLimite(codigoAcesso, novoLimite, LIMITE_RIO_MIN, LIMITE_RIO_MAX, "nível do rio (m)")) {
            return false;
        }
        this.limiteNivelRio = novoLimite;
        System.out.println("Limite do nível do rio alterado para " + this.limiteNivelRio + " m.");
        return true;
    }

    public boolean setLimiteUmidade(String codigoAcesso, double novoLimite) {
        if (!this.podeAlterarLimite(codigoAcesso, novoLimite, LIMITE_UMIDADE_MIN, LIMITE_UMIDADE_MAX, "umidade do solo (%)")) {
            return false;
        }
        this.limiteUmidade = novoLimite;
        System.out.println("Limite de umidade alterado para " + this.limiteUmidade + " %.");
        return true;
    }

    // Devolve o limiar certo para cada tipo de sensor
    public double getLimite(String tipoSensor) {
        if (Sensor.TIPO_CHUVA.equals(tipoSensor)) {
            return this.limiteChuva;
        }
        if (Sensor.TIPO_NIVEL_RIO.equals(tipoSensor)) {
            return this.limiteNivelRio;
        }
        if (Sensor.TIPO_UMIDADE.equals(tipoSensor)) {
            return this.limiteUmidade;
        }
        // Tipo desconhecido nunca dispara alerta falso
        System.out.println("Aviso: não existe limiar para o tipo " + tipoSensor + ".");
        return Double.MAX_VALUE;
    }

    // ---------- Código de acesso (nunca é exibido) ----------

    public boolean alterarCodigoAcesso(String codigoAtual, String novoCodigo) {
        if (!this.codigoConfere(codigoAtual)) {
            System.out.println("Acesso negado: código atual incorreto. Código mantido.");
            return false;
        }
        if (!this.codigoValido(novoCodigo)) {
            System.out.println("Erro: o novo código precisa ter pelo menos " + TAMANHO_MINIMO_CODIGO
                    + " caracteres. Código mantido.");
            return false;
        }
        this.codigoAcesso = novoCodigo;
        System.out.println("Código de acesso alterado com sucesso.");
        return true;
    }

    public String getResumo() {
        return "Região: " + this.nomeRegiao + " | População: " + this.populacao + " habitantes"
                + "\nLimiares -> Chuva: " + this.limiteChuva + " mm/h | Nível do rio: " + this.limiteNivelRio
                + " m | Umidade do solo: " + this.limiteUmidade + " %"
                + "\nCódigo de acesso: ****";
    }

    // ---------- Validações internas ----------

    private void definirCodigoInicial(String codigoAcesso) {
        if (!this.codigoValido(codigoAcesso)) {
            System.out.println("Erro: código inicial inválido. Usando \"0000\": troque assim que possível.");
            this.codigoAcesso = "0000";
            return;
        }
        this.codigoAcesso = codigoAcesso;
    }

    private boolean podeAlterarLimite(String codigo, double valor, double minimo, double maximo, String nomeLimite) {
        if (!this.codigoConfere(codigo)) {
            System.out.println("Acesso negado: código incorreto. Limite de " + nomeLimite + " mantido.");
            return false;
        }
        if (valor < minimo || valor > maximo) {
            System.out.println("Erro: limite de " + nomeLimite + " deve ficar entre " + minimo + " e " + maximo
                    + ". Valor anterior mantido.");
            return false;
        }
        return true;
    }

    private boolean codigoConfere(String codigo) {
        return codigo != null && codigo.equals(this.codigoAcesso);
    }

    private boolean codigoValido(String codigo) {
        return codigo != null && codigo.trim().length() >= TAMANHO_MINIMO_CODIGO;
    }
}