package com.lozano.camilo.shop;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.transition.Transition;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.lozano.camilo.shop.GridOffertsApp.AdapterGrid;
import com.lozano.camilo.shop.RecyclerApp.Item_products;
import com.lozano.camilo.shop.start_content.Login;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.github.nkzawa.emitter.Emitter;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import de.hdodenhof.circleimageview.CircleImageView;

public class Principal extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener, MenuItemCompat.OnActionExpandListener {

    private FloatingActionMenu fam;
    private SearchView searchView;

    String url;
    TextView Nav_email, Nav_Name;
    CircleImageView Nav_photo;
    ProgressDialog dialog;

    // gridview
    private GridView grid;
    AdapterGrid adapterGrid;
    //Recycler
    private List<Item_products> itemOfferts;
    SharedPreferences prefsUser;
    String imgbyts;
    String localhost;
    String typeLogin;
    GlobalClassMet gbl;
    String search;
    TextView texDialog;


    int mCartItemCount;
    TextView textCartItemCount;
    SharedPreferences.Editor edit;

    JSONObject jsonSendCOmpany = new JSONObject();
    Socket mSocket;

    EditText searchMoment;
    RelativeLayout LayoutSearch;
    String NameUser;
    MenuItem searchItem;
    FloatingActionButton fabPremium, fabmedium, fabLower;
    AlertDialog.Builder builder;
    LayoutInflater inflater;
    Dialog progress;
    Button CancelRequest;
    requiredTime timer = null;
    int countcicleTimer = 0;
    String MsgRequestOn;
    int NumWords;
    int CountWordsFalse;
    int NumCompanysAccept = 0;
    SharedPreferences configuration;
    private static String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
    int read;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        searchMoment = (EditText) findViewById(R.id.searchMoment);
        LayoutSearch = (RelativeLayout) findViewById(R.id.LayoutSearch);
        fabPremium = (FloatingActionButton) findViewById(R.id.fabPremium);
        fabmedium = (FloatingActionButton) findViewById(R.id.fabmedium);
        fabLower = (FloatingActionButton) findViewById(R.id.fabLower);
        PremiumOnclick();
        fabmediumOnclick();
        fabLowerOnclick();

        read = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (read == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, permission, 1);
        }

        configuration = PreferenceManager.getDefaultSharedPreferences(this);
        if (configuration.getBoolean("Enable", false)) {
            Toast.makeText(this, configuration.getString("Distance", "no hay"), Toast.LENGTH_LONG).show();
        }

        prefsUser = getSharedPreferences("UserData", this.MODE_PRIVATE);
        edit = prefsUser.edit();
        localhost = prefsUser.getString("localhost", String.valueOf(R.string.localhost));
        url = "http://" + localhost + ":8000/offerts";
        NameUser = prefsUser.getString("name", "Problemas con Nombre");
        searchMoment.setHint(NameUser.split(" ")[0] + ", que deseas buscar");

        gbl = new GlobalClassMet(this);

        SocketConnectMeth();
        mSocket.on("alert", onNewMessage);


        grid = (GridView) findViewById(R.id.GridOferts);
        itemOfferts = new ArrayList<>();


        adapterGrid = new AdapterGrid(this, itemOfferts);
        grid.setAdapter(adapterGrid);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(Principal.this, "item grid OFERTS", Toast.LENGTH_SHORT).show();
            }
        });

        LoadOfferts();

        countNotification();


        fam = (FloatingActionMenu) findViewById(R.id.fab_menu);

        fam.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                if (opened) {
                    //  Toast.makeText(Principal.this, "Url Actualizada", Toast.LENGTH_SHORT).show();

                } else {
                    // itemProducts.removeAll(itemProducts);
                    // LoadProductList();
                }
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView = navigationView.getHeaderView(0);

        Nav_email = (TextView) hView.findViewById(R.id.txMail);
        Nav_Name = (TextView) hView.findViewById(R.id.txtName);
        Nav_photo = (CircleImageView) hView.findViewById(R.id.NavPhoto);

        Nav_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, 100);
            }
        });

        Nav_email.setText(prefsUser.getString("email", "Problemas con el correo"));
        Nav_Name.setText(NameUser);
        imgbyts = prefsUser.getString("Imgbyts", null);
        typeLogin = prefsUser.getString("type_login", "ninguno");


        if (typeLogin.equals("bigapp")) {
            if (prefsUser.getString("urlImg", "").contains("http")) {
                if (gbl.ConectionValidate()) {
                    Picasso.with(getApplicationContext()).load(prefsUser.getString("urlImg", "https://imageog.flaticon.com/icons/png/512/16/16480.png?size=1200x630f&pad=10,10,10,10&ext=png&bg=FFFFFFFF")).resize(82, 75).placeholder(R.drawable.user).into(Nav_photo);
                }
            } else if (imgbyts != null) {
                loadingPicture();
            } else Nav_photo.setImageResource(R.drawable.user);

        } else {
            if (prefsUser.getString("urlImg", "").contains("http")) {
                // if api facebook
                if (AccessToken.getCurrentAccessToken() != null) {
                    // validation internet conection
                    if (gbl.ConectionValidate()) {
                        Picasso.with(getApplicationContext()).load(prefsUser.getString("urlImg", "")).into(Nav_photo);

                    } else {
                        // load img preferences when there is no internet
                        if (imgbyts != null) {
                            loadingPicture();
                        }
                    }
                }
            } else {
                loadingPicture();
            }
        }


    }

    private void SocketConnectMeth() {
        if (mSocket == null) {
            try {
                IO.Options opts = new IO.Options();
                opts.forceNew = true;
                mSocket = IO.socket("http://" + localhost + ":8000/");
                mSocket.connect();
            } catch (URISyntaxException e) {
            }
        }
    }

    private void countNotification() {
        url = "http://" + localhost + ":8000/offers/offers-company-count-app/" + prefsUser.getString("idSystemUser", "0");
        System.err.println("uri count "+url);
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            System.err.println(response.getString("success")+"       data: "+response.getInt("data"));
                            if (response.getString("success").equals("true")) {
                                mCartItemCount = response.getInt("data");
                                setupBadge();

                            } else {
                                Toast.makeText(Principal.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        queue.add(jsObjRequest);

    }

    public void LoadOfferts() {
        if (gbl.ConectionValidate()) {
            dialog = new ProgressDialog(this);
            dialog.setMessage("Loading..");
            dialog.show();
            url = "http://" + localhost + ":8000/offerts";
            RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.getString("success").equals("true")) {
                                    JSONArray js = new JSONArray(response.getString("data"));
                                    DecerializeJSONOfferts(js);
                                    adapterGrid.notifyDataSetChanged();
                                    if (dialog.isShowing()) dialog.dismiss();
                                } else {
                                    Toast.makeText(Principal.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                                    if (dialog.isShowing()) dialog.dismiss();
                                }

                            } catch (JSONException e) {
                                gbl.ProblemApp(Principal.this);
                                if (dialog.isShowing()) dialog.dismiss();
                            }

                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            gbl.ToastProblem(Principal.this);
                            if (dialog.isShowing()) dialog.dismiss();

                        }
                    });
            queue.add(jsObjRequest);


        } else {
            gbl.ToastInternetNot(Principal.this);
        }

    }

    private void DecerializeJSONOfferts(JSONArray js) {

        Item_products p;
        for (int i = 0; i < js.length(); i++) {
            try {
                JSONObject item = js.getJSONObject(i);
                p = new Item_products(item.getInt("id"),
                        item.getString("nameProduct"),
                        item.getString("offert"),
                        "$" + item.getString("price"),
                        20,
                        20,
                        item.getString("img_uri"),
                        item.getString("lat"),
                        item.getString("long"));

                itemOfferts.add(p);
            } catch (JSONException e) {
                System.err.println("Decerialize..  " + e.getMessage());
            }


        }
        adapterGrid.notifyDataSetChanged();
    }


    private void loadingPicture() {
        byte[] data = Base64.decode(imgbyts, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        Drawable d = new BitmapDrawable(getResources(), bitmap);
        Nav_photo.setImageDrawable(d);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(180, 180);
        Nav_photo.setLayoutParams(params);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 100) {
            InputStream streams = null;
            try {
                streams = getContentResolver().openInputStream(data.getData());
                Bitmap bitmap = BitmapFactory.decodeStream(streams);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                String base64_imagen = Base64.encodeToString(byteArray, Base64.DEFAULT);
                edit.putString("Imgbyts", base64_imagen);

                edit.putString("urlImg", data.getDataString());
                edit.commit();
                Nav_photo.setImageURI(Uri.parse(data.getDataString()));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(180, 180);
                Nav_photo.setLayoutParams(params);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.principal, menu);
        searchItem = menu.findItem(R.id.action_search);
        final MenuItem menuItem = menu.findItem(R.id.notification_action);


        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        MenuItemCompat.setOnActionExpandListener(searchItem, this);

        searchView.setQueryHint("Producto que desea Buscar");


        searchMoment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prefsUser.getString("type_search", "").equals("")) {
                    DialogCategory();
                } else {
                    LayoutSearch.setVisibility(View.GONE);
                    searchItem.expandActionView();
                }

            }
        });


        View actionView = MenuItemCompat.getActionView(menuItem);
        textCartItemCount = (TextView) actionView.findViewById(R.id.cart_badge);

        setupBadge();

        actionView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                // if (mCartItemCount == 0 && Integer.parseInt(prefsUser.getString("SendRequest", "0")) == 0) {
                // esto no debe ir
                if (mCartItemCount == 100) {
                    Toast.makeText(Principal.this, "No hay Solicitudes Recientes", Toast.LENGTH_SHORT).show();
                } else {
                    countcicleTimer = 3;
                    onSlideClicked();
                }

            }
        });

        return super.onCreateOptionsMenu(menu);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(gallery, 100);

        } else if (id == R.id.networt_media) {

        } else if (id == R.id.nav_manage) {
            Intent manager = new Intent(this, ManagerApp.class);
            startActivity(manager);
        } else if (id == R.id.oferts) {
            finish();
            startActivity(getIntent());

        } else if (id == R.id.end_sesion) {
            edit.putString("sesionOn", "no");
            edit.commit();
            Intent Inicio = new Intent(this, Login.class);
            startActivity(Inicio);
            LoginManager.getInstance().logOut();
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        NumWords = 0;
        CountWordsFalse = 0;
        NumCompanysAccept = 0;

        if (prefsUser.getString("type_search", "").equals("")) {
            Toast.makeText(Principal.this, "Selecciona una categoria en el boton mas", Toast.LENGTH_SHORT).show();
        } else {
            if (query.length() == 0) {
                grid.setVisibility(View.VISIBLE);
            } else {


                search = query;


                builder = new AlertDialog.Builder(this);
                inflater = this.getLayoutInflater();
                builder.setCancelable(false);
                View v = inflater.inflate(R.layout.progress_alert, null);
                builder.setView(v);
                builder.setCancelable(false);
                progress = builder.create();
                texDialog = (TextView) v.findViewById(R.id.texDialog);
                CancelRequest = (Button) v.findViewById(R.id.CancelRequest);


                texDialog.setText("Atendiendo solicitud.\n" + query + "\nespere...");

                CancelRequest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(Principal.this, "Mantenga sostenido par cancelar solicitud", Toast.LENGTH_SHORT).show();

                    }
                });
                CancelRequest.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        countcicleTimer = 0;
                        ErrorMethod();
                        edit.putString("SendRequest", "0");
                        edit.commit();
                        timer.cancel();
                        progress.dismiss();
                        grid.setVisibility(View.VISIBLE);
                        Toast.makeText(Principal.this, "Solicitud Cancelada", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });


                if (gbl.ConectionValidate()) {
                    MsgRequestOn = "Atendiendo solicitud.\n" + search + "\nen ";
                    timer = new requiredTime(60000, 1000);
                    timer.start();

                    NumWords = search.split(" ").length;


                    for (int i = 0; i < search.split(" ").length; i++) {
                        if (search.split(" ")[i].length() > 2) {
                            new MyTask(search.split(" ")[i]).execute();
                            progress.show();
                        } else {
                            NumWords--;
                        }


                    }
                    mSocket.on("to-accept", onrequestMessage);


                } else {
                    gbl.ToastInternetNot(this);
                }
            }
        }


        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
     /*   if (newText.length() == 0) {
            grid.setVisibility(View.VISIBLE);
            recyclerProduct.setVisibility(View.GONE);

        } else {
            url = "http://" + localhost + ":8000/search-companies/1&" + newText.trim().replace(" ", "%20");
            if (newText.length() > 3) {
                search = newText;
                LoadCompanys(0);
                adapter.notifyDataSetChanged();
            }
        }*/

        return false;

    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        if (prefsUser.getString("type_search", "").equals("")) {
            DialogCategory();

        } else {
            LayoutSearch.setVisibility(View.GONE);
            grid.setVisibility(View.GONE);
        }
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        grid.setVisibility(View.VISIBLE);
        return true;
    }


    public void PremiumOnclick() {
        fabPremium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit.putString("type_search", "Premium");
                edit.commit();
                Toast.makeText(Principal.this, "Premium", Toast.LENGTH_SHORT).show();
                fam.close(true);
            }
        });


    }


    public void fabmediumOnclick() {
        fabmedium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit.putString("type_search", "Estandar");
                edit.commit();
                Toast.makeText(Principal.this, "Estandar", Toast.LENGTH_SHORT).show();
                fam.close(true);
            }
        });
    }

    public void fabLowerOnclick() {
        fabLower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit.putString("type_search", "Economico");
                edit.commit();
                Toast.makeText(Principal.this, "Economico", Toast.LENGTH_SHORT).show();
                fam.close(true);
            }
        });
    }


    private void setupBadge() {

        if (textCartItemCount != null) {
            if (mCartItemCount == 0) {
                if (textCartItemCount.getVisibility() != View.GONE) {
                    textCartItemCount.setVisibility(View.GONE);
                }
            } else {
                textCartItemCount.setText(String.valueOf(Math.min(mCartItemCount, 99)));
                if (textCartItemCount.getVisibility() != View.VISIBLE) {
                    textCartItemCount.setVisibility(View.VISIBLE);
                }
            }
        }
    }


    private Emitter.Listener onNewMessage = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            Principal.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mCartItemCount += (int) args[0];
                    setupBadge();

                }
            });
        }
    };


    private Emitter.Listener onrequestMessage = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            Principal.this.runOnUiThread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void run() {
                    System.err.println("arg " + args.length);
                    NumCompanysAccept++;
                    if (NumCompanysAccept == 3) {
                        edit.putString("SendRequest", "1");
                        edit.commit();
                        timer.cancel();
                        progress.dismiss();
                        onSlideClicked();
                    }

                }
            });
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        if (timer != (null)) timer.cancel();

    }

    private void DialogCategory() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Principal.this);
        builder.setMessage("Selecciona una categoria")
                .setTitle("Información")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (searchItem.isActionViewExpanded()) searchItem.collapseActionView();
                        dialog.cancel();
                    }
                });
        builder.create().show();
        fam.open(true);
    }


    public class MyTask extends AsyncTask<Void, Void, Void> {

        String p;

        public MyTask(String p) {
            this.p = p;
        }

        public void onPreExecute() {

            try {
                jsonSendCOmpany.put("idAppUser", prefsUser.getString("idSystemUser", "no id"))
                        .put("word", p)
                        .put("search", search.trim())
                        .put("type_search", prefsUser.getString("type_search", ""));
            } catch (JSONException e) {

            }
            url = "http://" + localhost + ":8000/search-companies/" + prefsUser.getString("idSystemUser", "no id");
            System.err.println(url);
            progress.show();
            final RequestQueue queue = Volley.newRequestQueue(Principal.this);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonSendCOmpany, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        System.err.println("Responsi: " + response.getString("success"));
                        if (response.getString("success").equals("false")) {
                            CountWordsFalse++;
                            System.err.println(NumWords + "  " + CountWordsFalse);
                            if (NumWords == CountWordsFalse) {
                                NoRequest();
                            }
                        }

                    } catch (JSONException e) {
                        gbl.ProblemApp(Principal.this);
                        System.err.println("1 " + e + "    /// " + e.getMessage());
                        ErrorMethod();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //bl.ProblemApp(Principal.this);
                    System.err.println("2 " + error.getNetworkTimeMs() + "    /// " + error.getMessage());
                    //ErrorMethod();
                }
            });

            // hace que el reques espere un momento
            request.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(request);
        }

        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

        }


    }


    public class requiredTime extends CountDownTimer {

        public requiredTime(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onFinish() {
            timer.cancel();
            countcicleTimer++;
            MsgRequestOn = "Estamos analizando el presio  más conveniente para ti.\n" + search + "\nen ";
            if (countcicleTimer == 2) {
                if (NumCompanysAccept >= 1) {
                    ErrorMethod();
                    onSlideClicked();
                    Toast.makeText(Principal.this, "tenemos estas opciones para ti", Toast.LENGTH_LONG).show();
                } else {
                    NoRequest();
                }
            } else {
                timer = new requiredTime(60000, 1000);
                timer.start();
            }


        }

        @Override
        public void onTick(long millisUntilFinished) {
            texDialog.setText(MsgRequestOn + millisUntilFinished / 1000 + " segundos o antes...");


        }
    }


    public void onSlideClicked() {

        Intent NextNoti = new Intent(Principal.this, Notification.class);
        NextNoti.putExtra("valueDeserialize", countcicleTimer);
        startActivity(NextNoti);
    }

    public void ErrorMethod() {
        grid.setVisibility(View.VISIBLE);
        timer.cancel();
        progress.dismiss();
    }

    private void NoRequest() {
        Toast.makeText(Principal.this, "No se encontro resultados a su peticion", Toast.LENGTH_SHORT).show();
        ErrorMethod();
    }


}
