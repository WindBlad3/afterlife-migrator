package cl.afterlife.afterlife_migrator.service.impl;

import cl.afterlife.afterlife_migrator.client.service.GitlabClientService;
import cl.afterlife.afterlife_migrator.service.MigratorService;
import cl.afterlife.afterlife_migrator.service.dto.MigratorRequest;
import cl.afterlife.afterlife_migrator.util.MigratorUtils;
import feign.FeignException;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
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
    public ResponseEntity<Map<String, HashMap<String, String>>> migratorFromGithubToGitlab(String gitlabToken, MigratorRequest migratorRequest) throws IOException {
        this.createDownloadDirectory(migratorRequest);
        this.executeMirrorGitClone(migratorRequest);
        this.executeCreateProject(migratorRequest, gitlabToken);
        this.executeUploadProject(migratorRequest);
        log.info("Migration executed successfully!");
        return ResponseEntity.ok(this.migratorUtils.createResponse("Migration executed successfully!", false));
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
            CommandLine commandLineCloneMirror = CommandLine.parse("git");
            commandLineCloneMirror.addArgument("clone");
            commandLineCloneMirror.addArgument("--mirror");
            commandLineCloneMirror.addArgument(migratorRequest.getFromGithub().concat(migratorRequest.getFromName()).concat(".git"));
            commandLineCloneMirror.addArgument(migratorRequest.getDownloadDirectory());
            DefaultExecutor.builder().get().execute(commandLineCloneMirror);
        }
    }

    private void executeCreateProject(MigratorRequest migratorRequest, String gitlabToken) throws MalformedURLException {
        URL url = new URL(migratorRequest.getToGitlab());
        String urlGitlab = url.getProtocol().concat("://").concat(url.getAuthority()).concat("/api/v4");
        String namespaceId = this.gitlabClientService.getGroup(urlGitlab, gitlabToken, url.getPath().replace("/", "")).getBody().get("id").asText();
        try {
            ResponseEntity response = this.gitlabClientService.createProject(urlGitlab, gitlabToken, migratorRequest.getToName(), namespaceId, "private");
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Project created successfully in Gitlab!");
            }
        } catch (FeignException fe) {
            log.warn("Unexpected error trying create the project in Gitlab!");
        }
    }

    private void executeUploadProject(MigratorRequest migratorRequest) throws IOException {

        DefaultExecutor defaultExecutor = DefaultExecutor.builder().setWorkingDirectory(new File(migratorRequest.getDownloadDirectory())).get();

        CommandLine commandLineRemoteAdd = CommandLine.parse("git");
        commandLineRemoteAdd.addArgument("remote");
        commandLineRemoteAdd.addArgument("add");
        commandLineRemoteAdd.addArgument("gitlab");
        commandLineRemoteAdd.addArgument(migratorRequest.getToGitlab().concat(migratorRequest.getToName()).concat(".git"));
        defaultExecutor.execute(commandLineRemoteAdd);

        CommandLine commandLinePushMirror = CommandLine.parse("git");
        commandLinePushMirror.addArgument("push");
        commandLinePushMirror.addArgument("--mirror");
        commandLinePushMirror.addArgument("gitlab");
        defaultExecutor.execute(commandLinePushMirror);

    }

}