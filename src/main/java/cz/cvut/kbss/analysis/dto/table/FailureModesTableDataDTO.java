package cz.cvut.kbss.analysis.dto.table;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class FailureModesTableDataDTO {

    public FailureModesTableDataDTO(String name) {
        this.name = name;
    }

    private String name;
    private List<FailureModesTableField> columns = new ArrayList<>();
    private List<Map<String, Object>> rows = new ArrayList<>();

}
