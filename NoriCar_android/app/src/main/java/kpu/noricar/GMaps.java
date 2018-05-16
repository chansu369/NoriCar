package kpu.noricar;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GMaps extends FragmentActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerClickListener,
        LocationListener{

    private static final LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
    private static final String TAG = "Nori Car";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
    private static final int UPDATE_INTERVAL_MS = 15000;
    private static final int FASTEST_UPDATE_INTERVAL_MS = 15000;

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient = null;

    URL url;
    HttpURLConnection con;
    Marker selectedMarker;
    View marker_root_view;
    TextView tv_marker;
    ArrayList<MarkerItem> marker_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gmaps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        setCustomMarkerView();
        searchProviders();

        //마커 클릭 리스너 연결
        googleMap.setOnMarkerClickListener(this);
        //나침반이 나타나도록 설정
        googleMap.getUiSettings().setCompassEnabled(true);
        // 매끄럽게 이동함
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));


        //  API 23 이상이면 런타임 퍼미션 처리 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 사용권한체크
            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

            if ( hasFineLocationPermission == PackageManager.PERMISSION_DENIED) {
                //사용권한이 없을경우
                //권한 재요청
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            } else {
                //사용권한이 있는경우
                if ( googleApiClient == null) {
                    buildGoogleApiClient();
                }

                if ( ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                {
                    googleMap.setMyLocationEnabled(true);
                }
            }
        } else {

            if ( googleApiClient == null) {
                buildGoogleApiClient();
            }

            googleMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    private void buildGoogleApiClient() {
        if(googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .enableAutoManage(this, this)
                    .build();
            googleApiClient.connect();
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if ( !checkLocationServicesStatus()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("위치 서비스 비활성화");
            builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n" +
                    "위치 설정을 수정하십시오.");
            builder.setCancelable(true);
            builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent callGPSSettingIntent =
                            new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
                }
            });
            builder.setNegativeButton("취소", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            builder.create().show();
        }

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL_MS);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ( ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            }
        } else {

            this.mMap.getUiSettings().setCompassEnabled(true);
            this.mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }

    }

    @Override
    public void onConnectionSuspended(int cause) {
        if ( cause ==  CAUSE_NETWORK_LOST )
            Log.e(TAG, "onConnectionSuspended(): Google Play services " +
                    "connection lost.  Cause: network lost.");
        else if (cause == CAUSE_SERVICE_DISCONNECTED )
            Log.e(TAG,"onConnectionSuspended():  Google Play services " +
                    "connection lost.  Cause: service disconnected");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Location location = new Location("");
        location.setLatitude(DEFAULT_LOCATION.latitude);
        location.setLongitude((DEFAULT_LOCATION.longitude));
/*

        setCurrentLocation(location, "위치정보 가져올 수 없음",
                "위치 퍼미션과 GPS활성 여부 확인");
*/
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "onLocationChanged call..");
        //searchCurrentPlaces();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //각 마커 클릭
        Log.d("real marker : ",marker.getPosition().toString());

        Bundle bundle = new Bundle();
        CameraUpdate center = CameraUpdateFactory.newLatLng(marker.getPosition());
        mMap.animateCamera(center);
        Geocoder geocoder = new Geocoder(this, Locale.KOREAN);

        for(MarkerItem mk : marker_list){
            Log.d("marker for", mk.toString());

            LatLng f_marker = new LatLng(mk.getSt_lat(),mk.getSt_lon());

            Log.d("f_marker : ",f_marker.toString());

            if (f_marker.toString().equals(marker.getPosition().toString())){
                //출발지, 목적지, 요금, (사용자 정보) 다음 프레그먼트에서 불러옴
                try {
                    List<Address> addressList = geocoder.getFromLocation(marker.getPosition().latitude,marker.getPosition().longitude,1);    //출발지
                    List<Address> addressList2 = geocoder.getFromLocation(mk.getEn_lat(),mk.getEn_lon(),1);    //목적지
                    Log.d("Address1 : ", addressList.toString());
                    Log.d("Address2 : ", addressList2.toString());


                    /*bundle.putString("start_local",addressList.get(0).getLocality().toString());
                    bundle.putString("start_sub_local",addressList.get(0).getAdminArea().toString());

                    //목적지
                    bundle.putString("destination_local",addressList2.get(0).getLocality().toString());
                    bundle.putString("destination_sub_local",addressList2.get(0).getAdminArea().toString());

                    //요금,제공자 번호
                    bundle.putInt("fare",mk.getPrice());
                    bundle.putInt("usernum",mk.getUsernum());

                    //2번 fragment 값 전달
                    Fragment f = new Fragment2();
                    f.setArguments(bundle);

                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();

                    ft.add(R.id.pager, f); //add에서 replace로 바꿈
                    ft.commit();
*/

                }catch (IOException e){
                    Log.d("address Exception : ", e.toString());
                }
                //출발지
            }
        }
        changeSelectedMarker(marker);

        return true;
    }

    private void changeSelectedMarker(Marker marker) {
        // 선택했던 마커 되돌리기
        if (selectedMarker != null) {
            addMarker(selectedMarker, false);
            selectedMarker.remove();
        }

        // 선택한 마커 표시
        if (marker != null) {
            selectedMarker = addMarker(marker, true);
            marker.remove();
        }


    }

    public void searchProviders() {
        try {
            url = new URL("http://203.233.199.124:8888/wcar/selectAll");
        } catch (MalformedURLException e) {      //url 형식이 아니다
            Toast.makeText(getApplicationContext(), "잘못된 URL입니다.", Toast.LENGTH_SHORT).show();
        }

        //본격적으로 연결시작
        try {
            con = (HttpURLConnection) url.openConnection(); //내가 연결할 주소와 통신선을 연다.

            if (con != null) {
                con.setConnectTimeout(10000);    //연결제한시간. 0은 무한대기.
                con.setUseCaches(false);        //캐쉬 사용여부
                //Ajax형태로 보내기 때문에 반드시 있어야한다. (Spring에서도 @Responsebody 가 필요하다)
                con.setRequestProperty("Accept-Charset", "UTF-8"); // Accept-Charset 설정.
                con.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;charset=UTF-8");

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


    public void jsonParse(String page) {
        JSONArray jarray = null;
        JSONObject item = null;
        marker_list = new ArrayList<>();
        try {
            jarray = new JSONArray(page);


            for (int i = 0; i < jarray.length(); i++) {
                item = jarray.getJSONObject(i);
                //여기서 item별로 마커찍기
                marker_list.add(new MarkerItem(item.getInt("usernum"),
                        item.getDouble("startx"),
                        item.getDouble("starty"),
                        item.getDouble("destx"),
                        item.getDouble("desty"),
                        item.getInt("pay")));
                /*sb2.append("제공번호 :");
                sb2.append(item.getInt("pronum"));
                sb2.append("제공자 번호 :");
                sb2.append(item.getInt("usernum"));
                sb2.append("요금 :");
                sb2.append(item.getInt("pay"));
                sb2.append("출발지 위도 :");
                sb2.append(item.getDouble("startx"));
                sb2.append("출발지 경도 :");
                sb2.append(item.getDouble("starty"));
                sb2.append("도착지 위도 :");
                sb2.append(item.getDouble("destx"));
                sb2.append("도착지 경도 :");
                sb2.append(item.getDouble("desty"));
                sb2.append("요청flag :");
                sb2.append(item.getInt("request"));
                sb2.append("\n");
                */
                Log.d("Provide info",""+item);
            }
            for(MarkerItem mp : marker_list){
                addMarker(mp,false);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setCustomMarkerView() {

        marker_root_view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_marker, null);
        tv_marker = marker_root_view.findViewById(R.id.tv_marker);
    }

    private Marker addMarker(MarkerItem markerItem, boolean isSelectedMarker) {

        Log.d("markerItem : ", markerItem.toString());

        LatLng position = new LatLng(markerItem.getSt_lat(), markerItem.getSt_lon());
        int price = markerItem.getPrice();
        String formatted = String.valueOf(price + "원");

        tv_marker.setText(formatted);

        if (isSelectedMarker) {
            tv_marker.setBackgroundResource(R.drawable.marker_2);
            tv_marker.setTextColor(Color.WHITE);
        } else {
            tv_marker.setBackgroundResource(R.drawable.marker_1);
            tv_marker.setTextColor(Color.BLACK);
        }

        MarkerOptions markerOptions1 = new MarkerOptions();
        markerOptions1.title(Integer.toString(price));
        markerOptions1.position(position);
        markerOptions1.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(getApplicationContext(), marker_root_view)));


        return mMap.addMarker(markerOptions1);

    }


    private Marker addMarker(Marker marker, boolean isSelectedMarker) {
        double lat = marker.getPosition().latitude;
        double lon = marker.getPosition().longitude;
        int price = Integer.parseInt(marker.getTitle());
        MarkerItem temp = new MarkerItem(lat, lon, price);
        return addMarker(temp, isSelectedMarker);
    }

    // View를 Bitmap으로 변환
    private Bitmap createDrawableFromView(Context context, View view) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

}
