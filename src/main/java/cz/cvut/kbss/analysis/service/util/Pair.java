package cz.cvut.kbss.analysis.service.util;

import lombok.Value;

@Value(staticConstructor = "of")
public class Pair<F, S> {
    F first;
    S second;
}