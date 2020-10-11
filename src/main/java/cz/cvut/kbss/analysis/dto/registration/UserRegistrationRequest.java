package cz.cvut.kbss.analysis.dto.registration;

import lombok.Data;

@Data
public class UserRegistrationRequest {

    private String username;
    private String password;

}
