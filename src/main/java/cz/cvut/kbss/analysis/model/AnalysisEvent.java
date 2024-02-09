package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.cvut.kbss.jopa.model.annotations.OWLObjectProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@OWLClass(iri = Vocabulary.s_c_analysis_event)
@Getter
@Setter
public class AnalysisEvent<T extends AnalysisProduct> extends AbstractEntity{

    @OWLObjectProperty(iri = Vocabulary.s_p_performed_by)
    private User agent;

    @OWLObjectProperty(iri = Vocabulary.s_p_based_on)
    private NamedEntity analysisMethod;

    @OWLDataProperty(iri = Vocabulary.s_p_has_start)
    private Date startTime;
    @OWLDataProperty(iri = Vocabulary.s_p_has_end)
    private Date endTime;

    @OWLObjectProperty(iri = Vocabulary.s_p_creates)
    private T product;

}
