package pl.lodz.budgetmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

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

    private TextView findReceiptTitle;
    private Button findByNameButton;
    private Button findByMonthButton;
    private Button findGenreButton;
    private Button backButton;

    private boolean isFontHelper = false;
    private final FindReceiptActivity pom = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_receipt);
        Intent intent = getIntent();

        loadElements();
        renderList();

        if (intent.hasExtra("Font")) {
            changeFontSize(intent.getIntExtra("Font", 20));
            isFontHelper = true;
        }
    }

    private void changeFontSize(int newFontSize) {
        findReceiptTitle.setTextSize(newFontSize);
        filterName.setTextSize(newFontSize);
        filterMonth.setTextSize(newFontSize);
        findByNameButton.setTextSize(newFontSize - 6);
        findByMonthButton.setTextSize(newFontSize - 6);
        findGenreButton.setTextSize(newFontSize - 6);
        backButton.setTextSize(newFontSize);
    }

    private void loadElements() {
        list = findViewById(R.id.list);
        filterName = findViewById(R.id.filterName);
        filterMonth = findViewById(R.id.filterMonth);
        spinner = findViewById(R.id.spinner);
        findReceiptTitle = findViewById(R.id.findReceiptTitle);
        findByNameButton = findViewById(R.id.findByNameButton);
        findByMonthButton = findViewById(R.id.findByMonthButton);
        findGenreButton = findViewById(R.id.findGenreButton);
        backButton = findViewById(R.id.backButton);

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
        list.setOnItemLongClickListener((parent, view, position, id) -> {
            new AlertDialog.Builder(FindReceiptActivity.this)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle("What you want to do ?")
                    .setMessage("Click info for receipt info or delete to delete receipt")
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Receipt receipt = receipts.get(position);
                            receiptRepository.remove(receipt);
                            receipts.remove(receipt);
                            adapter.notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton("Info", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Receipt receipt = receipts.get(position);
                            Intent intent = new Intent(pom, ReceiptInfoActivity.class);
                            intent.putExtra("ReceiptInfo", receipt);
                            if (isFontHelper) intent.putExtra("Font", 20);
                            startActivity(intent);
                        }
                    })
                    .show();
            return true;
        });
    }

    public void findByName(View view) {
        String name = filterName.getText().toString();
        List<Receipt> newList =  receiptRepository.findAll(name);
        receipts.clear();
        receipts.addAll(newList);
        adapter.notifyDataSetChanged();
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
        List<Receipt> newList = receiptRepository.findAll(category);
        receipts.clear();
        receipts.addAll(newList);
        adapter.notifyDataSetChanged();
    }

    public void back(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        if (isFontHelper) intent.putExtra("Font", 20);
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