import android.util.Log;

public class LogManager
{
    public static final boolean _DEBUG = true;
    private static final String _TAG = "TMAP_OPEN_API";

    public static void printLog(String text)
    {
    	if ( _DEBUG )
    	{
    		Log.d(_TAG, text);
            //디버깅 목적으로 남기는 로그
    	}
    }


    public static void printError(String text)
    {	
    	if(_DEBUG)
    		Log.e(_TAG, "**ERROR** : " + text);
        //가장 심각한 발생이 생겼을때 작성하는 로그.
    }
}

/*로그는 각 5개가 있는데 e w i d v 가 있다.
순서대로 error warning inform debug verbose다.*/