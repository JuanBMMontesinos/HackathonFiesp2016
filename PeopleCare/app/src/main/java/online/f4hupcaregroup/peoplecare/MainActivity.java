package online.f4hupcaregroup.peoplecare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference reference;
    private TextView warning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        warning = (TextView) findViewById(R.id.txtWarning);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("usersPeopleCare").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        if(reference == null) Log.i("Info", "Banco vazio.");

        try {
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if(dataSnapshot.hasChildren()) {
                            Log.i("InfoX", "dataSnapshot.toString: " + dataSnapshot.toString());
                            Log.i("InfoX", "dataSnapshot.getKey: " + dataSnapshot.getKey());
                            Log.i("InfoX", "dataSnapshot.child: " + dataSnapshot.child("warning").getValue());
                            warning.setText(dataSnapshot.child("warning").getValue().toString());
                        }
                    }catch (Exception e) {
                        Log.e("Error", e.toString());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }catch (Exception e) {
            Log.e("Error", e.toString());
        }
    }

    public void disconnectUser(View view) {
        FirebaseAuth.getInstance().signOut();

        startActivity(new Intent(this, Login.class));
        finish();
    }
}
