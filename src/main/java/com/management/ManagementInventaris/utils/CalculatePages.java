package com.management.ManagementInventaris.utils;

/**
 * Utility class for calculating the total number of pages based on total count and page size.
 */
public final class CalculatePages {
    private final long totalCount;
    private final int pageSize;

    /**
     * Constructs a PageCalculator with the given total count and page size.
     *
     * @param totalCount The total number of items.
     * @param pageSize   The number of items per page.
     * @throws IllegalArgumentException if pageSize is less than or equal to 0.
     */
    public CalculatePages(long totalCount, int pageSize) {
        if (pageSize <= 0) {
            throw new IllegalArgumentException("Page size must be greater than zero (0)");
        }
        this.totalCount = totalCount;
        this.pageSize = pageSize;
    }

    /**
     * Calculates the total number of pages.
     *
     * @return The total number of pages.
     */
    public int calculateTotalPages() {
        return (int) Math.ceil((double) totalCount / pageSize);
    }
}