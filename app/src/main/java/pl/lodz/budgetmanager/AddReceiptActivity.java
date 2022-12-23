package pl.lodz.budgetmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.Serializable;
import java.time.LocalDate;

import pl.lodz.budgetmanager.R;
import pl.lodz.budgetmanager.model.Receipt;

public class AddReceiptActivity extends AppCompatActivity {

    private Button button;
    private TextView shopNameInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_receipt);
        button = findViewById(R.id.addProductsButton);
        button.setEnabled(false);
        shopNameInput = findViewById(R.id.shopNameInput);
        shopNameInput.addTextChangedListener(textWatcher);
    }

    public void addProducts(View view) {
        Intent intent = new Intent(this, AddPurchaseActivity.class);

        String shopName = shopNameInput.getText().toString();
        //FIXME temporary, need implement user input date
        LocalDate purchaseDate = LocalDate.now();

        intent.putExtra("ShopName", shopName);
        intent.putExtra("PurchaseDate", purchaseDate);
        startActivity(intent);
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!shopNameInput.getText().toString().isEmpty()) {
                button.setEnabled(true);
            }
        }
    };
}