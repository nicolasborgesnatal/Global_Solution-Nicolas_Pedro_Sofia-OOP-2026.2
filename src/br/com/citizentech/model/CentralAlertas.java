package br.com.citizentech.model;

import java.util.ArrayList;
import java.util.List;

// Compara as previsões com os limiares e decide se avisa a comunidade
public class CentralAlertas {

    // Faixas de cada nível em relação ao limite do território
    private static final double PERCENTUAL_ATENCAO = 80.0;
    private static final double PERCENTUAL_ALERTA = 100.0;
    private static final double PERCENTUAL_CRITICO = 130.0;

    private DadosTerritorio territorio;
    private PainelInformativo painel;
    private ArrayList<Alerta> alertasEmitidos;

    public CentralAlertas(DadosTerritorio territorio, PainelInformativo painel) {
        this.territorio = territorio;
        this.painel = painel;
        this.alertasEmitidos = new ArrayList<>();
    }

    // Avalia um sensor com todas as estratégias e emite no máximo um alerta
    public Alerta avaliar(Sensor sensor, List<EstrategiaPrevisao> estrategias) {
        System.out.println("\n>> Avaliando " + sensor.getNome() + " (" + sensor.getTipo() + ")");

        if (!sensor.verificarFuncionamento()) {
            System.out.println("   Sem avaliação: o sensor não está funcionando. Dado ruim não vira alerta.");
            return null;
        }

        List<Double> leituras = sensor.getHistorico().getLeituras();
        double limite = this.territorio.getLimite(sensor.getTipo());
        String explicacao = "";
        String piorNivel = null;
        String primeiroNivel = null;
        int estrategiasQuePreveram = 0;
        boolean estrategiasConcordam = true;

        for (EstrategiaPrevisao estrategia : estrategias) {
            if (!estrategia.podePrever(leituras)) {
                System.out.println("   " + estrategia.getMotivoSemPrevisao(leituras));
                continue;
            }
            double previsto = estrategia.prever(leituras);
            double percentual = Math.round(previsto / limite * 1000) / 10.0;
            String nivel = this.classificarNivel(percentual);
            String linha = estrategia.getNome() + " prevê " + previsto + " " + sensor.getUnidade() + " = "
                    + percentual + "% do limite de " + limite + " " + sensor.getUnidade()
                    + " -> " + (nivel == null ? "normal" : nivel);
            System.out.println("   " + linha);
            explicacao += "- " + linha + "\n";

            estrategiasQuePreveram++;
            if (estrategiasQuePreveram == 1) {
                primeiroNivel = nivel;
            } else if (!this.mesmoNivel(primeiroNivel, nivel)) {
                estrategiasConcordam = false;
            }
            if (this.gravidade(nivel) > this.gravidade(piorNivel)) {
                piorNivel = nivel;
            }
        }

        if (estrategiasQuePreveram == 0) {
            System.out.println("   O sistema se cala: ainda não há dados suficientes para prever.");
            return null;
        }
        if (piorNivel == null) {
            System.out.println("   Sem alerta: todas as previsões estão abaixo de " + PERCENTUAL_ATENCAO + "% do limite.");
            return null;
        }

        if (estrategiasQuePreveram > 1 && estrategiasConcordam) {
            explicacao += "Confiança maior: as " + estrategiasQuePreveram + " estratégias concordam no nível.";
        } else if (estrategiasQuePreveram > 1) {
            explicacao += "Confiança menor: as estratégias discordam. Foi usado o nível mais grave por precaução.";
        } else {
            explicacao += "Confiança menor: só uma estratégia teve dados suficientes.";
        }

        Alerta alerta = new Alerta(piorNivel, sensor, explicacao);
        this.alertasEmitidos.add(alerta);
        System.out.println("   >>> ALERTA EMITIDO: " + piorNivel);
        this.painel.exibirMensagem(alerta.getMensagemPainel());
        return alerta;
    }

    public List<Alerta> getAlertasEmitidos() {
        return new ArrayList<>(this.alertasEmitidos);
    }

    public int getQuantidadeAlertas() {
        return this.alertasEmitidos.size();
    }

    public int contarCriticos() {
        int total = 0;
        for (Alerta alerta : this.alertasEmitidos) {
            if (alerta.isCritico()) {
                total++;
            }
        }
        return total;
    }

    // Abaixo de 80% do limite não é alerta
    private String classificarNivel(double percentualDoLimite) {
        if (percentualDoLimite >= PERCENTUAL_CRITICO) {
            return Alerta.NIVEL_CRITICO;
        }
        if (percentualDoLimite >= PERCENTUAL_ALERTA) {
            return Alerta.NIVEL_ALERTA;
        }
        if (percentualDoLimite >= PERCENTUAL_ATENCAO) {
            return Alerta.NIVEL_ATENCAO;
        }
        return null;
    }

    // Transforma o nível em número para comparar qual é mais grave
    private int gravidade(String nivel) {
        if (Alerta.NIVEL_CRITICO.equals(nivel)) {
            return 3;
        }
        if (Alerta.NIVEL_ALERTA.equals(nivel)) {
            return 2;
        }
        if (Alerta.NIVEL_ATENCAO.equals(nivel)) {
            return 1;
        }
        return 0;
    }

    private boolean mesmoNivel(String nivelA, String nivelB) {
        return this.gravidade(nivelA) == this.gravidade(nivelB);
    }
}