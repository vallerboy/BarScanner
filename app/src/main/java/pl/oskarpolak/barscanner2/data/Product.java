package pl.oskarpolak.barscanner2.data;

import android.os.AsyncTask;
import android.util.Log;



import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import pl.oskarpolak.barscanner2.mysql.MysqlLocalConnector;

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


     private String defStawki;

     private int stanMagazynowy;

     private int nextListId;

     private int count ;
     private  boolean hasPartion;
     private int  stanMag;

     private List<String> doce;
     private List<String> dostawy;
     private Set<String> zasoby;
     private List<String> docHandlowe;

     private List<Partia> partie;
     private Partia wybranaPartia;
     private String wybranyZasob;

    public Product(String name, String id, String stawka) {
        this.name = name;
        this.desctription = "";
        // TODO partia
         stanMag = 0;
        this.count = 0;
        this.masa = masa;
        this.partion = "";
        this.id = id;
        doce = new ArrayList<String>();
        dostawy  = new ArrayList<String>();
        zasoby = new LinkedHashSet<>();
         defStawki = stawka;
        docHandlowe = new ArrayList<String>();
        partie = new ArrayList<>();
        roznica = 0;

    }

    public String getWybranyZasob() {
        return wybranyZasob;
    }

    public void setWybranyZasob(String wybranyZasob) {
        this.wybranyZasob = wybranyZasob;
    }

    public void addDocHandlowy(String s){
        docHandlowe.add(s);
    }
    public void addPartia(Partia p){
        partie.add(p);
    }

    public Partia getWybranaPartia() {
        return wybranaPartia;
    }

    public void setWybranaPartia(Partia wybranaPartia) {
        this.wybranaPartia = wybranaPartia;
    }

    public int roznica;

    public int getRoznica() {
        return roznica;
    }

    public void setRoznica(int roznica) {
        this.roznica = roznica;
    }

    public boolean isPartiaAdded(String partia){
        for(Partia p : partie){
            if(p.getPartia().equals(partia)){
                return true;
            }
        }
        return false;
    }



    public Partia getPariaByName(String name){
        for(Partia p: partie) {
            if (p.getPartia().equals(name)) {
                return p;
            }
        }
            return null;
    }

    public List<Partia> getPartie(){
        return partie;
    }
    public List<String> getDoceHandlowe(){
        return  docHandlowe;
    }
    public String getDefStawki() {
        return defStawki;
    }

    public void addZasob(String s) {
        zasoby.add(s);
    }
    public Set<String> getZasoby() {
        return zasoby;
    }

    public void setDefStawki(String defStawki) {
        this.defStawki = defStawki;
    }

    public void addDoc(String doc){
        Log.e("ZASOBY", "DODAJE DOCA " + doc + " DO PARTII " + getPartion());
        if(!doce.contains(doc)) {
            doce.add(doc);
        }

    }
    String wybranaDostawa;

    public String getWybranaDostawa() {
        return wybranaDostawa;
    }

    public void setWybranaDostawa(String wybranaDostawa) {
        this.wybranaDostawa = wybranaDostawa;
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

    public int addCount(int howmuch) {
        setRoznica(count);
        count += howmuch;
        return count;
    }
    public int remCount(int howmuch){
        setRoznica(count);
        if(count - howmuch > 0) {
            count -= howmuch;
        }else {
            count = 0;
        }
        return count;
    }
    public int getCount(){
        return count;
    }
    public void setCount(int count){
        roznica = this.count;
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;

        if (stanMagazynowy != product.stanMagazynowy) return false;
        if (hasPartion != product.hasPartion) return false;
        if (stanMag != product.stanMag) return false;
        if (name != null ? !name.equals(product.name) : product.name != null) return false;
        if (partion != null ? !partion.equals(product.partion) : product.partion != null)
            return false;
        if (code != null ? !code.equals(product.code) : product.code != null) return false;
        if (id != null ? !id.equals(product.id) : product.id != null) return false;
        if (defStawki != null ? !defStawki.equals(product.defStawki) : product.defStawki != null)
            return false;
        if (wybranaDostawa != null ? !wybranaDostawa.equals(product.wybranaDostawa) : product.wybranaDostawa != null)
            return false;
        return doc != null ? doc.equals(product.doc) : product.doc == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (partion != null ? partion.hashCode() : 0);
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (defStawki != null ? defStawki.hashCode() : 0);
        result = 31 * result + stanMagazynowy;
        result = 31 * result + (hasPartion ? 1 : 0);
        result = 31 * result + stanMag;
        result = 31 * result + (wybranaDostawa != null ? wybranaDostawa.hashCode() : 0);
        result = 31 * result + (doc != null ? doc.hashCode() : 0);
        return result;
    }


}
