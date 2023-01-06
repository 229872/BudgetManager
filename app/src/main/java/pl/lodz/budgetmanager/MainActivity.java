package pl.lodz.budgetmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import pl.lodz.budgetmanager.AddReceiptActivity;
import pl.lodz.budgetmanager.R;
import pl.lodz.budgetmanager.model.Budget;
import pl.lodz.budgetmanager.model.Receipt;
import pl.lodz.budgetmanager.repository.ReceiptRepository;

public class MainActivity extends AppCompatActivity {
    private ReceiptRepository receiptRepository = ReceiptRepository.getInstance();
    private Budget budget = Budget.getInstance(receiptRepository);
    private TextView currentSpendingsLabel;
    private TextView budgetLabel;
    private TextView remainingSpendingsLabel;
    private TextView budgetWarmingLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        System.out.println("Main budget: " + budget);
        TextView receiptList = findViewById(R.id.output);

        currentSpendingsLabel = findViewById(R.id.currentSpendingsLabel);
        budgetLabel = findViewById(R.id.budgetLabel);
        remainingSpendingsLabel = findViewById(R.id.remainingSpendingsLabel);
        budgetWarmingLabel = findViewById(R.id.budgetWarmingLabel);

        System.out.println(receiptRepository.getAll().size());


        receiptList.setText(receiptRepository.getAll().toString());
        initBudgetLabels();
        setBudgetWarmingLabel();
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

    private void setBudgetWarmingLabel() {
        double currentSpendings = Double.parseDouble(currentSpendingsLabel.getText().toString());

        if (currentSpendings > budget.getMonthlyBudget()) {
            budgetWarmingLabel.setText("Przekroczono budget");
        } else if (currentSpendings == budget.getMonthlyBudget()) {
            budgetWarmingLabel.setText("Osiagnieto budget");
        } else if (currentSpendings > budget.getLimit()) {
            budgetWarmingLabel.setText("Przekroczono limit");
        } else if (currentSpendings == budget.getLimit()) {
            budgetWarmingLabel.setText("Osiagnieto limit");
        } else if (currentSpendings >= budget.getWarmingLimit()) {
            budgetWarmingLabel.setText("Zblizasz sie do limitu");
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
}