package com.helpflow.support.adapter.in.rest;

import com.helpflow.support.adapter.out.persistence.UserEntity;
import com.helpflow.support.adapter.out.persistence.UserRepository;
import com.helpflow.support.domain.PersonStatus;
import com.helpflow.support.config.JwtService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    public AuthController(UserRepository users, PasswordEncoder encoder, JwtService jwt) { this.users=users; this.encoder=encoder; this.jwt=jwt; }
    @PostMapping("/register") @ResponseStatus(HttpStatus.CREATED)
    public Map<String,Object> register(@Valid @RequestBody RegisterRequest request) {
        if (users.existsByEmail(request.email()) || users.existsByDocument(request.document())) throw new IllegalArgumentException("Documento o correo ya registrado");
        var user=users.save(new UserEntity(request.document(),request.name(),request.email(),request.site(),request.area(),PersonStatus.ACTIVO,encoder.encode(request.password()),"ROLE_USUARIO"));
        return Map.of("id",user.getId(),"email",user.getEmail(),"role",user.getRole());
    }
    @PostMapping("/login")
    public Map<String,String> login(@Valid @RequestBody LoginRequest request) {
        var user=users.findByEmail(request.email()).filter(u -> u.getStatus()==PersonStatus.ACTIVO && encoder.matches(request.password(),u.getPasswordHash())).orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas"));
        return Map.of("accessToken",jwt.create(user.getEmail(),user.getRole()),"tokenType","Bearer");
    }
    public record RegisterRequest(@NotBlank String document,@NotBlank String name,@Email String email,@NotBlank String site,@NotBlank String area,@NotBlank @Size(min=8) String password) { }
    public record LoginRequest(@Email String email,@NotBlank String password) { }
}
