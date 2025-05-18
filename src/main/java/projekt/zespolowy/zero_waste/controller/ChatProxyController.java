package projekt.zespolowy.zero_waste.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatProxyController {

    @Value("${openrouter.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping
    public ResponseEntity<String> forwardPrompt(@RequestBody Map<String, String> body) {
        String prompt = body.get("prompt");
        String fullPrompt = "You are an online ecommerce shop assistant for shop named 'Zero Waste'. Reply briefly for the following prompt: " + prompt;

        String requestJson = """
            {
                "model": "meta-llama/llama-3.3-8b-instruct:free",
                "messages": [{"role":"user","content":"%s"}]
            }
        """.formatted(fullPrompt);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<String> request = new HttpEntity<>(requestJson, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://openrouter.ai/api/v1/chat/completions",
                    HttpMethod.POST,
                    request,
                    String.class
            );
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Something went wrong\"}");
        }
    }
}
