package com.myprojects.knowyouringredients;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TableActivity extends AppCompatActivity {
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

        linearLayout = findViewById(R.id.table_layout_id);

        String input = getIntent().getStringExtra("ingredients");
        if (input != null && !input.isEmpty()) {
            String[] values = input.split(",");

            // add header raw
            addRow("Text", "Description", true);

            // add data rows
            for (String value : values) {
                value = value.trim();
                if (!value.isEmpty()) {
                    addRow(value, value, false);
                }
            }

        }
    }

    private void addRow(String text, String description, boolean isHeader) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(8, 8, 8, 8);

        TextView textView1 = new TextView(this);
        TextView textView2 = new TextView(this);

        String question = "US Politics";


        textView1.setText(text);
        textView2.setText(R.string.fetching_answer_from_gemini);
        GeminiClient.fetchAnswer(text, question, new GeminiClient.GeminiCallback() {
            @Override
            public void onResponse(String result) {
                runOnUiThread(() -> textView2.setText(result));
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onError(String error) {
                runOnUiThread(() -> textView2.setText("Error: " + error));
            }
        });


        textView1.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        textView2.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        textView1.setPadding(8, 8, 8, 8);
        textView2.setPadding(8, 8, 8, 8);

        if (isHeader) {
            textView1.setText(R.string.keyword);
            textView2.setText(R.string.description);
            textView1.setTypeface(null, Typeface.BOLD);
            textView2.setTypeface(null, Typeface.BOLD);
            textView1.setBackgroundColor(Color.BLACK);
            textView2.setBackgroundColor(Color.BLACK);
        }

        row.addView(textView1);
        row.addView(textView2);

        linearLayout.addView(row);
    }

}
