package br.com.citizentech.model;

// Painel de rua que mostra os avisos para a comunidade
public class PainelInformativo extends ComponenteTerritorial {

    private static final String MENSAGEM_PADRAO = "Sem alertas no momento.";

    private String mensagemAtual;
    private int totalMensagensExibidas;

    public PainelInformativo(String id, String nome, String regiao) {
        super(id, nome, regiao);
        this.mensagemAtual = MENSAGEM_PADRAO;
        this.totalMensagensExibidas = 0;
    }

    public void exibirMensagem(String mensagem) {
        if (!this.isAtivo()) {
            System.out.println(this.getNome() + " está desligado: mensagem não exibida.");
            return;
        }
        if (mensagem == null || mensagem.trim().isEmpty()) {
            System.out.println("Erro: o painel não exibe mensagens vazias.");
            return;
        }
        this.mensagemAtual = mensagem.trim();
        this.totalMensagensExibidas++;
        System.out.println("[PAINEL " + this.getId() + "] " + this.mensagemAtual);
    }

    public void limparMensagem() {
        this.mensagemAtual = MENSAGEM_PADRAO;
    }

    @Override
    public String verificarStatus() {
        if (!this.isAtivo()) {
            return this.getNome() + " DESLIGADO: a comunidade não está recebendo avisos.";
        }
        return this.getNome() + " ligado | Mensagem atual: " + this.mensagemAtual
                + " | Mensagens exibidas: " + this.totalMensagensExibidas;
    }

    public String getMensagemAtual() {
        return this.mensagemAtual;
    }

    public int getTotalMensagensExibidas() {
        return this.totalMensagensExibidas;
    }
}