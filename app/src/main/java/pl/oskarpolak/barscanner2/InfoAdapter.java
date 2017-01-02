package pl.oskarpolak.barscanner2;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.oskarpolak.barscanner2.data.Product;
import pl.oskarpolak.barscanner2.mysql.MysqlLocalConnector;

/**
 * Created by OskarPraca on 2016-12-20.
 */

public class InfoAdapter extends BaseAdapter {

    ArrayList<Product> products;
    Context context;
    private LayoutInflater layoutInflater;
    private MysqlLocalConnector mysql;

    public InfoAdapter(List<Product> productList, Context con){
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



        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = layoutInflater.inflate(R.layout.custom_row_info, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
            TextView  text = holder.textView;
            new AsyncGetZasoby(position, text, holder.dostawy).execute();
        }

       holder.textID.setText((position+1)+".");
        holder.partia.setText("" + products.get(position).getPartion());
        return view;
    }



    public void refresh(){
        new sprawdzStanyRefre().execute();

    }

    private class AsyncGetZasoby extends AsyncTask<String ,Void, Integer> {
        String name  ;
        TextView view;
        TextView dostawy;

        int pos;
       public AsyncGetZasoby(int pos, TextView text, TextView dostawy){
           this.pos = pos;
           view = text;
           this.dostawy = dostawy;
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
                       p.addDostawa("Dostawa " + counter + ": " + rs.getString("IloscValue"));
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
            Log.e("debug", "Ustawiam ilość sztuk dla: " + pos + " (" + aVoid + ")");
            products.get(pos).setStanMag(aVoid);

            view.setText("" + products.get(pos).getName() + " : " + aVoid + " szt/kg");
            String dostawy = "Dostawy:\n";
            for(String s : products.get(pos).getDostawy()){
                dostawy += s+"\n";
            }
            this.dostawy.setText(dostawy);
            products.get(pos).getDostawy().clear();

        }
    }

    private class sprawdzStanyRefre extends AsyncTask<String ,Integer, Integer> {


        @Override
        protected Integer doInBackground(String ... params) {
            Statement statement = null;
            Statement statement1 = null;
            int i = 0;
            try {
                statement = mysql.getConnection().createStatement();
                statement1 = mysql.getConnection().createStatement();


                int counter1 = 1;
                for (Product p : products) {
                    if (p.isHasPartion()) {

                        // terz musze puscic petle bo tych docow jest wiecej niz 1
                        Log.e("debug", "ma partie");
                        List<String> doce = new ArrayList<String>();
                        for (String s : p.getDoce()) {
                            ResultSet rs1 = statement1.executeQuery("SELECT * FROM PozycjeDokHan WHERE ID=" + s);

                            while (rs1.next()) {
                                doce.add(rs1.getString("Dokument"));
                            }
                        }
                        int counter = 1;
                        for (String s : doce) {
                            String sql = "SELECT * FROM Zasoby WHERE Towar=" + p.getId() + " AND " +
                                    "PartiaDokument=" + s + ";";


                            ResultSet rs = statement.executeQuery(sql);
                            try {
                                while (rs.next()) {
                                    i += Long.parseLong(rs.getString("IloscValue"));
                                    p.addDostawa("Dostawa " + counter + ": " + rs.getString("IloscValue"));
                                    Log.e("debug", "" + rs.getString("IloscValue"));
                                    counter++;
                                }


                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        Log.e("debug", "nie ma");
                        String sql = "SELECT * FROM Zasoby WHERE Towar=" + p.getId() + " LIMIT 1";

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
                    counter1++;
                    p.setStanMag(i);

                }
            }catch(SQLException e){
                e.printStackTrace();
            }

            return i;
        }

        @Override
        protected void onPostExecute(Integer aVoid) {




        }
    }

    class ViewHolder {
        @BindView(R.id.textViewStan)
        TextView textView;

        @BindView(R.id.textViewNUMERID)
        TextView textID;

        @BindView(R.id.textDostawy)
        TextView dostawy;

        @BindView(R.id.textPartiaInfo)
        TextView partia;



        public ViewHolder(View v) {
            ButterKnife.bind(this, v);
        }


    }

}
