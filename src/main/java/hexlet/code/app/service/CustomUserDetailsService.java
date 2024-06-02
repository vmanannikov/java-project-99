package hexlet.code.app.service;

import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsManager {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void createUser(UserDetails userDetails) {
        var user = new User();
        user.setEmail(userDetails.getUsername());
        var hashedPassword = passwordEncoder.encode(userDetails.getPassword());
        user.setPasswordDigest(hashedPassword);
        userRepository.save(user);
    }

    @Override
    public void updateUser(UserDetails userDetails) {
        var user = userRepository.findByEmail(userDetails.getUsername());
        if (user != null) {
            user.get().setEmail(userDetails.getUsername());
            user.get().setPasswordDigest(passwordEncoder.encode(userDetails.getPassword()));
            userRepository.save(user.get());
        }
    }

    @Override
    public void deleteUser(String email) {
        var user = userRepository.findByEmail(email);
        if (user != null) {
            userRepository.delete(user.get());
        }
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {

    }

    @Override
    public boolean userExists(String username) {
        return false;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return user;
    }
}
