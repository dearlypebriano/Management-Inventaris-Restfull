package com.management.ManagementInventaris.store;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "store_accounting", indexes = {
        @Index(name = "idx_store_income_date", columnList = "income_date")
})
public class StoreAccounting implements Serializable {

    @Id
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private String id;

    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "income_date", nullable = false)
    private String incomeDate;

    @Column(name = "daily_income", nullable = false)
    private double dailyIncome;

    @Column(name = "saving_percentage", nullable = false)
    private double savingPercentage = 0.1; // Default 10%

    public double calculateSavings() {
        return dailyIncome * savingPercentage;
    }

    public double calculateNewStockBudget() {
        return dailyIncome - calculateSavings();
    }
}