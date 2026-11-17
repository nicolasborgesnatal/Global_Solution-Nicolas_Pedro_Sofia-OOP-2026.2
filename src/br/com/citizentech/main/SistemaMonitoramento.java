package br.com.citizentech.main;

import br.com.citizentech.model.PainelInformativo;

public class SistemaMonitoramento {

    // Main provisório: testa a classe abstrata antes do menu
    public static void main(String[] args) {

        System.out.println("=== Territory Predictive Insights: teste do ComponenteTerritorial ===\n");

        PainelInformativo painelPraca = new PainelInformativo("PNL-01", "Painel da Praça Central", "Jardim Pantanal");
        System.out.println(painelPraca.getResumo());
        System.out.println(painelPraca.verificarStatus());

        System.out.println("\n--- Exibindo mensagem ---");
        painelPraca.exibirMensagem("Teste do painel comunitário.");

        System.out.println("\n--- Desligando o painel ---");
        painelPraca.desativar();
        painelPraca.exibirMensagem("Esta mensagem não pode aparecer.");
        System.out.println(painelPraca.verificarStatus());

        System.out.println("\n--- Religando e validando ---");
        painelPraca.ativar();
        painelPraca.ativar();
        painelPraca.realocar("");
        painelPraca.realocar("Vila Itaim");
        System.out.println(painelPraca.getResumo());

        // ComponenteTerritorial generico = new ComponenteTerritorial("X", "Y", "Z");
        // Não compila: ComponenteTerritorial é abstrata
    }
}