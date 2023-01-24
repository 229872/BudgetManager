package pl.lodz.budgetmanager.repository;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

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

    public static Receipt mapToReceipt(Map<String, Object> map) {
        String shopName = (String) map.get("shopName");
        LocalDate purchaseDate = LocalDate.parse((String)map.get("purchaseDate"));
        List<Purchase> purchases = new ArrayList<>();
        Category category;
        try {
            category = Category.valueOf((String) map.get("category"));
        } catch (IllegalArgumentException ignored) {
            category = Category.OTHER;
        }

        return new Receipt(shopName, purchases, purchaseDate, category);
    }

    public boolean add(Receipt r){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(collectionName)
                .add(receiptToMap(r))
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

        return receipts.add(r);
    }

    public boolean remove(Receipt r) {
        return receipts.remove(r);
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
        receipts.clear();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(collectionName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            receipts.add(mapToReceipt(document.getData()));
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
