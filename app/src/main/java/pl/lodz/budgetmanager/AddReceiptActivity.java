package pl.lodz.budgetmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import pl.lodz.budgetmanager.R;

public class AddReceiptActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_receipt);

        Intent intent = getIntent();
    }

    public void addProducts(View view) {
        Intent intent = new Intent(this, AddPurchaseActivity.class);
        startActivity(intent);
    }
}