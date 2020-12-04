package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.UserDao;
import cz.cvut.kbss.analysis.dto.UserUpdateDTO;
import cz.cvut.kbss.analysis.environment.Environment;
import cz.cvut.kbss.analysis.environment.Generator;
import cz.cvut.kbss.analysis.exception.LogicViolationException;
import cz.cvut.kbss.analysis.exception.UsernameNotAvailableException;
import cz.cvut.kbss.analysis.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertThrows;

class UserRepositoryServiceTest {

    @Mock
    UserDao userDao;
    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserRepositoryService repositoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void register_usernameExists_shouldThrowException() {
        User user = new User();
        user.setUsername("user");
        user.setUsername("password");

        Mockito.when(userDao.existsWithUsername(user.getUsername())).thenReturn(true);

        assertThrows(UsernameNotAvailableException.class, () -> repositoryService.register(user));
    }

    @Test
    void register_shouldEncodePassword_shouldCallPersist() {
        User user = new User();
        user.setUsername("user");
        user.setUsername("password");

        Mockito.when(userDao.existsWithUsername(user.getUsername())).thenReturn(false);

        repositoryService.register(user);

        Mockito.verify(passwordEncoder).encode(user.getPassword());
        Mockito.verify(userDao).persist(user);
    }

    @Test
    void updateCurrent_passwordsDoNotMatch_shouldThrowException() {
        User user = new User();
        user.setUri(Generator.generateUri());
        Environment.setCurrentUser(user);

        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setUri(user.getUri());

        Mockito.when(passwordEncoder.matches(updateDTO.getPassword(), user.getPassword())).thenReturn(false);

        assertThrows(LogicViolationException.class, () -> repositoryService.updateCurrent(updateDTO));
    }

    @Test
    void updateCurrent_passwordsOk_shouldEncodePassword_shouldCallUpdate() {
        User user = new User();
        user.setUri(Generator.generateUri());
        Environment.setCurrentUser(user);

        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setNewPassword("oldPassword");
        updateDTO.setUri(user.getUri());

        Mockito.when(passwordEncoder.matches(updateDTO.getPassword(), user.getPassword())).thenReturn(true);
        Mockito.when(userDao.exists(user.getUri())).thenReturn(true);
        Mockito.when(userDao.update(user)).thenReturn(user);

        repositoryService.updateCurrent(updateDTO);

        Mockito.verify(passwordEncoder).encode(updateDTO.getNewPassword());
        Mockito.verify(userDao).update(user);
    }


}