package demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@EnableAutoConfiguration
public class Application {
	
	@RequestMapping("/")
	public String home() {
		return "/monitor";
	}
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
