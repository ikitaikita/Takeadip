package com.takeadip.takeadip;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.takeadip.takeadip.model.DipData;
import com.takeadip.takeadip.persistence.PersistenceSQL;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by vik on 11/10/2017.
 */

public class DipsAdapter extends RecyclerView.Adapter<DipsAdapter.ViewHolder>{

    private final static int NEARBY_VIEW = 0;
    private final static int DIPS_VIEW = 1;
    private final static int FAVS_VIEW = 2;

    private WeakReference<Context> context;
    private List<DipData> dips;
    private DipItemListener itemListener;

    public DipsAdapter (Context context, List<DipData> dips, DipItemListener itemListener) {
        this.context = new WeakReference<>(context);
        this.dips = dips;
        this.itemListener = itemListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener, View.OnLongClickListener
    {

        @Bind(R.id.txt_nameplace)
        TextView nameplaceTextView;
        @Bind(R.id.img_fav)
        ImageView favImageView;

        // Nearby view only

        @Bind(R.id.txt_distance)
        TextView distanceTextView;

        DipItemListener itemListener;

        public ViewHolder(View v, DipItemListener itemListener) {
            super(v);
            ButterKnife.bind(this, v);

            this.itemListener = itemListener;
            v.setOnClickListener( this );
            v.setOnLongClickListener( this );
        }

        @Override
        public void onClick (View v)
        {
            DipData dip = getItem( getAdapterPosition() );
            this.itemListener.onDipClick(dip.getId());
        }

        @Override
        public boolean onLongClick (View v)
        {
            DipData dip = getItem( getAdapterPosition() );
            this.itemListener.onDipLongClick( dip.getId() );

            return true;
        }
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        int layoutRes = 0;

        switch (viewType) {
            case NEARBY_VIEW:
                layoutRes = R.layout.list_itemdiplist_nearby;
                break;
            case DIPS_VIEW:
                layoutRes = R.layout.list_itemdiplist;
                break;
            case FAVS_VIEW:
                layoutRes = R.layout.list_itemdiplist_fav;
                break;
        }
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(layoutRes, viewGroup, false);

        return new ViewHolder(v, this.itemListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        DipData dip = dips.get(position);

        Resources res = viewHolder.itemView.getContext().getResources();

        if(viewHolder.distanceTextView != null) {
            // distanceTextView only exists in list_itemdiplist_nearby
            viewHolder.distanceTextView.setText(String.valueOf(dip.getDistance())+ " KM");
            viewHolder.distanceTextView.setTextColor(res.getColor(R.color.colorPrimaryDark));

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

    }

    @Override
    public int getItemCount() {
        return dips.size();
    }

    public void updateBooks(List<DipData> dips) {
        this.dips = dips;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        switch(position) {
            case 0:
                return NEARBY_VIEW;
            case 1:
                return DIPS_VIEW;
            case 2:
                return FAVS_VIEW;
            default:
                return DIPS_VIEW;
        }
    }

    private DipData getItem(int adapterPosition) {
        return dips.get(adapterPosition);
    }

    public interface DipItemListener
    {
        void onDipClick(String id);

        void onDipLongClick(String id);
    }
}
