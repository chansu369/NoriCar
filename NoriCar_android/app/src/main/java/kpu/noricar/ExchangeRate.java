package kpu.noricar;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class ExchangeRate extends AppCompatActivity implements OnClickListener{

    String sl,dl;
    int fare;
    MarkerItem item;
    TextView startLocal,destLocal,profare,pro_name,pro_phone;
    Bundle bundle;
    int usernum;
    SharedPreferences mPref;
    URL url;        //인터넷 주소 정보를 가지고 있는 변수(요청을 보낼 주소)
    HttpURLConnection con;      //웹으로 해당 URL 주소에 연결해줄 수 있게 도와주는 객체


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exchangerate);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        startLocal = findViewById(R.id.tv_startLocal);
        destLocal = findViewById(R.id.tv_destLocal);
        profare = findViewById(R.id.tv_profare);
        pro_name = findViewById(R.id.pro_name);
        pro_phone = findViewById(R.id.pro_phone);

        Button bt_provideInfo = findViewById(R.id.bt_provideInfo);
        try {
            Intent intent1 = getIntent();
            sl = intent1.getStringExtra("start_local");
            dl = intent1.getStringExtra("destination_local");
            fare = intent1.getIntExtra("fare",0);
            usernum = intent1.getIntExtra("usernum",0);

            item = new MarkerItem(intent1.getIntExtra("usernum",0)
                                  ,intent1.getDoubleExtra("st_lat",0.0)
                                  ,intent1.getDoubleExtra("st_lon",0.0)
                                  ,intent1.getDoubleExtra("en_lat",0.0)
                                  ,intent1.getDoubleExtra("en_lon",0.0));

            Log.d("usernum : ", ""+usernum);
            Log.d("bundle test : ", sl);
            Log.d("bundle test : ", dl);

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"지도에서 카풀정보를 클릭해주세요",Toast.LENGTH_SHORT).show();
        }

        bt_provideInfo.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        startLocal.setText(sl);
        destLocal.setText(dl);
        profare.setText(String.valueOf(fare));

        //사용자 정보 받아오기
        HashMap<String, String> params = new HashMap<>();
        params.put("usernum", String.valueOf(usernum));

        String param = new CommonUtils().makeParams(params);      //쿼리스트링 한줄 리턴

        try {
            url = new URL("http://203.233.199.124:8888/wcar/selectUser");
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
                    //JSON parse
                    jsonParse(page);
                }

            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "" + e.toString(), Toast.LENGTH_SHORT).show();
        } finally {
            if (con != null) {
                con.disconnect();       //연결 끊어라
            }
        }
    }
    public void jsonParse(String page){
        JSONObject item = null;
        try {

            item = new JSONObject(page);

            String name = item.getString("name");
            String phone = item.getString("phone");

            pro_name.setText(name);
            pro_phone.setText(phone);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //요청하기
    public void bt_request(View view){
        //요청
        HashMap<String, String> params = new HashMap<>();
        params.put("usernum", String.valueOf(item.getUsernum()));
        params.put("startx",Double.toString(item.getSt_lat()));
        params.put("starty",Double.toString(item.getSt_lon()));
        params.put("destx",Double.toString(item.getEn_lat()));
        params.put("desty",Double.toString(item.getEn_lon()));

        String param = new CommonUtils().makeParams(params);      //쿼리스트링 한줄 리턴
        try {
            url = new URL("http://203.233.199.124:8888/wcar/updateProvide");
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
                    if (page.equals("Request Success")){

                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("requestUsernum",usernum);
                        editor.commit();

                        AlertDialog.Builder alerBuilder = new AlertDialog.Builder(this);

                        alerBuilder.setTitle("요청 완료");

                        alerBuilder.setMessage("카풀을 요청하셨습니다.")
                                .setCancelable(false)
                                .setPositiveButton("메인 화면으로", new DialogInterface.OnClickListener() {
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
                    else{
                        Toast.makeText(getApplicationContext(),page,Toast.LENGTH_LONG);
                    }
                }

            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "" + e.toString(), Toast.LENGTH_SHORT).show();
        } finally {
            if (con != null) {
                con.disconnect();       //연결 끊어라
            }
        }
/*
        //push 까지
        try {
            url = new URL("http://192.168.11.5:8888/wcar/callPush");
        } catch (MalformedURLException e) {      //url 형식이 아니다
            Toast.makeText(getApplicationContext(), "잘못된 URL입니다.", Toast.LENGTH_SHORT).show();
        }

        //본격적으로 연결시작
        HashMap<String, String> params2 = new HashMap<>();
        params2.put("usernum",String.valueOf(usernum));
        params2.put("startx",Double.toString(item.getSt_lat()));
        params2.put("starty",Double.toString(item.getSt_lon()));
        params2.put("destx",Double.toString(item.getEn_lat()));
        params2.put("desty",Double.toString(item.getEn_lon()));

        String param2 = new CommonUtils().makeParams(params);      //쿼리스트링 한줄 리턴
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
                os.write(param2.getBytes("UTF-8"));
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

                }

            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "" + e.toString(), Toast.LENGTH_SHORT).show();
        } finally {
            if (con != null) {
                con.disconnect();       //연결 끊어라
            }
        }*/
    }

}