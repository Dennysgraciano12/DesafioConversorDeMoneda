package com.desafio.conversordemoneda;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class ConversorDeMoneda {

    private static final String CLAVE_API = "fe187acd61d81ecba1de8913";
    private static final String URL_API = "https://v6.exchangerate-api.com/v6/" + CLAVE_API + "/latest/";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** Bienvenido al Conversor de Monedas ***");
        String monedaOrigen = obtenerCodigoMoneda(scanner, "Seleccione la moneda de origen:");
        double cantidad = obtenerCantidad(scanner);
        String monedaDestino = obtenerCodigoMoneda(scanner, "Seleccione la moneda de destino:");

        try {
            double tasaConversion = obtenerTasaConversion(monedaOrigen, monedaDestino);
            double cantidadConvertida = cantidad * tasaConversion;

            System.out.printf("%.2f %s equivalen a %.2f %s%n", cantidad, monedaOrigen, cantidadConvertida, monedaDestino);
        } catch (Exception e) {
            System.out.println("Ocurrió un error al realizar la conversión: " + e.getMessage());
        }
        scanner.close();
    }

    private static String obtenerCodigoMoneda(Scanner scanner, String mensaje) {
        System.out.println(mensaje);
        System.out.println("1. USD - Dólar estadounidense");
        System.out.println("2. COP - Peso Colombiano");
        System.out.println("3. BRL - Real Brasileño");
        System.out.println("4. ARS - Peso Argentino");
        System.out.print("Opción elegida: ");
        int opcion = scanner.nextInt();
        return switch (opcion) {
            case 1 -> "USD";
            case 2 -> "COP";
            case 3 -> "BRL";
            case 4 -> "ARS";
            default -> throw new IllegalArgumentException("Opción no válida");
        };
    }

    private static double obtenerCantidad(Scanner scanner) {
        System.out.print("Ingrese la cantidad a convertir: ");
        return scanner.nextDouble();
    }

    private static double obtenerTasaConversion(String monedaOrigen, String monedaDestino) throws IOException, InterruptedException {
        HttpClient cliente = HttpClient.newHttpClient();
        HttpRequest solicitud = HttpRequest.newBuilder()
                .uri(URI.create(URL_API + monedaOrigen))
                .GET()
                .build();

        HttpResponse<String> respuesta = cliente.send(solicitud, HttpResponse.BodyHandlers.ofString());

        JsonObject objetoJson = JsonParser.parseString(respuesta.body()).getAsJsonObject();
        JsonObject tasasConversion = objetoJson.getAsJsonObject("conversion_rates");

        if (!tasasConversion.has(monedaDestino)) {
            throw new IllegalArgumentException("Moneda de destino no válida");
        }
        return tasasConversion.get(monedaDestino).getAsDouble();
    }
}
