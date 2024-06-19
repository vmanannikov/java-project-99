package hexlet.code.service;

import hexlet.code.dto.taskstatus.TaskStatusCreateDTO;
import hexlet.code.dto.taskstatus.TaskStatusDTO;
import hexlet.code.dto.taskstatus.TaskStatusUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.repository.TaskStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskStatusService {

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskStatusMapper taskStatusMapper;

    public List<TaskStatusDTO> getAll() {
        var statuses = taskStatusRepository.findAll();
        var result = statuses.stream()
                .map(taskStatusMapper::map)
                .toList();
        return result;
    }

    public TaskStatusDTO findById(Long id) {
        var status = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Status with id " + id + " not found"));

        return taskStatusMapper.map(status);
    }
    public TaskStatusDTO create(TaskStatusCreateDTO dto) {
        var status = taskStatusMapper.map(dto);
        taskStatusRepository.save(status);
        return taskStatusMapper.map(status);
    }
    public TaskStatusDTO update(TaskStatusUpdateDTO dto, Long id) {
        var status = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Status with id " + id + " not found"));

        taskStatusMapper.update(dto, status);
        taskStatusRepository.save(status);
        return taskStatusMapper.map(status);
    }
    public void delete(Long id) {
        var status = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Status with id " + id + " not found"));
        taskStatusRepository.deleteById(id);
    }
}
