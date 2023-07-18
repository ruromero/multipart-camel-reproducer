package com.github.ruromero.reproducer.multipart;

import org.apache.camel.Exchange;
import org.apache.camel.attachment.AttachmentMessage;
import org.apache.camel.builder.endpoint.EndpointRouteBuilder;

import jakarta.activation.DataHandler;

public class MultipartRoute extends EndpointRouteBuilder {

    @Override
    public void configure() throws Exception {
        
        rest().get("/")
            .routeId("getMultipart")
            .to("direct:multipart");
        
        from(direct("multipart"))
            .process(this::addAttachment)
            .setBody(constant("Hello Camel"))
            .marshal().mimeMultipart(false, false, true);
    }

    private void addAttachment(Exchange exchange) {
        exchange.getIn(AttachmentMessage.class).addAttachment("report.html", new DataHandler("<html><body>Hello Camel</body></html>", "text/html"));
    }
    
}
