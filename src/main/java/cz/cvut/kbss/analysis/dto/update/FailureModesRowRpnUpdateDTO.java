package cz.cvut.kbss.analysis.dto.update;

import cz.cvut.kbss.analysis.model.FailureModesRow;
import cz.cvut.kbss.analysis.model.RiskPriorityNumber;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class FailureModesRowRpnUpdateDTO extends AbstractUpdateDTO<FailureModesRow> {

    private Integer severity;
    private Integer occurrence;
    private Integer detection;

    @Override
    public void copyToEntity(FailureModesRow entity) {
        if (entity.getRiskPriorityNumber() == null) {
            entity.setRiskPriorityNumber(new RiskPriorityNumber());
        }

        entity.getRiskPriorityNumber().setSeverity(severity);
        entity.getRiskPriorityNumber().setOccurrence(occurrence);
        entity.getRiskPriorityNumber().setDetection(detection);
    }
}
