package pl.lodz.budgetmanager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import pl.lodz.budgetmanager.model.Category;
import pl.lodz.budgetmanager.model.Purchase;
import pl.lodz.budgetmanager.model.Receipt;
import pl.lodz.budgetmanager.repository.ReceiptRepository;

public class AddPurchaseActivity extends AppCompatActivity {
    private final ReceiptRepository receiptRepository = ReceiptRepository.getInstance();
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


        Intent intent = getIntent();
        shopName = intent.getStringExtra("ShopName");
        purchaseDate = (LocalDate) intent.getSerializableExtra("PurchaseDate");
        category = (Category) intent.getSerializableExtra("Category");
        purchaseStatus.setVisibility(View.INVISIBLE);
        doneButton.setEnabled(false);
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
        Receipt receipt = new Receipt(shopName, purchaseList, purchaseDate, category);
        receiptRepository.add(receipt, Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
        startActivity(intent);
    }
}