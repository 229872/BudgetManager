package pl.lodz.budgetmanager.model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

public class Purchase implements Serializable {
    private final String name;
    private final double price;
    private final int quantity;

    public Purchase(String name, double price, int quantity) {
        if (price <= 0) {
            throw new NumberFormatException("Invalid price!");
        }
        if (quantity <= 0) {
            throw new NumberFormatException("Invalid quantity!");
        }
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalPurchaseValue() {
        return getPrice() * getQuantity();
    }

    @NonNull
    @Override
    public String toString() {
        return getName() + " " + getPrice() + " x " + getQuantity();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Purchase purchase = (Purchase) o;
        return Double.compare(purchase.price, price) == 0 && quantity == purchase.quantity && Objects.equals(name, purchase.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price, quantity);
    }
}
