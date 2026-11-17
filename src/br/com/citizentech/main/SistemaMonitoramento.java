package br.com.citizentech.main;

import br.com.citizentech.model.ComponenteTerritorial;
import br.com.citizentech.model.Sensor;
import br.com.citizentech.model.SensorChuva;

public class SistemaMonitoramento {

    // Main provisório: testa a interface Sensor com o SensorChuva
    public static void main(String[] args) {

        System.out.println("=== Territory Predictive Insights: teste da interface Sensor ===\n");

        SensorChuva pluviometro = new SensorChuva("CHV-01", "Pluviômetro da Ponte", "Jardim Pantanal");

        // O mesmo objeto visto pelos dois tipos
        Sensor comoSensor = pluviometro;
        ComponenteTerritorial comoComponente = pluviometro;
        System.out.println(comoComponente.getResumo());
        System.out.println("Tipo: " + comoSensor.getTipo() + " | Unidade: " + comoSensor.getUnidade());

        System.out.println("\n--- 8 leituras simuladas ---");
        for (int hora = 1; hora <= 8; hora++) {
            double valor = comoSensor.lerValor();
            if (valor != Sensor.LEITURA_INVALIDA) {
                System.out.println("Hora " + hora + ": " + valor + " " + comoSensor.getUnidade());
            }
        }

        System.out.println("\n--- Funcionamento e limite ---");
        double limiteTeste = 30.0;
        System.out.println("Funcionando? " + comoSensor.verificarFuncionamento());
        System.out.println("Última leitura passou de " + limiteTeste + " mm/h? " + comoSensor.ultrapassouLimite(limiteTeste));
        System.out.println("Histórico: " + comoSensor.getHistorico().getLeituras());
        System.out.println(pluviometro.verificarStatus());

        System.out.println("\n--- Sensor desativado ---");
        pluviometro.desativar();
        comoSensor.lerValor();
        System.out.println("Funcionando? " + comoSensor.verificarFuncionamento());
        System.out.println(pluviometro.verificarStatus());

        // Tentando alterar o histórico pela cópia: o original não muda
        comoSensor.getHistorico().getLeituras().clear();
        System.out.println("Leituras guardadas após tentar apagar a cópia: " + comoSensor.getHistorico().getQuantidade());
    }
}