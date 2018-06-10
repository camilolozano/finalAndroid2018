package com.lozano.camilo.shop.start_content;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.lozano.camilo.shop.*;
import com.lozano.camilo.shop.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Login extends AppCompatActivity implements FacebookCallback<LoginResult> {


    CallbackManager callbackManager;
    Button loginBig;
    TextView register;
    Animation animation;
    LayoutAnimationController controller;
    LinearLayout in, log, phoneIN;
    TextInputLayout input_emailSt, input_passwSt;
    EditText EmailStart, PasswordStart;
    SharedPreferences prefsUser;
    SharedPreferences.Editor edit;
    String ReloadRegister;
    private static String[] permission = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};
    int read, read2;
    String localhost;
    JSONObject jsonObject = new JSONObject();
    GlobalClassMet gbl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        read = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        read2 = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (read == PackageManager.PERMISSION_DENIED || read2 == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, permission, 1);
        }

        super.onCreate(savedInstanceState);
        setContentView(com.lozano.camilo.shop.R.layout.activity_login);

        // layouts efecto animacion
        log = (LinearLayout) findViewById(R.id.log);
        in = (LinearLayout) findViewById(R.id.startLayout);
        phoneIN = (LinearLayout) findViewById(R.id.startPhoneLayout);

        gbl = new GlobalClassMet(this);


        // inicio elementos lngreso
        EmailStart = (EditText) findViewById(R.id.EmailStart);
        PasswordStart = (EditText) findViewById(R.id.PasswordStart);
        input_emailSt = (TextInputLayout) findViewById(R.id.input_emailSt);
        input_passwSt = (TextInputLayout) findViewById(R.id.input_passwSt);


        callbackManager = CallbackManager.Factory.create();
        loginBig = (Button) findViewById(R.id.loginBig);

        register = (TextView) findViewById(R.id.register);

        // preferens for user
        prefsUser = getSharedPreferences("UserData", this.MODE_PRIVATE);
        localhost =(prefsUser.getString("localhost", getResources().getString(R.string.localhost)));

        ReloadRegister = (prefsUser.getString("email", "Problemas con el correo"));
        if (!ReloadRegister.equals("Problemas con el correo")) {
            startSesionloading();
            EmailStart.setText(ReloadRegister);

            if (getIntent().getStringExtra("MsgFirstRegistres") != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Ingrese  a su correo para la validación de tu cuenta")
                        .setTitle("Información")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                builder.create().show();
            }
        }




    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSuccess(LoginResult loginResult) {

    }

    private void NexActivity() {
        Intent pIntent = new Intent().setClass(this, Principal.class);
        startActivity(pIntent);
        finish();
    }

    @Override
    public void onCancel() {
        Toast.makeText(this, "Cancelado", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onError(FacebookException error) {
        Toast.makeText(this, "Error " + error, Toast.LENGTH_SHORT).show();

    }


    public void StartSessionApp(View view) {
        if (EmailStart.getText().toString().equals("") || EmailStart.getText().toString().equals(null)) {
            input_emailSt.setError("Digite correo");
        } else if (!isValidEmail(EmailStart.getText().toString().trim())) {
            input_emailSt.setError("Email Invalido");
        } else if (PasswordStart.getText().toString().equals("") || PasswordStart.getText().toString().equals(null)) {
            input_passwSt.setError("DIgite Contraseña");
            input_emailSt.setErrorEnabled(false);
        } else {
            input_emailSt.setErrorEnabled(false);
            input_passwSt.setErrorEnabled(false);

            try {
                jsonObject.put("email", EmailStart.getText().toString().trim().toLowerCase())
                        .put("password", PasswordStart.getText().toString());
                LoginInto();
            } catch (JSONException e) {
                e.printStackTrace();
            }



           /*  Key key = MacProvider.generateKey();
           String compactJws = Jwts.builder().setSubject(String.valueOf(jsonObject)).signWith(SignatureAlgorithm.HS512, key).compact();
          System.err.println("llave: "+compactJws);
            String s = Jwts.parser().setSigningKey(key).parseClaimsJws(compactJws).getBody().getSubject();
            System.err.println("desencrip: "+s);*/
        }

    }


    public void startSesionBigapp(View view) {
        startSesionloading();
    }

    public void startSesionloading() {
        log.setVisibility(View.GONE);
        animationApp(true);
        in.setLayoutAnimation(controller);
        in.startAnimation(animation);
        in.setVisibility(View.VISIBLE);
    }

    public void AnimationRegister(View view) {
        Intent intent = new Intent().setClass(this, Register.class);
        startActivity(intent);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (log.getVisibility() == View.VISIBLE) {
                return super.onKeyDown(keyCode, event);
            } else if (in.getVisibility() == View.VISIBLE) {
                in.setVisibility(View.GONE);
                animationApp(false);
                in.setLayoutAnimation(controller);
                in.startAnimation(animation);
                log.setVisibility(View.VISIBLE);

            } else if (phoneIN.getVisibility() == View.VISIBLE) {
                phoneIN.setVisibility(View.GONE);
                animationApp(false);
                phoneIN.setLayoutAnimation(controller);
                phoneIN.startAnimation(animation);
                log.setVisibility(View.VISIBLE);
            }
            return true;

        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void animationApp(boolean mostrar) {
        AnimationSet set = new AnimationSet(true);
        animation = null;
        if (mostrar) {
            animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 1.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 1.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
        } else {
            animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
        }
        animation.setDuration(500);
        set.addAnimation(animation);
        LayoutAnimationController controller = new LayoutAnimationController(set, 0.25f);


    }

    public static boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    public void urlchangue(View view) {
        createLoginDialogo().show();
    }

    public AlertDialog createLoginDialogo() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();

        View v = inflater.inflate(R.layout.url_changue, null);
        final TextView uril = (TextView) v.findViewById(R.id.uril);

        localhost = (prefsUser.getString("localhost", getResources().getString(R.string.localhost)));
        uril.setText(localhost);
        builder.setView(v);

        builder.setTitle("Titulo")
                .setMessage("El Mensaje para el usuario")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                edit = prefsUser.edit();
                                edit.putString("localhost", uril.getText().toString());
                                edit.commit();
                            }
                        })
                .setNegativeButton("CANCELAR",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

        return builder.create();
    }

    public void LoginInto() {
        if (gbl.ConectionValidate()) {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Loading..");
            dialog.show();
            String urlvalidate = "http://" + localhost + ":8000/app-login";
            System.err.println(urlvalidate);
            final RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, urlvalidate, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.getString("success").equals("true")) {
                            if (!prefsUser.getString("idSystemUser", "").equals(response.getString("idSystemUser"))) {
                                prefsUser.edit().clear().commit();
                            }
                            edit = prefsUser.edit();
                            edit.putString("type_login", "bigapp");
                            edit.putString("idSystemUser", response.getString("idSystemUser"));
                            edit.putString("email", response.getString("DBemail"));
                            edit.putString("name", response.getString("fullName"));
                            edit.putString("address", response.getString("DBaddress"));
                            edit.putString("sesionOn", "ok");
                            edit.putString("urlImg", response.getString("avatar"));
                            edit.putString("SendRequest", "0");
                            edit.commit();
                            NexActivity();
                        } else {
                            Toast.makeText(Login.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            if (dialog.isShowing()) dialog.dismiss();
                        }
                    } catch (JSONException e) {
                        gbl.ProblemApp(Login.this);
                        e.printStackTrace();
                    }
                    if (dialog.isShowing()) dialog.dismiss();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    gbl.ToastProblem(Login.this);
                    if (dialog.isShowing()) dialog.dismiss();
                    System.err.println(error);
                }
            });
            queue.add(request);


        } else {
            gbl.ToastInternetNot(this);
        }
    }

    public void StartPhoneIN(View view) {
        Toast.makeText(this, "Registro con telefono", Toast.LENGTH_SHORT).show();
    }

    public void startSesionPhoneIN(View view) {
        log.setVisibility(View.GONE);
        animationApp(true);
        phoneIN.setLayoutAnimation(controller);
        phoneIN.startAnimation(animation);
        phoneIN.setVisibility(View.VISIBLE);
    }
}
