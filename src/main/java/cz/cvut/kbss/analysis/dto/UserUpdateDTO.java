package cz.cvut.kbss.analysis.dto;

import cz.cvut.kbss.analysis.model.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDTO extends User {
    private String newPassword;

    public User asUser() {
        User user = new User();
        user.setUri(getUri());
        user.setUsername(getUsername());
        return user;
    }

}
