package com.clay.halalrm.model;

import com.orm.SugarRecord;
import com.orm.dsl.Column;

public class DaftarMenu extends SugarRecord {
    public DaftarMenu() {
    }
    private String Harga;
    private String Nama;
    private long RumahMakanID;

    public DaftarMenu(String harga, String nama, long rumahMakanID) {
        Harga = harga;
        Nama = nama;
        RumahMakanID = rumahMakanID;
    }

    public String getHarga() {
        return Harga;
    }

    public void setHarga(String harga) {
        Harga = harga;
    }

    public String getNama() {
        return Nama;
    }

    public void setNama(String nama) {
        Nama = nama;
    }

    public long getRumahMakanID() {
        return RumahMakanID;
    }

    public void setRumahMakanID(long rumahMakanID) {
        RumahMakanID = rumahMakanID;
    }

    @Override
    public String toString() {
        return "DaftarMenu{" +
                "Harga='" + Harga + '\'' +
                ", Nama='" + Nama + '\'' +
                ", RumahMakanID=" + RumahMakanID +
                '}';
    }
}
