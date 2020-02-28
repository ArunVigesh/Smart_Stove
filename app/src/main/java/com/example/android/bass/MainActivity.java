package com.example.android.bass;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;



public class MainActivity extends AppCompatActivity {

    int min,hr;
    long milli;
    float ck,ca,sa,g,sk,temp=1;
    int d;
    String s,c;
    Data value ;
    JSONObject json,json1;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ImageView imageView =findViewById(R.id.imageView5);
        final ImageView imageView1 =findViewById(R.id.imageView4);
        final TextView textView=findViewById(R.id.textView);
        final TimePicker timePicker = findViewById(R.id.timePicker);
        final Button button=findViewById(R.id.button);
        Button button1=findViewById(R.id.button2);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference();
        myRef.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            value = dataSnapshot.getValue(Data.class);
            Log.d("TAG", "S Value is: " + value.getS());
            s=value.getS();
            c=value.getC();
            try {
                json =new JSONObject(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                sk=(float)json.getDouble("K");
                Log.d("TAG", "SK : "+sk);
                sa=(float)json.getDouble("A");
                Log.d("TAG", "A : "+sa);
                g=(float)json.getDouble("G");
                Log.d("TAG", "G : "+g);
                d=json.getInt("D");
                Log.d("TAG", "D : "+d);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("TAG", "C Value is: " + value.getC());
            try {
                json1 =new JSONObject(c);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                ck=(float)json1.getDouble("K");
                Log.d("TAG", "CK : "+ck);
                ca=(float)json1.getDouble("A");
                Log.d("TAG", "A : "+ca);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(sk==(float)0)
            {
                imageView.setVisibility(View.INVISIBLE);
            }
            else if(sk>(float)0)
            {
                imageView.setVisibility(View.VISIBLE);
            }
            if(d==1)
            {
                imageView1.setVisibility(View.VISIBLE);
            }
            else {
                imageView1.setVisibility(View.INVISIBLE);
            }
            rotateKnob(sk);
            if(sa!=(float)0)
            {
                int seconds = (int) (sa / 1000) % 60;
                int minutes = (int) ((sa / (1000 * 60)) % 60);
                int hours = (int) ((sa / (1000 * 60 * 60)) % 24);
                textView.setText("Time Remaining " + hours + " : " + minutes + " : " + seconds);
                button.setEnabled(false);
                c="";
                database.getReference("c").setValue(c);
            }
            else if(sa==(float)0&&temp==0)
            {
                textView.setText("");
                timePicker.setHour(0);
                timePicker.setMinute(0);
                button.setEnabled(true);
                ck=(float)0.0;
                c="{\"K\":"+ck+",\"A\":"+sa+"}";
                database.getReference("c").setValue(c);
            }
            else {
                textView.setText("");
                Toast toast=Toast.makeText(getApplicationContext(),"Done",Toast.LENGTH_SHORT);
                toast.show();
                timePicker.setHour(0);
                timePicker.setMinute(0);
                button.setEnabled(true);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
        });
            // Write a message to the database
        timePicker.setIs24HourView(true);
        timePicker.setHour(0);
        timePicker.setMinute(0);
        Button button2=findViewById(R.id.button3);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ck=(float)0.0;
                c="{\"K\":"+ck+"}";
                database.getReference("c").setValue(c);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(timePicker.getMinute()!=0||timePicker.getHour()!=0)
                { button.setEnabled(false);
                    min = timePicker.getMinute();
                    hr = timePicker.getHour();
                    milli = (hr * 60 + min) * 60000;
                }
                c = "{\"A\":" + milli + "}";
                database.getReference("c").setValue(c);
            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker.setHour(0);
                timePicker.setMinute(0);
                if(sk>(float)0) {
                    c="{\"A\":0.0}";
                }
                else
                {
                    ck = (float) 0.0;
                    c="{\"K\":"+ck+",\"A\":0.0}";
                }
                temp=1;
                database.getReference("c").setValue(c);
                if(timePicker.getMinute()!=0&&timePicker.getHour()!=0||!button.isEnabled()) {
                    textView.setText("");
                    Toast toast=Toast.makeText(getApplicationContext(),"Done",Toast.LENGTH_SHORT);
                    toast.show();
                }
                button.setEnabled(true);
            }
        });
        // Read from the database
    }
    private void rotateKnob(float degree) {
        ImageView imageView1=findViewById(R.id.imageView7);
        final RotateAnimation rotateAnim = new RotateAnimation(0.0f, degree,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        rotateAnim.setDuration(0);
        rotateAnim.setFillAfter(true);
        imageView1.startAnimation(rotateAnim);
    }
}