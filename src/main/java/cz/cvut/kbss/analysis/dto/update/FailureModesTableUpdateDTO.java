package cz.cvut.kbss.analysis.dto.update;

import cz.cvut.kbss.analysis.model.FailureModesTable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class FailureModesTableUpdateDTO extends AbstractUpdateDTO<FailureModesTable> {

    private String name;

    @Override
    public void copyToEntity(FailureModesTable entity) {
        entity.setName(name);
    }
}
