package pl.lodz.budgetmanager.model;

import java.time.LocalDate;
import java.time.Month;

import pl.lodz.budgetmanager.repository.ReceiptRepository;

public class Budget implements Observer {
    private double currentSpendings;
    private double monthlyBudget;
    private double remainingSpendings;
    private final ReceiptRepository receiptRep;
    private Month currentMonth;

    public Budget(ReceiptRepository receiptRep) {
        this.monthlyBudget = 0.0;
        this.receiptRep = receiptRep;
        this.remainingSpendings = monthlyBudget;
        this.currentSpendings = 0.0;
        receiptRep.attach(this);
        updateMonth();
    }

    public double getCurrentSpendings() {
        return currentSpendings;
    }

    public double getMonthlyBudget() {
        return monthlyBudget;
    }

    public double getRemainingSpendings() {
        return remainingSpendings;
    }

    public void setMonthlyBudget(double monthlyBudget) {
        this.monthlyBudget = monthlyBudget;
    }

    public void updateMonth() {
        currentMonth = LocalDate.now().getMonth();
    }

    @Override
    public void update() {
        if (receiptRep.findAll(currentMonth) != null) {
            double current = 0;
            for (Receipt receipt : receiptRep.findAll(currentMonth)) {
                current += receipt.getTotalPrice();
            }
            currentSpendings = current;
        } else {
            currentSpendings = 0;
        }
        remainingSpendings = getMonthlyBudget() - getCurrentSpendings();
    }
}
