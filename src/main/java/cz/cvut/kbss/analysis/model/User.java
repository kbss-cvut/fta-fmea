package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.model.util.AbstractEntity;
import cz.cvut.kbss.jopa.model.annotations.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

import static java.util.stream.Collectors.toList;

@EqualsAndHashCode(callSuper = true)
@Data
@OWLClass(iri = Vocabulary.User)
public class User extends AbstractEntity implements UserDetails {

    @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = Vocabulary.p_has_username)
    private String username;

    @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = Vocabulary.p_has_password)
    private String password;

    @Transient
    private List<String> roles = Collections.singletonList("ROLE_USER");

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream().map(SimpleGrantedAuthority::new).collect(toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
