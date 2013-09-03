@Grab("org.springframework.boot:stomp-websocket-autoconfigure:0.5.0.BUILD-SNAPSHOT")
@Controller
@Log
class Application implements CommandLineRunner {
  void run(String... args) {
    log.info("Hello World")
  }
}
