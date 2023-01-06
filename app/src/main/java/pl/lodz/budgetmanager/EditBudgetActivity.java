package pl.lodz.budgetmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
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
    private TextView errorLabel;
    private TextView budgetInfoLabel;
    private TextView limitInfoLabel;
    private TextView warmingInfoLabel;
    private TextView monthLabel;

    private Button budgetButton;
    private ReceiptRepository receiptRepository = ReceiptRepository.getInstance();
    private Budget budget = Budget.getInstance(receiptRepository);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("Edit budget: " + budget);
        setContentView(R.layout.activity_edit_budget);
        loadElements();
        loadValues();
    }

    private void loadElements() {
        budgetInput = findViewById(R.id.budgetInput);
        limitInput = findViewById(R.id.limitInput);
        warmingInput = findViewById(R.id.warmingInput);
        budgetButton = findViewById(R.id.budgetButton);
        errorLabel = findViewById(R.id.errorLabel);
        budgetInfoLabel = findViewById(R.id.budgetInfoLabel);
        limitInfoLabel = findViewById(R.id.limitInfoLabel);
        warmingInfoLabel = findViewById(R.id.warmingInfoLabel);
        monthLabel = findViewById(R.id.monthLabel);
    }

    private void loadValues() {
        budgetInfoLabel.setText(Double.toString(budget.getMonthlyBudget()));
        String limitString = Double.toString(budget.getLimit());
        if (limitString.contains("E")) {
            limitInfoLabel.setText("Not set");
        } else {
            limitInfoLabel.setText(limitString);
        }

        String warmingString = Double.toString(budget.getWarmingLimit());
        if (warmingString.contains("E")) {
            warmingInfoLabel.setText("Not set");
        } else {
            warmingInfoLabel.setText(warmingString);
        }
        monthLabel.setText(budget.getCurrentMonth().toString());
    }

    public void editBudget(View view) {
        double newBudget;
        double newLimit;
        double newWarmingLimit;

        try {
            newBudget = Double.parseDouble(budgetInput.getText().toString());
            newLimit = Double.parseDouble(limitInput.getText().toString());
            newWarmingLimit = Double.parseDouble(warmingInput.getText().toString());

            budget.setMonthlyBudget(newBudget);
            budget.setLimit(newLimit);
            budget.setWarmingLimit(newWarmingLimit);

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } catch (WrongValueException e) {
            errorLabel.setText(e.getMessage());
            errorLabel.setTextColor(Color.RED);
            errorLabel.setVisibility(View.VISIBLE);
            errorLabel.postDelayed(new Runnable() {
                @Override
                public void run() {
                    errorLabel.setVisibility(View.INVISIBLE);
                }
            }, 3000);

        } catch (Exception e) {
            errorLabel.setText("Fields cannot be empty");
            errorLabel.setTextColor(Color.RED);
            errorLabel.setVisibility(View.VISIBLE);
            errorLabel.postDelayed(new Runnable() {
                @Override
                public void run() {
                    errorLabel.setVisibility(View.INVISIBLE);
                }
            }, 3000);
        }


    }
}