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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewUser extends AppCompatActivity {

    // UI references.
    private AutoCompleteTextView mNameView;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mRetypePasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private FirebaseAuth oAuth;
    private FirebaseAuth.AuthStateListener oAuthListener;
    private View focusView = null;

    private Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

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

                    Intent intent = new Intent(NewUser.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Log.e("Error", "Falha ");
                }
            }
        };

        mNameView = (AutoCompleteTextView) findViewById(R.id.user);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mRetypePasswordView = (EditText) findViewById(R.id.retypePassword);
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

    public void createUser(View view){
        try {
            if(validadeLogin()) {
                oAuth.createUserWithEmailAndPassword(mEmailView.getText().toString(), mPasswordView.getText().toString())
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    Log.e("Error", "Falha na criação da conta");
                                    Snackbar.make(mEmailView, "Falha na criação da conta! O usuário já está cadastrado!", Snackbar.LENGTH_INDEFINITE).show();
                                } else {
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference ref = database.getReference("usersPeopleCare").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    ref.child("name").setValue(mNameView.getText().toString());
                                    ref.child("user").setValue(mEmailView.getText().toString());
                                    ref.child("email").setValue(mEmailView.getText().toString());
                                    ref.child("warning").setValue("OK");
                                    ref.child("showWarning").setValue("N");
                                    ref.child("uid").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    GlobalVars.userLogged = mNameView.getText().toString();
                                }
                            }
                        });
            }

        }
        catch (Exception e) {
            Log.i("InfoX","Resultado: " + validadeLogin());
            Log.e("Error" , e.toString());
        }
    }

    private boolean validadeLogin()
    {
        // Reset errors.
        mNameView.setError(null);
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mRetypePasswordView.setError(null);

        String name = mNameView.getText().toString();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String retypePassword = mRetypePasswordView.getText().toString();

        // Check if name is not empty.
        if (TextUtils.isEmpty(name))
        {
            mNameView.setError(getString(R.string.error_field_required));
            focusView = mNameView;
            return false;
        }

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

        // Check if passwords are different.
        if (!password.equals(retypePassword))
        {
            mPasswordView.setError(getString(R.string.error_not_match_password));
            mRetypePasswordView.setError(getString(R.string.error_not_match_password));
            focusView = mPasswordView;
            return false;
        }

        return true;
    }

}
