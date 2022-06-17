package com.example.mycarwatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    EditText logEmail, logPassword;
    Button userLogin;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        logEmail = findViewById(R.id.email);
        logPassword = findViewById(R.id.password);
        fAuth = FirebaseAuth.getInstance();

        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), Activity.class));
            finish();
        }

        userLogin = findViewById(R.id.btnlogin);
        userLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = logEmail.getText().toString().trim();
                String password = logPassword.getText().toString().trim();

                if(email.isEmpty()){
                    logEmail.setError("Введите почту!!!");
                    return;
                }
                if(password.isEmpty()){
                    logPassword.setError("Введите свой пароль!!!");
                    return;
                }else if(password.length() < 6){
                    logPassword.setError("Пароль должен содержать больше 6 символов");
                    return;
                }

                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Login.this,"Вы успешно вошли в свой аккаунт!",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Login.this, Activity.class));
                            finish();
                        }else {
                            Toast.makeText(Login.this,"Неверные введенные данные!"+ task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


    }
}