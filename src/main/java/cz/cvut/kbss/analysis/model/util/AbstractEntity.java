package cz.cvut.kbss.analysis.model.util;

import cz.cvut.kbss.jopa.model.annotations.Id;
import cz.cvut.kbss.jopa.model.annotations.MappedSuperclass;
import lombok.Data;

import java.io.Serializable;
import java.net.URI;

@MappedSuperclass
@Data
public class AbstractEntity implements HasIdentifier, Serializable {

    @Id(generated = true)
    private URI uri;

}
