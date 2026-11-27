package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.FetchType;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.cvut.kbss.jopa.model.annotations.Transient;
import lombok.Getter;
import lombok.Setter;

import java.net.URI;

@OWLClass(iri = Vocabulary.s_c_Person)
@Getter
@Setter
public class UserReference extends AbstractEntity{

    public static UserReference create(URI uri){
        if(uri == null)
            return null;
        UserReference reference = new UserReference();
        reference.setUri(uri);
        return reference;
    }

    public UserReference() {
    }

    public UserReference(User u) {
        this.setUri(u.getUri());
        this.setUsername(u.getUsername());
    }


    @Transient
    @OWLDataProperty(iri = Vocabulary.s_p_accountName, fetch = FetchType.EAGER )
    private String username;

}
