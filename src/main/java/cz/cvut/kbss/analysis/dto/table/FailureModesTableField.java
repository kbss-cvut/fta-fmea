package cz.cvut.kbss.analysis.dto.table;

import lombok.Data;

@Data
public class FailureModesTableField {

    public FailureModesTableField(String field, String headerName) {
        this.field = field;
        this.headerName = headerName;
    }

    private String field;
    private String headerName;
    private int flex = 1;
    private Boolean sortable = false;

}
