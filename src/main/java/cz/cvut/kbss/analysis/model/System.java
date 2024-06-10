package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.model.opdata.OperationalDataFilter;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.*;
import lombok.Getter;
import lombok.Setter;

@OWLClass(iri = Vocabulary.s_c_system)
@Getter
@Setter
public class System extends Item {

    @Transient
    @OWLObjectProperty(iri = Vocabulary.s_p_has_global_operational_data_filter)
    protected OperationalDataFilter globalOperationalDataFilter;

    @Transient
    @OWLObjectProperty(iri = Vocabulary.s_p_has_operational_data_filter)
    protected OperationalDataFilter operationalDataFilter;

    @Override
    public String toString() {
        return "System <" + getUri() + "/>";
    }
}
