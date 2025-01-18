package cl.afterlife.afterlife_migrator.client.service.impl;

import cl.afterlife.afterlife_migrator.client.GitlabClient;
import cl.afterlife.afterlife_migrator.client.builder.ClientBuilders;
import cl.afterlife.afterlife_migrator.client.service.GitlabClientService;
import cl.afterlife.afterlife_migrator.client.service.dto.CreateProjectRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import feign.FeignException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class GitlabClientServiceImpl implements GitlabClientService {

    @Autowired
    private ClientBuilders clientBuilders;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public ResponseEntity<ObjectNode> createProject(String url, String token, String name, String namespaceId, String visibility) {
        try {
            CreateProjectRequest createProjectRequest = CreateProjectRequest.builder().name(name).namespaceId(namespaceId).visibility("private").build();
            log.info("[Gitlab Client][Open feign][Create project] - Request -> {}", this.objectMapper.convertValue(createProjectRequest, ObjectNode.class));
            ResponseEntity<ObjectNode> response = this.clientBuilders.createClient(url, GitlabClient.class).createProject(token, createProjectRequest);
            log.info("[Gitlab Client][Open feign][Create project] - Response -> {}", response.getBody());
            return response;
        } catch (FeignException fe) {
            log.error("[Gitlab Client][Open feign][Create project] - Error:", fe);
            throw fe;
        }
    }

    @Override
    public ResponseEntity<ObjectNode> getGroup(String url, String token, String id) {
        try {
            log.info("[Gitlab Client][Open feign][Get Group] - Params -> id: {}.", id);
            ResponseEntity<ObjectNode> response = this.clientBuilders.createClient(url, GitlabClient.class).getGroup(token, id);
            log.info("[Gitlab Client][Open feign][Get Group] - Response -> {}", response.getBody());
            return response;
        } catch (FeignException fe) {
            log.error("[Gitlab Client][Open feign][Get Group] - Error:", fe);
            throw fe;
        }
    }
}
