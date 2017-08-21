package pl.oskarpolak.barscanner2;

import android.content.Context;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

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
 * Created by OskarPraca on 2016-11-13.
 */

public class ProductAdapter extends BaseAdapter {


    private List<Product> productArrayList;
    private LayoutInflater layoutInflater;
    NewDocumentActivity context;
    public ProductAdapter(List<Product> products, NewDocumentActivity context) {
        productArrayList = products;
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return productArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return productArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    int lastFocussedPosition = -1;
    boolean isBringedFromButton = false;
    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        final ViewHolder holder;
      //  if (view != null) {
       //     holder = (ViewHolder) view.getTag();
       // } else {
            view = layoutInflater.inflate(R.layout.layout_list_row, parent, false);
            holder = new ViewHolder(view);
         //   view.setTag(holder);
     //   }
          final Product p = ((Product) getItem(position));

        Log.e("PRODUKT", "Ilość ustawiona dla produktu: " + p.getName() + " to: " + p.getCount());

        holder.textName.setText(p.getName());
        holder.textPartion.setText(p.getPartion());
        holder.countText.setText(String.valueOf(p.getCount()));
        holder.numer.setText((position+1)+".");

         int oldCount = 0;



        holder.buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isBringedFromButton = true;
                context.runUpdatePojPozycja(p, p.getCount(), p.addCount(5));
                holder.countText.setText(String.valueOf(p.getCount()));
            }
        });

        holder.buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.runUpdatePojPozycja(p, p.getCount(), p.remCount(5));
                holder.countText.setText(String.valueOf(p.getCount()));


            }
        });


        if(p.getCount() == 0) {
            holder.countText.setText("");
        }

        holder.countText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                isBringedFromButton = false;
                Log.e("przycisk", "prepared");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("przycisk", "changed1");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().equals("")) {
                    context.runUpdatePojPozycja(p, p.getCount(),Integer.parseInt(s.toString()));
                        p.setCount(Integer.parseInt(s.toString()));
                        new sprawdzStanProduktu(holder.layout, holder.numer).execute(p);

                    Log.e("przycisk", "changed");
                }
            }
        });

        return view;
    }



    private class sprawdzStanProduktu extends AsyncTask<Product, Integer, Boolean> {


        RelativeLayout layout;
        TextView text;

        public sprawdzStanProduktu(RelativeLayout layout, TextView text){
            this.layout = layout;
            this.text = text;
        }

        @Override
        protected Boolean doInBackground(Product ... params) {
            Statement statement = null;
            Statement statement1 = null;

            try {
                statement = MysqlLocalConnector.getInstance(context).getConnection().createStatement();
                statement1 = MysqlLocalConnector.getInstance(context).getConnection().createStatement();

                int i = 0;
                int counter1 = 1;
                Product product = params[0];
                i = 0;
                if (product.isHasPartion()) {
                    Log.e("debug", "ma partie");
                    List<String> doce = new ArrayList<String>();
                    String sql = "SELECT * FROM Zasoby WHERE ID = " + product.getWybranyZasob();
                    ResultSet rs = statement.executeQuery(sql);
                    try {
                        while (rs.next()) {
                            i += Integer.valueOf(rs.getString("IloscValue"));

                        }

                        String sql1 = "SELECT * FROM TowaryCloud WHERE dostawa = " + product.getWybranyZasob();
                        ResultSet rs1 = statement.executeQuery(sql1);

                        while(rs1.next()){
                            i -= Integer.valueOf(rs1.getString("iloscSciagnieta"));
                            i += product.getCount();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                } else {
                    String sql = "SELECT * FROM Zasoby WHERE ID = " + product.getWybranyZasob();
                    String sql1 = "SELECT * FROM TowaryCloud WHERE dostawa = " + product.getWybranyZasob();
                    ResultSet rs1 = statement.executeQuery(sql1);

                    while(rs1.next()){
                        i -= Integer.valueOf(rs1.getString("iloscSciagnieta"));
                        i += product.getCount();
                    }

                    try {
                        ResultSet rs = statement.executeQuery(sql);
                        while (rs.next()) {
                            i += Long.parseLong(rs.getString("IloscValue"));
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }
                product.setStanMag(i);

                if(product.getStanMag() < product.getCount()){
                    Log.e("test", "Stan mag. : " + product.getStanMag() + " a ilosc: " + product.getCount());
                    return true;
                }


            } catch (SQLException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean aVoid) {

            if(aVoid){
                layout.setBackgroundColor(context.getResources().getColor(R.color.red));
                text.setTextColor(context.getResources().getColor(R.color.white));
            }else{
                layout.setBackgroundColor(context.getResources().getColor(R.color.white));
                text.setTextColor(context.getResources().getColor(R.color.red));
            }

        }
    }

     class ViewHolder {

         @BindView(R.id.produktLayout)
         RelativeLayout layout;

        @BindView(R.id.textDescription)
        TextView textName;

        @BindView(R.id.textPartion)
        TextView textPartion;

        @BindView(R.id.buttonAdd1)
        Button buttonAdd;

        @BindView(R.id.buttonRemove1)
        Button buttonRemove;

         @BindView(R.id.countText1)
         EditText countText;

         @BindView(R.id.numerID)
         TextView numer;

        public ViewHolder(View v)  {
            ButterKnife.bind(this, v);
        }


    }
}
