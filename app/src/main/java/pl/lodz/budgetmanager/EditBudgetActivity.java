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
    private TextView editBudgetTitle;
    private TextView infoTitle;
    private TextView budgetInfoTitle;
    private TextView liminitInfoTitle;
    private TextView warminTitle;

    private Button budgetButton;
    private ReceiptRepository receiptRepository = ReceiptRepository.getInstance();
    private Budget budget = Budget.getInstance(receiptRepository);
    private boolean isFontHelper = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        System.out.println("Edit budget: " + budget);
        setContentView(R.layout.activity_edit_budget);
        loadElements();
        loadValues();

        if (intent.hasExtra("Font")) {
            changeFontSize(intent.getIntExtra("Font", 20));
            isFontHelper = true;
        }
    }

    private void changeFontSize(int newFontSize) {
        editBudgetTitle.setTextSize(newFontSize);
        monthLabel.setTextSize(newFontSize);
        budgetInput.setTextSize(newFontSize);
        limitInput.setTextSize(newFontSize);
        warmingInput.setTextSize(newFontSize);
        budgetButton.setTextSize(newFontSize);
        infoTitle.setTextSize(newFontSize);
        warminTitle.setTextSize(newFontSize);
        budgetInfoTitle.setTextSize(newFontSize);
        liminitInfoTitle.setTextSize(newFontSize);
        warminTitle.setTextSize(newFontSize);
        budgetInfoLabel.setTextSize(newFontSize);
        limitInfoLabel.setTextSize(newFontSize);
        warmingInfoLabel.setTextSize(newFontSize);
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
        editBudgetTitle = findViewById(R.id.editBudgetTitle);
        infoTitle = findViewById(R.id.infoTitle);
        budgetInfoTitle = findViewById(R.id.budgetInfoTitle);
        liminitInfoTitle = findViewById(R.id.liminitInfoTitle);
        warminTitle = findViewById(R.id.warminTitle);
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
            if (isFontHelper) intent.putExtra("Font", 20);
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