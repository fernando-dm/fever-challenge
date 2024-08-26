package com.fever.infrastructure.clients;

import com.fever.domain.repositories.FindEvents;
import com.fever.domain.model.EventList;
import com.fever.infrastructure.xmlhandler.XmlProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
public class Client implements FindEvents {

    @Value("${client.url:http://localhost:8080/external/api/xml}")
    private String externalServiceUrl;

    private final XmlProcessor xmlProcessor;
    private final RestTemplate restTemplate;


    public Client(XmlProcessor xmlProcessor, RestTemplate restTemplate) {
        this.xmlProcessor = xmlProcessor;
        this.restTemplate = restTemplate;
    }

    @Override
    public List<EventList> getEventsBetweenDates(LocalDate startDate, LocalDate endDate) {

        // request to API externa
        ResponseEntity<String> response = restTemplate.getForEntity(externalServiceUrl + "?startDate=" + startDate + "&endDate=" + endDate, String.class);

        // Chequeo si es 200 (XX)
        if (response.getStatusCode().is2xxSuccessful()) {
            try {

                Document document = parseXmlDocument(response.getBody());
                return xmlProcessor.extractEventList(document, startDate, endDate);
            } catch (Exception e) {
                throw new RuntimeException("Error processing XML response", e);
            }
        } else {
            throw new RuntimeException("Failed to retrieve events: " + response.getStatusCode());
        }
    }

    private Document parseXmlDocument(String xmlContent) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new ByteArrayInputStream(xmlContent.getBytes()));
    }
}

