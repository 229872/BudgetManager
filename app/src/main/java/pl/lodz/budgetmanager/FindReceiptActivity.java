package pl.lodz.budgetmanager;

import static pl.lodz.budgetmanager.repository.ReceiptRepository.mapToReceipt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.time.Month;
import java.util.List;
import java.util.Locale;

import pl.lodz.budgetmanager.model.Category;
import pl.lodz.budgetmanager.model.Receipt;
import pl.lodz.budgetmanager.repository.ReceiptRepository;

public class FindReceiptActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private TextInputEditText filterName;
    private TextInputEditText filterMonth;
    private ListView list;
    private Spinner spinner;
    private ReceiptRepository receiptRepository = ReceiptRepository.getInstance();
    private List<Receipt> receipts;
    private ArrayAdapter<Receipt> adapter;
    private ArrayAdapter<CharSequence> categoryAdapter;
    private Category category;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

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
        spinner = findViewById(R.id.spinner);

        categoryAdapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(categoryAdapter);
        spinner.setOnItemSelectedListener(this);
    }

    private void renderList() {
        receipts = receiptRepository.findAll();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1,
                receipts);
        list.setAdapter(adapter);
        list.setOnItemClickListener((parent, view, position, id) -> {
            Receipt receipt = receipts.get(position);
            receiptRepository.remove(receipt);
            receipts.remove(receipt);
            adapter.notifyDataSetChanged();
        });
    }

    public void findByName(View view) {
        String name = filterName.getText().toString();
        receipts.clear();
        db.collection("receipts")
                .whereEqualTo("shopName", name)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            System.out.println(doc.getId() + " => " + doc.getData());
                            receipts.add(mapToReceipt(doc.getId(), doc.getData()));
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    public void findByMonth(View view) {
        String month = filterMonth.getText().toString().toUpperCase(Locale.ROOT);
        Month validMonth;
        try {
            validMonth = Month.valueOf(month);
        } catch (Exception e) {
            receipts.clear();
            adapter.notifyDataSetChanged();
            return;
        }
        List<Receipt> newLIst = receiptRepository.findAll(validMonth);
        receipts.clear();
        receipts.addAll(newLIst);
        adapter.notifyDataSetChanged();
    }

    public void findByCategory(View view) {
        // FIXME finding with ACCESSORY selected crashes the app
        String categoryName = category.name();
        System.out.println(categoryName);
        receipts.clear();
        db.collection("receipts")
                .whereEqualTo("category", categoryName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            System.out.println(doc.getId() + " => " + doc.getData());
                            receipts.add(mapToReceipt(doc.getId(), doc.getData()));
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    public void back(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        category = Category.getFromString(parent.getItemAtPosition(position).toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}