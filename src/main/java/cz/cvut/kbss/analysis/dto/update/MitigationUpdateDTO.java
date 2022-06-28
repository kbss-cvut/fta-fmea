package cz.cvut.kbss.analysis.dto.update;

import cz.cvut.kbss.analysis.model.Mitigation;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MitigationUpdateDTO extends AbstractUpdateDTO<Mitigation> {

    private String name;
    private String description;
    private String tablerow;

    @Override
    public void copyToEntity(Mitigation entity) {
        entity.setName(name);
        entity.setDescription(description);
    }
}
