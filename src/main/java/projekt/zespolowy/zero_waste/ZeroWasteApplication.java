package projekt.zespolowy.zero_waste;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ZeroWasteApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZeroWasteApplication.class, args);
	}

}
