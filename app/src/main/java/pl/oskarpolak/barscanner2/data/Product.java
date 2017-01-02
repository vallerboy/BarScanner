package pl.oskarpolak.barscanner2.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by OskarPraca on 2016-11-13.
 */

public class Product {

     private String name;
     private String partion ;
     private String desctription;
     private String code;
     private String id;
     private String masa;

     private int stanMagazynowy;

     private int nextListId;

     private int count ;
     private  boolean hasPartion;
     private int  stanMag;

     private List<String> doce;
     private List<String> dostawy;

    public Product(String name, String id) {
        this.name = name;
        this.desctription = "";
        // TODO partia
         stanMag = 0;
        this.count = 0;
        this.masa = masa;
        this.id = id;
        doce = new ArrayList<String>();
        dostawy  = new ArrayList<String>();
    }


    public void addDoc(String doc){
        if(!doce.contains(doc)){
            doce.add(doc);
        }
    }
    public List<String> getDoce(){
        return doce;
    }

    public void addDostawa(String doc){

            dostawy.add(doc);

    }
    public List<String> getDostawy(){
        return dostawy;
    }


    public boolean isHasPartion() {
        return hasPartion;
    }

    public void setHasPartion(boolean hasPartion) {
        this.hasPartion = hasPartion;
    }

    String doc;

    public String getDoc() {
        return doc;
    }

    public void setDoc(String doc) {
        this.doc = doc;
    }

    public int getStanMag() {
        return stanMag;
    }

    public void setStanMag(int stanMag) {
        this.stanMag = stanMag;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getMasaCalkowita(){
        return Double.valueOf(masa) * count;
    }
    public int getNextListId() {
        return nextListId;
    }

    public void setNextListId(int nextListId) {
        this.nextListId = nextListId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPartion(String partion) {
        this.partion = partion;
    }

    public void setDesctription(String desctription) {
        this.desctription = desctription;
    }

    public String getName() {
        return name;
    }

    public String getPartion() {
        return partion;
    }

    public String getDesctription() {
        return desctription;
    }

    public void addCount(int howmuch) {
        count += howmuch;
    }
    public void remCount(int howmuch){
        if(count - howmuch > 0) {
            count -= howmuch;
        }else {
            count = 0;
        }
    }
    public int getCount(){
        return count;
    }
    public void setCount(int count){
        this.count = count;
    }
}
