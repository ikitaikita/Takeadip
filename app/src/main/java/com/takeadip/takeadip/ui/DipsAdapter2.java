package com.takeadip.takeadip.ui;

import android.content.Context;
import android.content.res.Resources;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.takeadip.takeadip.Constants;
import com.takeadip.takeadip.R;
import com.takeadip.takeadip.model.Dip;
import com.takeadip.takeadip.persistence.PersistenceSQL;

import java.util.List;

/**
 * Created by vik on 20/10/2017.
 */

public class DipsAdapter2 extends ArrayAdapter<Dip> {

    private final Context context;

    private List<Dip> dipsList;
    int viewType;

    public DipsAdapter2(Context context, List<Dip> dipsList, int viewtype) {
        super(context, 0, dipsList);

        this.context = context;
        this.dipsList = dipsList;
        this.viewType = viewtype;

    }

    @Override public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        int layoutRes = 0;
        Resources res = view.getContext().getResources();
        if (view == null) {


            switch (viewType) {
                case Constants.NEARBY_VIEW:
                    layoutRes = R.layout.list_itemdiplist_nearby;
                    break;
                case Constants.DIPS_VIEW:
                    layoutRes = R.layout.list_itemdiplist;
                    break;
                case Constants.FAVS_VIEW:
                    layoutRes = R.layout.list_itemdiplist_fav;
                    break;
            }
            view = LayoutInflater.from(context).inflate(layoutRes, parent, false);
            holder = new ViewHolder();
            holder.nameplaceTextView = (TextView) view.findViewById(R.id.txt_nameplace);
            holder.distanceTextView = (TextView) view.findViewById(R.id.txt_distance);
            holder.favImageView = (ImageView) view.findViewById(R.id.img_fav);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        // Get the post for the current position.
        Dip dip = getItem(position);

        if(holder.distanceTextView != null) {
            // distanceTextView only exists in list_itemdiplist_nearby
            holder.distanceTextView.setText(String.valueOf(dip.getDistance())+ " KM");
            holder.distanceTextView.setTextColor(res.getColor(R.color.colorPrimaryDark));

        }

        String name = dip.getName()+ ", ";
        String address = dip.getProvince();

        int posstartname = 0;
        int posendname = name.length();

        Spannable wordtoSpan = new SpannableString(name +  address);
        wordtoSpan.setSpan(new ForegroundColorSpan(res.getColor(R.color.colorPrimary)), posstartname, posendname, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordtoSpan.setSpan(new ForegroundColorSpan(res.getColor(R.color.colorAccent)), posendname, wordtoSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        holder.nameplaceTextView.setText(wordtoSpan);

        if(PersistenceSQL.isFavourite(dip.getDip_id(), getContext()))
        {
            holder.favImageView.setVisibility(View.VISIBLE);
        }else holder.favImageView.setVisibility(View.INVISIBLE);



        return view;
    }

    @Override
    public Dip getItem(int position) {
        return dipsList.get(position);
    }

    static class ViewHolder {

        TextView nameplaceTextView;
        TextView distanceTextView;
        ImageView favImageView;
    }
}
