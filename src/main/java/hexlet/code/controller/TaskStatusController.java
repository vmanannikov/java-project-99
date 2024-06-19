package hexlet.code.controller;

import hexlet.code.dto.taskstatus.TaskStatusCreateDTO;
import hexlet.code.dto.taskstatus.TaskStatusDTO;
import hexlet.code.dto.taskstatus.TaskStatusUpdateDTO;
import hexlet.code.service.TaskStatusService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/task_statuses")
public class TaskStatusController {

    @Autowired
    private TaskStatusService statusService;

    public TaskStatusController(TaskStatusService statusService) {
        this.statusService = statusService;
    }

    @GetMapping(path = "")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<List<TaskStatusDTO>> index() {
        var tasksStatuses = statusService.getAll();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(tasksStatuses.size()))
                .body(tasksStatuses);
    }

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<TaskStatusDTO> show(@PathVariable Long id) {
        return ResponseEntity.ok()
                .body(statusService.findById(id));
    }

    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskStatusDTO create(@Valid @RequestBody TaskStatusCreateDTO taskStatusData) {
        return statusService.create(taskStatusData);
    }

    @PutMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskStatusDTO update(@Valid @RequestBody TaskStatusUpdateDTO taskStatusData, @PathVariable Long id) {
        return statusService.update(taskStatusData, id);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable Long id) {
        statusService.delete(id);
    }
}
