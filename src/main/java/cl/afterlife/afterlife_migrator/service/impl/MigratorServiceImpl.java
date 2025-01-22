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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Log4j2
@Service
public class MigratorServiceImpl implements MigratorService {

    @Autowired
    private GitlabClientService gitlabClientService;

    @Autowired
    private MigratorUtils migratorUtils;

    @Override
    public ResponseEntity<Map<String, HashMap<String, String>>> migrateFromGithubToGitlab(String gitlabToken, String gitlabGroupId, MigratorRequest migratorRequest) throws IOException {

        this.createDownloadDirectory(migratorRequest);
        this.executeMirrorGitClone(migratorRequest);
        this.executeCreateProject(migratorRequest, gitlabToken, gitlabGroupId);
        this.executeUploadProject(migratorRequest);

        log.info("Migration executed successfully!");

        return ResponseEntity.ok(this.migratorUtils.createResponse("Migration executed successfully!", false));

    }

    @Override
    public ResponseEntity<Map<String, HashMap<String, String>>> updateFromGithubToGitlab(String gitlabToken, String gitlabGroupId, MigratorRequest migratorRequest) throws IOException {

        Path path = Paths.get(String.format("%s/%s.git", migratorRequest.getDownloadDirectory(), migratorRequest.getFromName()));

        if (Files.exists(path)) {
            Set<PosixFilePermission> perms = EnumSet.of(
                    PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE,
                    PosixFilePermission.GROUP_READ, PosixFilePermission.OTHERS_READ
            );
            Files.setPosixFilePermissions(path, perms);
            this.deleteDirectory(path);
        }

        this.createDownloadDirectory(migratorRequest);
        this.executeMirrorGitClone(migratorRequest);
        this.executeUpdateProject(migratorRequest);

        log.info("Migration updated successfully!");

        return ResponseEntity.ok(this.migratorUtils.createResponse("Migration updated successfully!", false));

    }

    private void deleteDirectory(Path path) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
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

    private void executeCreateProject(MigratorRequest migratorRequest, String gitlabToken, String gitlabGroupId) throws MalformedURLException {

        URL url = new URL(migratorRequest.getToGitlab());
        String urlGitlab = url.getProtocol().concat("://").concat(url.getAuthority()).concat("/api/v4");

        this.gitlabClientService.createProject(urlGitlab, gitlabToken, migratorRequest.getToName(), gitlabGroupId, "private");

    }

    private void executeUploadProject(MigratorRequest migratorRequest) throws IOException {

        migratorRequest.setToGitlab(migratorRequest.getToGitlab().concat(migratorRequest.getToName()).concat(".git"));
        DefaultExecutor defaultExecutor = DefaultExecutor.builder().setWorkingDirectory(new File(migratorRequest.getDownloadDirectory())).get();

        try {
            CommandLine commandLineRemoteAdd = CommandLine.parse("git");
            commandLineRemoteAdd.addArgument("remote");
            commandLineRemoteAdd.addArgument("add");
            commandLineRemoteAdd.addArgument("gitlab");
            commandLineRemoteAdd.addArgument(migratorRequest.getToGitlab());
            defaultExecutor.execute(commandLineRemoteAdd);
        } catch (ExecuteException ee) {
            if (ee.getExitValue() != 3) {
                throw ee;
            }
        }

        CommandLine commandLinePushMirror = CommandLine.parse("git");
        commandLinePushMirror.addArgument("push");
        commandLinePushMirror.addArgument("--mirror");
        commandLinePushMirror.addArgument("gitlab");
        defaultExecutor.execute(commandLinePushMirror);

    }

    private void executeUpdateProject(MigratorRequest migratorRequest) throws IOException {

        migratorRequest.setToGitlab(migratorRequest.getToGitlab().concat(migratorRequest.getToName()).concat(".git"));
        DefaultExecutor defaultExecutor = DefaultExecutor.builder().setWorkingDirectory(new File(migratorRequest.getDownloadDirectory())).get();

        try {
            CommandLine commandLineRemoteAdd = CommandLine.parse("git");
            commandLineRemoteAdd.addArgument("remote");
            commandLineRemoteAdd.addArgument("add");
            commandLineRemoteAdd.addArgument("mirror");
            commandLineRemoteAdd.addArgument(migratorRequest.getToGitlab());
            defaultExecutor.execute(commandLineRemoteAdd);
        } catch (ExecuteException ee) {
            if (ee.getExitValue() != 3) {
                throw ee;
            }
        }

        CommandLine commandLinePushMirrorAll2 = CommandLine.parse("git");
        commandLinePushMirrorAll2.addArgument("remote");
        commandLinePushMirrorAll2.addArgument("update");
        defaultExecutor.execute(commandLinePushMirrorAll2);

        CommandLine commandLinePushMirrorAll = CommandLine.parse("git");
        commandLinePushMirrorAll.addArgument("push");
        commandLinePushMirrorAll.addArgument("--mirror");
        defaultExecutor.execute(commandLinePushMirrorAll);

        CommandLine commandLinePushMirrorTags = CommandLine.parse("git");
        commandLinePushMirrorTags.addArgument("push");
        commandLinePushMirrorTags.addArgument("mirror");
        commandLinePushMirrorTags.addArgument("--tags");
        defaultExecutor.execute(commandLinePushMirrorTags);

    }

}