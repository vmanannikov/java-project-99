package hexlet.code.controller;

import hexlet.code.dto.auth.AuthRequestDTO;
import hexlet.code.util.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api")
@RequiredArgsConstructor
public class AuthenticationController {

    private final JWTUtils jwtUtils;

    private final AuthenticationManager authenticationManager;

    @PostMapping(path = "/login")
    @ResponseStatus(HttpStatus.OK)
    public String create(@RequestBody AuthRequestDTO auth) {
        var authentication = new UsernamePasswordAuthenticationToken(
                auth.getUsername(),
                auth.getPassword()
        );

        authenticationManager.authenticate(authentication);

        var token = jwtUtils.generateToken(auth.getUsername());
        return token;
    }
}
