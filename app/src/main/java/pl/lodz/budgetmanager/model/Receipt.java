package pl.lodz.budgetmanager.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Receipt implements Serializable {
    private final String shopName;
    private final List<Purchase> purchases;
    //dodac datę zakupu
    private final LocalDate purchaseDate;
    private final LocalDate addedDate;
    private final Category category;

    public Receipt(String shopName, List<Purchase> purchases, LocalDate purchaseDate, Category category) {
        if (shopName.length() == 0) {
            throw new IllegalArgumentException("Invalid shop name!");
        }
        this.shopName = shopName;
        this.purchases = purchases;
        this.purchaseDate = purchaseDate;
        this.addedDate = LocalDate.now();
        this.category = category;
    }

    public double getTotalPrice() {
        double totalPrice = 0;
        for (Purchase p: getPurchases()) {
            totalPrice += p.getTotalPurchaseValue();
        }
        return totalPrice;
    }

    public String getShopName() {
        return shopName;
    }


    public List<Purchase> getPurchases() {
        return purchases;
    }


    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public LocalDate getAddedDate() {
        return addedDate;
    }

    public Category getCategory() {
        return category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Receipt receipt = (Receipt) o;
        return Objects.equals(shopName, receipt.shopName)
                && Objects.equals(purchases, receipt.purchases)
                && Objects.equals(purchaseDate, receipt.purchaseDate)
                && Objects.equals(addedDate, receipt.addedDate)
                && Objects.equals(category, receipt.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shopName, purchases, purchaseDate, addedDate, category);
    }

    @Override
    public String toString() {
        return getShopName() + ", " + getPurchaseDate() + ", " + getTotalPrice() + ", " + getCategory();
    }

    public String getInfo() {
        return "Shop name: " + getShopName() + "\n"
                + "\n" + "Purchases:\n    " +
                getPurchases().toString().replaceAll("[\\[\\]]", "").replace(", ", "\n    ")
                + "\n" + "Purchase date: " + getPurchaseDate() + "\n";
    }
}
