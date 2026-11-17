package br.com.citizentech.main;

import java.util.ArrayList;
import java.util.List;

import br.com.citizentech.model.EstrategiaPrevisao;
import br.com.citizentech.model.PrevisaoComparacao;
import br.com.citizentech.model.PrevisaoMediaMovel;
import br.com.citizentech.model.Sensor;
import br.com.citizentech.model.SensorChuva;
import br.com.citizentech.model.SensorNivelRio;
import br.com.citizentech.model.SensorUmidade;

public class SistemaMonitoramento {

    // Main provisório: 3 sensores e 2 previsões rodando juntos
    public static void main(String[] args) {

        System.out.println("=== Territory Predictive Insights: 3 sensores x 2 previsões ===\n");

        System.out.println("--- Conferindo a Comparação com leituras fixas ---");
        PrevisaoComparacao comparacao = new PrevisaoComparacao(3);
        List<Double> leiturasFixas = new ArrayList<>();
        leiturasFixas.add(10.0);
        leiturasFixas.add(12.0);
        leiturasFixas.add(15.0);
        leiturasFixas.add(20.0);
        leiturasFixas.add(25.0);
        leiturasFixas.add(30.0);
        System.out.println("Leituras: " + leiturasFixas);
        System.out.println(comparacao.descreverTendencia(leiturasFixas));
        System.out.println("Previsto: 25.0 + (25.0 - 12.33) = " + comparacao.prever(leiturasFixas));

        // Listas tipadas pelo contrato e pela classe mãe
        List<Sensor> sensores = new ArrayList<>();
        sensores.add(new SensorChuva("CHV-01", "Pluviômetro da Ponte", "Jardim Pantanal"));
        sensores.add(new SensorNivelRio("RIO-01", "Régua do Córrego Três Pontes", "Jardim Pantanal"));
        sensores.add(new SensorUmidade("UMD-01", "Umidade do Solo da Encosta", "Jardim Pantanal"));

        List<EstrategiaPrevisao> estrategias = new ArrayList<>();
        estrategias.add(new PrevisaoMediaMovel(5));
        estrategias.add(comparacao);

        System.out.println("\n--- Estratégias disponíveis ---");
        for (EstrategiaPrevisao estrategia : estrategias) {
            System.out.println("* " + estrategia.getDescricao());
        }

        System.out.println("\n--- 10 horas de leituras ---");
        for (int hora = 1; hora <= 10; hora++) {
            for (Sensor sensor : sensores) {
                sensor.lerValor();
            }
        }

        System.out.println("\n--- Previsões por sensor ---");
        for (Sensor sensor : sensores) {
            List<Double> historico = sensor.getHistorico().getLeituras();
            System.out.println("\n" + sensor.getNome() + " (" + sensor.getTipo() + ")");
            System.out.println("Histórico: " + historico);
            for (EstrategiaPrevisao estrategia : estrategias) {
                double previsto = estrategia.prever(historico);
                if (previsto == EstrategiaPrevisao.SEM_PREVISAO) {
                    System.out.println("  " + estrategia.getMotivoSemPrevisao(historico));
                } else {
                    System.out.println("  " + estrategia.getNome() + ": " + previsto + " " + sensor.getUnidade());
                }
            }
            System.out.println("  " + comparacao.descreverTendencia(historico));
        }
    }
}