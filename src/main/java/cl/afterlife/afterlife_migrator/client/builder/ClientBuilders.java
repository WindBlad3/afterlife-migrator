package cl.afterlife.afterlife_migrator.client.builder;

import feign.Feign;
import feign.Logger;
import feign.codec.Decoder;
import feign.codec.Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

@Import(FeignClientProperties.FeignClientConfiguration.class)
@Component
public class ClientBuilders {

    @Autowired
    private Decoder decoder;

    @Autowired
    private Encoder encoder;

    public <T> T createClient(String url, Class<T> client) {
        return Feign.builder()
                .encoder(encoder)
                .decoder(decoder)
                .logLevel(Logger.Level.FULL)
                .target(client, url);
    }

}
