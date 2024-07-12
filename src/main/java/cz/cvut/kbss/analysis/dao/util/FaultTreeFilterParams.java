package cz.cvut.kbss.analysis.dao.util;

import cz.cvut.kbss.analysis.model.FaultTree;
import cz.cvut.kbss.analysis.service.util.Pair;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Encapsulates {@link cz.cvut.kbss.analysis.dao.util.FaultTreeFilterParams} filtering criteria.
 */
public class FaultTreeFilterParams {

    /**
     * Value to filter fault tree according to sns system
     */
    private String snsLabel;

    private String label;

    public FaultTreeFilterParams() {
    }

    public String getSnsLabel() {
        return snsLabel;
    }

    public void setSnsLabel(String snsLabel) {
        this.snsLabel = snsLabel;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean matches(FaultTree ft) {
        return Stream.of(
                Pair.of(ft.getName(), label),
                Pair.of(Optional.ofNullable(ft.getSubsystem()).map(i -> i.getName()).orElse(null), snsLabel)
                )
                .allMatch(p -> this.matches(p.getFirst(), p.getSecond()));
    }

    protected boolean matches(String val, String pattern){
        return pattern == null || pattern.isBlank() || (val != null && val.contains(pattern.trim()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FaultTreeFilterParams that)) return false;
        return Objects.equals(snsLabel, that.snsLabel) && Objects.equals(label, that.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(snsLabel, label);
    }

    @Override
    public String toString() {
        return "FaultTreeFilterParams{" +
                "snsFilter='" + snsLabel + '\'' +
                ", nameFilter='" + label + '\'' +
                '}';
    }
}
