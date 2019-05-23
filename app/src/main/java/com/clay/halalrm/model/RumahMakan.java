package com.clay.halalrm.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.orm.SugarRecord;

import java.sql.Blob;
import java.util.List;

public class RumahMakan extends SugarRecord {

    private String  formatted_address;
    private String  name;
    private String  foto1,foto2,foto3;
    protected byte[] image1;
    protected byte[] image2;

    public byte[] getImage2() {
        return image2;
    }

    public void setImage2(byte[] image2) {
        this.image2 = image2;
    }

    public byte[] getImage3() {
        return image3;
    }

    public void setImage3(byte[] image3) {
        this.image3 = image3;
    }

    protected byte[] image3;

    public byte[] getImage1() {
        return image1;
    }

    public void setImage1(byte[] image1) {
        this.image1 = image1;
    }

    public Bitmap gambar1(){
       return BitmapFactory.decodeByteArray(getImage1(), 0, getImage1().length);
    };
    public Bitmap gambar2(){
       return BitmapFactory.decodeByteArray(getImage2(), 0, getImage2().length);
    };
    public Bitmap gambar3(){
       return BitmapFactory.decodeByteArray(getImage3(), 0, getImage3().length);
    };
    private String  place_id;
    private String  compound_code;
    private String  global_code;
    private Double  rating;
    private String  reference;
    private int     user_ratings_total;
    private Double lat;
    private Double lng;

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public RumahMakan() {
    }

    @Override
    public String toString() {
        return "RumahMakan{" +
                "id='" + getId() + '\'' +
                "name='" + name + '\'' +
                ", global_code='" + global_code + '\'' +
                '}';
    }

    public String getFormatted_address() {
        return formatted_address;
    }

    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFoto1() {
        return foto1;
    }

    public void setFoto1(String foto1) {
        this.foto1 = foto1;
    }

    public String getFoto2() {
        return foto2;
    }

    public void setFoto2(String foto2) {
        this.foto2 = foto2;
    }

    public String getFoto3() {
        return foto3;
    }

    public void setFoto3(String foto3) {
        this.foto3 = foto3;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getCompound_code() {
        return compound_code;
    }

    public void setCompound_code(String compound_code) {
        this.compound_code = compound_code;
    }

    public String getGlobal_code() {
        return global_code;
    }

    public void setGlobal_code(String global_code) {
        this.global_code = global_code;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public int getUser_ratings_total() {
        return user_ratings_total;
    }

    public void setUser_ratings_total(int user_ratings_total) {
        this.user_ratings_total = user_ratings_total;
    }

}
