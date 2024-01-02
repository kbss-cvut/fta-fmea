package cz.cvut.kbss.analysis.security.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LoginStatus {

    private boolean loggedIn;
    private boolean success;
    private String username;
    private String errorMessage;

}