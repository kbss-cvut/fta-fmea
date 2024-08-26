package cz.cvut.kbss.analysis.dto.error;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorInfo {

    private String message;

    private String messageId;

    private String requestUri;

}
