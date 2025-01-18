package cl.afterlife.afterlife_migrator.client.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateProjectRequest {
    private String name;
    @JsonProperty("namespace_id")
    private String namespaceId;
    private String visibility;
}
