package com.pequla.data.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pequla.data.ex.BackendException;
import com.pequla.data.model.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
public class BackendService {

    private final HttpClient client;
    private final ObjectMapper mapper;

    public BackendService() {
        this.client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        // Register json mapper
        this.mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public List<Integer> getDataIds() throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("https://link.samifying.com/api/data/list/ids?log=0"))
                .header("Content-Type", "application/json")
                .GET()
                .build();
        HttpResponse<String> json = client.send(req, HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(getResponseBody(json), new TypeReference<>() {
        });
    }

    public DataModel getData(int id) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("https://link.samifying.com/api/data/" + id + "?log=0"))
                .header("Content-Type", "application/json")
                .GET()
                .build();
        HttpResponse<String> json = client.send(req, HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(getResponseBody(json), DataModel.class);
    }

    public UserModel getUser(String uuid) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("https://link.samifying.com/api/user/" + uuid + "?log=0"))
                .header("Content-Type", "application/json")
                .GET()
                .build();
        HttpResponse<String> json = client.send(req, HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(getResponseBody(json), UserModel.class);
    }

    public AccountModel getAccount(String uuid) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("https://link.samifying.com/api/cache/uuid/" + uuid + "?log=0"))
                .header("Content-Type", "application/json")
                .GET()
                .build();
        HttpResponse<String> json = client.send(req, HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(getResponseBody(json), AccountModel.class);
    }

    private String getResponseBody(HttpResponse<String> rsp) throws JsonProcessingException {
        int code = rsp.statusCode();
        if (code == 200 || code == 204) {
            return rsp.body();
        }
        if (code == 500) {
            ErrorModel model = mapper.readValue(rsp.body(), ErrorModel.class);
            throw new BackendException(model.getMessage(), code);
        }
        throw new BackendException("Response code " + code, code);
    }
}
