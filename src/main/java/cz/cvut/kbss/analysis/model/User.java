package cz.cvut.kbss.analysis.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.cvut.kbss.jopa.model.annotations.ParticipationConstraints;
import cz.cvut.kbss.jopa.model.annotations.Transient;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static cz.cvut.kbss.analysis.service.validation.groups.ValidationScopes.Create;
import static cz.cvut.kbss.analysis.service.validation.groups.ValidationScopes.Update;
import static java.util.stream.Collectors.toList;

@OWLClass(iri = Vocabulary.s_c_Person)
@Getter
@Setter
public class User extends AbstractEntity implements UserDetails {

    @Size(min=3, max=15, groups = {Create.class, Update.class}, message = "Username should be at least 3 characters and at most 15")
    @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = Vocabulary.s_p_accountName, simpleLiteral = true)
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OWLDataProperty(iri = Vocabulary.s_p_password, simpleLiteral = true)
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

    @Override
    public String toString() {
        return "User <" + getUri() + "/>";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User that = (User) o;
        return Objects.equals(getUri(), that.getUri());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUri());
    }

    /**
     * @return A copy of this user.
     */
    public User copy() {
        final User copy = new User();
        copy.setUri(getUri());
        copy.setUsername(getUsername());
        copy.setPassword(getPassword());
        copy.setRoles(getRoles());
        return copy;
    }

}
