package com.takeadip.takeadip;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.takeadip.takeadip.internal.AccessInterface;
import com.takeadip.takeadip.internal.MyApplication;
import com.takeadip.takeadip.internal.Utils;
import com.takeadip.takeadip.model.Dip;
import com.takeadip.takeadip.persistence.PersistenceSQL;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TabFavouriteFragment extends Fragment {

    private static final String TAG = "TabFavouriteFragment";


    private ListView lv_dips_fav;

    private TextView txt_nofav;
    private MyApplication application;
    private ArrayList<Dip> l_favourites = new ArrayList<Dip>();






    //Spinner
    private boolean filtering = false;
    private Spinner sp_filter;
    private ArrayAdapter<String> adapter; // adapter for Spinner
    private ArrayList<Dip> l_dips_filter = new ArrayList<Dip>();
    String selection;
    private Dip dip;
    ///private String[]names = {"Todos","FP","PN","P","T"};

    private Handler handler = new Handler(new ResultMessageCallback());
    public final int RESULT_FAVOURITES_OK = 1;

    private ProgressDialog pDialogGetDipsFavourite;

    //CHILD FRAGMENT
    Fragment detaildipfragment;

    //new
    private RelativeLayout layout;
    private MyRenderer selectedRenderer;

    public TabFavouriteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mBundle = savedInstanceState;
        application = (MyApplication) getActivity().getApplicationContext();
        //loadList();
        if(application.getL_favourites()!=null)
            if(application.getL_favourites().size()==0)
                getFavourites();
            else
            {
                //l_dips = application.getL_dips();
                loadList();
            }

        Log.e(TAG, "onCreate");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.tab_favourites,container,false);
        Log.i("IN FRAME: ", TAG);


        sp_filter = (Spinner) v.findViewById(R.id.sp_typedips);
        loadSpinnerFilter();
        layout = (RelativeLayout)v.findViewById(R.id.layout);
        txt_nofav = (TextView)v.findViewById(R.id.txt_nofav);
        lv_dips_fav = (ListView)v.findViewById(R.id.list_fav);

        //lv_dips_fav.setAdapter(new MyAdapter(getActivity(),R.layout.list_itemdiplist_fav, l_favourites));
        lv_dips_fav.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                int fixedpos = arg2;

                //FriendsProfileFragment friendsprofilefragment = new FriendsProfileFragment();
                detaildipfragment = new DetailDipFragment();
                //OwnProfileFragment frag = new OwnProfileFragment();

                Bundle bundles = new Bundle();
                Dip dip =  l_favourites.get(fixedpos);

                // ensure your object has not null
                if (dip != null) {
                    bundles.putSerializable("dip", dip);
                    // Log.e("friend", "is valid");
                } else {
                    Log.e("dip", "is null");
                }
                detaildipfragment.setArguments(bundles);
                //getDarkerbackground to call childfragment

                   /* fadeBackground = v.findViewById(R.id.fadeBackground);
                    fadeBackground.setVisibility(View.VISIBLE);
                    fadeBackground.animate().alpha(0.7f);*/
                //getDarkerbackground to call childfragment

                android.support.v4.app.FragmentTransaction ft =  getActivity().getSupportFragmentManager().beginTransaction();


                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

                //ft.replace(android.R.id.content, frag);
                ft.add(android.R.id.content,detaildipfragment);
                ft.addToBackStack(getTag());
                ft.commit();


            }
        });

        lv_dips_fav.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos =  position;
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                builder1.setMessage(getResources().getString(R.string.removefavourites));
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                int fixedpos = pos;
                                Dip dip =  l_favourites.get(fixedpos);
                                removeFromfavourites(dip);

                                lv_dips_fav.setAdapter(new MyAdapter(getActivity(),R.layout.list_itemdiplist_fav, l_favourites));
                                dialog.cancel();
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();


                return true;
            }
        });







        return v;
    }

    private void getFavourites()
    {
        pDialogGetDipsFavourite = ProgressDialog.show(getActivity(), getString(R.string.info), getString(R.string.loading));
        Thread thread = new Thread(new GetFavourites());
        thread.start();
    }
    private class GetFavourites implements Runnable {



        public void run() {

            int mensajeDevuelto = RESULT_FAVOURITES_OK;


            l_favourites = PersistenceSQL.getFavourites(getContext());
            application.setL_favourites(l_favourites);







            handler.sendEmptyMessage(mensajeDevuelto);
        }
    }
    private class ResultMessageCallback implements Handler.Callback {

        public boolean handleMessage(android.os.Message arg0) {



            switch (arg0.what) {


                case RESULT_FAVOURITES_OK:

                    if(pDialogGetDipsFavourite != null)pDialogGetDipsFavourite.dismiss();
                    if(l_favourites.size()>0)
                    {
                        txt_nofav.setVisibility(View.GONE);
                        lv_dips_fav.setAdapter(new MyAdapter(getActivity(),R.layout.list_itemdiplist_fav, l_favourites));
                        application.setL_favourites(l_favourites);
                    }
                    //Toast.makeText(LoginActivity.this,"Success", Toast.LENGTH_LONG)	.show();

                    else
                    {
                        txt_nofav.setVisibility(View.VISIBLE);
                    }



                    break;





            }

            return true; // lo marcamos como procesado
        }

    }
    private void removeFromfavourites(Dip dip)
    {
        PersistenceSQL.deleteDip(dip.getDip_id(), getContext());
        Utils.showAlert(getContext(), "", "dip already deleted");
        l_favourites = PersistenceSQL.getFavourites(getContext());
        lv_dips_fav.setAdapter(new MyAdapter(getActivity(),R.layout.list_itemdiplist_fav, l_favourites));



    }
    private void loadList()
    {
        LoadListAsyncTask gfl = new LoadListAsyncTask();
        gfl.execute();


    }
    private class LoadListAsyncTask extends AsyncTask<Void, Void, List<Dip>> {

        @Override
        protected void onPreExecute() {
            // start loading animation maybe?
            //adapter.clear(); // clear "old" entries (optional)
        }

        @Override
        protected List<Dip> doInBackground(Void... params) {
            // everything in here gets executed in a separate thread
            l_favourites = application.getL_favourites();
            return l_favourites;
        }

        @Override
        protected void onPostExecute(List<Dip> items) {
            // stop the loading animation or something
            if(l_favourites.size()>0)
            {
                txt_nofav.setVisibility(View.GONE);
                lv_dips_fav.setAdapter(new MyAdapter(getActivity(),R.layout.list_itemdiplist_fav, l_favourites));
            }

            else txt_nofav.setVisibility(View.VISIBLE);
        }
    }
    /* public class MyRenderer extends RelativeLayout {

         public TextView textdipView, typedipView;

         public ImageView imageView;

        public MyRenderer(Context context, Dip dip) {
             super(context);
             setPadding(40, 40, 40, 40);
             //setBackgroundColor(0xFFFF0000);


             RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                     RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
             rlp.addRule(CENTER_IN_PARENT);

             textdipView = new TextView(context);
             typedipView = new TextView(context);
             textdipView.setText(dip.getName());
             typedipView.setText(dip.getType());

             addView(textdipView, rlp);

             addView(typedipView, rlp);
             rlp.addRule(RelativeLayout.BELOW, typedipView.getId());

         }

     }*/
    public class MyRenderer extends LinearLayout {

        private Dip mydip ;
        public TextView textdipView, typedipView, descdipView;


        public ImageView imageView;

        public MyRenderer(Context context, Dip dip) {

            super(context);
            mydip = dip;
            setPadding(10, 10, 10, 10);

            //setBackgroundColor(0xFFFF0000);
            LayoutParams rlp = new LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            setOrientation(LinearLayout.VERTICAL);



            String sringtypedip =Utils.getTypeStringFromDip(dip.getType());

            textdipView = new TextView(context);
            typedipView = new TextView(context);
            descdipView = new TextView(context);
            imageView = new ImageView(context);
            textdipView.setText(dip.getName());
            textdipView.setTextColor(getResources().getColor(R.color.colorPrimary));
            typedipView.setText(sringtypedip);
            //typedipView.setText(dip.getType());
            typedipView.setTextColor(getResources().getColor(R.color.colorAccent));
            descdipView.setText(dip.getDescription());
            descdipView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));


            addView(textdipView, rlp);
            addView(typedipView, rlp);


            //new ImageDownloaderTaskUser(imageView).execute(AccessInterface.URL_GETPHOTO );
            //addView(imageView, rlp);


        }

    }


    private void loadSpinnerFilter()
    {
        //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.recentsearches_array, android.R.layout.simple_spinner_item);
        //adapter=ArrayAdapter.createFromResource(getActivity(), R.array.filter_array, android.R.layout.simple_spinner_item);
        adapter =new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,Utils.names);
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
        sp_filter.setAdapter(adapter);

        sp_filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub


                //Toast.makeText(parent.getContext(), "OnItemSelectedListener : " + parent.getItemAtPosition(position).toString(),Toast.LENGTH_SHORT).show();

                selection =  parent.getItemAtPosition(position).toString();
                if(selection != "All")
                {
                    Log.i("selection: ",selection);
                    l_dips_filter.clear();
                    l_dips_filter = Utils.getDipsFromSpinner(selection, l_favourites);
                    lv_dips_fav.setAdapter(new Adapter(getActivity(),R.layout.list_itemdiplist, l_dips_filter));

                }else lv_dips_fav.setAdapter(new Adapter(getActivity(),R.layout.list_itemdiplist, l_favourites));


            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TOO Auto-generated method stub
                Toast toast1 =Toast.makeText(getActivity(),"no selection", Toast.LENGTH_SHORT);
                toast1.show();
            }

        });


    }

    /*private void getDips()
    {


        Log.i("l_fvourites: ", String.valueOf(l_favourites.size()));

    }*/




   /* public class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return 10;
        }

        @Override
        public String getItem(int position) {
            return "Hello World " + position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MyRenderer renderer;
            if (convertView != null)
                renderer = (MyRenderer) convertView;
            else
                renderer = new MyRenderer(getActivity());
            renderer.textView.setText(getItem(position));
            return renderer;
        }
    }*/

    public class MyAdapter extends ArrayAdapter<Dip>  {

        private ArrayList<Dip> items;
        //private DecimalFormat df = new DecimalFormat("0.00");

        public MyAdapter(Context context, int textViewResourceId,
                       List<Dip> items) {
            super(context, textViewResourceId, items);
            this.items = (ArrayList<Dip>) items;

        }
        /*@Override
        public int getCount() {
            return 10;
        }*/



        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;

            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.list_itemdiplist_fav, null);
            }

            final Dip p = items.get(position);


            if (p != null) {



                final TextView txt_nameplace = (TextView) v.findViewById(R.id.txt_nameplace);

                //final TextView txt_address = (TextView) v.findViewById(R.id.txt_address);
                //final ImageView img_photo = (ImageView)v.findViewById(R.id.img_photo);



                String name = p.getName()+ ", ";
                String address = p.getAddress();



                int posstartname = 0;
                int posendname = name.length();
                //Log.i("posendname: ", String.valueOf(posendname));
                //int length_hasbean = hasbeanplace.length();

                Spannable wordtoSpan = new SpannableString(name +  address);
                wordtoSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)), posstartname, posendname, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                wordtoSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), posendname, wordtoSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                txt_nameplace.setText(wordtoSpan);
                //txt_nameplace.setText(name);
                //txt_address.setText(address);

                //txt_address.setText(p.getAddress());




            }



            return v;
        }
    }
    private class Adapter extends ArrayAdapter<Dip> {

        private ArrayList<Dip> items;
        //private DecimalFormat df = new DecimalFormat("0.00");

        public Adapter(Context context, int textViewResourceId,
                       List<Dip> items) {
            super(context, textViewResourceId, items);
            this.items = (ArrayList<Dip>) items;

        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;

            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.list_itemdiplist_fav, null);
            }

            final Dip p = items.get(position);


            if (p != null) {



                final TextView txt_nameplace = (TextView) v.findViewById(R.id.txt_nameplace);

                //final TextView txt_address = (TextView) v.findViewById(R.id.txt_address);
                final ImageView img_photo = (ImageView)v.findViewById(R.id.img_photo);



                String name = p.getName()+ ", ";
                String address = p.getAddress();



                int posstartname = 0;
                int posendname = name.length();
                //Log.i("posendname: ", String.valueOf(posendname));
                //int length_hasbean = hasbeanplace.length();

                Spannable wordtoSpan = new SpannableString(name +  address);
                wordtoSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)), posstartname, posendname, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                wordtoSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), posendname, wordtoSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                txt_nameplace.setText(wordtoSpan);
                //txt_nameplace.setText(name);
                //txt_address.setText(address);

                //txt_address.setText(p.getAddress());

              /*  if (img_photo != null) {

                    if(p.getPhotoreference()!= null)
                    {
                        Bitmap bmp;
                        try {
                            Log.i("p.getPhotoreference(): ", p.getPhotoreference());
                            bmp = BitmapFactory.decodeStream(new java.net.URL(Constants.urlpicturebeanplaces + p.getPhotoreference()).openStream());
                            if(bmp!=null)img_photo.setImageBitmap(bmp);
                            else
                            {
                                bmp =  BitmapFactory.decodeStream(new java.net.URL(p.getPicture()).openStream());
                                if(bmp!=null)img_photo.setImageBitmap(bmp);
                                else img_photo.setBackgroundResource(R.drawable.addphoto);
                            }
                        } catch (MalformedURLException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        } catch (IOException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }

                    }else img_photo.setBackgroundResource(R.drawable.addphoto);
                }*/


            }

            return v;

        }

    }

    @Override
    public void onDestroyView() {

        try{
            FragmentTransaction ft =  getActivity().getSupportFragmentManager().beginTransaction();
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            if (detaildipfragment.isAdded())
            {
                Log.i("detaildipfragment", "isAdded");
                ft.hide(detaildipfragment);


            }

            ft.commit();
//				        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
//
//				        transaction.remove(friendsprofilefragment);
//
//				        transaction.commit();
        }catch(Exception e){
            Log.e("error: ", e.toString());
        }

        super.onDestroyView();
     /*   fadeBackground.animate().alpha(0.0f).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // As soon as the animation is finished we set the visiblity again back to GONE
                fadeBackground.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });*/
    }

}
