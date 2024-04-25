import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class ExchangeRateApiClient {
    private static final String API_URL = "https://api.exchangerate-api.com/v4/latest/";
    private static final String[] CURRENCY_CODES = { "ARS", "BOB", "BRL", "CLP", "COP", "USD" };

    public static void main(String[] args) {
        String baseCurrency = "USD"; // Moeda base

        // Monta a URL da API com as moedas de origem
        String apiUrl = API_URL + baseCurrency;

        // Cria uma instância do HttpClient
        HttpClient httpClient = HttpClient.newHttpClient();

        // Cria uma requisição GET
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .GET()
                .build();

        // Envia a requisição e recebe a resposta
        try {
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            // Verifica se a requisição foi bem-sucedida (status code 200)
            if (response.statusCode() == 200) {
                // Analisa o corpo da resposta JSON utilizando Gson
                Gson gson = new Gson();
                JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);

                // Filtra as moedas específicas
                JsonObject rates = jsonResponse.getAsJsonObject("rates");

                // Inicia o loop de interação com o usuário
                Scanner scanner = new Scanner(System.in);
                boolean continuar = true;
                while (continuar) {
                    // Exibe o menu de opções
                    System.out.println("Selecione uma opção:");
                    System.out.println("1. Converter moeda");
                    System.out.println("2. Sair");

                    // Captura a escolha do usuário
                    int escolha = scanner.nextInt();
                    switch (escolha) {
                        case 1:
                            realizarConversao(rates, scanner);
                            break;
                        case 2:
                            continuar = false;
                            System.out.println("Encerrando o programa...");
                            break;
                        default:
                            System.out.println("Opção inválida. Tente novamente.");
                    }
                }
            } else {
                System.out.println("Erro ao fazer a requisição: " + response.statusCode());
            }
        } catch (Exception e) {
            System.out.println("Erro ao fazer a requisição: " + e.getMessage());
        }
    }

    // Método para realizar a conversão de moedas
    private static void realizarConversao(JsonObject rates, Scanner scanner) {
        // Solicita ao usuário as moedas de origem e destino
        System.out.println("Insira a moeda de origem (USD, BRL, EUR, etc.): ");
        String sourceCurrency = scanner.next().toUpperCase();
        System.out.println("Insira a moeda de destino (USD, BRL, EUR, etc.): ");
        String targetCurrency = scanner.next().toUpperCase();

        // Verifica se as moedas inseridas são válidas
        if (isValidCurrency(sourceCurrency) && isValidCurrency(targetCurrency)) {
            // Solicita ao usuário o valor a ser convertido
            System.out.println("Insira o valor a ser convertido: ");
            double amount = scanner.nextDouble();

            // Calcula a conversão
            double sourceRate = rates.get(sourceCurrency).getAsDouble();
            double targetRate = rates.get(targetCurrency).getAsDouble();
            double convertedAmount = amount * (targetRate / sourceRate);

            // Exibe o resultado da conversão
            System.out
                    .println(amount + " " + sourceCurrency + " equivalem a " + convertedAmount + " " + targetCurrency);
        } else {
            System.out.println("Moeda de origem ou destino inválida.");
        }
    }

    // Verifica se a moeda é válida
    private static boolean isValidCurrency(String currencyCode) {
        for (String code : CURRENCY_CODES) {
            if (code.equals(currencyCode)) {
                return true;
            }
        }
        return false;
    }
}
