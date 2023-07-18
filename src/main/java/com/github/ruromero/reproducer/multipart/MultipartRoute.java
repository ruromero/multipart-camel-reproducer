package com.github.ruromero.reproducer.multipart;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Version;

import org.apache.camel.Exchange;
import org.apache.camel.attachment.AttachmentMessage;
import org.apache.camel.builder.endpoint.EndpointRouteBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.activation.DataHandler;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MultipartRoute extends EndpointRouteBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultipartRoute.class);

    @ConfigProperty(name = "version", defaultValue = "2")
    String version;

    @Override
    public void configure() throws Exception {

        rest().get("/")
                .routeId("getMultipart")
                .to("direct:multipart");

        from(direct("multipart"))
                .process(this::addAttachment)
                .setBody(constant("Hello Camel"))
                .marshal().mimeMultipart(false, false, true);

        from(timer("runOnce").repeatCount(1)).process(this::sendHttpRequest);
    }

    private void addAttachment(Exchange exchange) {
        exchange.getIn(AttachmentMessage.class).addAttachment("report.html",
                new DataHandler("<html><body>Hello Camel</body></html>", "text/html"));
    }

    private void sendHttpRequest(Exchange exchange) {
        Version httpVersion = Version.HTTP_2;
        if (version.equals("1.1")) {
            httpVersion = Version.HTTP_1_1;
        }
        var url = "http://localhost:8080";
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder(URI.create(url))
                .GET()
                .version(httpVersion)
                .build();

        LOGGER.info("Preparing HTTP GET Request {}", request.version().orElse(Version.HTTP_2));

        try {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            LOGGER.info("Received Response: {}", response.body());
        } catch (IOException | InterruptedException e) {
            LOGGER.error("Unable to send request", e);
        }

    }

}
