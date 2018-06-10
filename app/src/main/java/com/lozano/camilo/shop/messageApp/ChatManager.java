package com.lozano.camilo.shop.messageApp;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.lozano.camilo.shop.GlobalClassMet;
import com.lozano.camilo.shop.Notification;
import com.lozano.camilo.shop.R;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ChatManager extends AppCompatActivity {

    private ListView listChat;
    private ArrayList<Message> list;

    messageAdapter messageAdapter;
    GlobalClassMet gbl;
    SharedPreferences prefsUser;
    String localhost;
    EditText messageChat;
    int idCompany;
    Integer idMaster;
    int i = 0;
    Socket mSocket;
    JSONObject jsonSendMsg = new JSONObject();
    ProgressDialog dialog;
    Integer idDocument;
    JSONObject jsonMns = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        list = new ArrayList<>();
        messageAdapter = new messageAdapter(this, list);
        listChat = (ListView) findViewById(R.id.listmessage);
        listChat.setAdapter(messageAdapter);

        gbl = new GlobalClassMet(this);
        prefsUser = getSharedPreferences("UserData", this.MODE_PRIVATE);
        localhost = (prefsUser.getString("localhost", getResources().getString(R.string.localhost)));


        SocketConnectMeth();
        mSocket.on("send-msg-app", onNewMessage);
        mSocket.connect();
        System.err.println(mSocket.id());
        // mSocket.emit("get-chat-on", jsonSendMsg);

        idCompany = getIntent().getIntExtra("idCompany", 0);
        idDocument = getIntent().getIntExtra("idDocument", 0);
        idMaster = getIntent().getIntExtra("idMaster", 0);


        messageChat = (EditText) findViewById(R.id.messageChat);

        if (!getIntent().getBooleanExtra("statusChat", false)) {
            messageChat.setEnabled(false);
            Toast.makeText(this, "Su Solicitud está siendo procesada", Toast.LENGTH_SHORT).show();
        }
        LoadingChat();
    }

    private void LoadingChat() {


        String url = "http://" + localhost + ":8000/chat/talk/"+idMaster+"&1&1";
        System.err.println(url);

        if (gbl.ConectionValidate()) {
            if(!((ChatManager) this).isFinishing())
            {
                dialog = new ProgressDialog(this);
                dialog.setMessage("Loading..");
                dialog.show();
            }

            RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                    JSONArray js = new JSONArray(response.getString("data"));
                                    DecerializeJSONarray(js);
                                    if (dialog.isShowing()) dialog.dismiss();

                            } catch (JSONException e) {
                                System.err.println("error " + e.getMessage());
                                gbl.ProblemApp(ChatManager.this);
                                if (dialog.isShowing()) dialog.dismiss();
                            }

                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {

                            gbl.ToastProblem(ChatManager.this);
                            if (dialog.isShowing()) dialog.dismiss();

                        }
                    });
            queue.add(jsObjRequest);

        } else {
            gbl.ToastInternetNot(ChatManager.this);
        }
    }

    private void DecerializeJSONarray(JSONArray jsonArray) {

        list.removeAll(list);
        Message p;
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject item = jsonArray.getJSONObject(i);

                if (item.getString("flag").equals("empresa")) {
                    p = new Message(item.getString("message"),
                            item.getString("quien"),
                            (item.getString("createdAt").split("T")[1].substring(0,8)),
                            false);

                } else {
                    p = new Message(item.getString("message"),
                            item.getString("quien"),
                            (item.getString("createdAt").split("T")[1].substring(0,8)),
                            true);

                }
                list.add(p);

            } catch (JSONException e) {
                System.err.println("Decerialize...  " + e.getMessage());
            }
        }
        messageAdapter.notifyDataSetChanged();

    }


    public void SendMsgChat(View view) {
        if (messageChat.getText().toString().equals("")) return;

        try {
            jsonMns.put("idAppUser", prefsUser.getString("idSystemUser", "no id"))
                    .put("message", messageChat.getText().toString().trim())
                    .put("idDocument", idDocument)
                    .put("idMaster", idMaster);
        } catch (JSONException e) {

        }

        String url = "http://" + localhost + ":8000/chat";
        if (gbl.ConectionValidate()) {
            dialog = new ProgressDialog(this);
            dialog.setMessage("Loading..");
            dialog.show();
            RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.POST, url, jsonMns, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.getString("success").equals("true")) {
                                    // si se guarda agrega el mns a chat

                                    Calendar c = Calendar.getInstance();
                                    String hour = c.get(Calendar.HOUR) == 0 ? "00" : c.get(Calendar.HOUR) + "";
                                    hour = hour.length() > 1 ? hour : "0" + hour;
                                    String minutes = c.get(Calendar.MINUTE) + "".length() == 1 ? c.get(Calendar.MINUTE) + "" : c.get(Calendar.MINUTE) + "";

                                    Message m;

                                    m = new Message(messageChat.getText().toString().trim(),
                                            prefsUser.getString("name", "Nombre"),
                                            hour + ":" + minutes,
                                            true);

                                    list.add(m);
                                    messageAdapter.notifyDataSetChanged();
                                    messageChat.setText("");

                                    if (dialog.isShowing()) dialog.dismiss();
                                } else {
                                    Toast.makeText(ChatManager.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                                    if (dialog.isShowing()) dialog.dismiss();
                                }


                            } catch (JSONException e) {
                                System.err.println("error " + e.getMessage());
                                gbl.ProblemApp(ChatManager.this);
                                if (dialog.isShowing()) dialog.dismiss();
                            }

                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {

                            gbl.ToastProblem(ChatManager.this);
                            if (dialog.isShowing()) dialog.dismiss();

                        }
                    });
            queue.add(jsObjRequest);

        } else {
            gbl.ToastInternetNot(ChatManager.this);
        }


    }


    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ChatManager.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    System.err.println(args[0]);
                    LoadingChat();
                    messageAdapter.notifyDataSetChanged();


                }
            });
        }
    };

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

    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
    }
}
