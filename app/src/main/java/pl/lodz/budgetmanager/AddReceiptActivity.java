package pl.lodz.budgetmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import pl.lodz.budgetmanager.R;
import pl.lodz.budgetmanager.model.Category;
import pl.lodz.budgetmanager.model.Receipt;

public class AddReceiptActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Button button;
    private TextView shopNameInput;
    private CalendarView calendarView;
    private String date = LocalDate.now().toString();
    private ArrayAdapter<CharSequence> adapter;
    private Spinner spinner;
    private Category category;

    private TextView shopNameLabel;
    private TextView chooseCategoryLabel;
    private TextView dateOfPurchaseLabel;
    private boolean isFontHelper = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_receipt);
        Intent intent = getIntent();

        loadElements();

        adapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        calendarView.setOnDateChangeListener((calendarView, year, month, day) -> {
            date = year + "-" + (month < 10 ? "0" : "") + (month + 1) + "-" + day;
        });

        if (intent.hasExtra("Font")) {
            changeFontSize(intent.getIntExtra("Font", 20));
            isFontHelper = true;
        }
    }

    public void addProducts(View view) {
        Intent intent = new Intent(this, AddPurchaseActivity.class);

        String shopName = shopNameInput.getText().toString();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-d");
        LocalDate purchaseDate = LocalDate.parse(date, formatter);
        intent.putExtra("ShopName", shopName);
        intent.putExtra("PurchaseDate", purchaseDate);
        intent.putExtra("Category", category);
        if (isFontHelper) intent.putExtra("Font", 20);
        startActivity(intent);
    }

    private void loadElements() {
        button = findViewById(R.id.addProductsButton);
        button.setEnabled(false);
        shopNameInput = findViewById(R.id.shopNameInput);
        shopNameInput.addTextChangedListener(textWatcher);
        calendarView = findViewById(R.id.calendarView);
        spinner = findViewById(R.id.categoryInput);
        shopNameLabel = findViewById(R.id.shopNameLabel);
        chooseCategoryLabel = findViewById(R.id.chooseCategoryLabel);
        dateOfPurchaseLabel = findViewById(R.id.dateOfPurchaseLabel);
    }

    private void changeFontSize(int newFontSize) {
        shopNameLabel.setTextSize(newFontSize);
        chooseCategoryLabel.setTextSize(newFontSize);
        dateOfPurchaseLabel.setTextSize(newFontSize);
        shopNameInput.setTextSize(newFontSize);
        button.setTextSize(newFontSize);
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        category = Category.getFromString(parent.getItemAtPosition(position).toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}