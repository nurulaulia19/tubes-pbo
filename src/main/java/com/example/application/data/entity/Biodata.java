package com.example.application.data.entity;

import java.time.LocalDate;
import javax.persistence.Entity;

@Entity
public class Biodata extends AbstractEntity {

    private String nik;
    private String nama;
    private String puskemas;
    private LocalDate tanggalLahir;
    private String pendidikan;
    private String noHp;
    private String alamat;
    private boolean important;

    public String getNik() {
        return nik;
    }
    public void setNik(String nik) {
        this.nik = nik;
    }
    public String getNama() {
        return nama;
    }
    public void setNama(String nama) {
        this.nama = nama;
    }
    public String getPuskemas() {
        return puskemas;
    }
    public void setPuskemas(String puskemas) {
        this.puskemas = puskemas;
    }
    public LocalDate getTanggalLahir() {
        return tanggalLahir;
    }
    public void setTanggalLahir(LocalDate tanggalLahir) {
        this.tanggalLahir = tanggalLahir;
    }
    public String getPendidikan() {
        return pendidikan;
    }
    public void setPendidikan(String pendidikan) {
        this.pendidikan = pendidikan;
    }
    public String getNoHp() {
        return noHp;
    }
    public void setNoHp(String noHp) {
        this.noHp = noHp;
    }
    public String getAlamat() {
        return alamat;
    }
    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }
    public boolean isImportant() {
        return important;
    }
    public void setImportant(boolean important) {
        this.important = important;
    }

}
