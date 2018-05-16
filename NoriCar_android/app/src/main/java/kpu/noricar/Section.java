package kpu.noricar;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.UiThread;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class Section extends AppCompatActivity {
    URL url;
    HttpURLConnection con;
    int requestUsernum;
    int userNum;
    SharedPreferences mPref;
    Object lock = new Object();
    Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.section);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Button btn_provide = findViewById(R.id.btn_provide);
        Button btn_request = findViewById(R.id.btn_request);

        mHandler = new Handler();
        mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        requestUsernum = mPref.getInt("requestUsernum", 0);
        userNum = mPref.getInt("usernum",0);

        if (requestUsernum != userNum) {
            requestThread requestThread = new requestThread();
            Thread thread1 = new Thread(requestThread);
            thread1.setDaemon(true);
            thread1.start();
        }

        btn_provide.setOnClickListener(new BtnClick());
        btn_request.setOnClickListener(new BtnClick());
    }

    class BtnClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.btn_provide) {
                //카풀 제공 인텐트
                Intent intent = new Intent(getApplicationContext(), Map.class);
                startActivity(intent);
            } else if (view.getId() == R.id.btn_request) {
                //카풀 요청 인텐트
                Intent intent2 = new Intent(getApplicationContext(), Request.class);
                startActivity(intent2);
            }
        }
    }

    class requestThread extends AppCompatActivity implements Runnable {

        @Override
        public void run() {

            if(requestUsernum != 0){
                try {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                AlertDialog.Builder builder = new AlertDialog.Builder(Section.this);
                                AlertDialog dialog = builder.setTitle("요청 들어옴")
                                        .setMessage("카풀 요청이 들어왔습니다")
                                        .setPositiveButton("요청 확인 완료", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                releaseLock();
                                            }
                                        })
                                        .setCancelable(false)
                                        .create();
                                dialog.show();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });

                }catch (Exception e){
                    e.printStackTrace();

                }
            }

            while (true) {
                HashMap<String, String> params = new HashMap<>();

                Log.d("requestUsernum", String.valueOf(requestUsernum));

                params.put("usernum", String.valueOf(requestUsernum));
                String param = new CommonUtils().makeParams(params);      //쿼리스트링 한줄 리턴

                try {
                    url = new URL("http://203.233.199.124:8888/wcar/checkRequest");
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

                            Log.d("Request select : ", page);
                            if (page.equals("Request select")) {

                                HashMap<String, String> params2 = new HashMap<>();
                                params.put("usernum", String.valueOf(requestUsernum));
                                String param2 = new CommonUtils().makeParams(params);      //쿼리스트링 한줄 리턴

                                try {
                                    url = new URL("http://203.233.199.124:8888/wcar/deleteProvide");
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


                                        os = con.getOutputStream();    //연결해준 통신선안에서 스트림을 연다.
                                        os.write(param2.getBytes("UTF-8"));
                                        os.flush();
                                        os.close();     //데이터 보냄요


                                        //내가 데이터 보냈는데 그 요청이 성공했는지??
                                        if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {     //200(정상처리),404(에러),500(에러),,,,,

                                            reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));

                                            String line2;
                                            String page2 = "";

                                            while ((line2 = reader.readLine()) != null) {
                                                page2 += line2;
                                            }
                                            if (page2.equals("Delete Success")) {
                                                SharedPreferences.Editor editor = mPref.edit();
                                                editor.remove("requestUsernum");
                                                editor.commit();

                                                Intent intent1 = new Intent(Section.this, Section.class);
                                                startActivity(intent1);
                                                finish();
                                            } else {
                                                return;
                                            }
                                        }

                                    }
                                    synchronized (lock) {
                                        lock.wait();
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
                    }
                } catch (Exception e) {
                } finally {
                    if (con != null) {
                        con.disconnect();       //연결 끊어라
                    }
                }
                try {
                    Thread.sleep(5000); // 쓰레드를 잠시 멈춤
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }
    void releaseLock() {
        synchronized (lock) {
            lock.notify();
            Log.e("TAG", "notify");
        }
    }


}