package cz.cvut.kbss.analysis.model.diagram;


import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.Id;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.cvut.kbss.jopa.model.annotations.Transient;
import lombok.Getter;
import lombok.Setter;

import java.net.URI;

@OWLClass(iri = Vocabulary.s_c_rectangle)
@Getter
@Setter
public class Rectangle {

    @Id(generated = true)
    private URI uri;

    @OWLDataProperty(iri = Vocabulary.s_p_x)
    private Double x;
    @OWLDataProperty(iri = Vocabulary.s_p_y)
    private Double y;

    @OWLDataProperty(iri = Vocabulary.s_p_width)
    private Double width;
    @OWLDataProperty(iri = Vocabulary.s_p_height)
    private Double height;

    public Rectangle() {
    }
    public Rectangle(Double[] array){
        this(array[0], array[1], array[2], array[3]);
    }

    public Rectangle(Double x, Double y, Double width, Double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {
        return "Rectangle[" +
                "x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                ']';
    }

}
