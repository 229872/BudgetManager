package pl.lodz.budgetmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import pl.lodz.budgetmanager.model.Purchase;
import pl.lodz.budgetmanager.model.Receipt;

public class ReceiptInfoActivity extends AppCompatActivity {

    private TextView receiptInfoTitle;
    private TextView receiptObject;
    private Button showPhotoButton;
    private ListView purchasesList;
    private ArrayAdapter<Purchase> adapter;
    private List<Purchase> purchases;
    private boolean isFontHelper = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_info);
        Intent intent = getIntent();
        Receipt receipt = (Receipt) intent.getSerializableExtra("ReceiptInfo");

        initElements();

        purchases = receipt.getPurchases();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1,
                purchases);
        purchasesList.setAdapter(adapter);
        if (intent.hasExtra("Font")) {
            changeFontSize(intent.getIntExtra("Font", 20));
            isFontHelper = true;
        }

    }

    private void initElements() {
        purchasesList = findViewById(R.id.purchasesList);
        receiptInfoTitle = findViewById(R.id.receiptInfoTitle);
        receiptObject = findViewById(R.id.receiptObject);
        showPhotoButton = findViewById(R.id.showPhotoButton);
    }

    private void changeFontSize(int font) {
        receiptInfoTitle.setTextSize(font);
        receiptObject.setTextSize(font);
        showPhotoButton.setTextSize(font);
    }

    public void displayPhoto(View view) {
       // TODO photo
    }
}