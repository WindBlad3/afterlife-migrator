package cl.afterlife.afterlife_migrator.util;

import cl.afterlife.afterlife_migrator.enums.MigratorEnum;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MigratorUtils {

    public Map<String, HashMap<String, String>> createResponse(String result, boolean isError) {
        HashMap resultResponse = new HashMap<String, String>();
        resultResponse.put("status", isError ? MigratorEnum.ERROR.name() : MigratorEnum.OK.name());
        resultResponse.put("result", result);
        HashMap response = new HashMap<String, HashMap<String, String>>();
        response.put("Afterl1f3-migrat0r", resultResponse);
        return response;
    }
}
