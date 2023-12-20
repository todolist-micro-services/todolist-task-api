package com.funnyproject.todolisttaskapi.task;

import com.funnyproject.todolisttaskapi.AppConfig;
import com.funnyproject.todolisttaskapi.dto.EventDto;
import com.funnyproject.todolisttaskapi.dto.UserDto;
import com.funnyproject.todolisttaskapi.utils.InitDataInterface;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import todolist.database.DataInterface;
import todolist.database.dataType.Event;
import todolist.database.dataType.Task;
import todolist.database.dataType.User;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
public class RetrieveTaskController {

    private final DataInterface dataInterface;

    public RetrieveTaskController(AppConfig appConfig) {
        this.dataInterface = InitDataInterface.initDataInterface(appConfig.getDbUrl(), appConfig.getDbUserName(), appConfig.getDbPassword());
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<Object> retrieveAllProjectTasks(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @PathVariable String projectId
    ) {
        final String[] authorization = authorizationHeader.split(" ");

        if (authorization.length != 2) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"Bad authorization header\"}");
        }
        User databaseUser = this.dataInterface.retrieveUserFromToken(authorization[1]);
        if (databaseUser == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"User not found\"}");
        try {
            if (isUserLinkToProject(Integer.parseInt(projectId), databaseUser.userId) != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"user must be link to project\"}");
            }
            List<Task> dbTasks = this.dataInterface.retrieveAllProjectTasks(Integer.parseInt(projectId));
            return ResponseEntity.status(HttpStatus.OK).body(dbTasks == null ? new ArrayList<>() : dbTasks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"projectId must be an integer\"}");
        }
    }

    @GetMapping("/list/{listId}")
    public ResponseEntity<Object> retrieveAllListTasks(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @PathVariable String listId
    ) {
        final String[] authorization = authorizationHeader.split(" ");

        if (authorization.length != 2) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"Bad authorization header\"}");
        }
        User databaseUser = this.dataInterface.retrieveUserFromToken(authorization[1]);
        if (databaseUser == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"User not found\"}");
        try {
            List<Task> dbTasks = this.dataInterface.retrieveAllListTasks(Integer.parseInt(listId));
            return ResponseEntity.status(HttpStatus.OK).body(dbTasks == null ? new ArrayList<>() : dbTasks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"projectId must be an integer\"}");
        }
    }

    @GetMapping("/users/{taskId}")
    public ResponseEntity<Object> retrieveAllUserLinkToTask(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @PathVariable String taskId
    ) {
        List<UserDto> users = new ArrayList<>();
        final String[] authorization = authorizationHeader.split(" ");

        if (authorization.length != 2) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"Bad authorization header\"}");
        }
        User databaseUser = this.dataInterface.retrieveUserFromToken(authorization[1]);
        if (databaseUser == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"User not found\"}");
        try {
            List<User> dbUsers = this.dataInterface.retrieveAllUserLinkToTask(Integer.parseInt(taskId));
            if (dbUsers != null)
                for (int i = 0; i != dbUsers.size(); ++i)
                    users.add(new UserDto(dbUsers.get(i).userId, dbUsers.get(i).firstname, dbUsers.get(i).lastname, dbUsers.get(i).email));
            return ResponseEntity.status(HttpStatus.OK).body(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"eventId must be an integer\"}");
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
