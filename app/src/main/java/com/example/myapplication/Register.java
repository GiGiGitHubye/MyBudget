package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

public class Register extends AppCompatActivity {

    private EditText name, password, retypepassword;
    private Button register;

    database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db=new database();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        TextInputLayout passwordtext = findViewById(R.id.rpassword);
        TextInputLayout retypepasswordtext = findViewById(R.id.repassword);

        name = findViewById(R.id.rname);
        password = passwordtext.getEditText();
        retypepassword = retypepasswordtext.getEditText();

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 4) {
                    name.setError("Name must have at least 4 characters");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String username = name.getText().toString();
                db.checkDuplicateUser(username, isDuplicate -> {
                    if (isDuplicate) {
                        // Show an error if the username is a duplicate
                        runOnUiThread(() -> name.setError("Name has been taken"));
                    } else {
                        // Clear the error if the username is not a duplicate
                        runOnUiThread(() -> name.setError(null));
                    }
                });

                password.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() < 6) {
                            password.setError("Password must have at least 6 characters");
                        } else password.setError(null);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });

                retypepassword.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String check = password.getText().toString();
                        String check2 = retypepassword.getText().toString();

                        if (!check.equals(check2)) {
                            retypepassword.setError("Your Password is different");
                        } else retypepassword.setError(null);
                    }
                });

                register = findViewById(R.id.button);

                register.setOnClickListener(v -> {
                    if (name.getError() == null && password.getError() == null && retypepassword.getError() == null) {
                        Toast.makeText(Register.this, "Registration successful", Toast.LENGTH_SHORT).show();
                        String username1 = name.getText().toString();
                        String psw = password.getText().toString();

                        // Store data in Firestore
                        db.storeDataGuessed(username1, psw, Register.this);

                        Intent intent = new Intent(Register.this, Login.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(Register.this, "Please check your name or password", Toast.LENGTH_SHORT).show();
                    }
                });
            }


        });
    }
}
