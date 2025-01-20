package cl.afterlife.afterlife_migrator.client.service;

public interface GitlabClientService {
    void createProject(String url, String token, String name, String namespaceId, String visibility);

}
