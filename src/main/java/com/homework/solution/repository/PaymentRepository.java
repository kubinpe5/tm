package com.homework.solution.repository;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PaymentRepository {
    private List<PaymentRecord> paymentRecords;
    private Map<String, Integer> maxForPartners;

    public PaymentRepository() {
        paymentRecords = new ArrayList<>();
        maxForPartners = new HashMap<>();
    }

    public PaymentRepository(final List<PaymentRecord> paymentRecords, final Map<String, Integer> maxForPartners) {
        this.paymentRecords = paymentRecords;
        this.maxForPartners = maxForPartners;
    }

    public List<PaymentRecord> getOrderedElements() {
        return paymentRecords;
    }

    public void setOrderedElements(final List<PaymentRecord> paymentRecords) {
        this.paymentRecords = paymentRecords;
    }

    public Map<String, Integer> getMaxForPartners() {
        return maxForPartners;
    }

    public void setMaxForPartners(final Map<String, Integer> maxForPartners) {
        this.maxForPartners = maxForPartners;
    }

    static List<PaymentRecord> sortList(List<PaymentRecord> originalList) {
        final List<PaymentRecord> sorted = new ArrayList<>(originalList);
        Collections.sort(sorted, (a,b) -> {
            return a.getDateTime().compareTo(b.getDateTime());
        });
        return sorted;
    }

    public void addRecord(final PaymentRecord paymentRecord) {
        paymentRecords.add(paymentRecord);
    }

    /**
     * Sorts partners by date and add the order number as position
     */
    public void addPositionForPartner() {

        final Map<String, List<PaymentRecord>> paymentsByPartner = paymentRecords.stream().collect(Collectors.groupingBy(PaymentRecord::getPartner));

        final Comparator<? super PaymentRecord> byDateComparator = (a, b) -> {
            return a.getDateTime().compareTo(b.getDateTime());
        };

        Map<String, List<PaymentRecord>> timeSortedByPartner = paymentsByPartner.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> sortList(entry.getValue())
                ));

        for (Map.Entry<String, List<PaymentRecord>> entry : timeSortedByPartner.entrySet()) {
            int i = 0;
            for (PaymentRecord record : entry.getValue()) {
                record.setPosition(++i);
            }
            maxForPartners.put(entry.getKey(), i);
        }
    }
}
