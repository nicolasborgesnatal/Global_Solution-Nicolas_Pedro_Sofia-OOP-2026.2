package br.com.citizentech.main;

import br.com.citizentech.model.DadosTerritorio;
import br.com.citizentech.model.Sensor;
import br.com.citizentech.model.SensorChuva;

public class SistemaMonitoramento {

    // Main provisório: testa o encapsulamento do DadosTerritorio
    public static void main(String[] args) {

        System.out.println("=== Territory Predictive Insights: teste do DadosTerritorio ===\n");

        DadosTerritorio territorio = new DadosTerritorio("Jardim Pantanal", 45000, "defesa2026");
        System.out.println(territorio.getResumo());

        System.out.println("\n--- Região e população inválidas ---");
        territorio.setPopulacao(-500);
        territorio.setNomeRegiao("   ");
        territorio.setPopulacao(46000);

        System.out.println("\n--- Alterando limiares ---");
        territorio.setLimiteChuva("1234", 40.0);
        territorio.setLimiteChuva("defesa2026", 500.0);
        territorio.setLimiteNivelRio("defesa2026", -2.0);
        territorio.setLimiteChuva("defesa2026", 25.0);

        System.out.println("\n--- Trocando o código de acesso ---");
        territorio.alterarCodigoAcesso("errado", "novo2026");
        territorio.alterarCodigoAcesso("defesa2026", "123");
        territorio.alterarCodigoAcesso("defesa2026", "novo2026");
        territorio.setLimiteUmidade("defesa2026", 90.0);
        territorio.setLimiteUmidade("novo2026", 90.0);

        System.out.println("\n--- Estado final ---");
        System.out.println(territorio.getResumo());

        System.out.println("\n--- Sensor usando o limiar do território ---");
        Sensor pluviometro = new SensorChuva("CHV-01", "Pluviômetro da Ponte", "Jardim Pantanal");
        double limite = territorio.getLimite(pluviometro.getTipo());
        for (int hora = 1; hora <= 5; hora++) {
            double valor = pluviometro.lerValor();
            if (valor == Sensor.LEITURA_INVALIDA) {
                continue;
            }
            String situacao = pluviometro.ultrapassouLimite(limite) ? "ACIMA do limite" : "dentro do limite";
            System.out.println("Hora " + hora + ": " + valor + " mm/h -> " + situacao + " (" + limite + " mm/h)");
        }

        // territorio.limiteChuva = 999; // Não compila: atributo privado
        // territorio.getCodigoAcesso(); // Não compila: esse getter não existe
    }
}