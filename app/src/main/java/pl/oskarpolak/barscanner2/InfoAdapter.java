package pl.oskarpolak.barscanner2;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
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
    Handler handler = new Handler();

    public InfoAdapter(List<Product> productList, Context con){
        products = (ArrayList<Product>) productList;
        context = con;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mysql = MysqlLocalConnector.getInstance(con);



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
        }


        int i = getIloscSciagnieta(products.get(position));
        String dostawy = "Sciągnięto już: "  + i+"\n";
        holder.dostawy.setText(dostawy);



            holder.textID.setText((position+1)+".");
            holder.partia.setText("Partia: " + products.get(position).getWybranaPartia().getPartia());



        if(i > products.get(position).getStanMag()) {
            holder.dostawy.setTextColor(context.getResources().getColor(R.color.red));
        }else {
            holder.dostawy.setTextColor(context.getResources().getColor(R.color.black));
        }



        TextView  text = holder.textView;
        new AsyncGetZasoby(position, text, holder.dostawy).execute();

        return view;
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


                final Product p = products.get(pos);
       if(products.get(pos).isHasPartion()) {

           p.getDostawy().clear();
           // terz musze puscic petle bo tych docow jest wiecej niz 1
           Log.e("debug", "ma partie");
           List<String> doce = new ArrayList<String>();




          //for(String s : p.getWybranaPartia().getZasoby()) {
              String sql = "SELECT * FROM Zasoby WHERE ID = " + p.getWybranyZasob();


              ResultSet rs = statement.executeQuery(sql);
              try {
                  while (rs.next()) {
                      i += Integer.valueOf(rs.getString("IloscValue"));
                      p.addDostawa("Ilość sztuk w dostawie" + ": " + rs.getString("IloscValue"));
                      Log.e("debug", "" + rs.getString("IloscValue"));

                  }

                  String sql1 = "SELECT * FROM TowaryCloud WHERE dostawa = " + p.getWybranyZasob();
                  final ResultSet rs1 = statement.executeQuery(sql1);

                  int sciagnieto = 0;
                  while(rs1.next()){
                      sciagnieto = Integer.valueOf(rs1.getString("iloscSciagnieta"));
                      i -= Integer.valueOf(rs1.getString("iloscSciagnieta"));
                      final int finalSciagnieto = sciagnieto;
                      handler.post(new Runnable() {
                          @Override
                          public void run() {

                                  dostawy.setText(dostawy.getText() + "\nIlość zarezerwowana " + (finalSciagnieto -getIloscSciagnieta(p)));

                          }
                      });
                      i += getIloscSciagnieta(p);
                  }




              } catch (SQLException e) {
                  e.printStackTrace();
              }

        //  }
      }else {
           Log.e("debug", "nie ma");
           String sql = "SELECT * FROM Zasoby WHERE ID = " + p.getWybranyZasob();

           try {
               ResultSet rs = statement.executeQuery(sql);
               while (rs.next()) {
                   i += Long.parseLong(rs.getString("IloscValue"));
                   Log.e("debug", "" + i);
               }

               String sql1 = "SELECT * FROM TowaryCloud WHERE dostawa = " + p.getWybranyZasob();
               final ResultSet rs1 = statement.executeQuery(sql1);
int sciagnieto = 0;
               while(rs1.next()){
                   i -= Integer.valueOf(rs1.getString("iloscSciagnieta"));
                   sciagnieto = Integer.valueOf(rs1.getString("iloscSciagnieta"));
                   final int finalSciagnieto = sciagnieto;
                   handler.post(new Runnable() {
                       @Override
                       public void run() {

                               dostawy.setText(dostawy.getText() + "\nIlość zarezerwowana: " + (finalSciagnieto -getIloscSciagnieta(p)));

                       }
                   });
                   i += getIloscSciagnieta(p);
               }


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
        }
    }

    private int getIloscSciagnieta(Product p1) {
        int ilosc = 0;



        for(Product p : NewDocumentActivity.getProducts()) {

            if(p.getWybranyZasob().equals(p1.getWybranyZasob()) && p.getPartion().equals(p1.getPartion())) {
                ilosc += p.getCount();
            }
        }


        return ilosc;
    }




    class ViewHolder {

        @BindView(R.id.infoLayout)
        RelativeLayout layout;

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
