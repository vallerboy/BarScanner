package pl.oskarpolak.barscanner2.data;

/**
 * Created by OskarPraca on 2016-12-29.
 */

public class Partia {

    String partia;
    Product product;


    public Partia(String partia, Product product) {
        this.partia = partia;
        this.product = product;
    }

    public Partia() { }

    public String getPartia() {
        return partia;
    }

    public void setPartia(String partia) {
        this.partia = partia;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
