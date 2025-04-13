package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.FailureModesRowDao;
import cz.cvut.kbss.analysis.dto.update.FailureModesRowRpnUpdateDTO;
import cz.cvut.kbss.analysis.environment.Generator;
import cz.cvut.kbss.analysis.model.FailureModesRow;
import cz.cvut.kbss.analysis.service.validation.EntityValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

class FailureModesRowRepositoryServiceTest {

    @Mock
    FailureModesRowDao failureModesRowDao;
    @Mock
    EntityValidator validator;
    @InjectMocks
    FailureModesRowRepositoryService repositoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void updateByDTO_shouldFind_shouldCopyData_shouldCallUpdate() {
        FailureModesRowRpnUpdateDTO updateDTO = new FailureModesRowRpnUpdateDTO();
        updateDTO.setUri(Generator.generateUri());
        updateDTO.setSeverity(Generator.randomInt(1, 10));
        updateDTO.setOccurrence(Generator.randomInt(1, 10));
        updateDTO.setDetection(Generator.randomInt(1, 10));

        FailureModesRow row = new FailureModesRow();
        row.setUri(updateDTO.getUri());

        Mockito.when(failureModesRowDao.find(eq(updateDTO.getUri()))).thenReturn(Optional.of(row));
        Mockito.when(failureModesRowDao.exists(eq(updateDTO.getUri()))).thenReturn(true);
        Mockito.when(validator.supports(any())).thenReturn(true);
        Mockito.when(failureModesRowDao.update(eq(row))).thenReturn(row);

        repositoryService.updateByDTO(updateDTO);

        Mockito.verify(failureModesRowDao).update(row);
        Assertions.assertEquals(updateDTO.getSeverity(), row.getRiskPriorityNumber().getSeverity());
        Assertions.assertEquals(updateDTO.getOccurrence(), row.getRiskPriorityNumber().getOccurrence());
        Assertions.assertEquals(updateDTO.getDetection(), row.getRiskPriorityNumber().getDetection());
    }

}