package hexlet.code.service;

import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserDTO;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;

import hexlet.code.mapper.UserMapper;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserDTO findById(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        return userMapper.map(user);
    }

    public List<UserDTO> findAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::map)
                .toList();
    }

    public UserDTO findByEmail(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found by: " + email));
        return userMapper.map(user);
    }

    public UserDTO createUser(UserCreateDTO userData) {
        var user = userMapper.map(userData);
        var encodedPassword = passwordEncoder.encode(userData.getPassword());
        user.setPasswordDigest(encodedPassword);
        userRepository.save(user);
        return userMapper.map(user);
    }

    public UserDTO updateUser(UserUpdateDTO userData, Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));

        userMapper.update(userData, user);
        userRepository.save(user);
        return userMapper.map(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
