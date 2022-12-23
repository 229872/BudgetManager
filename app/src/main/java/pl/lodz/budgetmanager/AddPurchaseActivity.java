package pl.lodz.budgetmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AddPurchaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_purchase);
    }

    public void addPurchase(View view) {
    }

    public void createReceipt(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}