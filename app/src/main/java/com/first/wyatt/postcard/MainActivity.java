package com.first.wyatt.postcard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private Button logIn_btn;
    private Button register_btn;
    private TextView forgetPw_btn;
    private EditText Email;
    private EditText Password;
    private ProgressBar progressBar;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        logIn_btn = findViewById(R.id.SignIn_Btn);
        register_btn = findViewById(R.id.New_Btn);
        forgetPw_btn = findViewById(R.id.forget_password);
        Email = findViewById(R.id.Email);
        Password = findViewById(R.id.Password);
        progressBar = findViewById(R.id.progress);

        if(firebaseUser != null){
            Toast.makeText(getApplicationContext(), "Token is valid. Logining in!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(MainActivity.this,HomeActivity.class);
            startActivity(intent);
            finish();
        }

    }



    public void signIn(View view){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String email = Email.getText().toString();
                String password = Password.getText().toString();
                progressBar.setVisibility(View.VISIBLE);

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Please enter your email!", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Please enter password!", Toast.LENGTH_LONG).show();
                    return;
                }
                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else {
                                    Log.d("LOG IN RESULT: ", task.getException()+"");
                                    Toast.makeText(getApplicationContext(), "Email and password does not match", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
    }

    public void forgetPass(View view){
        String email;
        email = Email.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(getApplicationContext(),"Please enter your email address",Toast.LENGTH_LONG).show();
        }
        else{
            firebaseAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(MainActivity.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"A reset password email has been sent to you",Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    public void register(View view){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }


}