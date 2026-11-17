package br.com.citizentech.main;

import java.util.ArrayList;
import java.util.List;

import br.com.citizentech.model.Alerta;
import br.com.citizentech.model.CentralAlertas;
import br.com.citizentech.model.DadosTerritorio;
import br.com.citizentech.model.EstrategiaPrevisao;
import br.com.citizentech.model.PainelInformativo;
import br.com.citizentech.model.PrevisaoComparacao;
import br.com.citizentech.model.PrevisaoMediaMovel;
import br.com.citizentech.model.Sensor;
import br.com.citizentech.model.SensorChuva;
import br.com.citizentech.model.SensorNivelRio;
import br.com.citizentech.model.SensorUmidade;

public class SistemaMonitoramento {

    // Main provisório: testa o sistema de alertas
    public static void main(String[] args) {

        System.out.println("=== Territory Predictive Insights: teste da CentralAlertas ===");

        DadosTerritorio territorio = new DadosTerritorio("Jardim Pantanal", 45000, "defesa2026");
        PainelInformativo painel = new PainelInformativo("PNL-01", "Painel da Praça Central", "Jardim Pantanal");
        CentralAlertas central = new CentralAlertas(territorio, painel);

        List<Sensor> sensores = new ArrayList<>();
        sensores.add(new SensorChuva("CHV-01", "Pluviômetro da Ponte", "Jardim Pantanal"));
        sensores.add(new SensorNivelRio("RIO-01", "Régua do Córrego Três Pontes", "Jardim Pantanal"));
        sensores.add(new SensorUmidade("UMD-01", "Umidade do Solo da Encosta", "Jardim Pantanal"));

        List<EstrategiaPrevisao> estrategias = new ArrayList<>();
        estrategias.add(new PrevisaoMediaMovel(5));
        estrategias.add(new PrevisaoComparacao(3));

        System.out.println("\n========== RODADA 1: só 3 horas de leitura ==========");
        lerHoras(sensores, 3);
        avaliarTodos(central, sensores, estrategias);

        System.out.println("\n========== RODADA 2: 12 horas, limiares padrão ==========");
        lerHoras(sensores, 9);
        avaliarTodos(central, sensores, estrategias);

        System.out.println("\n========== RODADA 3: Defesa Civil baixa os limiares ==========");
        territorio.setLimiteChuva("defesa2026", 10.0);
        territorio.setLimiteNivelRio("defesa2026", 2.0);
        territorio.setLimiteUmidade("defesa2026", 50.0);
        avaliarTodos(central, sensores, estrategias);

        System.out.println("\n========== RESUMO DOS ALERTAS ==========");
        System.out.println("Alertas emitidos: " + central.getQuantidadeAlertas() + " | Críticos: " + central.contarCriticos());
        for (Alerta alerta : central.getAlertasEmitidos()) {
            System.out.println("\n" + alerta.getDescricao());
        }
        System.out.println("\n" + painel.verificarStatus());
    }

    private static void lerHoras(List<Sensor> sensores, int horas) {
        for (int hora = 1; hora <= horas; hora++) {
            for (Sensor sensor : sensores) {
                sensor.lerValor();
            }
        }
    }

    private static void avaliarTodos(CentralAlertas central, List<Sensor> sensores, List<EstrategiaPrevisao> estrategias) {
        for (Sensor sensor : sensores) {
            central.avaliar(sensor, estrategias);
        }
    }
}