package gr.betting.admin.bettingtips;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Admin on 19/6/2017.
 */

public class StandardNewTipsFragment extends Fragment {
    private FirebaseAnalytics mFirebaseAnalytics;
    @Nullable
    @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstance){
        final View v = inflater.inflate(R.layout.standard_new_tips_layout,null);



        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

        Bundle bundle = new Bundle();
        bundle.putString("Standard_new_tips",(String)getActivity().getTitle());
        mFirebaseAnalytics.logEvent("Standard_new",bundle);

        /*mAdView = (AdView) v.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/

        this.getActivity().setTitle(getString(R.string.nav_today));



        final ListView listView = (ListView) v.findViewById(R.id.listview);

        String response = CallHolder.getStandard_new();
        final ArrayList<betItem> adapterList = new ArrayList<betItem>();
        try {
            if(response==null) {
                Toast.makeText(StandardNewTipsFragment.this.getActivity(), R.string.error_server, Toast.LENGTH_LONG).show();
                System.out.println("No available tips");
                ImageView sad_face = (ImageView) v.findViewById(R.id.sad_face);
                sad_face.setVisibility(View.VISIBLE);
                TextView sorry_text = (TextView) v.findViewById(R.id.sorry_text);
                sorry_text.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            }
            else{
                if (!response.contains("The coordinates or dimensions of the range are invalid.")) {
                    JSONObject jsonResult = new JSONObject(response);
                    final JSONArray results = (JSONArray) jsonResult.get(getString(R.string.standard_today));
                    int counter = 0;
                    for(int i=0;i<results.length();i++){
                        betItem tempItem = new betItem();
                        counter++;

                        JSONObject tip = results.getJSONObject(i);

                        tempItem.setDate(tip.getString("date"));
                        tempItem.setTime(tip.getString("time"));

                        tempItem.setHome_team_name(tip.getString("homeTeam"));
                        tempItem.setAway_team_name(tip.getString("awayTeam"));

                        tempItem.setHome_team_score(tip.getString("homeScore"));
                        tempItem.setAway_team_score(tip.getString("awayScore"));

                        tempItem.setOdd(tip.getString("odd"));
                        tempItem.setTip(tip.getString("tip"));

                        tempItem.setCountry_league(tip.getString("countryleague"));
                        tempItem.setGotcha(tip.getString("gotcha"));

                        adapterList.add(tempItem);

                    }
                    System.out.println("Counter : "+counter);


                    betListAdapter myAdapter = new betListAdapter(StandardNewTipsFragment.this.getActivity(), adapterList );
                    listView.setAdapter(myAdapter);
                }else{
                    System.out.println("No available tips");
                    ImageView sad_face = (ImageView) v.findViewById(R.id.sad_face);
                    sad_face.setVisibility(View.VISIBLE);
                    TextView sorry_text = (TextView) v.findViewById(R.id.sorry_text);
                    sorry_text.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);

                }

            }


        } catch (JSONException e) {
            System.out.println("ERROR : onPostExecute");
            StandardNewTipsFragment.this.getActivity().finish();
            e.printStackTrace();
        } catch (Exception e){
            System.out.println("ERROR : onPostExecute");
            e.printStackTrace();
        }




        return v;
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
