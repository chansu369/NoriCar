package kpu.noricar;

import java.util.HashMap;

/**
 * Created by chansu on 2018-03-08.
 */

public class CommonUtils {

    //Hashmap을 사용해서 쿼리스트링으로 만들어줄 함수
    public String makeParams(HashMap<String,String> params){
        StringBuffer sbParam = new StringBuffer();
        String key = "";
        String value = "";
        boolean isAnd = false;      //변수 값이 몇개인지 판단

        for(java.util.Map.Entry<String,String> elem : params.entrySet()){
            key = elem.getKey();
            value = elem.getValue();

            if(isAnd){
                sbParam.append("&");
            }

            //concat
            sbParam.append(key).append("=").append(value);

            if(!isAnd){
                if(params.size() >= 2){
                    isAnd = true;
                }
            }
        }

        return sbParam.toString();
    }
}
