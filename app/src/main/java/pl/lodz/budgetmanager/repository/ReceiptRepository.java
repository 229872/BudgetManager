package pl.lodz.budgetmanager.repository;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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

    public static Map<String, Object> receiptToMap(Receipt r) {
        Map<String, Object> receipt = new HashMap<>();
        receipt.put("shopName", r.getShopName());
        receipt.put("addedDate", r.getAddedDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        receipt.put("purchaseDate", r.getPurchaseDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        receipt.put("category", r.getCategory());
        receipt.put("purchases", r.getPurchases());
        receipt.put("totalPrice", r.getTotalPrice());
//        try {
//            receipt.put("userId", AdvertisingIdClient.getAdvertisingIdInfo(context).getId());
//        } catch (IOException | GooglePlayServicesNotAvailableException |
//                 GooglePlayServicesRepairableException e) {
//            throw new RuntimeException(e);
//        }
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

        return new Receipt(id, shopName, purchases, purchaseDate, category);
    }

    public void add(Receipt r) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(collectionName)
                .add(receiptToMap(r))
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

    public Receipt get(int index) {
        return receipts.get(index);
    }

    public Receipt find(String shopName) {
        for (Receipt r : receipts) {
            if (r.getShopName().equals(shopName)) {
                return r;
            }
        }
        return null;
    }

    public Receipt find(Category category) {
        for (Receipt r : receipts) {
            if (r.getCategory().equals(category)) {
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
            if (r.getShopName().equals(shopName)) {
                found.add(r);
            }
        }
        return found;
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

    public List<Receipt> findAll() {
        receipts.clear();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(collectionName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            receipts.add(mapToReceipt(document.getId(), document.getData()));
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
        return receipts;
    }

    public List<Receipt> findAll(Category category) {
        List<Receipt> found = new ArrayList<>();
        for (Receipt r : receipts) {
            if (r.getCategory().equals(category)) {
                found.add(r);
            }
        }
        return found;
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
