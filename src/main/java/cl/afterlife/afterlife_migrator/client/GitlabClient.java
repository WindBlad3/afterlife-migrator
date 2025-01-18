package cl.afterlife.afterlife_migrator.client;

import cl.afterlife.afterlife_migrator.client.service.dto.CreateProjectRequest;
import com.fasterxml.jackson.databind.node.ObjectNode;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "gitlabClient")
public interface GitlabClient {
    @RequestLine("POST /projects")
    @Headers("PRIVATE-TOKEN: {token}")
    ResponseEntity<ObjectNode> createProject(@Param("token") String token, @RequestBody CreateProjectRequest createProjectRequest);

    @RequestLine("GET /groups/{id}")
    @Headers("PRIVATE-TOKEN: {token}")
    ResponseEntity<ObjectNode> getGroup(@Param("token") String token, @Param("id") String id);
}
