package cz.cvut.kbss.analysis.dto.update;

import cz.cvut.kbss.analysis.model.Component;

import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class ComponentUpdateDTO extends AbstractUpdateDTO<Component> {

    private String name;

    @Override
    public void copyToEntity(Component entity) {
        entity.setName(getName());
    }
}
