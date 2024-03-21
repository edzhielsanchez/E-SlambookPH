        package com.example.e_slambookph;

        import androidx.appcompat.app.AppCompatActivity;

        import android.content.ContentValues;
        import android.content.Context;
        import android.content.Intent;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.Toast;

        public class RegisterActivity extends AppCompatActivity {

            EditText editTextName, editTextUsername, editTextEmail, editTextPassword, editRetypePass;
            Button buttonRegister;
            DBHelper dbHelper;

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_register);

                dbHelper = new DBHelper(this);

                editTextName = findViewById(R.id.editTextName);
                editTextUsername = findViewById(R.id.editTextUsername2);
                editTextEmail = findViewById(R.id.editTextEmail2);
                editTextPassword = findViewById(R.id.editTextPassword2);
                editRetypePass = findViewById(R.id.editRetypePassword);
                buttonRegister = findViewById(R.id.buttonRegister);

                buttonRegister.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = editTextName.getText().toString().trim();
                        String username = editTextUsername.getText().toString().trim();
                        String email = editTextEmail.getText().toString().trim();
                        String password = editTextPassword.getText().toString().trim();
                        String repass = editRetypePass.getText().toString().trim();

                        if (name.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() || repass.isEmpty()) {
                            Toast.makeText(RegisterActivity.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                        } else {
                            if (password.equals(repass)) {
                                if (!dbHelper.checkUsername(username)) {
                                    boolean insert = dbHelper.insertData(name, username, email, password);
                                    if (insert) {
                                        Toast.makeText(RegisterActivity.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Username already exists! Please choose another username", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        }