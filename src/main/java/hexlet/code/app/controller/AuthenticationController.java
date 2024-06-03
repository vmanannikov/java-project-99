package hexlet.code.app.controller;

import hexlet.code.app.dto.user.AuthRequestDTO;
import hexlet.code.app.util.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthenticationController {

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    public String create(@RequestBody AuthRequestDTO auth) {
        var authentication = new UsernamePasswordAuthenticationToken(
                auth.getUsername(), auth.getPassword());
        authenticationManager.authenticate(authentication);
        return jwtUtils.generateToken(auth.getUsername());
    }
}
