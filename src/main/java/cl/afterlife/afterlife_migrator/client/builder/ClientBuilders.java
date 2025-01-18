package cl.afterlife.afterlife_migrator.client.builder;

import feign.Feign;
import feign.codec.Decoder;
import feign.codec.Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

@Import(FeignClientsConfiguration.class)
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
                .target(client, url);
    }

}
