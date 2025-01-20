package cl.afterlife.afterlife_migrator.controller;

import cl.afterlife.afterlife_migrator.service.MigratorService;
import cl.afterlife.afterlife_migrator.service.dto.MigratorRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/afterlife-migrator")
public class MigratorController {

    @Autowired
    private MigratorService migratorService;

    @PostMapping(value = "/repositories/github/to/gitlab", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, HashMap<String, String>>> repositoriesMigratorGithubToGitlab(@RequestHeader(name = "Gitlab-Token") String gitlabToken,
                                                                                                   @RequestHeader(name = "Gitlab-Group-Id") String gitlabGroupId,
                                                                                                   @Valid @RequestBody MigratorRequest migratorRequest) throws IOException {
        return this.migratorService.migratorFromGithubToGitlab(gitlabToken, gitlabGroupId, migratorRequest);
    }

}
