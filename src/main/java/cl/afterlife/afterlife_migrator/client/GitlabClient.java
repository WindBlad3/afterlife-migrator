package cl.afterlife.afterlife_migrator.client;

import cl.afterlife.afterlife_migrator.client.service.dto.Project;
import cl.afterlife.afterlife_migrator.config.openfeign.MigratorOpenfeignConfiguration;
import com.fasterxml.jackson.databind.node.ObjectNode;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "gitlabClient", configuration = MigratorOpenfeignConfiguration.class)
public interface GitlabClient {
    @RequestLine("POST /projects")
    @Headers("PRIVATE-TOKEN: {token}")
    ResponseEntity<ObjectNode> createProject(@Param("token") String token, @RequestBody Project project);

    @RequestLine("GET /groups/{id}")
    @Headers("PRIVATE-TOKEN: {token}")
    ResponseEntity<ObjectNode> getGroup(@Param("token") String token, @Param("id") String id);
}
