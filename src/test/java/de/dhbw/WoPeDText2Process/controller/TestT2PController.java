package de.dhbw.WoPeDText2Process.controller;

import static org.junit.Assert.assertNotNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import net.minidev.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

public class TestT2PController {

  static String generateUrl;
  static RestTemplate restTemplate;
  static HttpHeaders httpHeaders;
  static JSONObject pnmlJsonObject;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeClass
  public static void runBeforeAllTest() {
    generateUrl = "http://localhost:8081/t2p/generatePNML";

    restTemplate = new RestTemplate();
    httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    pnmlJsonObject = new JSONObject();
    pnmlJsonObject.put(null, "The consultant creates a file.");
  }

  @Test
  public void testGeneratePetriNetFromText() throws IOException {
    HttpEntity<String> request = new HttpEntity<String>(pnmlJsonObject.toString(), httpHeaders);
    String pnmlResultAsJsonStr = restTemplate.postForObject(generateUrl, request, String.class);
    JsonNode root = objectMapper.readTree(pnmlResultAsJsonStr);

    assertNotNull(pnmlResultAsJsonStr);
    assertNotNull(root);
    assertNotNull(root.path("text").asText());
  }
}
