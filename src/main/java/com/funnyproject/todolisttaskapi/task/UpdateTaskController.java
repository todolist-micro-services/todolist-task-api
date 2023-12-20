package com.funnyproject.todolisttaskapi.task;

import com.funnyproject.todolisttaskapi.AppConfig;
import com.funnyproject.todolisttaskapi.dto.EventDto;
import com.funnyproject.todolisttaskapi.utils.InitDataInterface;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import todolist.database.DataInterface;
import todolist.database.dataType.Project;
import todolist.database.dataType.Task;
import todolist.database.dataType.User;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
public class UpdateTaskController {

    private final DataInterface dataInterface;

    public UpdateTaskController(AppConfig appConfig) {
        this.dataInterface = InitDataInterface.initDataInterface(appConfig.getDbUrl(), appConfig.getDbUserName(), appConfig.getDbPassword());
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateList(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody UpdateTaskRequest updateTaskRequest
    ) {
        final String[] authorization = authorizationHeader.split(" ");
        ResponseEntity<Object> checkBodyError = this.checkBody(updateTaskRequest);
        User databaseUser;

        if (checkBodyError != null)
            return checkBodyError;
        if (authorization.length != 2) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"Bad authorization header\"}");
        }
        databaseUser = this.dataInterface.retrieveUserFromToken(authorization[1]);
        if (databaseUser == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"User not found\"}");
        return getNewEvent(updateTaskRequest);
    }

    private ResponseEntity<Object> getNewEvent(UpdateTaskRequest updateTaskRequest) {
        final User user = new User(Integer.parseInt(updateTaskRequest.getCreator()), "", "", "", "");
        final todolist.database.dataType.List list = new todolist.database.dataType.List(Integer.parseInt(updateTaskRequest.getList()), "", "", null, null, null);
        final Task task = new Task(Integer.parseInt(updateTaskRequest.getId()), updateTaskRequest.getName(), updateTaskRequest.getDescription(), LocalDateTime.parse(updateTaskRequest.getCreationDate().replace(" ", "T")), user, list);
        final String dbResponse = this.dataInterface.updateListTask(task);
        if (!dbResponse.isEmpty())
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Internal server error\"}");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(task);
    }

    private ResponseEntity<Object> isUserLinkToProject(int projectId, int userId) {
        List<User> users = this.dataInterface.retrieveAllUserLinkToProject(projectId);
        for (int i = 0; i != users.size(); ++i)
            if (users.get(i).userId == userId)
                return null;
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"User must be link to project\"}");
    }

    private ResponseEntity<Object> returnNewUser(final String token) {
        EventDto user = new EventDto();
        User databaseUser = this.dataInterface.retrieveUserFromToken(token);

        if (databaseUser == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"User not found\"}");
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    private ResponseEntity<Object> checkBody(UpdateTaskRequest updateTaskRequest) {
        try {
            this.validateUpdateRequest(updateTaskRequest);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("{\"error\": \"Missing parameters, needs : firstname, lastname\"}");
        }
        return null;
    }

    private void validateUpdateRequest(UpdateTaskRequest updateTaskRequest) {
        if (updateTaskRequest == null ||
                updateTaskRequest.getId() == null ||
                updateTaskRequest.getName() == null ||
                updateTaskRequest.getDescription() == null ||
                updateTaskRequest.getCreationDate() == null ||
                updateTaskRequest.getList() == null ||
                updateTaskRequest.getCreator() == null)
            throw new IllegalArgumentException("Missing required parameters");
    }

}
