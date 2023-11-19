import com.rsg.ms.config.RetryDataConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

@Configuration
@Component
@ComponentScan(basePackages = "com.rsg")
public class RetryConfig {

    private final RetryDataConfig retryDataConfig;

    public RetryConfig(RetryDataConfig retryDataConfig) {
        this.retryDataConfig = retryDataConfig;
    }

    @Bean
    public RetryTemplate retryTemplate(){
        RetryTemplate retryTemplate=new RetryTemplate();
        ExponentialBackOffPolicy exponentialBackOffPolicy=new ExponentialBackOffPolicy();

        exponentialBackOffPolicy.setInitialInterval(retryDataConfig.getInitialIntervalMs());
        exponentialBackOffPolicy.setMaxInterval(retryDataConfig.getMaxIntervalMs());
        exponentialBackOffPolicy.setMultiplier(retryDataConfig.getMultiplier());

        retryTemplate.setBackOffPolicy(exponentialBackOffPolicy);
        SimpleRetryPolicy simpleRetryPolicy=new SimpleRetryPolicy();
        simpleRetryPolicy.setMaxAttempts(retryDataConfig.getMaxAttempts());
        retryTemplate.setRetryPolicy(simpleRetryPolicy);

        return retryTemplate;

    }
}
