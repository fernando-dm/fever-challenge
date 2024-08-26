package com.fever.infrastructure.clients;

import com.fever.domain.repositories.FindEvents;
import com.fever.domain.model.EventList;
import com.fever.infrastructure.xmlhandler.XmlProcessor;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;


@Service
public class MockClient implements FindEvents {

    private final String XML_FILE = "src/main/resources/xml/events.xml";
    private final XmlProcessor xmlProcessor;

    public MockClient(XmlProcessor xmlProcessor) {
        this.xmlProcessor = xmlProcessor;
    }

    @Override
    public List<EventList> getEventsBetweenDates(LocalDate startDate, LocalDate endDate) {

        try {
            // busco xml
            File file = new File(XML_FILE);

            // creo objeto documento xml
            Document document = parseXmlDocument(file);

            List<EventList> eventLists = xmlProcessor.extractEventList(document, startDate, endDate);

            return eventLists;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private static Document parseXmlDocument(File file) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(file);
    }
}

