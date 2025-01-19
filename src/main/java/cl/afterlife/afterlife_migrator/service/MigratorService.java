package cl.afterlife.afterlife_migrator.service;

import cl.afterlife.afterlife_migrator.service.dto.MigratorRequest;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public interface MigratorService {

    ResponseEntity<Map<String, HashMap<String, String>>> migratorFromGithubToGitlab(String gitlabToken, MigratorRequest migratorRequest) throws IOException;

}
