package pl.oskarpolak.barscanner2;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import pl.oskarpolak.barscanner2.data.Partia;
import pl.oskarpolak.barscanner2.data.Product;
import pl.oskarpolak.barscanner2.mysql.MysqlLocalConnector;

/**
 * Created by OskarPraca on 2016-12-20.
 */

public class DostawaAdapter extends BaseAdapter {

    Product p;
    Context context;
    private LayoutInflater layoutInflater;
    private MysqlLocalConnector mysql;

    public DostawaAdapter(Product productList, Context con){
        p =  productList;
        context = con;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mysql = MysqlLocalConnector.getInstance(con);
    }
    @Override
    public int getCount() {
        if(p.isHasPartion()) {
            return p.getWybranaPartia().getZasoby().size();
        }else{
            return p.getPartie().size();
        }
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

        view = layoutInflater.inflate(R.layout.row_dostawa, parent, false);


        TextView  name = (TextView) view.findViewById(R.id.testNameProduct1);
        TextView  partia = (TextView) view.findViewById(R.id.textPartia1);
        TextView  sciagnieto = (TextView) view.findViewById(R.id.textViewSciagnietoR);



        name.setText("Dostawa " + position);
        partia.setText("Ilość: ");

        new AsyncSciagnieto(p, position, sciagnieto).execute();
        new AsyncGetZasoby(position, partia).execute();

        return view;
    }




    private class AsyncSciagnieto extends AsyncTask<Void, Integer, Integer>{

        Product product;
        int position;
        TextView view;
        public AsyncSciagnieto(Product p, int pos, TextView sciagnieto){
            product = p;
            position = pos;
            view = sciagnieto;
        }
        @Override
        protected Integer doInBackground(Void... params) {

            return    getIloscSciagnieta(product, position);
        }

        @Override
        protected void onPostExecute(Integer aVoid) {
            Log.e("nowysystem", "Ilosc w dostawie: " + aVoid);
            view.setText("R: " + aVoid);
        }
    }




    private int getIloscSciagnieta(Product p1, int pos) {
        int ilosc = 0;
        try {

            Statement statement5 = MysqlLocalConnector.getInstance(context).getConnection().createStatement();
            String sql1;
            if(p1.isHasPartion()){
                sql1 = "SELECT * FROM TowaryCloud WHERE dostawa = " + p1.getWybranaPartia().getZasoby().get(pos);
            }else{
                sql1 = "SELECT * FROM TowaryCloud WHERE dostawa = " + p1.getPartie().get(pos).getZasoby().get(0);
            }
            final ResultSet rs2 = statement5.executeQuery(sql1);

            while (rs2.next()) {
                ilosc += Integer.valueOf(rs2.getString("iloscSciagnieta"));
                ilosc -= p.getCount();
            }
        }catch(SQLException e) {
            e.printStackTrace();
        }

//        for(Product p : NewDocumentActivity.getProducts()) {
//            if(p1.isHasPartion()){
//               if( p.getWybranyZasob().equals(p1.getWybranaPartia().getZasoby().get(pos)) && p.getWybranaPartia().getPartia().equals(p1.getWybranaPartia().getPartia())){
//                   ilosc += p.getCount();
//                }
//            }else{
//                if( p.getWybranaPartia().getPozcyje().get(0).equals(p1.getPartie().get(pos).getPozcyje().get(0)) && p.getId().equals(p1.getId())){
//                    ilosc += p.getCount();
//                }
//            }
//        }

        return ilosc;
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



               if(p.isHasPartion()) {

                    // terz musze puscic petle bo tych docow jest wiecej niz 1
                    Log.e("debug", "ma partie");
                    List<String> doce = new ArrayList<String>();

                    int counter = 1;
                    if(p.getWybranaPartia().getZasoby().size()  > 1) {
                            String sql = "SELECT * FROM Zasoby WHERE ID = " + p.getWybranaPartia().getZasoby().get(pos);
                            ResultSet rs = statement.executeQuery(sql);
                            try {
                                while (rs.next()) {
                                    i += Long.parseLong(rs.getString("IloscValue"));
                                    counter ++;
                                }


                            } catch (SQLException e) {
                                e.printStackTrace();
                            }

                    }else {
                        for (String s1 : p.getWybranaPartia().getZasoby()) {
                            String sql = "SELECT * FROM Zasoby WHERE ID = " + s1;


                            ResultSet rs = statement.executeQuery(sql);
                            try {
                                while (rs.next()) {
                                    i += Long.parseLong(rs.getString("IloscValue"));
                                    counter++;
                                }


                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }else {

                       String sql = "SELECT * FROM Zasoby WHERE ID = " + p.getPartie().get(pos).getZasoby().get(0);

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

            p.setStanMag(aVoid);
            view.setText(view.getText()+"\n" + "Ilosc: " + aVoid + "");


        }
    }


    private class AsyncGetZasoby1 extends AsyncTask<String ,Void, Integer> {
        String name  ;
        TextView view;


        int pos;
        public AsyncGetZasoby1(int pos, TextView text){
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



                //   if(p.isHasPartion()) {

                // terz musze puscic petle bo tych docow jest wiecej niz 1
                Log.e("debug", "ma partie");
                List<String> doce = new ArrayList<String>();


                int counter = 1;
                for(String s1 : p.getZasoby()) {
                    String sql = "SELECT * FROM Zasoby WHERE ID = " + s1;


                    ResultSet rs = statement.executeQuery(sql);
                    try {
                        while (rs.next()) {
                            Log.e("debug", "ilosc : " + i);
                            i += Long.parseLong(rs.getString("IloscValue"));

                            Log.e("debug", "" + rs.getString("IloscValue"));
                            counter ++;
                        }


                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                //  }else {
//                    Log.e("debug", "nie ma");
//                    String sql = "SELECT * FROM Zasoby WHERE Towar=" + p.getId() + " LIMIT 1";
//
//                    try {
//                        ResultSet rs = statement.executeQuery(sql);
//                        while (rs.next()) {
//                            i += Long.parseLong(rs.getString("IloscValue"));
//                            Log.e("debug", "" + i);
//                        }
//                        // nie ma partii
//                    } catch (SQLException e) {
//                        e.printStackTrace();
//                    }

                //  }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return i;
        }

        @Override
        protected void onPostExecute(Integer aVoid) {

            p.setStanMag(aVoid);
            view.setText(view.getText()+"\n" + "Ilosc: " + aVoid + "");


        }
    }


}
