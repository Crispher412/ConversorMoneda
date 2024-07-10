import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ExchangeRateAPI {

    private static final String API_KEY = "0bedc34c61fdd7ffbfbef4be"; // Reemplaza con tu clave de API
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/";

    public static void main(String[] args) {
        Map<Integer, String> conversionOptions = new HashMap<>();
        conversionOptions.put(1, "USD to EUR");
        conversionOptions.put(2, "USD to GBP");
        conversionOptions.put(3, "EUR to JPY");

        System.out.println("Selecciona una opción de conversión:");
        for (Map.Entry<Integer, String> entry : conversionOptions.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        Scanner scanner = new Scanner(System.in);
        int option = scanner.nextInt();

        if (!conversionOptions.containsKey(option)) {
            System.out.println("Opción no válida");
            return;
        }

        String conversion = conversionOptions.get(option);
        String[] currencies = conversion.split(" to ");
        String fromCurrency = currencies[0];
        String toCurrency = currencies[1];

        System.out.println("Ingresa la cantidad que deseas convertir de " + fromCurrency + " a " + toCurrency + ":");
        double amount = scanner.nextDouble();

        double exchangeRate = getExchangeRate(fromCurrency, toCurrency);
        if (exchangeRate == -1) {
            System.out.println("Error al obtener la tasa de cambio.");
            return;
        }

        double convertedAmount = amount * exchangeRate;
        System.out.println("Cantidad convertida: " + convertedAmount + " " + toCurrency);
    }

    private static double getExchangeRate(String fromCurrency, String toCurrency) {
        try {
            String endpoint = BASE_URL + API_KEY + "/latest/" + fromCurrency;
            URL url = new URL(endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            } else {
                String inline = "";
                Scanner scanner = new Scanner(url.openStream());

                while (scanner.hasNext()) {
                    inline += scanner.nextLine();
                }
                scanner.close();

                JsonObject json = JsonParser.parseString(inline).getAsJsonObject();
                JsonObject rates = json.getAsJsonObject("conversion_rates");
                return rates.get(toCurrency).getAsDouble();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
