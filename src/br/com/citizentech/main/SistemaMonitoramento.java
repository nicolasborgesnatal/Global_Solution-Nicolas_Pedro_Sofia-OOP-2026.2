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
        System.out.println("0. Sair");
    }

    // ---------- 1. Verificar sensores ----------

    private void verificarSensores() {
        System.out.println("\n=== EQUIPAMENTOS INSTALADOS ===");
        this.listarComponentes();
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
        int alertasAntes = this.central.getQuantidadeAlertas();
        for (Sensor sensor : this.sensores) {
            this.central.avaliar(sensor, this.estrategias);
        }
        int alertasNovos = this.central.getQuantidadeAlertas() - alertasAntes;
        System.out.println("\nResultado: " + alertasNovos + " alerta(s) emitido(s) nesta rodada.");
        if (alertasNovos == 0) {
            this.painel.limparMensagem();
        }
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