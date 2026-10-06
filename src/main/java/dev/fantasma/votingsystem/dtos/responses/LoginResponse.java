package dev.fantasma.votingsystem.dtos.responses;

import dev.fantasma.votingsystem.data.models.Role;
import lombok.Data;

import java.util.Set;

@Data
public class LoginResponse {
    private String token;
    private String email;
    private Set<Role> roles;
}
