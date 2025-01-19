package cl.afterlife.afterlife_migrator.exception.handler;

import cl.afterlife.afterlife_migrator.util.MigratorUtils;
import org.apache.commons.exec.ExecuteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.net.MalformedURLException;

@ControllerAdvice
public class MigratorExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private MigratorUtils migratorUtils;

    @ExceptionHandler(ExecuteException.class)
    public ResponseEntity handleExecuteException(ExecuteException ee) {
        return ResponseEntity.status(409).body(this.migratorUtils.createResponse("Error in execute the command, error: ".concat(ee.getMessage()), true));
    }

    @ExceptionHandler(MalformedURLException.class)
    public ResponseEntity handleMalformedURLException(MalformedURLException me) {
        return ResponseEntity.status(400).body(this.migratorUtils.createResponse("Incorrect gitlab URL structure, please check, error: ".concat(me.toString()), true));
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity handleIOException(IOException ee) {
        return ResponseEntity.status(512).body(this.migratorUtils.createResponse("Error access or created local directory, error: ".concat(ee.toString()), true));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleException(Exception ee) {
        return ResponseEntity.status(500).body(this.migratorUtils.createResponse("Unexpected error: ".concat(ee.getMessage()), true));
    }

}
