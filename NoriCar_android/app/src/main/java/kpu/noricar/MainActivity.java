package kpu.noricar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //1번루트 : 카풀 제공
        final ImageButton bt1 = findViewById(R.id.toTheFirstPassage);
        bt1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent1 = new Intent(getApplicationContext(), Login.class);
                startActivity(intent1);
            }
        });

        //how to use
        final ImageButton bt2 = findViewById(R.id.toTheSecondPassage);
        bt2.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent2 = new Intent(getApplicationContext(), HowToUse.class);
                startActivity(intent2);
            }
        });

        //신고
        final ImageButton bt3 = findViewById(R.id.toTheThirdPassage);
        bt3.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent3 = new Intent(getApplicationContext(), Report.class);
                startActivity(intent3);
            }
        });
    }


}


