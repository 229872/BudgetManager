package pl.lodz.budgetmanager.model;

import java.time.LocalDate;
import java.time.Month;

import pl.lodz.budgetmanager.repository.ReceiptRepository;

public class Budget {
    private double monthlyBudget;
    private final ReceiptRepository receiptRep;
    private Month currentMonth;

    public Budget(ReceiptRepository receiptRep) {
        this.monthlyBudget = 0.0;
        this.receiptRep = receiptRep;
        updateMonth();
    }

    public double getCurrentSpendings() {
        return this.receiptRep.getTotalSpendings();
    }

    public double getMonthlyBudget() {
        return monthlyBudget;
    }

    public double getRemainingSpendings() {
        return this.monthlyBudget - getCurrentSpendings();
    }

    public void setMonthlyBudget(double monthlyBudget) {
        this.monthlyBudget = monthlyBudget;
    }

    public void updateMonth() {
        currentMonth = LocalDate.now().getMonth();
    }

    public Month getCurrentMonth() {
        return currentMonth;
    }
}
