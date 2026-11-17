package br.com.citizentech.model;

// Contrato que todo sensor do território precisa cumprir
public interface Sensor {

    // Tipos de sensor usados no recorte de clima
    String TIPO_CHUVA = "CHUVA";
    String TIPO_NIVEL_RIO = "NIVEL_RIO";
    String TIPO_UMIDADE = "UMIDADE";

    // Valor devolvido quando a leitura não pôde ser feita
    double LEITURA_INVALIDA = -1.0;

    // Faz uma nova leitura e guarda no histórico
    double lerValor();

    boolean verificarFuncionamento();

    // Já vem pronto da ComponenteTerritorial: o sensor se identifica nos alertas
    String getNome();

    String getTipo();

    String getUnidade();

    // Compara a última leitura com o limite de alerta
    boolean ultrapassouLimite(double limite);

    HistoricoLeituras getHistorico();
}