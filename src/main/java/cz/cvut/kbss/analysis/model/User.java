package cz.cvut.kbss.analysis.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

import static java.util.stream.Collectors.toList;

@EqualsAndHashCode(callSuper = true)
@Data
@OWLClass(iri = Vocabulary.s_c_User)
public class User extends AbstractEntity implements UserDetails {

    @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = Vocabulary.s_p_hasUsername)
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = Vocabulary.s_p_hasPassword)
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
