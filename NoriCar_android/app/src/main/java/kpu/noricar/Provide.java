package kpu.noricar;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class Provide extends AppCompatActivity {
    double wayDistance, st_La, st_Lo, end_La, end_Lo;
    int ex_fare;
    String re_fare,st_A,st_O,end_A,end_O,usernum;
    TextView tv_way, tv_fare;
    EditText et_fare;
    URL url;        //인터넷 주소 정보를 가지고 있는 변수(요청을 보낼 주소)
    HttpURLConnection con;      //웹으로 해당 URL 주소에 연결해줄 수 있게 도와주는 객체
    SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.last_provide);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Intent intent = getIntent();
        wayDistance = intent.getDoubleExtra("wayDistance", 0.0);
        ex_fare = intent.getIntExtra("variable", 0);
        st_La = intent.getDoubleExtra("st_La", 0.0);
        st_Lo = intent.getDoubleExtra("st_Lo", 0.0);
        end_La = intent.getDoubleExtra("end_La", 0.0);
        end_Lo = intent.getDoubleExtra("end_Lo", 0.0);

        mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int usernum1 = mPref.getInt("usernum",0);
        Log.d("user info :",""+usernum1);
        usernum = Integer.toString(usernum1);

        tv_way = findViewById(R.id.tv_way);
        tv_fare = findViewById(R.id.tv_fare);
        et_fare = findViewById(R.id.et_fare);

        tv_way.setText(String.valueOf(wayDistance));
        tv_fare.setText(String.valueOf(ex_fare));
    }

    public void btn_Provide(View view) {
        // DB ㄱㄱ
        re_fare = et_fare.getText().toString();
        st_A = Double.toString(st_La);
        st_O = Double.toString(st_Lo);
        end_A = Double.toString(end_La);
        end_O = Double.toString(end_Lo);


        HashMap<String, String> params = new HashMap<>();
        params.put("usernum", usernum);
        params.put("pay", re_fare);
        params.put("startx", st_A);
        params.put("starty", st_O);
        params.put("destx", end_A);
        params.put("desty", end_O);

        String param = new CommonUtils().makeParams(params);      //쿼리스트링 한줄 리턴
        Log.d("param Log",param);

        try {
            url = new URL("http://203.233.199.124:8888/wcar/insertProvide");
        } catch (MalformedURLException e) {      //url 형식이 아니다
            Toast.makeText(getApplicationContext(), "잘못된 URL입니다.", Toast.LENGTH_SHORT).show();
        }

        //본격적으로 연결시작
        try {
            con = (HttpURLConnection) url.openConnection(); //내가 연결할 주소와 통신선을 연다.

            if (con != null) {

                con.setConnectTimeout(10000);    //연결제한시간. 0은 무한대기.
                con.setUseCaches(false);        //캐쉬 사용여부
                con.setRequestMethod("POST"); // URL 요청에 대한 메소드 설정 : POST (default : GET)

                //Ajax형태로 보내기 때문에 반드시 있어야한다. (Spring에서도 @Responsebody 가 필요하다)
                con.setRequestProperty("Accept-Charset", "UTF-8"); // Accept-Charset 설정.
                con.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;charset=UTF-8");


                OutputStream os = con.getOutputStream();    //연결해준 통신선안에서 스트림을 연다.
                os.write(param.getBytes("UTF-8"));
                os.flush();
                os.close();     //데이터 보냄요


                //내가 데이터 보냈는데 그 요청이 성공했는지??
                if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {     //200(정상처리),404(에러),500(에러),,,,,

                    BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));

                    String line;
                    String page = "";

                    while ((line = reader.readLine()) != null) {
                        page += line;
                    }

                    Toast.makeText(getApplicationContext(), page, Toast.LENGTH_SHORT).show();

                    if(page.equals("Provide info Success")){
                        AlertDialog.Builder alerBuilder = new AlertDialog.Builder(this);

                        alerBuilder.setTitle("카풀 서비스 등록 완료");

                        alerBuilder.setMessage("카풀 등록을 완료하셨습니다")
                                .setCancelable(false)
                                .setPositiveButton("메인 페이지로", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent1 = new Intent(getApplicationContext(),Section.class);
                                        startActivity(intent1);
                                        finish();
                                    }
                                });
                        AlertDialog alertDialog = alerBuilder.create();
                        alertDialog.show();
                    }
                }
                Log.d("ResponseCode",""+con.getResponseCode());
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "" + e.toString(), Toast.LENGTH_SHORT).show();
        } finally {
            if (con != null) {
                con.disconnect();       //연결 끊어라
            }
        }
    }
}
