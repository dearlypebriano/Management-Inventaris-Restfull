package com.management.ManagementInventaris.store;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StoreAccountingResponse implements Serializable {

    private String id;

    private String storeName;

    private String incomeDate;

    private String dailyIncome;

    private String savings;

    private String newStockBudget;

    private String savingPercentage;
}