package com.myprojects.knowyouringredients;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText ingredient_edit_text;
    TextView answerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ingredient_edit_text = findViewById(R.id.ingredient_edit_text_id);
        Button submit_button = findViewById(R.id.submit_button_id);

        submit_button.setOnClickListener(view -> {
            String ingredients = ingredient_edit_text.getText().toString().trim();
            Intent intent = new Intent(MainActivity.this, TableActivity.class);
            intent.putExtra("ingredients", ingredients);
            Log.d("ingredients", ingredients);
            Toast.makeText(MainActivity.this, ingredients, Toast.LENGTH_LONG).show();
            startActivity(intent);
        });
    }
}