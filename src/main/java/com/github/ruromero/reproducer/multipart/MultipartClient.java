package com.github.ruromero.reproducer.multipart;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Version;

import org.apache.camel.quarkus.main.CamelMainApplication;

import io.quarkus.runtime.Quarkus;

public class MultipartClient {

    public static void main(String[] args) {
        Quarkus.run(CamelMainApplication.class);

        var url = "http://localhost:8080";
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder(URI.create(url))
                .GET()
                .version(Version.HTTP_1_1)
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(System.out::println)
                .thenRun(Quarkus::asyncExit)
                .join();

    }
}
