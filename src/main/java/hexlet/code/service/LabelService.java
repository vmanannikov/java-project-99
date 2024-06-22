package hexlet.code.service;

import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelDTO;
import hexlet.code.dto.label.LabelUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.repository.LabelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LabelService {

    private final LabelRepository labelRepository;

    private final LabelMapper labelMapper;

    public List<LabelDTO> findAll() {
        var label = labelRepository.findAll();
        return label.stream()
                .map(labelMapper::map)
                .toList();
    }

    public LabelDTO findById(Long id) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label with id " + id + " not found"));

        return labelMapper.map(label);
    }

    public LabelDTO create(LabelCreateDTO labelData) {
        var label = labelMapper.map(labelData);
        labelRepository.save(label);

        return labelMapper.map(label);
    }

    public LabelDTO update(LabelUpdateDTO labelData, Long id) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label with id " + id + " not found"));

        labelMapper.update(labelData, label);
        labelRepository.save(label);

        return labelMapper.map(label);
    }

    public void delete(Long id) {
        labelRepository.deleteById(id);
    }
}
