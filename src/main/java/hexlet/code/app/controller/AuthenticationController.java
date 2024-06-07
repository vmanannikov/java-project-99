package hexlet.code.app.controller;

import hexlet.code.app.dto.auth.AuthRequestDTO;
import hexlet.code.app.util.JWTUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthenticationController {

    private JWTUtils jwtUtils;

    private AuthenticationManager authenticationManager;

    public AuthenticationController(JWTUtils jwtUtils, AuthenticationManager authenticationManager) {
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public String create(@RequestBody AuthRequestDTO auth) {
        var authentication = new UsernamePasswordAuthenticationToken(
                auth.getUsername(), auth.getPassword());
        authenticationManager.authenticate(authentication);
        return jwtUtils.generateToken(auth.getUsername());
    }
}
