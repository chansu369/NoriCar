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
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class Login extends AppCompatActivity{

    int thusernum;
    EditText et_logEmail,et_logPwd;
    String email,pwd,usernum;
    URL url;        //인터넷 주소 정보를 가지고 있는 변수(요청을 보낼 주소)
    HttpURLConnection con;      //웹으로 해당 URL 주소에 연결해줄 수 있게 도와주는 객체

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        et_logEmail = findViewById(R.id.et_loginEmail);
        et_logPwd = findViewById(R.id.et_loginPwd);
    }
    public void bt_JoinPage(View view){
        Intent intent = new Intent(getApplicationContext(),Join.class);
        startActivity(intent);
    }

    //로그인
    public void bt_login(View view){
        //DB - 값 판별
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d("user_token",token);

        email = et_logEmail.getText().toString();
        pwd = et_logPwd.getText().toString();

        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", pwd);
        params.put("token", token);

        String param = new CommonUtils().makeParams(params);      //쿼리스트링 한줄 리턴

        try {
            url = new URL("http://203.233.199.124:8888/wcar/loginUser");
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
                    if(jsonParse(page)){
                        //intent --> maps
                        Intent intent = new Intent(getApplicationContext(),Section.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(getApplicationContext(),"사용자 정보가 없습니다",Toast.LENGTH_LONG);
                        return;
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
    }

    public boolean jsonParse(String page){
        JSONObject item = null;
        boolean login_flag = false;
        try {

            item = new JSONObject(page);

            if(item != null){
                login_flag = true;

                thusernum = item.getInt("usernum");
                Log.d("item info : ",item.toString());
                //SharedPreferences 사용자 정보 셋팅 (세션 역할)
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("usernum",item.getInt("usernum"));
                editor.putString("name",item.getString("name"));
                editor.putString("phone",item.getString("phone"));
                editor.putString("email",item.getString("email"));
                editor.commit();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return login_flag;
    }

    //쓰레드

}
