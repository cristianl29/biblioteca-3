package com.parqueadero.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.parqueadero.model.ParkingSpot;

import java.net.URI;
import java.net.http.*;
import java.util.List;

public class ApiClient {
    private final HttpClient client;
    private final Gson gson;
    private final String baseUrl = "http://localhost:3000";

    public ApiClient() {
        client = HttpClient.newHttpClient();
        gson = new Gson();
    }

    public List<ParkingSpot> getSpots() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/spots"))
                .GET()
                .build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
            return gson.fromJson(resp.body(), new TypeToken<List<ParkingSpot>>(){}.getType());
        } else {
            throw new RuntimeException("Error al obtener spots: " + resp.statusCode());
        }
    }

    public ParkingSpot getSpot(int id) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/spots/" + id))
                .GET()
                .build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() == 200) {
            return gson.fromJson(resp.body(), ParkingSpot.class);
        } else {
            throw new RuntimeException("Spot no encontrado: " + resp.statusCode());
        }
    }

    public ParkingSpot createSpot(ParkingSpot spot) throws Exception {
        String json = gson.toJson(spot);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/spots"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() == 201 || resp.statusCode() == 200) {
            return gson.fromJson(resp.body(), ParkingSpot.class);
        } else {
            throw new RuntimeException("Error al crear spot: " + resp.statusCode());
        }
    }

    public ParkingSpot updateSpot(ParkingSpot spot) throws Exception {
        String json = gson.toJson(spot);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/spots/" + spot.getId()))
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
            return gson.fromJson(resp.body(), ParkingSpot.class);
        } else {
            throw new RuntimeException("Error al actualizar spot: " + resp.statusCode());
        }
    }

    public boolean deleteSpot(int id) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/spots/" + id))
                .DELETE()
                .build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        return resp.statusCode() == 200 || resp.statusCode() == 204;
    }
}
