package com.takeadip.takeadip;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.takeadip.takeadip.model.Dip;

public class DetailDipActivity extends Activity {

    static final String EXTRA_DIP = "dip";
    private Dip dip;
    private TextView txt_dip;
    private TextView txt_typedip;
    private ImageView img_photo;
    private TextView txt_desc;
    private TextView txt_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_detail_dip_two);
        TextView txt_dip = (TextView) findViewById(R.id.txt_dip);
        TextView txt_typedip = (TextView) findViewById(R.id.txt_typedip);
        TextView txt_desc = (TextView) findViewById(R.id.txt_desc);
        TextView txt_address = (TextView) findViewById(R.id.txt_address);
        ImageView img_photo = (ImageView) findViewById(R.id.img_photo);

        dip = (Dip) getIntent().getExtras().getSerializable(EXTRA_DIP);
        setData();

    }

    private void setData()
    {
        if(dip!=null)
        {
            txt_dip.setText(dip.getName());
            txt_typedip.setText(dip.getType());
            txt_desc.setText(dip.getDescription());
            txt_address.setText(dip.getAddress());
        }
    }
}
