package pl.oskarpolak.barscanner2;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import pl.oskarpolak.barscanner2.data.Product;
import pl.oskarpolak.barscanner2.mysql.MysqlLocalConnector;

/**
 * Created by OskarPraca on 2016-12-20.
 */

public class PartiaAdapter extends BaseAdapter {

    ArrayList<Product> products;
    Context context;
    private LayoutInflater layoutInflater;
    private MysqlLocalConnector mysql;

    public PartiaAdapter(List<Product> productList, Context con){
        products = (ArrayList<Product>) productList;
        context = con;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mysql = MysqlLocalConnector.getInstance().getInstance();
    }
    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        view = layoutInflater.inflate(R.layout.row_partia, parent, false);


        TextView  name = (TextView) view.findViewById(R.id.testNameProduct);
        TextView  partia = (TextView) view.findViewById(R.id.textPartia);

        name.setText(products.get(position).getName());
        partia.setText("Partia: "  + products.get(position).getPartion());

        new AsyncGetZasoby(position, partia).execute();

        return view;
    }

    private class AsyncGetZasoby extends AsyncTask<String ,Void, Integer> {
        String name  ;
        TextView view;


        int pos;
        public AsyncGetZasoby(int pos, TextView text){
            this.pos = pos;
            view = text;

        }
        @Override
        protected Integer doInBackground(String ... params) {
            Statement statement = null;
            Statement statement1 = null;
            int i = 0;
            try {
                statement = mysql.getConnection().createStatement();
                statement1 = mysql.getConnection().createStatement();



                if(products.get(pos).isHasPartion()) {
                    final Product p = products.get(pos);
                    // terz musze puscic petle bo tych docow jest wiecej niz 1
                    Log.e("debug", "ma partie");
                    List<String> doce = new ArrayList<String>();
                    for(String s : p.getDoce()) {
                        ResultSet rs1 = statement1.executeQuery("SELECT * FROM PozycjeDokHan WHERE ID=" + s);

                        while (rs1.next()) {
                            doce.add(rs1.getString("Dokument"));
                        }
                    }
                    int counter = 1;
                    for(String s : doce) {
                        String sql = "SELECT * FROM Zasoby WHERE Towar=" + products.get(pos).getId() + " AND " +
                                "PartiaDokument=" + s + ";";


                        ResultSet rs = statement.executeQuery(sql);
                        try {
                            while (rs.next()) {
                                i += Long.parseLong(rs.getString("IloscValue"));

                                Log.e("debug", "" + rs.getString("IloscValue"));
                                counter ++;
                            }


                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }else {
                    Log.e("debug", "nie ma");
                    String sql = "SELECT * FROM Zasoby WHERE Towar=" + products.get(pos).getId() + " LIMIT 1";

                    try {
                        ResultSet rs = statement.executeQuery(sql);
                        while (rs.next()) {
                            i += Long.parseLong(rs.getString("IloscValue"));
                            Log.e("debug", "" + i);
                        }
                        // nie ma partii
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return i;
        }

        @Override
        protected void onPostExecute(Integer aVoid) {

            products.get(pos).setStanMag(aVoid);
            view.setText(view.getText()+"\n" + "Ilosc: " + aVoid + " szt/kg");


        }
    }




}
