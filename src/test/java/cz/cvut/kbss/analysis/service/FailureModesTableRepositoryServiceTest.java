package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.FailureModesTableDao;
import cz.cvut.kbss.analysis.dto.update.FailureModesTableUpdateDTO;
import cz.cvut.kbss.analysis.environment.Generator;
import cz.cvut.kbss.analysis.model.FailureModesTable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.Validator;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

class FailureModesTableRepositoryServiceTest {

    @Mock
    FailureModesTableDao failureModesTableDao;
    @Mock
    Validator validator;
    @InjectMocks
    FailureModesTableRepositoryService repositoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void updateByDTO_shouldFind_shouldCopyData_shouldCallUpdate() {
        FailureModesTableUpdateDTO updateDTO = new FailureModesTableUpdateDTO();
        updateDTO.setUri(Generator.generateUri());
        updateDTO.setName("UpdatedName");

        FailureModesTable table = new FailureModesTable();
        table.setUri(updateDTO.getUri());
        table.setName("oldName");

        Mockito.when(failureModesTableDao.find(eq(updateDTO.getUri()))).thenReturn(Optional.of(table));
        Mockito.when(failureModesTableDao.exists(eq(table.getUri()))).thenReturn(true);
        Mockito.when(validator.supports(any())).thenReturn(true);
        Mockito.when(failureModesTableDao.update(eq(table))).thenReturn(table);

        repositoryService.updateByDTO(updateDTO);

        Mockito.verify(failureModesTableDao).update(table);
        Assertions.assertEquals(updateDTO.getName(), table.getName());
    }

}