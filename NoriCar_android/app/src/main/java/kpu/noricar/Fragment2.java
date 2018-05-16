package kpu.noricar;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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


public class Fragment2 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

     String ss,sl,ds,dl;
    int fare;
    TextView startLocal,destLocal,profare,pro_name,pro_phone;
    Bundle bundle;
    int usernum;
    URL url;        //인터넷 주소 정보를 가지고 있는 변수(요청을 보낼 주소)
    HttpURLConnection con;      //웹으로 해당 URL 주소에 연결해줄 수 있게 도와주는 객체

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public Fragment2() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static Fragment2 newInstance(String param1, String param2) {
        Fragment2 fragment = new Fragment2();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_request2, container, false);

        startLocal = linearLayout.findViewById(R.id.tv_startLocal);
        destLocal = linearLayout.findViewById(R.id.tv_destLocal);
        profare = linearLayout.findViewById(R.id.tv_profare);
        pro_name = linearLayout.findViewById(R.id.pro_name);
        pro_phone = linearLayout.findViewById(R.id.pro_phone);

        Button bt_provideInfo = linearLayout.findViewById(R.id.bt_provideInfo);

        Log.d("onCreateView: ","exec");

        try {
            sl = getArguments().getString("start_local");
            ss = getArguments().getString("start_sub_local");
            dl = getArguments().getString("destination_local");
            ds = getArguments().getString("destination_sub_local");
            fare = getArguments().getInt("fare");
            usernum = getArguments().getInt("usernum");

            Log.d("usernum : ", ""+usernum);
            Log.d("bundle test : ", sl);
            Log.d("bundle test : ", ss);
            Log.d("bundle test : ", dl);
            Log.d("bundle test : ", ds);

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getContext(),"지도에서 카풀정보를 클릭해주세요",Toast.LENGTH_SHORT).show();
        }



        bt_provideInfo.setOnClickListener(new BtnClick());



        /*
        startLocal = getView().findViewById(R.id.tv_startLocal);
        destLocal = getView().findViewById(R.id.tv_destLocal);
        profare = getView().findViewById(R.id.tv_profare);
        pro_name = getView().findViewById(R.id.pro_name);
        pro_phone = getView().findViewById(R.id.pro_phone);
*/

        return linearLayout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
    }

    class BtnClick implements View.OnClickListener{
        @Override
        public void onClick(View view) {

            startLocal.setText(sl +  ss);
            destLocal.setText(dl + ds);
            profare.setText(String.valueOf(fare));

            //사용자 정보 받아오기
            HashMap<String, String> params = new HashMap<>();
            params.put("usernum", String.valueOf(usernum));

            String param = new CommonUtils().makeParams(params);      //쿼리스트링 한줄 리턴

            try {
                url = new URL("http://192.168.11.5:8888/wcar/selectUser");
            } catch (MalformedURLException e) {      //url 형식이 아니다
                Toast.makeText(getContext(), "잘못된 URL입니다.", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(), "" + e.toString(), Toast.LENGTH_SHORT).show();
            } finally {
                if (con != null) {
                    con.disconnect();       //연결 끊어라
                }
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

    public void bt_request(View view){

    }

}
