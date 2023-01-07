package pl.lodz.budgetmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.time.LocalDate;
import java.util.List;

import pl.lodz.budgetmanager.model.Budget;
import pl.lodz.budgetmanager.model.Receipt;
import pl.lodz.budgetmanager.repository.ReceiptRepository;

public class MainActivity extends AppCompatActivity {
    private ReceiptRepository receiptRepository = ReceiptRepository.getInstance();
    private Budget budget = Budget.getInstance(receiptRepository);
    private List<Receipt> receipts;
    private ListView receiptList;
    private ArrayAdapter<Receipt> adapter;

    private TextView currentSpendingsLabel;
    private TextView budgetLabel;
    private TextView remainingSpendingsLabel;
    private TextView budgetWarmingLabel;
    private Button findButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadElements();
        renderReceiptList();
        initBudgetLabels();
        setBudgetWarmingLabel();
    }

    public void addReceipt(View view) {
        Intent intent = new Intent(this, AddReceiptActivity.class);
        startActivity(intent);
    }

    public void findReceipt(View view) {
        Intent intent = new Intent(this, FindReceiptActivity.class);
        startActivity(intent);
    }

    public void editBudget(View view) {
        Intent intent = new Intent(this, EditBudgetActivity.class);
        startActivity(intent);
    }

    private void setBudgetWarmingLabel() {
        double currentSpendings = Double.parseDouble(currentSpendingsLabel.getText().toString());

        if (budget.getMonthlyBudget() == 0) {
            budgetWarmingLabel.setText("Nie ustawiono budgetu");
            budgetWarmingLabel.setTextColor(Color.CYAN);
        } else if (currentSpendings > budget.getMonthlyBudget()) {
            budgetWarmingLabel.setText("Przekroczono budget");
            budgetWarmingLabel.setTextColor(Color.RED);
        } else if (currentSpendings == budget.getMonthlyBudget()) {
            budgetWarmingLabel.setText("Osiagnieto budget");
            budgetWarmingLabel.setTextColor(Color.RED);
        } else if (currentSpendings > budget.getLimit()) {
            budgetWarmingLabel.setText("Przekroczono limit");
            budgetWarmingLabel.setTextColor(Color.rgb(255,140,0));
        } else if (currentSpendings == budget.getLimit()) {
            budgetWarmingLabel.setText("Osiagnieto limit");
            budgetWarmingLabel.setTextColor(Color.YELLOW);
        } else if (currentSpendings >= budget.getWarmingLimit()) {
            budgetWarmingLabel.setText("Zblizasz sie do limitu");
            budgetWarmingLabel.setTextColor(Color.CYAN);
        }

    }

    private void setBudgetLabel() {
        budgetLabel.setText(Double.toString(budget.getMonthlyBudget()));
    }

    private void setCurrentSpendingsLabel() {
        currentSpendingsLabel.setText(Double.toString(budget.getCurrentSpendings()));
    }

    private void setRemainingSpendingsLabel() {
        remainingSpendingsLabel.setText(Double.toString(budget.getRemainingSpendings()));
    }

    private void initBudgetLabels() {
        setBudgetLabel();
        setCurrentSpendingsLabel();
        setRemainingSpendingsLabel();
    }

    private void loadElements() {
        receiptList = findViewById(R.id.output);
        currentSpendingsLabel = findViewById(R.id.currentSpendingsLabel);
        budgetLabel = findViewById(R.id.budgetLabel);
        remainingSpendingsLabel = findViewById(R.id.remainingSpendingsLabel);
        budgetWarmingLabel = findViewById(R.id.budgetWarmingLabel);
        findButton = findViewById(R.id.findButton);
        if (receiptRepository.getAll().size() == 0) {
            findButton.setEnabled(false);
        }
    }

    private void renderReceiptList() {
        receipts = receiptRepository.findAll(LocalDate.now().getMonth());
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1,
                receipts);
        receiptList.setAdapter(adapter);
        receiptList.setOnItemLongClickListener((parent, view, position, id) -> {
            new AlertDialog.Builder(MainActivity.this)
                    .setIcon(android.R.drawable.ic_delete)
                    .setTitle("Are you sure?")
                    .setMessage("Do you want to delete this item")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Receipt receipt = receipts.get(position);
                            receiptRepository.remove(receipt);
                            receipts.remove(receipt);
                            adapter.notifyDataSetChanged();
                            initBudgetLabels();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        });
    }

}