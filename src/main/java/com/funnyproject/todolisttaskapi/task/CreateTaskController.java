package com.funnyproject.todolisttaskapi.task;

import com.funnyproject.todolisttaskapi.AppConfig;
import com.funnyproject.todolisttaskapi.utils.InitDataInterface;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import todolist.database.DataInterface;
import todolist.database.dataType.List;
import todolist.database.dataType.Project;
import todolist.database.dataType.Task;
import todolist.database.dataType.User;

import java.time.LocalDateTime;

@RestController
@RequestMapping(value = "/tasks", produces = "application/json")
public class CreateTaskController {

    private final DataInterface dataInterface;

    public CreateTaskController(AppConfig appConfig) {
        this.dataInterface = InitDataInterface.initDataInterface(appConfig.getDbUrl(), appConfig.getDbUserName(),
                appConfig.getDbPassword());
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createList(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody CreateTaskRequest createTaskRequest
    ) {
        final String[] authorization = authorizationHeader.split(" ");

        if (authorization.length != 2) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED) .body("{\"error\": \"Bad authorization header\"}");
        }
        ResponseEntity<Object> response = this.checkParameters(createTaskRequest);
        if (response != null)
            return response;
        final User user = new User(Integer.parseInt(createTaskRequest.getCreator()), "", "", "", "");
        final List list = new List(Integer.parseInt(createTaskRequest.getList()), "", "", null, null, null);
        final Task task = new Task(0, createTaskRequest.getName(), createTaskRequest.getDescription(), LocalDateTime.parse(createTaskRequest.getCreationDate().replace(" ", "T")), user, list);
        final String dbResponse = this.dataInterface.createListTask(task);
        if (!dbResponse.isEmpty())
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Internal server error\"}");
        final Task dbTask = this.dataInterface.retrieveListTaskByName(Integer.parseInt(createTaskRequest.getList()), createTaskRequest.getName());
        if (dbTask == null)
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Internal server error\"}");
        final String formatOutput = String.format("{\"task\": \"%s\"}", String.valueOf(dbTask.taskId));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(formatOutput);
    }

    private ResponseEntity<Object> checkParameters(CreateTaskRequest createTaskRequest) {
        try {
            validateProjectCreationRequest(createTaskRequest);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body("{\"error\": \"Missing parameters, needs : name, description, creationDate, creator, list\"}");
        }
        return null;
    }

    private void validateProjectCreationRequest(CreateTaskRequest createTaskRequest) {
        if (createTaskRequest == null ||
                createTaskRequest.getCreator() == null ||
                createTaskRequest.getCreationDate() == null ||
                createTaskRequest.getDescription() == null ||
                createTaskRequest.getList() == null ||
                createTaskRequest.getName() == null) {
            throw new IllegalArgumentException("Missing required parameters");
        }
    }
}
