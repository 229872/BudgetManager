package pl.lodz.budgetmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import pl.lodz.budgetmanager.AddReceiptActivity;
import pl.lodz.budgetmanager.R;
import pl.lodz.budgetmanager.model.Receipt;
import pl.lodz.budgetmanager.repository.ReceiptRepository;

public class MainActivity extends AppCompatActivity {
    private ReceiptRepository receiptRepository = ReceiptRepository.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        TextView receiptList = findViewById(R.id.output);

        receiptList.setText(receiptRepository.getAll().toString());


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