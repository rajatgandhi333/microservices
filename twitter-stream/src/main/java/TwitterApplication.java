
import com.rsg.init.StreamInitialzier;
import com.rsg.runner.TweetRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(basePackages = "com.rsg")
public class TwitterApplication implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterApplication.class);

    private final TweetRunner tweetRunner;

    private final StreamInitialzier streamInitialzier;

    public TwitterApplication(TweetRunner tweetRunner, StreamInitialzier streamInitialzier) {
        this.tweetRunner = tweetRunner;
        this.streamInitialzier = streamInitialzier;
    }

    public static void main(String[] args) {
        SpringApplication.run(TwitterApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Application Starts");
        streamInitialzier.init();
        tweetRunner.start();
    }
}
