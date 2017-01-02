package pl.oskarpolak.barscanner2;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.oskarpolak.barscanner2.data.Product;

/**
 * Created by OskarPraca on 2016-11-13.
 */

public class ProductAdapter extends BaseAdapter {


    private List<Product> productArrayList;
    private LayoutInflater layoutInflater;

    public ProductAdapter(List<Product> products, Context context) {
        productArrayList = products;

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

        holder.buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p.addCount(5);
                holder.countText.setText(String.valueOf(p.getCount()));
            }
        });

        holder.buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p.remCount(5);
                holder.countText.setText(String.valueOf(p.getCount()));
            }
        });



        holder.countText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().equals("")) {
                    p.setCount(Integer.parseInt(s.toString()));
                }
            }
        });

        return view;
    }

     class ViewHolder {

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
