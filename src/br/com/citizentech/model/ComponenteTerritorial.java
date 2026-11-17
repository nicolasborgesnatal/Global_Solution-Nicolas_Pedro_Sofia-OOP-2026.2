package br.com.citizentech.model;

// Molde de todo equipamento instalado no território: nunca existe sozinho
public abstract class ComponenteTerritorial {

    public static final String STATUS_ATIVO = "ATIVO";
    public static final String STATUS_INATIVO = "INATIVO";

    private String id;
    private String nome;
    private String regiao;
    private String status;

    public ComponenteTerritorial(String id, String nome, String regiao) {
        this.id = this.validarTexto(id, "id", "SEM-ID");
        this.nome = this.validarTexto(nome, "nome", "Componente sem nome");
        this.setRegiao(regiao);
        this.status = STATUS_ATIVO;
    }

    // Cada componente concreto explica como está funcionando
    public abstract String verificarStatus();

    public void ativar() {
        if (this.isAtivo()) {
            System.out.println("Aviso: " + this.nome + " já está ativo.");
            return;
        }
        this.status = STATUS_ATIVO;
        System.out.println(this.nome + " foi ativado.");
    }

    public void desativar() {
        if (!this.isAtivo()) {
            System.out.println("Aviso: " + this.nome + " já está desativado.");
            return;
        }
        this.status = STATUS_INATIVO;
        System.out.println(this.nome + " foi desativado.");
    }

    // Mudar de região é um processo real (reinstalação), não um set livre
    public void realocar(String novaRegiao) {
        System.out.println("Realocando " + this.nome + "...");
        this.setRegiao(novaRegiao);
    }

    public boolean isAtivo() {
        return STATUS_ATIVO.equals(this.status);
    }

    public String getResumo() {
        return "[" + this.id + "] " + this.nome + " | Região: " + this.regiao + " | Status: " + this.status;
    }

    public String getId() {
        return this.id;
    }

    public String getNome() {
        return this.nome;
    }

    public String getRegiao() {
        return this.regiao;
    }

    public String getStatus() {
        return this.status;
    }

    private void setRegiao(String regiao) {
        if (regiao == null || regiao.trim().isEmpty()) {
            System.out.println("Erro: a região não pode ficar vazia. Valor anterior mantido.");
            if (this.regiao == null) {
                this.regiao = "Região não informada";
            }
            return;
        }
        this.regiao = regiao.trim();
    }

    private String validarTexto(String valor, String campo, String padrao) {
        if (valor == null || valor.trim().isEmpty()) {
            System.out.println("Erro: o campo " + campo + " não pode ficar vazio. Usando \"" + padrao + "\".");
            return padrao;
        }
        return valor.trim();
    }
}