package com.takeadip.takeadip.ui;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.takeadip.takeadip.Constants;
import com.takeadip.takeadip.R;
import com.takeadip.takeadip.data.model.DipData;
import com.takeadip.takeadip.persistence.PersistenceSQL;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by vik on 11/10/2017.
 */

public class DipsAdapter extends RecyclerView.Adapter<DipsAdapter.DipViewHolder>{


    int type;

    private WeakReference<Context> context;
    private List<DipData> dips = new ArrayList<DipData>();
    //private DipItemListener itemListener;
    private OnDipClickListener onDipClickListener;

   /* public DipsAdapter (Context context, List<DipData> dips, DipItemListener itemListener) {
        this.context = new WeakReference<>(context);
        this.dips = dips;
        this.itemListener = itemListener;
    }*/
    public DipsAdapter (@NonNull List<DipData> dips,
                        @NonNull OnDipClickListener
                                onDipClickListener, int layoutType) {

        this.dips = dips;
        this.onDipClickListener = onDipClickListener;
        this.type = layoutType;
    }

    @Override
    public DipViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        int layoutRes = 0;

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
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(layoutRes, viewGroup, false);

        return new DipViewHolder(v );
    }

    @Override
    public void onBindViewHolder(DipViewHolder viewHolder, final int position) {
        final DipData dip = dips.get(position);

        Resources res = viewHolder.itemView.getContext().getResources();

        // If you wanted to check view type without checking if views exist
        //int viewType = getItemViewType(position);

        if(viewHolder.distanceTextView != null) {
            // distanceTextView only exists in list_itemdiplist_nearby
            if(dip.getDistancia()!= null)
            {
                viewHolder.distanceTextView.setText(String.valueOf(dip.getDistancia())+ " KM");
                viewHolder.distanceTextView.setTextColor(res.getColor(R.color.colorPrimaryDark));
            }
            else
            {
                viewHolder.distanceTextView.setText("0"+ " KM");
                viewHolder.distanceTextView.setTextColor(res.getColor(R.color.colorPrimaryDark));
            }

        }

        String name = dip.getNombre()+ ", ";
        String address = dip.getProvincia();

        int posstartname = 0;
        int posendname = name.length();

        Spannable wordtoSpan = new SpannableString(name +  address);
        wordtoSpan.setSpan(new ForegroundColorSpan(res.getColor(R.color.colorPrimary)), posstartname, posendname, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordtoSpan.setSpan(new ForegroundColorSpan(res.getColor(R.color.colorAccent)), posendname, wordtoSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        viewHolder.nameplaceTextView.setText(wordtoSpan);

        if(PersistenceSQL.isFavourite(dip.getId(), context.get()))
        {
            viewHolder.favImageView.setVisibility(View.VISIBLE);
        }else viewHolder.favImageView.setVisibility(View.INVISIBLE);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDipClickListener.onDipClick(dip);
            }
        });

    }

   /* public class DipViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener, View.OnLongClickListener
    {

        @Bind(R.id.txt_nameplace) TextView nameplaceTextView;
        @Bind(R.id.img_fav) ImageView favImageView;

        // Nearby view only

        @Bind(R.id.txt_distance) TextView distanceTextView;

        //DipItemListener itemListener;

        public DipViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);

            //this.itemListener = itemListener;
            v.setOnClickListener( this );
            v.setOnLongClickListener( this );
        }

        @Override
        public void onClick (View v)
        {
           //DipData dip = getItem( getAdapterPosition() );
            //this.itemListener.onDipClick(dip);

            onDipClickListener.onDipClick(v, getAdapterPosition());
        }

        @Override
        public boolean onLongClick (View v)
        {
            onDipClickListener.onDipLongClick(v, getAdapterPosition());

            return true;
        }
    }*/
    public class DipViewHolder extends RecyclerView.ViewHolder  {

       public TextView nameplaceTextView;
       public TextView distanceTextView;

       public ImageView favImageView;
       /* @Bind(R.id.txt_nameplace) TextView nameplaceTextView;
        @Bind(R.id.img_fav) ImageView favImageView;
        // Nearby view only
        @Bind(R.id.txt_distance) TextView distanceTextView;*/

        //DipItemListener itemListener;

        public DipViewHolder(View v) {
            super(v);
            //ButterKnife.bind(this, v);
            nameplaceTextView = (TextView) v.findViewById(R.id.txt_nameplace);
            distanceTextView = (TextView) v.findViewById(R.id.txt_distance);
            favImageView = (ImageView) v.findViewById(R.id.img_fav);

            //this.itemListener = itemListener;

        }


    }







    @Override
    public int getItemCount() {
        return dips.size();
    }





    @Override
    public int getItemViewType(int position) {
        switch(type) {
            case 0:
                return Constants.NEARBY_VIEW;
            case 1:
                return Constants.DIPS_VIEW;
            case 2:
                return Constants.FAVS_VIEW;
            default:
                return -1;
                //return DIPS_VIEW;
        }
    }

    private DipData getItem(int adapterPosition) {
        return dips.get(adapterPosition);
    }
    public void updateDips(List<DipData> dips) {
        this.dips = dips;
        notifyDataSetChanged();
    }

}
