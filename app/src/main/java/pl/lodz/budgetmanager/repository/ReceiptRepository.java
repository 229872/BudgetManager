package pl.lodz.budgetmanager.repository;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.lodz.budgetmanager.model.Category;
import pl.lodz.budgetmanager.model.Purchase;
import pl.lodz.budgetmanager.model.Receipt;

public class ReceiptRepository implements Serializable {
    private List<Receipt> receipts = new ArrayList<>();
    private static ReceiptRepository instance;
    private final String collectionName = "receipts";

    private ReceiptRepository() {
    }

    synchronized public static ReceiptRepository getInstance() {
        if (instance == null) {
            instance = new ReceiptRepository();
        }
        return instance;
    }

    public static Map<String, Object> receiptToMap(Receipt r, String deviceId) {
        Map<String, Object> receipt = new HashMap<>();
        receipt.put("shopName", r.getShopName());
        receipt.put("addedDate", r.getAddedDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        receipt.put("purchaseDate", r.getPurchaseDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        receipt.put("category", r.getCategory());
        receipt.put("purchases", r.getPurchases());
        receipt.put("totalPrice", r.getTotalPrice());
        receipt.put("deviceId", deviceId);

        List<Map<String, Object>> purchases = new ArrayList<>();
        r.getPurchases().forEach(p -> {
            Map<String, Object> purchaseMap = new HashMap<>();
            purchaseMap.put("name", p.getName());
            purchaseMap.put("price", p.getPrice());
            purchaseMap.put("quantity", p.getQuantity());

            purchases.add(purchaseMap);
        });

        receipt.put("purchases", purchases);


        return receipt;
    }

    public static Receipt mapToReceipt(String id, Map<String, Object> map) {
        String shopName = (String) map.get("shopName");
        LocalDate purchaseDate = LocalDate.parse((String) map.get("purchaseDate"));
        List<Purchase> purchases = new ArrayList<>();
        Category category;
        try {
            category = Category.valueOf((String) map.get("category"));
        } catch (IllegalArgumentException ignored) {
            category = Category.OTHER;
        }

        ((ArrayList) map.get("purchases")).forEach(p -> {
            purchases.add(mapToPurchase((HashMap<String, Object>) p));
        });

        return new Receipt(id, shopName, purchases, purchaseDate, category);
    }

    private static Purchase mapToPurchase(Map<String, Object> map) {
        return new Purchase(
                (String) map.get("name"),
                (Double) map.get("price"),
                Math.toIntExact((Long) (map.get("quantity"))));
    }

    public void add(Receipt r, String deviceId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(collectionName)
                .add(receiptToMap(r, deviceId))
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    documentReference
                            .get()
                            .addOnCompleteListener(task -> {
                                Receipt receipt = mapToReceipt(
                                        documentReference.getId(), task.getResult().getData()); // hope this does not crash anytime soon
                                receipts.add(receipt);
                            });
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

        receipts.add(r);
    }

    public void remove(Receipt r) {
        receipts.remove(r);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("receipts")
                .document(r.getId())
                .delete();
    }

    public List<Receipt> findAll(Month month) {
        List<Receipt> found = new ArrayList<>();
        for (Receipt r : receipts) {
            if (r.getPurchaseDate().getMonth().equals(month)) {
                found.add(r);
            }
        }
        return found;
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
