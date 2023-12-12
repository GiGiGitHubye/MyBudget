package com.example.myapplication;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Arrays;


public class Login extends AppCompatActivity implements database.AuthenticationCallback {

    ImageView googleicon;
    ImageView fbicon;
    TextView tv;
    Button login;

    private EditText password;
    private EditText username2;
    private TextInputLayout passwordtext;
    int RC_SIGN_IN=20;

    CallbackManager mCallbackManager;
    GoogleSignInClient googlelogin;

    database db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tv=findViewById(R.id.register);
        tv.setPaintFlags(tv.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        tv.setClickable(true);
        db=new database();


        passwordtext=findViewById(R.id.editTextTextPassword);
        password=passwordtext.getEditText();
        username2=findViewById(R.id.username);
        tv.setOnClickListener(v -> {
            Intent intent=new Intent(Login.this,Register.class);
            startActivity(intent);
        });
        login=findViewById(R.id.login);
        login.setOnClickListener(v -> {
            String username3 = username2.getText().toString();
            String pwd = password.getText().toString();

            db.authenticateUser(username3, pwd, Login.this,Login.this);
        });

        googleicon=findViewById(R.id.google);
        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        googlelogin=GoogleSignIn.getClient(this,gso);
        googleicon.setOnClickListener(v -> googlelogin.signOut().addOnCompleteListener(Login.this, task -> {
            Intent intent = googlelogin.getSignInIntent();
            startActivityForResult(intent, RC_SIGN_IN, null);
        }));

        fbicon = findViewById(R.id.fb);
        mCallbackManager = CallbackManager.Factory.create();


        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                db.authenticateFacebookUser(loginResult.getAccessToken(), Login.this, Login.this);
            }
            @Override
            public void onCancel() {
            }

            @Override
            public void onError(@NonNull FacebookException error) {
            }
        });
        fbicon.setOnClickListener(v -> LoginManager.getInstance().logInWithReadPermissions(Login.this, Arrays.asList("public_profile")));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==RC_SIGN_IN){
            Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount account=task.getResult(ApiException.class);
                db.Auth(account.getIdToken(),Login.this,Login.this);
            }
            catch(Exception e) {
                throw new RuntimeException(e);
            }
        }
        else{
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }




    public void onAuthenticationSuccess() {

        runOnUiThread(() -> {
            Intent intent = new Intent(Login.this, Home.class);
            startActivity(intent);
            finish();
        });
    }


    public void onAuthenticationFailure() {
        // Authentication failed, handle accordingly (e.g., show an error message)
        Toast.makeText(Login.this, "Authentication failed", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finishAffinity();
        System.exit(0);
    }







}