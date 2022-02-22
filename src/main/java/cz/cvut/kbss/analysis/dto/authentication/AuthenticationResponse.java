package cz.cvut.kbss.analysis.dto.authentication;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.net.URI;

@Data
@AllArgsConstructor
public class AuthenticationResponse {
    private URI uri;
    private String username;
    private String token;
    private String[] roles;
}
