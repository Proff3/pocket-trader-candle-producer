package ru.pronin.candlekafkaproducer.broker.tinkoff.utils;

import ru.tinkoff.piapi.contract.v1.MoneyValue;
import ru.tinkoff.piapi.contract.v1.Quotation;

import java.math.BigDecimal;

public class TinkoffUtils {

    public static double fromMoneyValueToDouble(MoneyValue moneyValue) {
        BigDecimal value = new BigDecimal(moneyValue.getUnits() + "." + moneyValue.getNano());
        return value.doubleValue();
    }

    public static MoneyValue fromDoubleToMoneyValue(double value, String currency) {
        BigDecimal valueToParse = new BigDecimal(String.valueOf(value));
        int nano = valueToParse.remainder(BigDecimal.ONE).movePointRight(valueToParse.scale()).abs().intValue();
        BigDecimal nanoToSet = new BigDecimal("0." + nano);
        return MoneyValue.newBuilder()
                .setUnits(valueToParse.subtract(nanoToSet).intValue())
                .setNano(nanoToSet.movePointRight(9).intValue())
                .setCurrency(currency)
                .build();
    }

    public static Quotation fromDoubleToQuotation(double value) {
        BigDecimal valueToParse = new BigDecimal(String.valueOf(value));
        int nano = valueToParse.remainder(BigDecimal.ONE).movePointRight(valueToParse.scale()).abs().intValue();
        BigDecimal nanoToSet = new BigDecimal("0." + nano);
        return Quotation.newBuilder()
                .setUnits(valueToParse.subtract(nanoToSet).intValue())
                .setNano(nanoToSet.movePointRight(9).intValue())
                .build();
    }
}
