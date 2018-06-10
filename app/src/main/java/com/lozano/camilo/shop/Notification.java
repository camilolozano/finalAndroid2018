package com.lozano.camilo.shop;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.lozano.camilo.shop.ListViewApp.ItemCompany;
import com.lozano.camilo.shop.RecyclerApp.GridSpacingItemDecoration;
import com.lozano.camilo.shop.RecyclerApp.RecyclerAdapter;
import com.lozano.camilo.shop.RecyclerApp.RecyclerItemClickListener;
import com.lozano.camilo.shop.messageApp.ChatManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Notification extends AppCompatActivity {


    GlobalClassMet gbl;
    SharedPreferences prefsUser;
    String localhost;
    ProgressDialog dialog;

    private RecyclerView recyclerProduct;
    private RecyclerAdapter adapter;
    private List<ItemCompany> itemCompanyList;
    RecyclerView.LayoutManager mLayoutManager;
    ImageView PhotoCheckChat;
    ImageView checkMap;
    int positionRecycler;
    int numItems = 0;
    int NumItemsJson = 0;
    JSONArray js;
    int TypeDecerializacion;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*Fade fadein = new Fade(Fade.IN);
        fadein.setDuration(1000);
        fadein.setInterpolator(new DecelerateInterpolator());
        getWindow().setEnterTransition(fadein);*/

        setContentView(R.layout.activity_notification);
        TypeDecerializacion = getIntent().getIntExtra("valueDeserialize", 0);

        recyclerProduct = (RecyclerView) findViewById(R.id.recycler_view);
        itemCompanyList = new ArrayList<>();

        adapter = new RecyclerAdapter(this, itemCompanyList, 0);
        mLayoutManager = new GridLayoutManager(this, 1);
        recyclerProduct.setLayoutManager(mLayoutManager);
        recyclerProduct.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerProduct.setItemAnimator(new DefaultItemAnimator());
        recyclerProduct.setAdapter(adapter);


        recyclerProduct.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerProduct, new RecyclerItemClickListener.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        positionRecycler = position;

                        PhotoCheckChat = (ImageView) view.findViewById(R.id.PhotoCheckChat);
                        checkMap = (ImageView) view.findViewById(R.id.checkMap);

                        PhotoCheckChat.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent nextChat = new Intent(Notification.this, ChatManager.class);
                                nextChat.putExtra("idCompany", itemCompanyList.get(positionRecycler).getIdComp());
                                nextChat.putExtra("statusChat", itemCompanyList.get(positionRecycler).isEstatusimgCheck());
                                nextChat.putExtra("idDocument", itemCompanyList.get(positionRecycler).getIdDocument());
                                nextChat.putExtra("idMaster", itemCompanyList.get(positionRecycler).getIdMaster());
                                startActivity(nextChat);
                            }
                        });

                        checkMap.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //Toast.makeText(Notification.this, "map", Toast.LENGTH_SHORT).show();
                                Intent nextMap = new Intent(Notification.this, DetailsMap.class);
                                nextMap.putExtra("lat", itemCompanyList.get(positionRecycler).getLatitud());
                                nextMap.putExtra("lon", itemCompanyList.get(positionRecycler).getLongitud());
                                nextMap.putExtra("nameCompany", itemCompanyList.get(positionRecycler).getNameComp());
                                startActivity(nextMap);
                            }
                        });


                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }


                })
        );


        gbl = new GlobalClassMet(this);
        prefsUser = getSharedPreferences("UserData", this.MODE_PRIVATE);
        localhost = (prefsUser.getString("localhost", getResources().getString(R.string.localhost)));

        fillLlist();
    }

    private void fillLlist() {
        String url = "http://" + localhost + ":8000/offers/list-offers-company/" + prefsUser.getString("idSystemUser", "no id");
        System.err.println(url);
        if (gbl.ConectionValidate()) {
            dialog = new ProgressDialog(this);
            dialog.setMessage("Loading..");
            dialog.show();
            RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.getString("success").equals("true")) {
                                    js = new JSONArray(response.getString("data"));

                                    DecerializeJSONarray(js);
                                    if (dialog.isShowing()) dialog.dismiss();
                                } else {
                                    Toast.makeText(Notification.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                                    if (dialog.isShowing()) dialog.dismiss();
                                }


                            } catch (JSONException e) {
                                System.err.println("error " + e.getMessage());
                                gbl.ProblemApp(Notification.this);
                                if (dialog.isShowing()) dialog.dismiss();
                            }

                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {

                            gbl.ToastProblem(Notification.this);
                            if (dialog.isShowing()) dialog.dismiss();

                        }
                    });
            queue.add(jsObjRequest);


        } else {
            gbl.ToastInternetNot(Notification.this);
        }
    }

    private void DecerializeJSONarray(JSONArray jsonArray) {
        itemCompanyList.removeAll(itemCompanyList);
        ItemCompany p;
        NumItemsJson = jsonArray.length();
        System.err.println("Num " + TypeDecerializacion);
        switch (TypeDecerializacion) {
            case 2:
                numItems = jsonArray.length() - 1;
                break;
            case 3:
                if (jsonArray.length() > 3) {
                    numItems = numItems + 2;
                } else {
                    numItems = jsonArray.length() - 1;
                }

                break;
            default:
                numItems = numItems + 2;
                break;

        }


        for (int i = 0; i < numItems + 1; i++) {
            try {
                JSONObject item = jsonArray.getJSONObject(i);
                p = new ItemCompany(
                        item.getInt("idDocument"),
                        item.getInt("idCompany"),
                        item.getInt("idMaster"),
                        item.getString("namebusiness"),
                        item.getString("avatarCompany"),
                        item.getString("answerText"),
                        item.getString("searchText"),
                        true,
                        item.getString("latitude"),
                        item.getString("longitude"));

                itemCompanyList.add(p);

            } catch (JSONException e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                System.err.println("Decerialize...  " + e.getMessage());
            }
        }


        adapter.notifyDataSetChanged();

        System.err.println(numItems + " ...");
        if (numItems != -1) {
            recyclerProduct.smoothScrollToPosition(numItems);
            numItems--;
        } else {
            Toast.makeText(this, "No hay Solicitudes", Toast.LENGTH_SHORT).show();
        }


    }

    //Recycler Converting dp to pixel
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }


    public void more_iremsR(View view) {
        fillLlist();
        if (NumItemsJson >= (itemCompanyList.size() + 1)) {
            if (NumItemsJson > ((itemCompanyList.size() + 3))) {
                numItems++;
                DecerializeJSONarray(js);
            } else {
                TypeDecerializacion = 2;
                DecerializeJSONarray(js);
            }

        } else {
            Toast.makeText(this, "No se obtuvo mas opciones", Toast.LENGTH_SHORT).show();
        }
    }
}
