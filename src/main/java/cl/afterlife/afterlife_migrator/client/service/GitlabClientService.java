package cl.afterlife.afterlife_migrator.client.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.ResponseEntity;

public interface GitlabClientService {
    ResponseEntity<ObjectNode> createProject(String url, String token, String name, String namespaceId, String visibility);

    ResponseEntity<ObjectNode> getGroup(String url, String token, String id);
}
