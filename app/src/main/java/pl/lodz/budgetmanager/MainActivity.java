package pl.lodz.budgetmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import pl.lodz.budgetmanager.AddReceiptActivity;
import pl.lodz.budgetmanager.R;
import pl.lodz.budgetmanager.model.Receipt;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        if (intent.hasExtra("Receipt")) {
            TextView receiptList = findViewById(R.id.output);

            Receipt receipt = (Receipt) intent.getSerializableExtra("Receipt");
            receiptList.setText(receipt.toString());
        }


    }

    public void addReceipt(View view) {
        Intent intent = new Intent(this, AddReceiptActivity.class);
        startActivity(intent);
    }

    public void deleteReceipt(View view) {
        Intent intent = new Intent(this, DeleteReceiptActivity.class);
        startActivity(intent);
    }

    public void editBudget(View view) {
        Intent intent = new Intent(this, EditBudgetActivity.class);
        startActivity(intent);
    }
}