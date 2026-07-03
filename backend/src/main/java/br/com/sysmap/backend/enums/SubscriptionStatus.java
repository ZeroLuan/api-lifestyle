package br.com.sysmap.backend.enums;

import lombok.Getter;

@Getter
public enum SubscriptionStatus {
    PENDING("Pendente"),
    APPROVED("Aprovado"),
    REJECTED("Rejeitado"),
    CANCELED("Cancelado");

    private final String description;

    SubscriptionStatus(String description) {
        this.description = description;
    }
}
