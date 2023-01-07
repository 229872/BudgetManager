package pl.lodz.budgetmanager.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Month;

import pl.lodz.budgetmanager.exceptions.WrongValueException;
import pl.lodz.budgetmanager.repository.ReceiptRepository;

public class Budget implements Serializable {
    private double monthlyBudget;
    private double limit;
    private double warmingLimit;
    private final ReceiptRepository receiptRep;
    private Month currentMonth;

    private static Budget instance;

    synchronized public static Budget getInstance(ReceiptRepository receiptRep) {
        if (instance == null) {
            instance = new Budget(receiptRep);
        }
        return instance;
    }

    private Budget(ReceiptRepository receiptRep) {
        this.monthlyBudget = 0.0;
        this.limit = Double.MAX_VALUE;
        this.warmingLimit = Double.MAX_VALUE;
        this.receiptRep = receiptRep;
        updateMonth();
    }

    public double getCurrentSpendings() {
        return this.receiptRep.getSpendingsByMonth(LocalDate.now().getMonth());
    }

    public double getMonthlyBudget() {
        return monthlyBudget;
    }

    public double getRemainingSpendings() {
        return this.monthlyBudget - getCurrentSpendings();
    }

    public Month getCurrentMonth() {
        return currentMonth;
    }

    public double getLimit() {
        return limit;
    }

    public double getWarmingLimit() {
        return warmingLimit;
    }

    public void setMonthlyBudget(double monthlyBudget) {
        this.monthlyBudget = monthlyBudget;
    }

    public void setLimit(double limit) throws WrongValueException {
        if (limit <= monthlyBudget) {
            this.limit = limit;
        } else {
            throw new WrongValueException("Limit cannot be higher than budget");
        }

    }

    public void setWarmingLimit(double warmingLimit) throws WrongValueException {
        if (warmingLimit <= limit) {
            this.warmingLimit =  warmingLimit;
        } else {
            throw new WrongValueException("Warming limit cannot be higher than limit");
        }
    }

    public void updateMonth() {
        currentMonth = LocalDate.now().getMonth();
    }


}
