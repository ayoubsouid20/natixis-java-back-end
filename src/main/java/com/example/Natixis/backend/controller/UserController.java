package com.example.Natixis.backend.controller;

import com.example.Natixis.backend.constants.ApplicationConstants;
import com.example.Natixis.backend.dto.LoginRequestDTO;
import com.example.Natixis.backend.dto.LoginResponseDTO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final AuthenticationManager authenticationManager;
    private final Environment environment;

    @RequestMapping("/login")
    public ResponseEntity<LoginResponseDTO> apiLogin(@RequestBody LoginRequestDTO loginRequest) {
        String jwt = "";
        Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.username(),
                loginRequest.password());
        Authentication authenticationResponse = authenticationManager.authenticate(authentication);
        if (null != authenticationResponse && authenticationResponse.isAuthenticated()) {
            if (environment != null) {
                String secret = environment.getProperty(ApplicationConstants.JWT_SECRET_KEY,
                        ApplicationConstants.JWT_DEFAULT_VALUE);
                SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
                jwt = Jwts.builder().issuer("Natixis").subject("JWT Token")
                        .claim("username", authenticationResponse.getName())
                        .claim("authorities", authenticationResponse.getAuthorities().stream().map(
                                GrantedAuthority::getAuthority).collect(Collectors.joining(",")))
                        .issuedAt(new Date())
                        .expiration(new Date((new Date()).getTime() + 30000000))
                        .signWith(secretKey).compact();
            }
        }
        return ResponseEntity.status(HttpStatus.OK).header(ApplicationConstants.JWT_HEADER, jwt)
                .body(new LoginResponseDTO(HttpStatus.OK.getReasonPhrase(), jwt));

    }
}
