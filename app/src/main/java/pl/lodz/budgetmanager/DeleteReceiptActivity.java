package pl.lodz.budgetmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import pl.lodz.budgetmanager.R;
import pl.lodz.budgetmanager.model.Receipt;
import pl.lodz.budgetmanager.repository.ReceiptRepository;

public class DeleteReceiptActivity extends AppCompatActivity {

    private ListView list;
    private ReceiptRepository receiptRepository = ReceiptRepository.getInstance();
    private List<Receipt> receipts;
    private ArrayAdapter<Receipt> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_receipt);
        list = findViewById(R.id.list);
        receipts = receiptRepository.getRepository();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1,
                receipts);
        list.setAdapter(adapter);
        list.setOnItemClickListener((parent, view, position, id) -> {
            Receipt receipt = receipts.get(position);
            System.out.println(receiptRepository.remove(receipt) + " ref: " + receipt + " repoRef: " + receiptRepository.get(0));
            receipts.remove(receipt);
            adapter.notifyDataSetChanged();
        });
    }

    public void back(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}