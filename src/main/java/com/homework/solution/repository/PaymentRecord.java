package com.homework.solution.repository;

import java.time.LocalDateTime;

public class PaymentRecord implements Comparable<PaymentRecord> {

    private String nameOfTransaction;

    private String partner;

    private Integer position;

    private Integer maxPosition;

    private LocalDateTime dateTime;

    public String getNameOfTransaction() {
        return nameOfTransaction;
    }

    public void setNameOfTransaction(final String nameOfTransaction) {
        this.nameOfTransaction = nameOfTransaction;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(final String partner) {
        this.partner = partner;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(final Integer position) {
        this.position = position;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(final LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Integer getMaxPosition() {
        return maxPosition;
    }

    public void setMaxPosition(final Integer maxPosition) {
        this.maxPosition = maxPosition;
    }

    @Override
    public int compareTo(PaymentRecord o) {
        return getDateTime().compareTo(o.getDateTime());
    }
}
