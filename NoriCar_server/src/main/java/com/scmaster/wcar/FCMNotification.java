package com.scmaster.wcar;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.scmaster.wcar.dao.WcarDAO;
import com.scmaster.wcar.provide.Provide;
import com.scmaster.wcar.user.User;

import net.sf.json.JSONObject;

@Controller
public class FCMNotification {
	 // Method to send Notifications from server to client end.
   
	@Autowired
	static
	WcarDAO dao;
	
	public final static String AUTH_KEY_FCM = "AAAAMbGL1iI:APA91bGFVbBR_H2HPtDO5Go3386YLaBX2Pizi6ROViBEzLxY5AiZFAVQ4AwW7scB2LEP73zsCaLfzHiGp4RhhqWhZon8Cd6LCs-np71oPq4SWnLO4Po8adDrYGdSBUDv6oH3pdjodmgv";
    public final static String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";

   
    @ResponseBody
    @RequestMapping(value="callPush",method=RequestMethod.POST)
    public static void pushFCMNotification(int usernum,double startx,double starty,double destx,double desty) throws Exception {

    	Provide provide = new Provide();
    	provide.setUsernum(usernum);
    	provide.setStartx(startx);
    	provide.setStarty(starty);
    	provide.setDestx(destx);
    	provide.setDesty(desty);
    	
    	System.out.println(provide);
    	Provide r_provide = dao.selectPush(provide);
    	
    	User user = new User();
    	user.setUsernum(usernum);
    	
    	User r_user = dao.selectUser(user);
    	
    	if(r_provide.getRequest() == 1){
        	
            String authKey = AUTH_KEY_FCM; // You FCM AUTH key
            String FMCurl = API_URL_FCM;

    		URL url = new URL(FMCurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "key=" + authKey);
            conn.setRequestProperty("Content-Type", "application/json");

            JSONObject data = new JSONObject();
            data.put("to", r_user.getToken().trim());
            JSONObject info = new JSONObject();
            info.put("title", "[ Push : Nori Car Request Adapting!! ]"); // Notification title
            info.put("body", "[ Request Adapt ]"); // Notification body
            data.put("data", info);

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data.toString());
            wr.flush();
            wr.close();

            int responseCode = conn.getResponseCode();
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
    	}

    }

    @SuppressWarnings("static-access")
    public static void main(String[] args) throws Exception {
        FCMNotification obj = new FCMNotification();
        obj.pushFCMNotification(0, 0, 0, 0, 0);
    }
}