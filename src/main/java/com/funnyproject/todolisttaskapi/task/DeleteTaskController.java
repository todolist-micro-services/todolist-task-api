package com.funnyproject.todolisttaskapi.task;

import com.funnyproject.todolisttaskapi.AppConfig;
import com.funnyproject.todolisttaskapi.utils.InitDataInterface;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import todolist.database.DataInterface;
import todolist.database.dataType.User;

import java.util.List;

@RestController
@RequestMapping(value = "/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
public class DeleteTaskController {

    private final DataInterface dataInterface;

    public DeleteTaskController(AppConfig appConfig) {
        this.dataInterface = InitDataInterface.initDataInterface(appConfig.getDbUrl(), appConfig.getDbUserName(), appConfig.getDbPassword());
    }

    @DeleteMapping("/delete/{taskId}")
    public ResponseEntity<Object> deleteList(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @PathVariable String taskId
    ) {
        final String[] authorization = authorizationHeader.split(" ");

        if (authorization.length != 2) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"Bad authorization header\"}");
        }
        User databaseUser = this.dataInterface.retrieveUserFromToken(authorization[1]);
        if (databaseUser == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"User not found\"}");
        try {
            if (!this.dataInterface.deleteListTask(Integer.parseInt(taskId)).isEmpty())
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Internal server error\"}");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"taskId must be an integer\"}");
        }
    }

    private ResponseEntity<Object> isUserLinkToProject(int projectId, int userId) {
        List<User> users = this.dataInterface.retrieveAllUserLinkToProject(projectId);
        for (int i = 0; i != users.size(); ++i)
            if (users.get(i).userId == userId)
                return null;
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"User must be link to project\"}");
    }

}
