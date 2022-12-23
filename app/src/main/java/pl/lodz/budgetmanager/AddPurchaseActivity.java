package pl.lodz.budgetmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import pl.lodz.budgetmanager.model.Purchase;
import pl.lodz.budgetmanager.model.Receipt;

public class AddPurchaseActivity extends AppCompatActivity {
    private List<Purchase> purchaseList = new ArrayList<>();

    private TextInputEditText productNameInput;
    private TextInputEditText priceInput;
    private TextInputEditText quantityInput;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_purchase);
        productNameInput = findViewById(R.id.productNameInput);
        priceInput = findViewById(R.id.priceInput);
        quantityInput = findViewById(R.id.quantityInput);

    }

    public void addPurchase(View view) {
        String productName = productNameInput.getText().toString();
        double price =  Double.parseDouble(priceInput.getText().toString());
        int quantity = Integer.parseInt(quantityInput.getText().toString());
        Purchase purchase = new Purchase(productName, price, quantity);
        purchaseList.add(purchase);
    }

    public void createReceipt(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        Receipt receipt = new Receipt("default", purchaseList, LocalDate.now());
        intent.putExtra("Receipt", receipt);
        startActivity(intent);
    }
}