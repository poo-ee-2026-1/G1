package br.ufg.simulador.api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ClienteTopografia 
{
    private static final String API_URL = "https://api.opentopodata.org/v1/srtm90m?";
    private final HttpClient client = HttpClient.newHttpClient();

    public double getAltitude(double latitude, double longitude) {
        // Formata a URL para buscar a altitude de um único ponto
        String url = API_URL + "locations=" + latitude + "," + longitude;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

            // Envia a requisição e obtém a resposta como uma String
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Processa o JSON da resposta
            JSONObject jsonResponse = new JSONObject(response.body());
            JSONArray results = jsonResponse.getJSONArray("results");

            if (results.length() > 0) {
                JSONObject firstResult = results.getJSONObject(0);
                return firstResult.getDouble("elevation");
            }

        } catch (IOException | InterruptedException | JSONException e) {
            System.err.println("Erro ao buscar altitude da API: " + e.getMessage());
            // Em caso de erro, podemos retornar 0 ou lançar uma exceção
        }

        return 0.0; // Valor padrão em caso de falha
    }

    public double[] getPerfilTerreno(double lat1, double lon1, double lat2, double lon2, int pontos) {
        // Formata a URL para buscar um perfil de terreno entre dois pontos
        String url = String.format("%slocations=%f,%f|%f,%f&samples=%d",
            API_URL, lat1, lon1, lat2, lon2, pontos);

        double[] perfil = new double[pontos];

        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JSONObject jsonResponse = new JSONObject(response.body());
            JSONArray results = jsonResponse.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                perfil[i] = results.getJSONObject(i).getDouble("elevation");
            }

        } catch (IOException | InterruptedException | JSONException e) {
            System.err.println("Erro ao buscar perfil de terreno da API: " + e.getMessage());
            // Em caso de erro, o array 'perfil' estará preenchido com zeros
        }
        return perfil;
    }
}
