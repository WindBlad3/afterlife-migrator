package cl.afterlife.afterlife_migrator.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MigratorRequest {

    @NotNull
    @NotBlank
    @NotEmpty
    @JsonProperty("from-github")
    private String fromGithub;

    @NotNull
    @NotBlank
    @NotEmpty
    @JsonProperty("from-name")
    private String fromName;

    @NotNull
    @NotBlank
    @NotEmpty
    @JsonProperty("to-gitlab")
    private String toGitlab;

    @NotNull
    @NotBlank
    @NotEmpty
    @JsonProperty("to-name")
    private String toName;

    @NotNull
    @NotBlank
    @NotEmpty
    @JsonProperty("download-directory")
    private String downloadDirectory;
}
