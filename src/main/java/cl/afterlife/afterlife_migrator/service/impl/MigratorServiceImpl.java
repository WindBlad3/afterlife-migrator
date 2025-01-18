package cl.afterlife.afterlife_migrator.service.impl;

import cl.afterlife.afterlife_migrator.client.service.GitlabClientService;
import cl.afterlife.afterlife_migrator.service.MigratorService;
import cl.afterlife.afterlife_migrator.service.dto.MigratorRequest;
import cl.afterlife.afterlife_migrator.util.MigratorUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@Service
public class MigratorServiceImpl implements MigratorService {

    @Autowired
    private GitlabClientService gitlabClientService;

    @Autowired
    private MigratorUtils migratorUtils;

    @Override
    public ResponseEntity<Map<String, HashMap<String, String>>> migratorFromGithubToGitlab(String gitlabToken, MigratorRequest migratorRequest) {
        try {
            this.createDownloadDirectory(migratorRequest);
            this.executeMirrorGitClone(migratorRequest);
            this.executeCreateProject(migratorRequest, gitlabToken);
            this.executeUploadProject(migratorRequest);
            return ResponseEntity.ok(this.migratorUtils.createResponse("Migration executed successfully!", false));
        } catch (ExecuteException ee) {
            return ResponseEntity.status(409).body(this.migratorUtils.createResponse("Error in execute the command, error: ".concat(ee.getMessage()), true));
        } catch (MalformedURLException me) {
            return ResponseEntity.status(400).body(this.migratorUtils.createResponse("Incorrect gitlab URL structure, please check, error: ".concat(me.toString()), true));
        } catch (IOException io) {
            return ResponseEntity.status(512).body(this.migratorUtils.createResponse("Error created local directory, error: ".concat(io.toString()), true));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(this.migratorUtils.createResponse("Unexpected error: ".concat(e.getMessage()), true));
        }
    }

    private void createDownloadDirectory(MigratorRequest migratorRequest) throws IOException {
        migratorRequest.setDownloadDirectory(String.format("%s/%s.git", migratorRequest.getDownloadDirectory(), migratorRequest.getFromName()));
        Path path = Paths.get(migratorRequest.getDownloadDirectory());
        if (Files.notExists(path)) {
            Files.createDirectories(path);
        }
    }

    private void executeMirrorGitClone(MigratorRequest migratorRequest) throws IOException {
        Path path = Paths.get(migratorRequest.getDownloadDirectory());
        if (Files.exists(path) && Files.list(path).count() == 0) {
            CommandLine commandLine = CommandLine.parse("git");
            commandLine.addArgument("clone");
            commandLine.addArgument("--mirror");
            commandLine.addArgument(migratorRequest.getFromGithub().concat(migratorRequest.getFromName()).concat(".git"));
            commandLine.addArgument(migratorRequest.getDownloadDirectory());
            DefaultExecutor.builder().get().execute(commandLine);
        }
    }

    private void executeCreateProject(MigratorRequest migratorRequest, String gitlabToken) throws MalformedURLException {
        URL url = new URL(migratorRequest.getToGitlab());
        String urlGitlab = url.getProtocol().concat("://").concat(url.getAuthority()).concat("/api/v4");
        String namespaceId = this.gitlabClientService.getGroup(urlGitlab, gitlabToken, url.getPath().replace("/", "")).getBody().get("id").asText();
        this.gitlabClientService.createProject(urlGitlab, gitlabToken, migratorRequest.getToName(), namespaceId, "private");

    }

    private void executeUploadProject(MigratorRequest migratorRequest) {


    }

}