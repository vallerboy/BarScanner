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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import pl.oskarpolak.barscanner2.data.Product;
import pl.oskarpolak.barscanner2.mysql.MysqlLocalConnector;

/**
 * Created by OskarPraca on 2016-12-20.
 */

public class PartiaAdapter extends BaseAdapter {
    Product product;
    Context context;
    private LayoutInflater layoutInflater;
    private MysqlLocalConnector mysql;

    public PartiaAdapter(Product productList, Context con){
        product =  productList;
        context = con;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mysql = MysqlLocalConnector.getInstance(con);
    }
    @Override
    public int getCount() {
        return product.getPartie().size();
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

        name.setText(product.getName());
        partia.setText("Partia: "  + product.getPartie().get(position).getPartia());

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

                if(product.isHasPartion()) {
                    final Product p = product;
                    // terz musze puscic petle bo tych docow jest wiecej niz 1
                    Log.e("debug", "ma partie");

                    int counter = 1;
                    for (String s : p.getPartie().get(pos).getZasoby()) {

                        String sql = "SELECT * FROM Zasoby WHERE ID = " + s;

                        ResultSet rs = statement.executeQuery(sql);
                        try {
                            while (rs.next()) {
                                i += Long.parseLong(rs.getString("IloscValue"));
                            }


                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        counter++;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return i;
        }

        @Override
        protected void onPostExecute(Integer aVoid) {

            product.setStanMag(aVoid);
            view.setText(view.getText()+"\n" + "Ilosc: " + aVoid + " szt/kg");


        }
    }




}
