package kpu.noricar;

/**
 * Created by chansu on 2018-03-09.
 */

public class MarkerItem {

    int usernum;
    double st_lat;
    double st_lon;
    double en_lat;
    double en_lon;
    int price;

    public MarkerItem(){
        super();
    }

    public MarkerItem(int usernum,double st_lat,double st_lon,int price){
        this.usernum = usernum;
        this.st_lat = st_lat;
        this.st_lon = st_lon;
        this.price = price;
    }
    public MarkerItem(double st_lat,double st_lon,int price){
        this.st_lat = st_lat;
        this.st_lon = st_lon;
        this.price = price;
    }
    public MarkerItem(int usernum,double en_lat,double en_lon){
        this.usernum = usernum;
        this.en_lat = en_lat;
        this.en_lon = en_lon;
    }

    public MarkerItem(int usernum,double st_lat,double st_lon,double en_lat,double en_lon,int price){
        this.usernum = usernum;
        this.st_lat = st_lat;
        this.st_lon = st_lon;
        this.en_lat = en_lat;
        this.en_lon = en_lon;
        this.price = price;
    }
    public MarkerItem(int usernum,double st_lat,double st_lon,double en_lat,double en_lon){
        this.usernum = usernum;
        this.st_lat = st_lat;
        this.st_lon = st_lon;
        this.en_lat = en_lat;
        this.en_lon = en_lon;
    }

    public int getUsernum() {
        return usernum;
    }

    public void setUsernum(int usernum) {
        this.usernum = usernum;
    }

    public double getSt_lat() {
        return st_lat;
    }

    public void setSt_lat(double st_lat) {
        this.st_lat = st_lat;
    }

    public double getSt_lon() {
        return st_lon;
    }

    public void setSt_lon(double st_lon) {
        this.st_lon = st_lon;
    }

    public double getEn_lat() {
        return en_lat;
    }

    public void setEn_lat(double en_lat) {
        this.en_lat = en_lat;
    }

    public double getEn_lon() {
        return en_lon;
    }

    public void setEn_lon(double en_lon) {
        this.en_lon = en_lon;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "MarkerItem{" +
                "usernum=" + usernum +
                ", st_lat=" + st_lat +
                ", st_lon=" + st_lon +
                ", en_lat=" + en_lat +
                ", en_lon=" + en_lon +
                ", price=" + price +
                '}';
    }
}
