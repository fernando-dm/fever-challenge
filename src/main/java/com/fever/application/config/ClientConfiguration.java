package com.fever.application.config;

import com.fever.domain.repositories.FindEvents;
import com.fever.infrastructure.clients.Client;
import com.fever.infrastructure.clients.MockClient;
import com.fever.infrastructure.xmlhandler.XmlProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ClientConfiguration {

    @Value("${client.type:mock}") // Default esta seteado a mock
    private String clientType;

    private final XmlProcessor xmlProcessor;

    public ClientConfiguration(XmlProcessor xmlProcessor) {
        this.xmlProcessor = xmlProcessor;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    // La idea de este codigo es usar un servicio externo o uno mock (cache)
    @Bean
    public FindEvents findEvents(RestTemplate restTemplate) {
        if ("mock".equalsIgnoreCase(clientType)) {
            return new MockClient(xmlProcessor);
        } else {
            return new Client(xmlProcessor, restTemplate);
        }
    }
}

