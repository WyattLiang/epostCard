package com.first.wyatt.postcard;

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
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    private TextView backToSign_in;
    private EditText email;
    private EditText password;
    private EditText first_name;
    private EditText last_name;
    private EditText phone;
    private EditText user_name;
    private ProgressBar progressBar;
    private Button submit;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();

        backToSign_in = findViewById(R.id.back_toSignIn);
        email = findViewById(R.id.email_register);
        password = findViewById(R.id.password);
        first_name = findViewById(R.id.firstN_register);
        last_name = findViewById(R.id.lastN_register);
        phone = findViewById(R.id.phone_register);
        user_name = findViewById(R.id.userN_register);

        submit = findViewById(R.id.Confirm_Register);
        progressBar = findViewById(R.id.register_progress);


        submit.setOnClickListener(button -> register());

        backToSign_in.setOnClickListener(button -> {
            backToSignIn();
        });
    }
    private void register(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String Email,Password,firstN,lastN,Phone,userN;
                Email = email.getText().toString();
                Password = password.getText().toString();
                firstN = first_name.getText().toString();
                lastN = last_name.getText().toString();
                Phone = phone.getText().toString();
                userN = user_name.getText().toString();

                if(TextUtils.isEmpty(Email)||TextUtils.isEmpty(Password)||TextUtils.isEmpty(firstN)||TextUtils.isEmpty(lastN)||
                        TextUtils.isEmpty(Phone)||TextUtils.isEmpty(userN)){
                    Toast.makeText(RegisterActivity.this, "Please enter all information above",Toast.LENGTH_LONG).show();
                }
                else if(userN.length() > 10){
                    Toast.makeText(RegisterActivity.this,"User Name can not be longer than 10 letters",Toast.LENGTH_LONG).show();
                }
                else if(lastN.length() > 15 || firstN.length() > 15){
                    Toast.makeText(RegisterActivity.this,"First Name of Last Name can not be longer than 15 letters",
                            Toast.LENGTH_LONG).show();
                }
                else if(Phone.length() > 10){
                    Toast.makeText(RegisterActivity.this,"Invalid Phone number",Toast.LENGTH_LONG).show();
                }
                else if(!isContainUpperCase(Password)){
                    Toast.makeText(RegisterActivity.this,"Password at least contains one upper case letter",Toast.LENGTH_LONG).show();
                }
                else if(Password.length() > 8){
                    Toast.makeText(RegisterActivity.this,"Password has to be 8 characters long contains at least one upper case",
                            Toast.LENGTH_LONG).show();
                }
                else{
                    progressBar.setVisibility(View.VISIBLE);
                    User user_new = new User(Email,userN,firstN,lastN,Phone);
                    firebaseAuth.createUserWithEmailAndPassword(Email,Password)
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d("RESULT","Register status: " + task.isSuccessful());
                                    if(!task.isSuccessful())
                                    {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(RegisterActivity.this,"Registration failed: " + task.getException(),Toast.LENGTH_LONG)
                                                .show();
                                    }
                                    else{
                                        FirebaseDatabase.getInstance().getReference("users")
                                                .child(firebaseAuth.getCurrentUser().getUid())
                                                .setValue(user_new,user_new.getemail())
                                                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        progressBar.setVisibility(View.GONE);
                                                        if(!task.isSuccessful()){
                                                            Toast.makeText(RegisterActivity.this,"Registration failed: " + task.getException(),Toast.LENGTH_LONG)
                                                                    .show();
                                                        }else{
                                                            Toast.makeText(RegisterActivity.this,"You have successfully registered",Toast.LENGTH_LONG).show();
                                                            email.setText("");
                                                            user_name.setText("");
                                                            first_name.setText("");
                                                            last_name.setText("");
                                                            phone.setText("");
                                                            password.setText("");

                                                        }
                                                    }
                                                });

                                    }
                                }
                            });
                }
            }
        });
    }

    private void backToSignIn(){
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private boolean isContainUpperCase(String pw){
        for(int index = 0; index < pw.length();index++){
            char letter = pw.charAt(index);
            if(Character.isUpperCase(letter))
                return true;
        }
        return false;
    }
}
