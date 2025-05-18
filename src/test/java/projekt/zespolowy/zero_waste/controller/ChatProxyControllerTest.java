package projekt.zespolowy.zero_waste.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatProxyController.class)
@AutoConfigureMockMvc(addFilters = false)
class ChatProxyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ChatProxyController controller;

    @Test
    void shouldReturn403WhenNoPermitAll() throws Exception {
        String requestBody = """
            {
                "prompt": "Hello"
            }
        """;

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(csrf()))
                        .andExpect(status().isOk());
    }

    @Test
    void shouldReturnMockedResponse() throws Exception {
        RestTemplate mockRestTemplate = Mockito.mock(RestTemplate.class);

        String mockedResponse = """
            {
                "choices": [
                    {
                        "message": {
                            "content": "Hello! How can I help you?"
                        }
                    }
                ]
            }
        """;

        ResponseEntity<String> responseEntity = new ResponseEntity<>(mockedResponse, HttpStatus.OK);
        Mockito.when(mockRestTemplate.exchange(
                Mockito.anyString(),
                Mockito.eq(HttpMethod.POST),
                Mockito.any(HttpEntity.class),
                Mockito.eq(String.class)
        )).thenReturn(responseEntity);

        Field field = ChatProxyController.class.getDeclaredField("restTemplate");
        field.setAccessible(true);
        field.set(controller, mockRestTemplate);

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"prompt\":\"hello\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.choices[0].message.content").value("Hello! How can I help you?"));
    }
}
