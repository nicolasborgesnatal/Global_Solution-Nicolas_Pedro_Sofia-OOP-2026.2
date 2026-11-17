package br.com.citizentech.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import br.com.citizentech.model.Alerta;
import br.com.citizentech.model.CentralAlertas;
import br.com.citizentech.model.ComponenteTerritorial;
import br.com.citizentech.model.DadosTerritorio;
import br.com.citizentech.model.EstrategiaPrevisao;
import br.com.citizentech.model.PainelInformativo;
import br.com.citizentech.model.PrevisaoComparacao;
import br.com.citizentech.model.PrevisaoMediaMovel;
import br.com.citizentech.model.Sensor;
import br.com.citizentech.model.SensorChuva;
import br.com.citizentech.model.SensorNivelRio;
import br.com.citizentech.model.SensorUmidade;

// Classe principal: monta o território e conversa com o usuário pelo menu
public class SistemaMonitoramento {

    private static final String REGIAO = "Jardim Pantanal";
    private static final int MAXIMO_HORAS_POR_SIMULACAO = 48;
    private static final int HORAS_DO_DIA = 24;

    private Scanner entrada;
    private DadosTerritorio territorio;
    private PainelInformativo painel;
    private CentralAlertas central;
    private List<Sensor> sensores;
    private List<ComponenteTerritorial> componentes;
    private List<EstrategiaPrevisao> estrategias;
    private int horaAtual;

    public SistemaMonitoramento() {
        // Locale.US: números decimais com ponto (ex: 25.5)
        this.entrada = new Scanner(System.in).useLocale(Locale.US);
        this.territorio = new DadosTerritorio(REGIAO, 45000, "defesa2026");
        this.painel = new PainelInformativo("PNL-01", "Painel da Praça Central", REGIAO);
        this.central = new CentralAlertas(this.territorio, this.painel);
        this.sensores = new ArrayList<>();
        this.componentes = new ArrayList<>();
        this.estrategias = new ArrayList<>();
        this.horaAtual = 0;
        this.instalarEquipamentos();
    }

    public static void main(String[] args) {
        SistemaMonitoramento sistema = new SistemaMonitoramento();
        sistema.iniciar();
    }

    // O mesmo sensor entra nas duas listas: como Sensor e como ComponenteTerritorial
    private void instalarEquipamentos() {
        SensorChuva pluviometro = new SensorChuva("CHV-01", "Pluviômetro da Ponte", REGIAO);
        SensorNivelRio reguaCorrego = new SensorNivelRio("RIO-01", "Régua do Córrego Três Pontes", REGIAO);
        SensorUmidade umidadeEncosta = new SensorUmidade("UMD-01", "Umidade do Solo da Encosta", REGIAO);

        this.sensores.add(pluviometro);
        this.sensores.add(reguaCorrego);
        this.sensores.add(umidadeEncosta);

        this.componentes.add(this.painel);
        this.componentes.add(pluviometro);
        this.componentes.add(reguaCorrego);
        this.componentes.add(umidadeEncosta);

        this.estrategias.add(new PrevisaoMediaMovel(5));
        this.estrategias.add(new PrevisaoComparacao(3));
    }

    public void iniciar() {
        System.out.println("\n==============================================");
        System.out.println("   TERRITORY PREDICTIVE INSIGHTS - CitizenTech");
        System.out.println("   Monitoramento de alagamentos: " + this.territorio.getNomeRegiao());
        System.out.println("==============================================");

        int opcao;
        do {
            this.exibirMenu();
            opcao = this.lerInteiro("Escolha uma opção: ");
            switch (opcao) {
                case 1:
                    this.verificarSensores();
                    break;
                case 2:
                    this.simularLeituras();
                    break;
                case 3:
                    this.rodarPrevisoes();
                    break;
                case 4:
                    this.gerenciarTerritorio();
                    break;
                case 5:
                    this.exibirStatusCompleto();
                    break;
                case 6:
                    this.simular24Horas();
                    break;
                case 0:
                    System.out.println("\nEncerrando o monitoramento. Até logo!");
                    break;
                default:
                    System.out.println("Opção inválida. Escolha um número do menu.");
            }
        } while (opcao != 0);
        this.entrada.close();
    }

    private void exibirMenu() {
        System.out.println("\n----------------- MENU (hora " + this.horaAtual + ") -----------------");
        System.out.println("1. Verificar sensores");
        System.out.println("2. Simular próximas leituras");
        System.out.println("3. Rodar previsões e alertas");
        System.out.println("4. Gerenciar dados do território");
        System.out.println("5. Exibir status completo");
        System.out.println("6. Simular 24 horas com alertas (bônus)");
        System.out.println("0. Sair");
    }

    // ---------- 1. Verificar sensores ----------

    private void verificarSensores() {
        System.out.println("\n=== EQUIPAMENTOS INSTALADOS ===");
        this.listarComponentes();
        this.verificarLimitesAtuais();
        int escolha = this.lerInteiro("\nDigite o número de um equipamento para ligar/desligar (0 para voltar): ");
        if (escolha == 0) {
            return;
        }
        if (escolha < 1 || escolha > this.componentes.size()) {
            System.out.println("Equipamento inexistente.");
            return;
        }
        ComponenteTerritorial escolhido = this.componentes.get(escolha - 1);
        if (escolhido.isAtivo()) {
            escolhido.desativar();
        } else {
            escolhido.ativar();
        }
    }

    private void listarComponentes() {
        for (int i = 0; i < this.componentes.size(); i++) {
            ComponenteTerritorial componente = this.componentes.get(i);
            System.out.println((i + 1) + ". " + componente.getResumo());
            System.out.println("   " + componente.verificarStatus());
        }
    }

    // Compara a última leitura de cada sensor com o limite: risco agora, não previsto
    private void verificarLimitesAtuais() {
        System.out.println("\n--- Leituras atuais x limites ---");
        for (Sensor sensor : this.sensores) {
            double limite = this.territorio.getLimite(sensor.getTipo());
            String situacao;
            if (sensor.getHistorico().estaVazio()) {
                situacao = "sem leituras";
            } else if (!sensor.verificarFuncionamento()) {
                situacao = "sem leitura confiável (desligado ou com falha)";
            } else if (sensor.ultrapassouLimite(limite)) {
                situacao = "ACIMA DO LIMITE";
            } else {
                situacao = "dentro do limite";
            }
            System.out.println(sensor.getNome() + ": " + situacao + " (limite " + limite + " " + sensor.getUnidade() + ")");
        }
    }

    // ---------- 2. Simular leituras ----------

    private void simularLeituras() {
        int horas = this.lerInteiro("Quantas horas simular (1 a " + MAXIMO_HORAS_POR_SIMULACAO + ")? ");
        if (horas < 1 || horas > MAXIMO_HORAS_POR_SIMULACAO) {
            System.out.println("Quantidade inválida. Use um valor entre 1 e " + MAXIMO_HORAS_POR_SIMULACAO + ".");
            return;
        }
        System.out.println("\n=== LEITURAS SIMULADAS ===");
        for (int i = 1; i <= horas; i++) {
            this.horaAtual++;
            System.out.println(this.lerTodosOsSensores());
        }
        System.out.println(horas + " hora(s) simulada(s). Use a opção 3 para prever.");
    }

    // Uma linha por hora com a leitura de cada sensor
    private String lerTodosOsSensores() {
        String linha = "Hora " + String.format("%02d", this.horaAtual);
        for (Sensor sensor : this.sensores) {
            double valor = sensor.lerValor();
            String leitura = (valor == Sensor.LEITURA_INVALIDA) ? "---" : valor + " " + sensor.getUnidade();
            linha += " | " + sensor.getTipo() + ": " + leitura;
        }
        return linha;
    }

    // ---------- 3. Previsões e alertas ----------

    private void rodarPrevisoes() {
        System.out.println("\n=== PREVISÕES PARA A PRÓXIMA HORA ===");
        List<Alerta> alertasDaRodada = this.central.avaliarRodada(this.sensores, this.estrategias);
        System.out.println("\nResultado: " + alertasDaRodada.size() + " alerta(s) emitido(s) nesta rodada.");
    }

    // ---------- 4. Dados do território ----------

    private void gerenciarTerritorio() {
        int opcao;
        do {
            System.out.println("\n=== DADOS DO TERRITÓRIO ===");
            System.out.println("1. Ver dados");
            System.out.println("2. Alterar nome da região");
            System.out.println("3. Alterar população");
            System.out.println("4. Alterar limite de chuva (código)");
            System.out.println("5. Alterar limite do nível do rio (código)");
            System.out.println("6. Alterar limite de umidade do solo (código)");
            System.out.println("7. Trocar código de acesso");
            System.out.println("0. Voltar");
            opcao = this.lerInteiro("Escolha uma opção: ");
            switch (opcao) {
                case 1:
                    System.out.println(this.territorio.getResumo());
                    break;
                case 2:
                    this.territorio.setNomeRegiao(this.lerTexto("Novo nome da região: "));
                    break;
                case 3:
                    this.territorio.setPopulacao(this.lerInteiro("Nova população: "));
                    break;
                case 4:
                    this.territorio.setLimiteChuva(this.lerTexto("Código de acesso: "),
                            this.lerDecimal("Novo limite de chuva em mm/h (5 a 100): "));
                    break;
                case 5:
                    this.territorio.setLimiteNivelRio(this.lerTexto("Código de acesso: "),
                            this.lerDecimal("Novo limite do rio em metros (0.5 a 10): "));
                    break;
                case 6:
                    this.territorio.setLimiteUmidade(this.lerTexto("Código de acesso: "),
                            this.lerDecimal("Novo limite de umidade em % (30 a 100): "));
                    break;
                case 7:
                    this.territorio.alterarCodigoAcesso(this.lerTexto("Código atual: "),
                            this.lerTexto("Novo código (mínimo 4 caracteres): "));
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        } while (opcao != 0);
    }

    // ---------- 5. Status completo ----------

    private void exibirStatusCompleto() {
        System.out.println("\n=============== STATUS COMPLETO ===============");
        System.out.println("Hora simulada: " + this.horaAtual);
        System.out.println(this.territorio.getResumo());

        System.out.println("\n--- Equipamentos ---");
        this.listarComponentes();

        this.verificarLimitesAtuais();

        System.out.println("\n--- Estratégias de previsão ---");
        for (EstrategiaPrevisao estrategia : this.estrategias) {
            System.out.println("* " + estrategia.getDescricao());
        }

        System.out.println("\n--- Alertas ---");
        System.out.println("Total emitidos: " + this.central.getQuantidadeAlertas()
                + " | Críticos: " + this.central.contarCriticos());
        List<Alerta> alertas = this.central.getAlertasEmitidos();
        if (alertas.isEmpty()) {
            System.out.println("Nenhum alerta emitido até agora.");
        } else {
            System.out.println("Último alerta:\n" + alertas.get(alertas.size() - 1).getDescricao());
        }
        System.out.println("===============================================");
    }

    // ---------- 6. Bônus: simulação de 24 horas ----------

    // Lê e avalia hora a hora, como o sistema rodaria sozinho durante um dia
    private void simular24Horas() {
        System.out.println("\n=== SIMULAÇÃO DE 24 HORAS (bônus) ===");
        System.out.println("Cada hora: leitura dos sensores e, se houver risco, o aviso no painel.\n");

        int alertasAntes = this.central.getQuantidadeAlertas();
        int primeiraHoraComAlerta = 0;
        int horasComAlerta = 0;

        this.central.setExibirDetalhes(false);
        for (int i = 1; i <= HORAS_DO_DIA; i++) {
            this.horaAtual++;
            System.out.println(this.lerTodosOsSensores());
            List<Alerta> alertasDaHora = this.central.avaliarRodada(this.sensores, this.estrategias);
            if (!alertasDaHora.isEmpty()) {
                horasComAlerta++;
                if (primeiraHoraComAlerta == 0) {
                    primeiraHoraComAlerta = this.horaAtual;
                }
            }
        }
        this.central.setExibirDetalhes(true);

        this.exibirResumoDoDia(alertasAntes, primeiraHoraComAlerta, horasComAlerta);
    }

    private void exibirResumoDoDia(int alertasAntes, int primeiraHoraComAlerta, int horasComAlerta) {
        List<Alerta> alertas = this.central.getAlertasEmitidos();
        int alertasDoDia = alertas.size() - alertasAntes;
        int criticosDoDia = 0;
        for (int i = alertasAntes; i < alertas.size(); i++) {
            if (alertas.get(i).isCritico()) {
                criticosDoDia++;
            }
        }

        System.out.println("\n=============== RESUMO DAS 24 HORAS ===============");
        System.out.println("Alertas emitidos: " + alertasDoDia + " | Críticos: " + criticosDoDia);
        if (alertasDoDia == 0) {
            System.out.println("Nenhuma previsão chegou a 80% dos limites: dia sem risco.");
        } else {
            System.out.println("Primeiro alerta na hora " + primeiraHoraComAlerta + ".");
            System.out.println("Horas com pelo menos um alerta: " + horasComAlerta + " de " + HORAS_DO_DIA + ".");
        }

        System.out.println("\n--- Pico de cada sensor nas últimas " + HORAS_DO_DIA + " leituras ---");
        for (Sensor sensor : this.sensores) {
            System.out.println(sensor.getNome() + ": " + this.calcularPico(sensor) + " " + sensor.getUnidade()
                    + " (limite " + this.territorio.getLimite(sensor.getTipo()) + ")");
        }
        System.out.println("\n" + this.painel.verificarStatus());
        System.out.println("===================================================");
    }

    private double calcularPico(Sensor sensor) {
        List<Double> leituras = sensor.getHistorico().getLeituras();
        int inicio = Math.max(0, leituras.size() - HORAS_DO_DIA);
        double pico = 0;
        for (int i = inicio; i < leituras.size(); i++) {
            if (leituras.get(i) > pico) {
                pico = leituras.get(i);
            }
        }
        return pico;
    }

    // ---------- Leitura segura do teclado ----------

    private int lerInteiro(String mensagem) {
        System.out.print(mensagem);
        while (!this.entrada.hasNextInt()) {
            System.out.print("Valor inválido. Digite um número inteiro: ");
            this.entrada.next();
        }
        int valor = this.entrada.nextInt();
        this.entrada.nextLine();
        return valor;
    }

    private double lerDecimal(String mensagem) {
        System.out.print(mensagem);
        while (!this.entrada.hasNextDouble()) {
            System.out.print("Valor inválido. Digite um número (use ponto, ex: 25.5): ");
            this.entrada.next();
        }
        double valor = this.entrada.nextDouble();
        this.entrada.nextLine();
        return valor;
    }

    private String lerTexto(String mensagem) {
        System.out.print(mensagem);
        return this.entrada.nextLine();
    }
}