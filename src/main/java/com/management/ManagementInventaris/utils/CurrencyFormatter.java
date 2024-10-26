package com.management.ManagementInventaris.utils;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

/**
 * Utility class for formatting currency values
 * This class provides methods to format amounts in various currencies
 * using the proper currency symbols and formatting conventions.
 */
public final class CurrencyFormatter {

    /**
     * Formats a given BigDecimal amount into a currency format based on the provided locale.
     * <p>
     * This method uses the specified locale to apply the correct currency symbols
     * and number formatting conventions for that locale. The formatted currency will
     * include the appropriate currency symbol, thousands separators, and decimal places.
     * </p>
     *
     * Example:
     * <pre>
     * BigDecimal amount = new BigDecimal("1000.50");
     * String formattedAmount = CurrencyFormatter.formatCurrency(amount, Locale.US);
     * System.out.println(formattedAmount); // Outputs: "$1,000.50"
     * </pre>
     *
     * @param amount the amount to be formatted, represented as a BigDecimal.
     *               The amount should not be null, and it should represent a positive or negative monetary value.
     * @param locale the locale to use for formatting, representing the country's currency format (e.g., Locale.US, Locale.UK).
     * @return a formatted string representing the amount in the currency format for the specified locale.
     * @throws IllegalArgumentException if the amount or locale is null.
     */
    public static String formatCurrency(BigDecimal amount, Locale locale) {
        if (amount == null) throw new IllegalArgumentException("Amount cannot be null");
        if (locale == null) throw new IllegalArgumentException("Locale cannot be null");

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);
        Currency currency = Currency.getInstance(locale);
        symbols.setCurrencySymbol(currency.getSymbol(locale));

        DecimalFormat formatter = (DecimalFormat) NumberFormat.getCurrencyInstance(locale);
        formatter.setDecimalFormatSymbols(symbols);
        return formatter.format(amount);
    }

    /**
     * Formats a given BigDecimal amount into Indonesian Rupiah (IDR) currency format.
     * <p>
     * The method uses the Indonesian locale (Locale("id", "ID")) to apply the
     * correct currency symbols and number formatting conventions. The formatted
     * currency will include the "Rp. " symbol, and the number will be formatted
     * with thousands separators and two decimal places, following the IDR format.
     * </p>
     *
     * Example:
     * <pre>
     * BigDecimal amount = new BigDecimal("1000000.50");
     * String formattedAmount = CurrencyFormatter.formatIDR(amount);
     * System.out.println(formattedAmount); // Outputs: "Rp. 1.000.000,50"
     * </pre>
     *
     * @param amount the amount to be formatted, represented as a BigDecimal.
     *               The amount should not be null, and it should represent a positive or negative monetary value.
     * @return a formatted string representing the amount in Indonesian Rupiah (IDR), including the "Rp. " currency symbol.
     * @throws IllegalArgumentException if the amount is null.
     */
    public static String formatIDR(@NotNull BigDecimal amount) {
        return formatCurrency(amount, new Locale("id", "ID"));
    }

    /**
     * Formats a given BigDecimal amount into a currency format based on the provided currency code and locale.
     * <p>
     * This method allows for flexible formatting by specifying a currency code (e.g., "USD", "EUR", "JPY")
     * and a locale to apply the correct currency symbols and formatting conventions. The formatted
     * currency will include the appropriate currency symbol, thousands separators, and decimal places.
     * </p>
     *
     * Example:
     * <pre>
     * BigDecimal amount = new BigDecimal("1000.50");
     * String formattedAmount = CurrencyFormatter.formatCurrencyByCode(amount, "USD", Locale.US);
     * System.out.println(formattedAmount); // Outputs: "$1,000.50"
     * </pre>
     *
     * @param amount       the amount to be formatted, represented as a BigDecimal.
     *                     The amount should not be null, and it should represent a positive or negative monetary value.
     * @param currencyCode the ISO 4217 currency code (e.g., "USD" for US Dollar, "EUR" for Euro, etc.).
     * @param locale       the locale to use for formatting, representing the country's currency format (e.g., Locale.US, Locale.UK).
     * @return a formatted string representing the amount in the specified currency format.
     * @throws IllegalArgumentException if the amount, currencyCode, or locale is null.
     */
    public static String formatCurrencyByCode(BigDecimal amount, String currencyCode, Locale locale) {
        if (amount == null) throw new IllegalArgumentException("Amount cannot be null");
        if (currencyCode == null || currencyCode.isEmpty()) throw new IllegalArgumentException("Currency code cannot be null or empty");
        if (locale == null) throw new IllegalArgumentException("Locale cannot be null");

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);
        Currency currency = Currency.getInstance(currencyCode);
        symbols.setCurrencySymbol(currency.getSymbol(locale));

        DecimalFormat formatter = (DecimalFormat) NumberFormat.getCurrencyInstance(locale);
        formatter.setDecimalFormatSymbols(symbols);
        return formatter.format(amount);
    }
}