package pl.lodz.budgetmanager;

import static android.content.ContentValues.TAG;
import static pl.lodz.budgetmanager.repository.ReceiptRepository.receiptToMap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import pl.lodz.budgetmanager.model.Category;
import pl.lodz.budgetmanager.model.Purchase;
import pl.lodz.budgetmanager.model.Receipt;

public class AddPurchaseActivity extends AppCompatActivity {
    private String shopName;
    private LocalDate purchaseDate;
    private Category category;
    private List<Purchase> purchaseList = new ArrayList<>();


    private TextView purchaseListOutput;
    private TextInputEditText productNameInput;
    private TextInputEditText priceInput;
    private TextInputEditText quantityInput;
    private TextView purchaseStatus;
    private Button doneButton;
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private Button addPurchaseButton;
    private TextView purchaseTitle;


    @SuppressLint("HardwareIds")
    @Override
    protected void onStart() {
        super.onStart();
        // Registers a photo picker activity launcher in single-select mode.
        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    // Callback is invoked after the user selects a media item or closes the
                    // photo picker.
                    if (uri != null) {
                        Log.d("PhotoPicker", "Selected URI: " + uri);
                        Intent intent = new Intent(this, MainActivity.class);
                        Receipt receipt = new Receipt(shopName, purchaseList, purchaseDate, category);
                        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageRef = storage.getReference();
                        byte[] fileBytes;
                        try {
                            fileBytes = getFileBytes(uri);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("receipts")
                                .add(receiptToMap(receipt, deviceId))
                                .addOnSuccessListener(documentReference -> {
                                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                    StorageReference riversRef = storageRef.child(documentReference.getId());
                                    riversRef.putBytes(fileBytes);

                                    startActivity(intent);

                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error adding document", e);
                                    }
                                });
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });
    }

    private byte[] getFileBytes(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }
        return baos.toByteArray();
    }
    private boolean isFontHelper = false;

    private void changeFontSize(int newFontSize) {
        purchaseTitle.setTextSize(newFontSize);
        productNameInput.setTextSize(newFontSize);
        priceInput.setTextSize(newFontSize);
        quantityInput.setTextSize(newFontSize);
        addPurchaseButton.setTextSize(newFontSize);
        doneButton.setTextSize(newFontSize);
        purchaseStatus.setTextSize(newFontSize);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_purchase);

        productNameInput = findViewById(R.id.productNameInput);
        priceInput = findViewById(R.id.priceInput);
        quantityInput = findViewById(R.id.quantityInput);
        purchaseStatus = findViewById(R.id.purchaseStatus);
        doneButton = findViewById(R.id.doneButton);
        purchaseListOutput = findViewById(R.id.purchaseListOutput);
        addPurchaseButton = findViewById(R.id.addPurchaseButton);
        purchaseTitle = findViewById(R.id.purchaseTitle);


        Intent intent = getIntent();

        shopName = intent.getStringExtra("ShopName");
        purchaseDate = (LocalDate) intent.getSerializableExtra("PurchaseDate");
        category = (Category) intent.getSerializableExtra("Category");
        purchaseStatus.setVisibility(View.INVISIBLE);
        doneButton.setEnabled(false);

        if (intent.hasExtra("Font")) {
            changeFontSize(intent.getIntExtra("Font", 20));
            isFontHelper = true;
        }
    }



    public void addPurchase(View view) {
        try {
            String productName = productNameInput.getText().toString();
            double price =  Double.parseDouble(priceInput.getText().toString());
            int quantity = Integer.parseInt(quantityInput.getText().toString());
            Purchase purchase = new Purchase(productName, price, quantity);
            purchaseList.add(purchase);
            purchaseStatus.setText("Success");
            purchaseStatus.setTextColor(Color.GREEN);
            purchaseStatus.setVisibility(View.VISIBLE);
            purchaseStatus.postDelayed(new Runnable() {
                @Override
                public void run() {
                    purchaseStatus.setVisibility(View.INVISIBLE);
                }
            }, 3000);
            doneButton.setEnabled(true);
            purchaseListOutput.setText(purchaseList.toString());

        } catch (Exception e) {
            purchaseStatus.setText("Wrong input");
            purchaseStatus.setTextColor(Color.RED);
            purchaseStatus.setVisibility(View.VISIBLE);
            purchaseStatus.postDelayed(new Runnable() {
                @Override
                public void run() {
                    purchaseStatus.setVisibility(View.INVISIBLE);
                }
            }, 3000);
        }


    }

    @SuppressLint("HardwareIds")
    public void createReceipt(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        if (isFontHelper) intent.putExtra("Font", 20);
        // Launch the photo picker and allow the user to choose only images.
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType((ActivityResultContracts.PickVisualMedia.VisualMediaType) ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }
}