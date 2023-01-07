package pl.lodz.budgetmanager.repository;

import java.io.Serializable;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pl.lodz.budgetmanager.model.Receipt;

public class ReceiptRepository implements Serializable {
    private List<Receipt> receipts = new ArrayList<>();
    private static ReceiptRepository instance;

    private ReceiptRepository() {}

    synchronized public static ReceiptRepository getInstance() {
        if (instance == null) {
            instance = new ReceiptRepository();
        }
        return instance;
    }

    public List<Receipt> getRepository() {
        return receipts;
    }

    public boolean add(Receipt r){
        return receipts.add(r);
    }

    public boolean remove(Receipt r) {
        return receipts.remove(r);
    }

    public Receipt get(int index) {
        return receipts.get(index);
    }

    public List<Receipt> getAll() {
        return Collections.unmodifiableList(receipts);
    }

    public Receipt find(String shopName) {
        for (Receipt r : receipts) {
            if (r.getShopName().equals(shopName)) {
                return r;
            }
        }
        return null;
    }

    public Receipt find(Month month) {
        for (Receipt r : receipts) {
            if (r.getPurchaseDate().getMonth().equals(month)) {
                return r;
            }
        }
        return null;
    }

    public List<Receipt> findAll(String shopName) {
        List<Receipt> found = new ArrayList<>();
        for (Receipt r : receipts) {
            if ( r.getShopName().equals(shopName)) {
                found.add(r);
            }
        }
        return found;
    }

    public List<Receipt> findAll(Month month) {
        List<Receipt> found = new ArrayList<>();
        for (Receipt r : receipts) {
            if ( r.getPurchaseDate().getMonth().equals(month)) {
                found.add(r);
            }
        }
        return found;
    }

    public List<Receipt> findAll() {
        return new ArrayList<>(receipts);
    }

    public double getTotalSpendings() {
        double spendings = 0;
        for (Receipt r : receipts) {
            spendings += r.getTotalPrice();
        }
        return spendings;
    }

    public double getSpendingsByMonth(Month month) {
        List<Receipt> found = findAll(month);
        double spendings = 0;
        for (Receipt r : found) {
            spendings += r.getTotalPrice();
        }
        return spendings;
    }
}
