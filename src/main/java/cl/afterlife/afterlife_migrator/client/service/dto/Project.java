package cl.afterlife.afterlife_migrator.client.service.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Project {
    @JsonProperty("name")
    private String name;
    @JsonProperty("path")
    private String path;
    @JsonProperty("namespace_id")
    private String namespaceId;
    @JsonProperty("visibility")
    private String visibility;
}
