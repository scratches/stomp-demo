@Grab("org.springframework.boot:spring-boot-starter-actuator:0.5.0.BUILD-SNAPSHOT")
@Controller
@Log
class Application implements CommandLineRunner {

  void run(String... args) {
    log.info("Hello World")
  }
}
