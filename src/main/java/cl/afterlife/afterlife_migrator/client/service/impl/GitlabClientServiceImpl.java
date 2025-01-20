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
    public void createProject(String url, String token, String name, String namespaceId, String visibility) {
        try {
            CreateProjectRequest createProjectRequest = CreateProjectRequest.builder().name(name).namespaceId(namespaceId).visibility("private").build();
            log.info("[Gitlab Client][Open feign][Create project] - Request -> {}", this.objectMapper.convertValue(createProjectRequest, ObjectNode.class));
            ResponseEntity<ObjectNode> response = this.clientBuilders.createClient(url, GitlabClient.class).createProject(token, createProjectRequest);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.debug("[Gitlab Client][Open feign][Create project] - Response detail -> {}", response.getBody());
                log.info("[Gitlab Client][Open feign][Create project] - Response -> Project created successfully in Gitlab!");
            }
        } catch (FeignException fe) {
            if (fe.status() == 400 && fe.getMessage().contains("has already been taken")) {
                log.warn("[Gitlab Client][Open feign][Create project] - Warn: The project already exists, the files from the source repository will be uploaded...");
            } else {
                log.error("[Gitlab Client][Open feign][Create project] - Unexpected error trying create the project in Gitlab!, Error:", fe);
                throw fe;
            }
        }
    }
}
