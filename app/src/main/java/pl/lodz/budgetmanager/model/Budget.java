package pl.lodz.budgetmanager.model;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;

import pl.lodz.budgetmanager.exceptions.WrongValueException;
import pl.lodz.budgetmanager.repository.ReceiptRepository;

public class Budget implements Serializable {
    private double monthlyBudget;
    private double limit;
    private double warmingLimit;
    private final ReceiptRepository receiptRep;
    private Month currentMonth;

    private final String deviceId;

    private static Budget instance;

    synchronized public static Budget getInstance(ReceiptRepository receiptRep, String deviceId) {
        if (instance == null) {
            instance = new Budget(receiptRep, deviceId);
        }
        return instance;
    }

    private Budget(ReceiptRepository receiptRep, String deviceId) {
        this.monthlyBudget = 0.0;
        this.limit = Double.MAX_VALUE;
        this.warmingLimit = Double.MAX_VALUE;
        this.receiptRep = receiptRep;
        this.deviceId = deviceId;

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("budgets")
                .document(deviceId)
                .get()
                .addOnCompleteListener(docRef -> {
                    DocumentSnapshot docSnapshot = docRef.getResult();
                    if (docSnapshot.exists()) {
                        this.monthlyBudget = (Double) docSnapshot.get("monthlyBudget");
                        this.limit = (Double) docSnapshot.get("limit");
                        this.warmingLimit = (Double) docSnapshot.get("warningLimit");
                    } else {
                        db.collection("budgets")
                                .document(deviceId)
                                .set(new HashMap<String, Object>() {{
                                    put("monthlyBudget", monthlyBudget);
                                    put("limit", limit);
                                    put("warningLimit", warmingLimit);
                                }});
                    }
                });

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
        FirebaseFirestore.getInstance()
                .collection("budgets")
                .document(deviceId)
                .update("monthlyBudget", monthlyBudget);
    }

    public void setLimit(double limit) throws WrongValueException {
        if (limit <= monthlyBudget) {
            this.limit = limit;
            FirebaseFirestore.getInstance()
                    .collection("budgets")
                    .document(deviceId)
                    .update("limit", limit);
        } else {
            throw new WrongValueException("Limit cannot be higher than budget");
        }

    }

    public void setWarmingLimit(double warmingLimit) throws WrongValueException {
        if (warmingLimit <= limit) {
            this.warmingLimit = warmingLimit;
            FirebaseFirestore.getInstance()
                    .collection("budgets")
                    .document(deviceId)
                    .update("warningLimit", warmingLimit);
        } else {
            throw new WrongValueException("Warming limit cannot be higher than limit");
        }
    }

    public void updateMonth() {
        currentMonth = LocalDate.now().getMonth();
    }


}
