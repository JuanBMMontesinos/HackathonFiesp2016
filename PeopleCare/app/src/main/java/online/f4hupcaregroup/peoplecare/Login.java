package online.f4hupcaregroup.peoplecare;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mLoginFormView;
    private FirebaseAuth oAuth;
    private FirebaseAuth.AuthStateListener oAuthListener;
    private View focusView = null;
    private Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        utils = new Utils();

        oAuth = FirebaseAuth.getInstance();
        oAuthListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null)
                {
                    Log.i("InfoX", "Usuário Conectado: " + user.getUid());

                    GlobalVars.emailUserLogged = user.getEmail();
                    GlobalVars.uidUserLogged = user.getUid();

                    Intent intent = new Intent(Login.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Log.d("MeuLog", "Falha ");
                }
            }
        };

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mLoginFormView = findViewById(R.id.login_form);
    }

    public void onStart() {
        super.onStart();
        oAuth.addAuthStateListener(oAuthListener);
    }

    @Override
    public void onStop()     {
        super.onStop();
        if (oAuthListener != null) oAuth.removeAuthStateListener(oAuthListener);
    }


    public void login(final View view) {
        try {
            oAuth.signInWithEmailAndPassword(mEmailView.getText().toString(), mPasswordView.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Log.d("MeuLog", "Falha na autenticação");
                                Snackbar.make(view, "Falha na autenticação! Usuário não cadastrado ou senha inválida!", Snackbar.LENGTH_INDEFINITE).show();
                            } else {

                            }
                        }
                    });
        }
        catch (Exception e) {
            Log.i("InfoX","Resultado: " + validadeLogin());
            Log.e("Error" , e.toString());
        }
    }

    public void createUser(View view){
        startActivity(new Intent(Login.this, NewUser.class));
        finish();
    }

    private boolean validadeLogin()
    {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        // Check for a valid email address.
        if (TextUtils.isEmpty(email))
        {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            return false;
        }
        else if (!utils.isEmailValid(email))
        {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            return false;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password))
        {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            return false;
        }
        else if (!utils.isPasswordValid(password))
        {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            return false;
        }
        return true;
    }
}
