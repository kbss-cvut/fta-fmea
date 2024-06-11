package cz.cvut.kbss.analysis.model.opdata;

import cz.cvut.kbss.analysis.model.AbstractEntity;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import lombok.Getter;
import lombok.Setter;

@OWLClass(iri = Vocabulary.s_c_operational_data_filter)
@Getter
@Setter
public class OperationalDataFilter extends AbstractEntity {

    @OWLDataProperty(iri = Vocabulary.s_p_min_operational_hours)
    protected Double minOperationalHours;

    public void setAs(OperationalDataFilter other){
        this.minOperationalHours = other.getMinOperationalHours();
    }
}
