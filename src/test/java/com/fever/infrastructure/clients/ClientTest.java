package com.fever.infrastructure.clients;

import com.fever.domain.model.EventList;
import com.fever.infrastructure.xmlhandler.XmlProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientTest {

    @Mock
    private XmlProcessor xmlProcessor;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private Client client;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        client = new Client(xmlProcessor, restTemplate);
        setPrivateField(client, "externalServiceUrl", "http://localhost:8080/external/api/xml");
    }

    @Test
    public void getEventsBetweenDates_with_valid_response_returns_EventList() throws Exception {
        // Given
        String mockXmlResponse = "<xml>Mock XML</xml>";
        ResponseEntity<String> mockResponse = new ResponseEntity<>(mockXmlResponse, HttpStatus.OK);

        when(restTemplate.getForEntity(any(String.class), eq(String.class))).thenReturn(mockResponse);

        Document mockDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(mockXmlResponse.getBytes()));
        List<EventList> mockEventList = List.of(new EventList("baseId", "sellMode", "title", List.of()));
        when(xmlProcessor.extractEventList(any(Document.class), any(LocalDate.class), any(LocalDate.class))).thenReturn(mockEventList);

        // When
        List<EventList> eventList = client.getEventsBetweenDates(LocalDate.now().minusDays(1), LocalDate.now());

        // Then
        assertEquals(mockEventList, eventList);
        verify(restTemplate, times(1)).getForEntity(any(String.class), eq(String.class));
        verify(xmlProcessor, times(1)).extractEventList(any(Document.class), any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    public void getEventsBetweenDates_withNon2xxResponse_throwsRuntimeException() {
        // Given
        ResponseEntity<String> mockResponse = new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        when(restTemplate.getForEntity(any(String.class), eq(String.class))).thenReturn(mockResponse);

        // When
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            client.getEventsBetweenDates(LocalDate.now().minusDays(1), LocalDate.now());
        });

        //Then
        assertEquals("Failed to retrieve events: 400 BAD_REQUEST", exception.getMessage());
        verify(restTemplate, times(1)).getForEntity(any(String.class), eq(String.class));
        verify(xmlProcessor, times(0)).extractEventList(any(Document.class), any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    public void getEventsBetweenDates_withXmlParsingFailure_throwsRuntimeException() {
        // Given
        String validXmlResponse = "<root><event>Valid Event</event></root>";
        ResponseEntity<String> mockResponse = new ResponseEntity<>(validXmlResponse, HttpStatus.OK);

        when(restTemplate.getForEntity(any(String.class), eq(String.class))).thenReturn(mockResponse);

        when(xmlProcessor.extractEventList(any(Document.class), any(LocalDate.class), any(LocalDate.class)))
                .thenThrow(new RuntimeException("XML parsing error"));

        // When
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            client.getEventsBetweenDates(LocalDate.now().minusDays(1), LocalDate.now());
        });

        // Then
        assertEquals("Error processing XML response", exception.getMessage());
        verify(restTemplate, times(1)).getForEntity(any(String.class), eq(String.class));
        verify(xmlProcessor, times(1)).extractEventList(any(Document.class), any(LocalDate.class), any(LocalDate.class));
    }

    private void setPrivateField(Object target, String fieldName, String value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
