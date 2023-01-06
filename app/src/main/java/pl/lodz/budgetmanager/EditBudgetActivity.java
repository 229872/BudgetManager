package pl.lodz.budgetmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import pl.lodz.budgetmanager.exceptions.WrongValueException;
import pl.lodz.budgetmanager.model.Budget;
import pl.lodz.budgetmanager.repository.ReceiptRepository;

public class EditBudgetActivity extends AppCompatActivity {

    private TextInputEditText budgetInput;
    private TextInputEditText limitInput;
    private TextInputEditText warmingInput;
    private Button budgetButton;
    private ReceiptRepository receiptRepository = ReceiptRepository.getInstance();
    private Budget budget = Budget.getInstance(receiptRepository);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("Edit budget: " + budget);
        setContentView(R.layout.activity_edit_budget);
        loadElements();
    }

    private void loadElements() {
        budgetInput = findViewById(R.id.budgetInput);
        limitInput = findViewById(R.id.limitInput);
        warmingInput = findViewById(R.id.warmingInput);
        budgetButton = findViewById(R.id.budgetButton);
    }

    public void editBudget(View view) {
        double newBudget = Double.parseDouble(budgetInput.getText().toString());
        double newLimit = Double.parseDouble(limitInput.getText().toString());
        double newWarmingLimit = Double.parseDouble(warmingInput.getText().toString());

        try {
            budget.setMonthlyBudget(newBudget);
            budget.setLimit(newLimit);
            budget.setWarmingLimit(newWarmingLimit);

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } catch (WrongValueException e) {
            e.printStackTrace();
        }


    }
}