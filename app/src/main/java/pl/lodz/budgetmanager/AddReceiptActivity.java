package pl.lodz.budgetmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import pl.lodz.budgetmanager.R;
import pl.lodz.budgetmanager.model.Receipt;

public class AddReceiptActivity extends AppCompatActivity {

    private Button button;
    private TextView shopNameInput;
    private CalendarView calendarView;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_receipt);
        button = findViewById(R.id.addProductsButton);
        button.setEnabled(false);
        shopNameInput = findViewById(R.id.shopNameInput);
        shopNameInput.addTextChangedListener(textWatcher);
        calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener((calendarView, year, month, day) -> {
            date = year + "/" + month + "/" + day;
        });
    }

    public void addProducts(View view) {
        Intent intent = new Intent(this, AddPurchaseActivity.class);

        String shopName = shopNameInput.getText().toString();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/d");
        LocalDate purchaseDate = LocalDate.parse(date, formatter);
        System.out.println(purchaseDate);

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