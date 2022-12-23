package pl.lodz.budgetmanager.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Receipt {
    private String shopName;
    private List<String> tags;
    private final List<Purchase> purchases;
    //dodac datę zakupu
    private final LocalDate purchaseDate;
    private final LocalDate addedDate;

    public Receipt(String shopName, List<String> tags, List<Purchase> purchases, LocalDate purchaseDate) {
        if (shopName.length() == 0) {
            throw new IllegalArgumentException("Invalid shop name!");
        }
        this.shopName = shopName;
        this.tags = tags;
        this.purchases = purchases;
        this.purchaseDate = purchaseDate;
        this.addedDate = LocalDate.now();
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

    public List<String> getTags() {
        return new ArrayList<>(tags);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Receipt receipt = (Receipt) o;
        return Objects.equals(shopName, receipt.shopName)
                && Objects.equals(tags, receipt.tags) && Objects.equals(purchases, receipt.purchases)
                && Objects.equals(purchaseDate, receipt.purchaseDate)
                && Objects.equals(addedDate, receipt.addedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shopName, tags, purchases, purchaseDate, addedDate);
    }

    @Override
    public String toString() {
        return getShopName() + ", " + getPurchaseDate();
    }

    public String getInfo() {
        return "Shop name: " + getShopName() + "\n" + "Tags: " + getTags().toString().replaceAll("[\\[\\]]", "")
                + "\n" + "Purchases:\n    " +
                getPurchases().toString().replaceAll("[\\[\\]]", "").replace(", ", "\n    ")
                + "\n" + "Purchase date: " + getPurchaseDate() + "\n";
    }
}
