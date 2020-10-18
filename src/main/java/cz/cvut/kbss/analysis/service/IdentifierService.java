package cz.cvut.kbss.analysis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
@Slf4j
public class IdentifierService {

    public URI composeIdentifier(String prefix, String fragment) {
        return URI.create(prefix + "/" + fragment);
    }

}
