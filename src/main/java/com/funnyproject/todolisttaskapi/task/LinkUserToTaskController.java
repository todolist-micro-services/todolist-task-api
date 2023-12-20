package com.funnyproject.todolisttaskapi.task;

import com.funnyproject.todolisttaskapi.AppConfig;
import com.funnyproject.todolisttaskapi.utils.InitDataInterface;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import todolist.database.DataInterface;

@RestController
@RequestMapping(value = "/tasks", produces = "application/json")
public class LinkUserToTaskController {

    private final DataInterface dataInterface;

    public LinkUserToTaskController(AppConfig appConfig) {
        this.dataInterface = InitDataInterface.initDataInterface(appConfig.getDbUrl(), appConfig.getDbUserName(),
                appConfig.getDbPassword());
    }

    @PostMapping("/link")
    public ResponseEntity<Object> addLinkBetweenUserAndList(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody LinkUserToTaskRequest linkUserToTaskRequest
    ) {
        final String[] authorization = authorizationHeader.split(" ");

        if (authorization.length != 2) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED) .body("{\"error\": \"Bad authorization header\"}");
        }
        ResponseEntity<Object> response = this.checkParameters(linkUserToTaskRequest);
        if (response != null)
            return response;
        final String dbResponse = this.dataInterface.linkUserToTask(Integer.parseInt(linkUserToTaskRequest.getUser()), Integer.parseInt(linkUserToTaskRequest.getTask()));
        if (!dbResponse.isEmpty())
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"Error\": \"Internal server error\"}");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("{\"Link\": \"Successful\"}");
    }

    @DeleteMapping("/link")
    public ResponseEntity<Object> deleteLinkBetweenUserAndList(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody LinkUserToTaskRequest linkUserToTaskRequest
    ) {
        final String[] authorization = authorizationHeader.split(" ");

        if (authorization.length != 2) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED) .body("{\"error\": \"Bad authorization header\"}");
        }
        ResponseEntity<Object> response = this.checkParameters(linkUserToTaskRequest);
        if (response != null)
            return response;
        final String dbResponse = this.dataInterface.unLinkUserToTask(Integer.parseInt(linkUserToTaskRequest.getUser()), Integer.parseInt(linkUserToTaskRequest.getTask()));
        if (!dbResponse.isEmpty())
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"Error\": \"Internal server error\"}");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("{\"Unlink\": \"Successful\"}");
    }

    private ResponseEntity<Object> checkParameters(LinkUserToTaskRequest linkUserToTaskRequest) {
        try {
            validateProjectCreationRequest(linkUserToTaskRequest);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body("{\"error\": \"Missing parameters, needs : user and project\"}");
        }
        return null;
    }

    private void validateProjectCreationRequest(LinkUserToTaskRequest linkUserToTaskRequest) {
        if (linkUserToTaskRequest == null ||
                linkUserToTaskRequest.getTask() == null ||
                linkUserToTaskRequest.getUser() == null)
            throw new IllegalArgumentException("Missing required parameters");
    }
}
