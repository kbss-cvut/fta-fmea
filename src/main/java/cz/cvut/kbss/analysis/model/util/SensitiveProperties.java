package cz.cvut.kbss.analysis.model.util;

import java.util.HashSet;
import java.util.Set;

public class SensitiveProperties {
    protected Set<String> partNumberSet = new HashSet<String>();
    protected Set<String> stockSet = new HashSet<>();
    protected Set<String> failureRateSet  = new HashSet<>();
    protected Set<String> faultEventNamesSet = new HashSet<>();

    public SensitiveProperties add(String partNumber, String stock, String failureRate, String faultEventName) {
        addPartNumber(partNumber);
        addStock(stock);
        addFailureRate(failureRate);
        addFaultEventName(faultEventName);
        return this;

    }

    public SensitiveProperties addPartNumber(String partNumber) {
        if (partNumber != null)
            partNumberSet.add(partNumber);
        return this;
    }


    public SensitiveProperties addStock(String stock) {
        if(stock != null)
            stockSet.add(stock);
        return this;
    }

    public SensitiveProperties addFailureRate(String failureRate) {
        if(failureRate != null)
            failureRateSet.add(failureRate);
        return this;
    }
    public SensitiveProperties addFaultEventName(String faultEventName) {
        if(faultEventName != null)
            faultEventNamesSet.add(faultEventName);
        return this;
    }


    public Set<String> getPartNumberSet() {
        return partNumberSet;
    }

    public Set<String> getStockSet() {
        return stockSet;
    }

    public Set<String> getFailureRateSet() {
        return failureRateSet;
    }

    public Set<String> getFaultEventNamesSet() {
        return faultEventNamesSet;
    }
}
