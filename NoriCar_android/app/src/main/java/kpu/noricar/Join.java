package kpu.noricar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class Join extends AppCompatActivity {

    EditText et_name, et_phone, et_email, et_pwd, et_pwCheck;
    String name, phone, email, pwd, pwCheck;
    URL url;        //인터넷 주소 정보를 가지고 있는 변수(요청을 보낼 주소)
    HttpURLConnection con;      //웹으로 해당 URL 주소에 연결해줄 수 있게 도와주는 객체
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        et_name = findViewById(R.id.et_name);
        et_phone = findViewById(R.id.et_phone);
        et_email = findViewById(R.id.et_email);
        et_pwd = findViewById(R.id.et_pwd);
        et_pwCheck = findViewById(R.id.et_pwCheck);
    }

    //Join 버튼 클릭
    public void bt_Join(View view) {
        name = et_name.getText().toString();
        phone = et_phone.getText().toString();
        email = et_email.getText().toString();
        pwd = et_pwd.getText().toString();
        pwCheck = et_pwCheck.getText().toString();

        if (pwd.equals(pwCheck)) {
            //비밀번호 체크 성공
            HashMap<String, String> params = new HashMap<>();
            params.put("name", name);
            params.put("phone", phone);
            params.put("email", email);
            params.put("password", pwd);

            String param = new CommonUtils().makeParams(params);      //쿼리스트링 한줄 리턴

            try {
                url = new URL("http://203.233.199.124:8888/wcar/insertUser");
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

                        if (page.equals("User Insert Success")){
                            AlertDialog.Builder alerBuilder = new AlertDialog.Builder(this);

                            alerBuilder.setTitle("로그인 완료");

                            alerBuilder.setMessage("회원가입 성공하셨습니다.")
                                    .setCancelable(false)
                                    .setPositiveButton("로그인 창으로", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent intent1 = new Intent(getApplicationContext(),Login.class);
                                            startActivity(intent1);
                                            finish();
                                        }
                                    });
                            AlertDialog alertDialog = alerBuilder.create();
                            alertDialog.show();
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

        } else {
            //비밀번호 실패
            Toast.makeText(getApplicationContext(),"동일한 비밀번호를 입력해 주세요.",Toast.LENGTH_SHORT).show();
            return;
        }
    }


}
