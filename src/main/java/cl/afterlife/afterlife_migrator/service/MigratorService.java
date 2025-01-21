package cl.afterlife.afterlife_migrator.service;

import cl.afterlife.afterlife_migrator.service.dto.MigratorRequest;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public interface MigratorService {
    ResponseEntity<Map<String, HashMap<String, String>>> migrateFromGithubToGitlab(String gitlabToken, String gitlabGroupId, MigratorRequest migratorRequest) throws IOException;

    ResponseEntity<Map<String, HashMap<String, String>>> updateFromGithubToGitlab(String gitlabToken, String gitlabGroupId, MigratorRequest migratorRequest) throws IOException;
}
