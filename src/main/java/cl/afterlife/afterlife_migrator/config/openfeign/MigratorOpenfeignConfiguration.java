package cl.afterlife.afterlife_migrator.config.openfeign;

import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MigratorOpenfeignConfiguration {

    @Bean
    @Primary
    Decoder Decoder() {
        return new ResponseEntityDecoder(new JacksonDecoder());
    }

    @Bean
    @Primary
    Encoder encoder() {
        return new JacksonEncoder();
    }

//    @Bean
//    @Primary
//    Contract contract() {
//        return new Contract.Default();
//    }

}
