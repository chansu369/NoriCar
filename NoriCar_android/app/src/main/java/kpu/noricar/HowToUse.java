package kpu.noricar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class HowToUse extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.howtouse_anim);

        (findViewById(R.id.buttonLeft)).setOnClickListener(this);
        (findViewById(R.id.buttonRight)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonLeft:
                startActivity(new Intent(HowToUse.this, MainActivity.class));
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
                finish();
                break;
            case R.id.buttonRight:
                startActivity(new Intent(HowToUse.this, HowToUse2.class));
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
                finish();
                break;
        }
    }
}
