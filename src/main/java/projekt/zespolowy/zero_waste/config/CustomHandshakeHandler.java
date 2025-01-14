package projekt.zespolowy.zero_waste.config;

import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.socket.WebSocketSession;

import java.security.Principal;
import java.util.Map;

public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    protected Principal determineUser(WebSocketSession session, Map<String, Object> attributes) {
        // Use a unique identifier for the user, e.g., username passed as a query param
        String username = session.getAttributes().get("username").toString();
        return () -> username; // Return an anonymous Principal implementation
    }
}
