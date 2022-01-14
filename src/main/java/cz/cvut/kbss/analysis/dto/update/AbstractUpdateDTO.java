package cz.cvut.kbss.analysis.dto.update;

import cz.cvut.kbss.analysis.dto.UpdatableEntity;
import lombok.Data;

import java.net.URI;

@Data
public abstract class AbstractUpdateDTO<T> implements UpdatableEntity<T> {

    private URI uri;

}
