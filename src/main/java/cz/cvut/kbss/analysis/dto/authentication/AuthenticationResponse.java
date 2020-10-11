package cz.cvut.kbss.analysis.dto.authentication;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthenticationResponse {
    private String username;
    private String token;
}
