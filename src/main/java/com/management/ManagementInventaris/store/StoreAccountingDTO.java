package com.management.ManagementInventaris.store;

import com.management.ManagementInventaris.utils.CurrencyFormatter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoreAccountingDTO {

    private String id;

    private String storeName;

    private String incomeDate;

    private String dailyIncome;

    private String savings;

    private String newStockBudget;

    private String savingPercentage;

    public static StoreAccountingDTO fromEntity(StoreAccounting storeAccounting) {
        String dailyIncomes = CurrencyFormatter.formatIDR(BigDecimal.valueOf(storeAccounting.getDailyIncome()));
        String savings = CurrencyFormatter.formatIDR(BigDecimal.valueOf(storeAccounting.calculateSavings()));
        String newStockBudget = CurrencyFormatter.formatIDR(BigDecimal.valueOf(storeAccounting.calculateNewStockBudget()));
        String formatSavingPercentage = String.format("%.0f%%", storeAccounting.getSavingPercentage() * 100);

        return StoreAccountingDTO.builder()
                .id(storeAccounting.getId())
                .storeName(storeAccounting.getStore().getStoreName())
                .incomeDate(storeAccounting.getIncomeDate().toString())
                .dailyIncome(dailyIncomes)
                .savings(savings)
                .newStockBudget(newStockBudget)
                .savingPercentage(formatSavingPercentage)
                .build();
    }
}