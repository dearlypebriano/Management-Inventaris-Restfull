package com.management.ManagementInventaris;

public class Isasa {
    public static void main(String[] args) {
        String teks = "";

        // Menghilangkan koma, titik, dan spasi
        String teksTanpaSpasiDanKoma = teks.replaceAll("[,\\. ]", "");

        // Menghitung jumlah karakter
        int jumlahKarakter = teksTanpaSpasiDanKoma.length();

        System.out.println("Jumlah karakter (tanpa spasi dan koma): " + jumlahKarakter);
    }
}
