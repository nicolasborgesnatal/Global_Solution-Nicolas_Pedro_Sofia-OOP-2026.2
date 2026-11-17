package br.com.citizentech.main;

import java.util.ArrayList;
import java.util.List;

import br.com.citizentech.model.EstrategiaPrevisao;
import br.com.citizentech.model.PrevisaoMediaMovel;
import br.com.citizentech.model.Sensor;
import br.com.citizentech.model.SensorChuva;

public class SistemaMonitoramento {

    // Main provisório: testa a herança da EstrategiaPrevisao
    public static void main(String[] args) {

        System.out.println("=== Territory Predictive Insights: teste da EstrategiaPrevisao ===\n");

        EstrategiaPrevisao mediaMovel = new PrevisaoMediaMovel(5);
        System.out.println(mediaMovel.getDescricao());

        System.out.println("\n--- Janela inválida ---");
        EstrategiaPrevisao janelaInvalida = new PrevisaoMediaMovel(1);
        System.out.println(janelaInvalida.getDescricao());

        System.out.println("\n--- Poucas leituras: o sistema se cala ---");
        List<Double> poucasLeituras = new ArrayList<>();
        poucasLeituras.add(10.0);
        poucasLeituras.add(12.0);
        poucasLeituras.add(15.0);
        if (!mediaMovel.podePrever(poucasLeituras)) {
            System.out.println(mediaMovel.getMotivoSemPrevisao(poucasLeituras));
        }

        System.out.println("\n--- Leituras fixas (conta conferível) ---");
        List<Double> leiturasFixas = new ArrayList<>(poucasLeituras);
        leiturasFixas.add(20.0);
        leiturasFixas.add(25.0);
        leiturasFixas.add(30.0);
        System.out.println("Leituras: " + leiturasFixas);
        System.out.println("Média das últimas 5 (12+15+20+25+30)/5 = " + mediaMovel.prever(leiturasFixas) + " mm/h");

        System.out.println("\n--- Previsão com o sensor de chuva ---");
        Sensor pluviometro = new SensorChuva("CHV-01", "Pluviômetro da Ponte", "Jardim Pantanal");
        for (int hora = 1; hora <= 8; hora++) {
            pluviometro.lerValor();
        }
        List<Double> historico = pluviometro.getHistorico().getLeituras();
        System.out.println("Histórico: " + historico);
        double previsto = mediaMovel.prever(historico);
        if (previsto == EstrategiaPrevisao.SEM_PREVISAO) {
            System.out.println(mediaMovel.getMotivoSemPrevisao(historico));
        } else {
            System.out.println("Próxima hora prevista (" + mediaMovel.getNome() + "): " + previsto + " "
                    + pluviometro.getUnidade());
        }

        // EstrategiaPrevisao generica = new EstrategiaPrevisao("X", 5); // Não compila: classe abstrata
    }
}