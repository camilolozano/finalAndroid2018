package com.lozano.camilo.shop.start_content;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
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
import com.lozano.camilo.shop.GlobalClassMet;
import com.lozano.camilo.shop.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {

    TextInputLayout input_names, input_lastN, input_email, input_phone, input_Password, input_check;
    EditText names, last_name, email, phone, Password, check;
    LinearLayout reg, pol;
    ProgressDialog dialog;
    TextView msgPolitic;
    GlobalClassMet gbl;
    SharedPreferences prefsUser;
    String localhost;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        gbl = new GlobalClassMet(this);

        reg = (LinearLayout) findViewById(R.id.RegisterLayout);
        pol = (LinearLayout) findViewById(R.id.quality_policies);


        // registrarse
        names = (EditText) findViewById(R.id.names);
        last_name = (EditText) findViewById(R.id.last_name);
        email = (EditText) findViewById(R.id.email);
        phone = (EditText) findViewById(R.id.phone);
        Password = (EditText) findViewById(R.id.Password);
        check = (EditText) findViewById(R.id.check);

        input_names = (TextInputLayout) findViewById(R.id.input_names);
        input_lastN = (TextInputLayout) findViewById(R.id.input_lastN);
        input_email = (TextInputLayout) findViewById(R.id.input_email);
        input_phone = (TextInputLayout) findViewById(R.id.input_phone);
        input_Password = (TextInputLayout) findViewById(R.id.input_Password);
        input_check = (TextInputLayout) findViewById(R.id.input_check);

        msgPolitic = (TextView) findViewById(R.id.msgPolitic);
        msgPolitic.setText(Html.fromHtml(getResources().getString(R.string.msg_politicas)));

        prefsUser = getSharedPreferences("UserData", this.MODE_PRIVATE);
        localhost = (prefsUser.getString("localhost", getResources().getString(R.string.localhost)));


    }

    public void SaveRegister(View view) {

        if (names.getText().toString().equals("") || names.getText().toString().equals(null)) {
            input_names.setError("Digite sus nombres");
            return;
        }

        input_names.setErrorEnabled(false);

        if (last_name.getText().toString().equals("") || last_name.getText().toString().equals(null)) {
            input_lastN.setError("Digite sus apelllidos");
            return;
        }
        input_lastN.setErrorEnabled(false);

        if (email.getText().toString().equals("") || email.getText().toString().equals(null)) {
            input_email.setError("Digite correo");
            return;
        }
        input_email.setErrorEnabled(false);

        if (!Login.isValidEmail(email.getText().toString().trim())) {
            input_email.setError("Email Invalido");
            return;
        }
        input_email.setErrorEnabled(false);

        if (phone.getText().toString().equals("") || phone.getText().toString().equals(null)) {
            input_phone.setError("Digite su telefono");
            return;
        }
        input_phone.setErrorEnabled(false);


        if (Password.getText().toString().equals("") || Password.getText().toString().equals(null)) {
            input_Password.setError("Digite contraseña");
            return;
        }
        input_Password.setErrorEnabled(false);


        if (Password.getText().toString().length() < 6) {
            input_Password.setError("Contraseña muy debil mas de 6 caracteres");
            return;
        }
        input_Password.setErrorEnabled(false);


        if (check.getText().toString().equals("") || check.getText().toString().equals(null)) {
            input_check.setError("Digite Verificacion de contraseña");
            return;
        }
        input_check.setErrorEnabled(false);


        if (!check.getText().toString().equals(Password.getText().toString())) {
            input_check.setError("Las contraseñas no coinciden");
            return;
        }
        input_check.setErrorEnabled(false);


        NexActivity();

    }


    private void NexActivity() {
        String urlvalidate = "http://" + localhost + ":8000/create-user-app/validate-email/" + email.getText().toString().toLowerCase();
        System.err.println(urlvalidate);
        if (gbl.ConectionValidate() ){
            dialog = new ProgressDialog(this);
            dialog.setMessage("Loading..");
            dialog.show();

            final RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, urlvalidate, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.getString("success").equals("false")) {
                                    Intent pIntent = new Intent().setClass(Register.this, Map.class);
                                    pIntent.putExtra("firstNameUser", names.getText().toString());
                                    pIntent.putExtra("lastNameUser", last_name.getText().toString());
                                    pIntent.putExtra("contactUser", phone.getText().toString());
                                    pIntent.putExtra("emailUsername", email.getText().toString());
                                    pIntent.putExtra("password", Password.getText().toString());

                                    startActivity(pIntent);
                                    if (dialog.isShowing()) dialog.dismiss();

                                } else {
                                    Toast.makeText(Register.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                                    if (dialog.isShowing()) dialog.dismiss();
                                }

                            } catch (JSONException e) {
                                if (dialog.isShowing()) dialog.dismiss();
                                gbl.ProblemApp(Register.this);}

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                           gbl.ToastProblem(Register.this);
                            if (dialog.isShowing()) dialog.dismiss();
                            System.err.println(error);

                        }
                    });
            queue.add(jsObjRequest);
        } else {
            gbl.ToastInternetNot(this);
        }


    }


    public void PoliticesAcepted(View view) {
        pol.setVisibility(View.GONE);
        reg.setVisibility(View.VISIBLE);
    }


    public void loadingUriPolitic(View view) {
        if (gbl.ConectionValidate()) {
            Uri uri = Uri.parse("https://www.aviatur.com/contenidos/politica-de-privacidad");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } else {
            gbl.ToastInternetNot(this);
        }

    }

    public boolean isValidPass(String pass) {
        String PASS_PATTERN = "[a-z]+[0-9]";

        Pattern pattern = Pattern.compile(PASS_PATTERN);
        Matcher matcher = pattern.matcher(pass);
        return matcher.matches();
    }

}
