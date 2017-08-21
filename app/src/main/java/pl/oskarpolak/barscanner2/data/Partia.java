package pl.oskarpolak.barscanner2.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by OskarPraca on 2016-12-29.
 */

public class Partia {

    String partia;
    List<String> pozycjeDoc;
    List<String> dokHandlowe;
    List<String> zasoby;
    String id;


    public Partia(String partia) {
        this.partia = partia;
        pozycjeDoc = new ArrayList<>();
        dokHandlowe = new ArrayList<>();
        zasoby = new ArrayList<>();
        id = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Partia() {
        partia = null;
        pozycjeDoc = new ArrayList<>();
        dokHandlowe = new ArrayList<>();
        zasoby = new ArrayList<>();
    }

    public boolean isPartiaExist() {
        return partia == null ? true : false;
    }

    public String getPartia() {
        return partia;
    }

    public void setPartia(String partia) {
        this.partia = partia;
    }

    public void addPozycja(String s ){
        if(!pozycjeDoc.contains(s))
        pozycjeDoc.add(s);
    }

    public List<String> getPozcyje(){
        return pozycjeDoc;
    }

    public void addDokHandlowy(String s ){
        if(!dokHandlowe.contains(s))
        dokHandlowe.add(s);
    }

    public List<String> getDokHandlowe(){
        return dokHandlowe;
    }

    public void addZasob(String s){
        if(!zasoby.contains(s))
        zasoby.add(s);
    }
    public List<String> getZasoby() {
        return zasoby;
    }

    @Override
    public String toString() {
        return "Partia{" +
                "partia='" + partia + '\'' +
                ", pozycjeDoc=" + pozycjeDoc.toString() +
                ", dokHandlowe=" + dokHandlowe.toString() +
                ", zasoby=" + zasoby.toString() +
                "ID: " + id +
                '}';
    }
}
