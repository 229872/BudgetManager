package pl.lodz.budgetmanager.repository;

import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pl.lodz.budgetmanager.model.Observer;
import pl.lodz.budgetmanager.model.Receipt;

public class ReceiptRepository {
    private List<Receipt> receipts = new ArrayList<>();
    private final List<Observer> observers = new ArrayList<>();

    public List<Receipt> getRepository() {
        return receipts;
    }

    public boolean add(Receipt r){
        receipts.add(r);
        notifyObservers();
        return true;
    }

    public boolean remove(Receipt r) {
        receipts.remove(r);
        notifyObservers();
        return true;
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
        if (found.size() == 0) {
            return null;
        } else {
            return found;
        }
    }

    public List<Receipt> findAll(Month month) {
        List<Receipt> found = new ArrayList<>();
        for (Receipt r : receipts) {
            if ( r.getPurchaseDate().getMonth().equals(month)) {
                found.add(r);
            }
        }
        if (found.size() == 0) {
            return null;
        } else {
            return found;
        }
    }

    public double getTotalSpendings() {
        double spendings = 0;
        for (Receipt r : receipts) {
            spendings += r.getTotalPrice();
        }
        return spendings;
    }

    public void attach(Observer o) {
        observers.add(o);
    }

    public void detach(Observer o) {
        observers.remove(o);
    }

    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update();
        }
    }
}
