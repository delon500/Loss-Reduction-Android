package com.example.lossreductionsystemandroid;

import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;

public class TabsActivity extends AppCompatActivity {
    private int clientId, projectId;
    private String clientName, projectName;

    private LinearLayout tabAsset, tabExpense;
    private MaterialCardView assetIcon, expenseIcon;
    private TextView assetLabel, expenseLabel;
    private View tabIndicator;

    private enum TAB {ASSET,EXPENSE}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tabs);

        // 1) READ INTENT EXTRA
        clientId = getIntent().getIntExtra("client_id", -1);
        clientName = getIntent().getStringExtra("client_name");
        projectId = getIntent().getIntExtra("project_id",-1);
        projectName = getIntent().getStringExtra("project_name");

        if (clientId < 0 || clientName == null || projectId < 0 || projectName == null) {
            Toast.makeText(this, "Missing client/project data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        //2) Wire up views
        tabAsset = findViewById(R.id.tabAsset);
        tabExpense = findViewById(R.id.tabExpense);
        assetIcon = findViewById(R.id.assetIcon);
        expenseIcon = findViewById(R.id.expanseIcon);
        assetLabel    = findViewById(R.id.assetLabel);
        expenseLabel  = findViewById(R.id.expenseLabel);
        tabIndicator  = findViewById(R.id.tabIndicator);

        // 3) Set click listener
        tabAsset.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            selectedTab(TAB.ASSET);
        });
        tabExpense.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            selectedTab(TAB.EXPENSE);
        });

        // 4) Load initial fragment only once
        if(savedInstanceState == null){
            selectedTab(TAB.ASSET);
        }
    }

    private void selectedTab(TAB which){
        Fragment frag;
        int colorPrimary = ContextCompat.getColor(this, R.color.dodger_blue);
        int colorGrayLight = ContextCompat.getColor(this, R.color.gray_light);
        int colorGrayDark = ContextCompat.getColor(this, R.color.gray_dark);

        if(which == TAB.ASSET){
            // PASS args into AssetFragment
            AssetFragment assetFragment = new AssetFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("client_id",clientId);
            bundle.putString("client_name",clientName);
            bundle.putInt("project_id",projectId);
            bundle.putString("project_name",projectName);
            assetFragment.setArguments(bundle);
            frag = assetFragment;

            // Style active/inactive
            assetIcon.setCardBackgroundColor(colorPrimary);
            expenseIcon.setCardBackgroundColor(colorGrayLight);
            assetLabel.setTextColor(colorPrimary);
            expenseLabel.setTextColor(colorGrayDark);

            //Animate indicator after layout
            tabIndicator.post(()->moveIndicatorTo(tabAsset));
        } else {
            // PASS args into ExpenseFragment
            ExpenseFragment expenseFragment = new ExpenseFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("client_id",clientId);
            bundle.putString("client_name",clientName);
            bundle.putInt("project_id",projectId);
            bundle.putString("project_name",projectName);
            expenseFragment.setArguments(bundle);
            frag = expenseFragment;

            // Style active/inactive
            expenseIcon.setCardBackgroundColor(colorPrimary);
            assetIcon.setCardBackgroundColor(colorGrayLight);
            expenseLabel.setTextColor(colorPrimary);
            assetLabel.setTextColor(colorGrayDark);

            // Animate indicator after layout
            tabIndicator.post(() -> moveIndicatorTo(tabExpense));
        }

        //Swap fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer,frag)
                .commit();
    }

    private void moveIndicatorTo(View tab){
        float targetX = tab.getX() + (tab.getWidth() - tabIndicator.getWidth()) / 2f;
        tabIndicator.animate()
                .x(targetX)
                .setDuration(200)
                .start();
    }

}