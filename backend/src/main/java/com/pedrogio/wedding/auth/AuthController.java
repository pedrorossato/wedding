package com.pedrogio.wedding.auth;

import com.pedrogio.wedding.config.WeddingProperties;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final WeddingProperties properties;
    private final JwtService jwtService;
    private final EncryptionService encryptionService;

    public AuthController(WeddingProperties properties,
                          JwtService jwtService,
                          EncryptionService encryptionService) {
        this.properties = properties;
        this.jwtService = jwtService;
        this.encryptionService = encryptionService;
    }

    @PostMapping("/api/auth/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        var admin = properties.getAdmin();

        String decryptedPassword = encryptionService.decrypt(request.password());

        if (!admin.getUsername().equals(request.username()) ||
            !admin.getPassword().equals(decryptedPassword)) {
            return ResponseEntity.status(401).build();
        }

        String token = jwtService.generateToken(request.username());
        return ResponseEntity.ok(new LoginResponse(token));
    }
}
