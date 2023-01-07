package pl.lodz.budgetmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import pl.lodz.budgetmanager.model.Receipt;
import pl.lodz.budgetmanager.repository.ReceiptRepository;

public class FindReceiptActivity extends AppCompatActivity {

    private TextInputEditText filterName;
    private TextInputEditText filterMonth;
    private ListView list;
    private ReceiptRepository receiptRepository = ReceiptRepository.getInstance();
    private List<Receipt> receipts;
    private ArrayAdapter<Receipt> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_receipt);

        loadElements();
        renderList();
    }

    private void loadElements() {
        list = findViewById(R.id.list);
        filterName = findViewById(R.id.filterName);
        filterMonth = findViewById(R.id.filterMonth);
    }

    private void renderList() {
        receipts = receiptRepository.getAll();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1,
                receipts);
        list.setAdapter(adapter);
        list.setOnItemClickListener((parent, view, position, id) -> {
            Receipt receipt = receipts.get(position);
            receipts.remove(receipt);
            adapter.notifyDataSetChanged();
        });
    }

    public void findByName(View view) {
        String name = filterName.getText().toString();
        receipts.clear();
        List<Receipt> newList =  receiptRepository.findAll(name);
        receipts.addAll(newList);

    }

    public void findByMonth(View view) {
    }
}