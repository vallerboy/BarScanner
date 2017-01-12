package pl.oskarpolak.barscanner2.data;

import java.util.List;
import java.util.UUID;

/**
 * Created by OskarPraca on 2016-11-13.
 */

public class Document {

     /*

       kierunekMagazynu = -1
       definicja = ?

      */
    private String id;
    private String lastFactureId;
    private String lastWZId;

    private String lastGUID;

    private String facturID;

    private String lasNumerPelnyWZ;
    private String lastNumerPelnyFV;
    private String lastNumerPelnyREZFV;

    private String  lastNumerREZFV;

    public String getLastWZId() {
        return lastWZId;
    }

    public String getLastNumerPelnyREZFV() {
        return lastNumerPelnyREZFV;
    }

    public void setLastNumerPelnyREZFV(String lastNumerPelnyREZFV) {
        this.lastNumerPelnyREZFV = lastNumerPelnyREZFV;
    }

    public String getFacturID() {
        // usunieto +1
        int i = Integer.parseInt(facturID);
        return String.valueOf(i);
    }

    public void setFacturID(String facturID) {
        this.facturID = facturID;
    }

    public void setLastWZId(String lastWZId) {
        this.lastWZId = lastWZId;
    }

    public String getLastNumerREZFV() {
        int i = Integer.parseInt(lastNumerREZFV) + 1;
        return String.valueOf(i);
    }

    public void setLastNumerREZFV(String lastNumerREZFV) {
        this.lastNumerREZFV = lastNumerREZFV;
    }

    public String getLastGUID() {
        return UUID.randomUUID().toString();
    }

    public void setLastGUID(String lastGUID) {
        this.lastGUID = lastGUID;
    }

    public String getLasNumerPelnyWZ() {
        return lasNumerPelnyWZ;
    }

    public void setLasNumerPelnyWZ(String lasNumerPelnyWZ) {
        this.lasNumerPelnyWZ = lasNumerPelnyWZ;
    }

    public String getLastNumerPelnyFV() {
        return lastNumerPelnyFV;
    }

    public void setLastNumerPelnyFV(String lastNumerPelnyFV) {
        this.lastNumerPelnyFV = lastNumerPelnyFV;
    }



    private List<Product> products;

    public String getFULLIDREZConformed() {
        String roboczo = "";
        // 6 zer
        String liczba = this.getLastNumerREZFV();
        int iloscZer = 6 - liczba.length();
        for(int i = 0; i < iloscZer; i++){
             roboczo += "0";
        }
        roboczo += liczba;
        return roboczo;
    }

    public String getFULL() {
        String roboczo = "";
        // 4  zer
        String liczba = this.getLastFactureId();
        int iloscZer = 4 - liczba.length();
        for(int i = 0; i < iloscZer; i++){
            roboczo += "0";
        }
        roboczo += liczba;
        return roboczo;


    }


    public String getFULLIDFVConformed() {
        String roboczo = "";
        // 6 zer
        String liczba = this.getLastFactureId();
        int iloscZer = 6 - liczba.length();
        for(int i = 0; i < iloscZer; i++){
            roboczo += "0";
        }
        roboczo += liczba;
        return roboczo;
    }

    public String getLastFactureId() {
        int i = Integer.parseInt(lastFactureId) + 1;
        return String.valueOf(i);
    }

    public void setLastFactureId(String lastFactureId) {
        this.lastFactureId = lastFactureId;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    @Override
    public String toString() {
        return "Document{" +
                "id='" + id + '\'' +
                ", lastFactureId='" + lastFactureId + '\'' +
                ", lastWZId='" + lastWZId + '\'' +
                ", lastGUID='" + lastGUID + '\'' +
                ", lasNumerPelnyWZ='" + lasNumerPelnyWZ + '\'' +
                ", lastNumerPelnyFV='" + lastNumerPelnyFV + '\'' +
                ", lastNumerPelnyREZFV='" + lastNumerPelnyREZFV + '\'' +
                ", lastNumerREZFV='" + lastNumerREZFV + '\'' +
                ", products=" + products +
                '}';
    }
}
