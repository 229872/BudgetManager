package pl.lodz.budgetmanager;

import static android.content.ContentValues.TAG;
import static pl.lodz.budgetmanager.repository.ReceiptRepository.mapToReceipt;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import pl.lodz.budgetmanager.model.Budget;
import pl.lodz.budgetmanager.model.Receipt;
import pl.lodz.budgetmanager.repository.ReceiptRepository;

public class MainActivity extends AppCompatActivity {
    private String deviceId;
    private ReceiptRepository receiptRepository = ReceiptRepository.getInstance();
    private Budget budget;
    private List<Receipt> receipts;
    private ListView receiptList;
    private ArrayAdapter<Receipt> adapter;

    private TextView currentSpendingsLabel;
    private TextView budgetLabel;
    private TextView remainingSpendingsLabel;
    private TextView budgetWarmingLabel;

    private TextView budgetTitle;
    private TextView costTitle;
    private TextView remainingMoneyTitle;
    private TextView receiptsTitle;
    private Button findButton;
    private Button editBudgetButton;
    private Button addReceiptButton;
    private Button fontButton;
    private boolean isFontHelper = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();

        render();

        if (intent.hasExtra("Font")) {
            changeFontSize(intent.getIntExtra("Font", 20));
            isFontHelper = true;
        }
        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        budget = Budget.getInstance(receiptRepository, deviceId);



    }

    public void addReceipt(View view) {
        Intent intent = new Intent(this, AddReceiptActivity.class);
        if (isFontHelper) intent.putExtra("Font", 18);
        startActivity(intent);
    }

    public void findReceipt(View view) {
        Intent intent = new Intent(this, FindReceiptActivity.class);
        if (isFontHelper) intent.putExtra("Font", 20);
        startActivity(intent);
    }

    public void editBudget(View view) {
        Intent intent = new Intent(this, EditBudgetActivity.class);
        if (isFontHelper) intent.putExtra("Font", 20);
        startActivity(intent);
    }

    private void setBudgetWarmingLabel() {
        double currentSpendings = Double.parseDouble(currentSpendingsLabel.getText().toString());

        if (budget.getMonthlyBudget() == 0) {
            budgetWarmingLabel.setText("Budget not set");
            budgetWarmingLabel.setTextColor(Color.CYAN);
        } else if (currentSpendings > budget.getMonthlyBudget()) {
            budgetWarmingLabel.setText("Budget exceeded");
            budgetWarmingLabel.setTextColor(Color.RED);
            showBudgetExceededAlert();
        } else if (currentSpendings == budget.getMonthlyBudget()) {
            budgetWarmingLabel.setText("Budget reached");
            budgetWarmingLabel.setTextColor(Color.RED);
            showBudgetReachedAlert();
        } else if (currentSpendings > budget.getLimit()) {
            budgetWarmingLabel.setText("Limit exceeded");
            budgetWarmingLabel.setTextColor(Color.rgb(255, 140, 0));
            showLimitExceededAlert();
        } else if (currentSpendings == budget.getLimit()) {
            budgetWarmingLabel.setText("Limit reached");
            budgetWarmingLabel.setTextColor(Color.YELLOW);
            showLimitReachedAlert();
        } else if (currentSpendings >= budget.getWarmingLimit()) {
            budgetWarmingLabel.setText("Close to the limit");
            budgetWarmingLabel.setTextColor(Color.CYAN);
            showLimitWarmingAlert();
        }

    }

    private void setBudgetLabel() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("budgets")
                .document(deviceId)
                .get()
                .addOnCompleteListener(docRef -> {
                    DocumentSnapshot docSnapshot = docRef.getResult();
                    if (docSnapshot.exists()) {
                        budgetLabel.setText(String.format("%.2f", docSnapshot.get("monthlyBudget")));
                    }
                });
    }

    private void setCurrentSpendingsLabel() {
        currentSpendingsLabel.setText(String.format("%.2f", budget.getCurrentSpendings()));
    }

    private void setRemainingSpendingsLabel() {
        remainingSpendingsLabel.setText(String.format("%.2f", budget.getRemainingSpendings()));
    }

    private void initBudgetLabels() {
        setBudgetLabel();
        setCurrentSpendingsLabel();
        setRemainingSpendingsLabel();
    }

    @SuppressLint({"SuspiciousIndentation", "HardwareIds"})
    private void render() {
        receiptList = findViewById(R.id.output);
        currentSpendingsLabel = findViewById(R.id.currentSpendingsLabel);
        budgetLabel = findViewById(R.id.budgetLabel);
        remainingSpendingsLabel = findViewById(R.id.remainingSpendingsLabel);
        budgetWarmingLabel = findViewById(R.id.budgetWarmingLabel);
        findButton = findViewById(R.id.findButton);
        receiptsTitle = findViewById(R.id.receiptsTitle);
        budgetTitle = findViewById(R.id.budgetTitle);
        costTitle = findViewById(R.id.costTitle);
        remainingMoneyTitle = findViewById(R.id.remainingMoneyTitle);
        editBudgetButton = findViewById(R.id.editBudgetButton);
        addReceiptButton = findViewById(R.id.addReceiptButton);
        fontButton = findViewById(R.id.fontButton);


        receipts = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("receipts")
                .whereEqualTo("deviceId", deviceId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            if (LocalDate.parse((String) document.getData().get("purchaseDate"),
                                            DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                    .getMonth().equals(LocalDate.now().getMonth())) {
                                receipts.add(mapToReceipt(document.getId(), document.getData()));
                            }
                        }

                        if (receipts.size() == 0) {
                            findButton.setEnabled(false);
                        }

                        receiptRepository.setReceipts(receipts);

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
                        initBudgetLabels();
                        setBudgetWarmingLabel();

                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
    }

    private void showBudgetExceededAlert() {
        new AlertDialog.Builder(MainActivity.this)
                .setIcon(android.R.drawable.ic_delete)
                .setTitle("Budget exceeeded")
                .setMessage("Your budget has been exceed")
                .setPositiveButton("Ok", (DialogInterface dialog, int which) -> {} )
                .show();
    }

    private void showBudgetReachedAlert() {
        new AlertDialog.Builder(MainActivity.this)
                .setIcon(android.R.drawable.ic_delete)
                .setTitle("Budget reached")
                .setMessage("Your budget has been reached")
                .setPositiveButton("Ok", (DialogInterface dialog, int which) -> {} )
                .show();
    }

    private void showLimitExceededAlert() {
        new AlertDialog.Builder(MainActivity.this)
                .setIcon(android.R.drawable.ic_delete)
                .setTitle("Limit exceeeded")
                .setMessage("Your set limit has been exceeded")
                .setPositiveButton("Ok", (DialogInterface dialog, int which) -> {} )
                .show();
    }

    private void showLimitReachedAlert() {
        new AlertDialog.Builder(MainActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Limit reached")
                .setMessage("Your set limit has been reached")
                .setPositiveButton("Ok", (DialogInterface dialog, int which) -> {} )
                .show();
    }

    private void showLimitWarmingAlert() {
        new AlertDialog.Builder(MainActivity.this)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle("Close to the limit")
                .setMessage("You are close to exceed the limit")
                .setPositiveButton("Ok", (DialogInterface dialog, int which) -> {} )
                .show();
    }


    public void increaseFontSize(View view) {
        if (!isFontHelper) {
            changeFontSize(20);
        } else {
            changeFontSize(17);
        }
        isFontHelper = !isFontHelper;
    }

    public void changeFontSize(int newFontSize) {
        currentSpendingsLabel.setTextSize(newFontSize);
        remainingSpendingsLabel.setTextSize(newFontSize);
        budgetLabel.setTextSize(newFontSize);
        remainingMoneyTitle.setTextSize(newFontSize);
        costTitle.setTextSize(newFontSize);
        budgetTitle.setTextSize(newFontSize);
        budgetWarmingLabel.setTextSize(newFontSize);
        findButton.setTextSize(newFontSize);
        receiptsTitle.setTextSize(newFontSize);
        addReceiptButton.setTextSize(newFontSize);
        fontButton.setTextSize(newFontSize);
        editBudgetButton.setTextSize(newFontSize);
    }
}