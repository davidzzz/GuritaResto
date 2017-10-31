package com.ushare;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import com.ushare.app.myapp;
import com.ushare.model.ItemOrder;
import com.ushare.util.Constant;
import com.ushare.util.SessionManager;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class OrderDetail extends AppCompatActivity {
    private Toolbar toolbar;
    TextView txtTotal, txtName, txtStatus,textNotes,txtbiodata,txtOngkir,txtSubtotal, orderList;
    Button btnCancel, btnAcpt;
    String ID, URL_CANCEL, userid, URL_ACCEPT;
    SessionManager session;
    HashMap<String, String> user;
    LinearLayout lytbiodata,lytbtn;
    ItemOrder item_order;
    DecimalFormat formatduit = new DecimalFormat();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_detail);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Order Detail");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        lytbtn = (LinearLayout)findViewById(R.id.lytbtn);
        session = new SessionManager(getApplicationContext());
        user = session.getUserDetails();
        userid = user.get(session.KEY_PASSENGER_ID);
        lytbiodata = (LinearLayout)findViewById(R.id.lytbiodata);
        txtTotal = (TextView) findViewById(R.id.txtTotal);
        txtName = (TextView) findViewById(R.id.txtName);
        txtStatus = (TextView) findViewById(R.id.txtStatus);
        txtbiodata = (TextView)findViewById(R.id.txtbiodata);
        textNotes = (TextView) findViewById(R.id.textNotes);
        txtOngkir = (TextView)findViewById(R.id.txtOngkir);
        txtSubtotal =(TextView)findViewById(R.id.txtSubtotal);
        orderList = (TextView) findViewById(R.id.orderList);
        URL_CANCEL = Constant.URLADMIN + "api/cancel_seller.php";
        btnAcpt = (Button) findViewById(R.id.btnAcpt);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        item_order = getIntent().getParcelableExtra("item_order");
        ID = item_order.getId();
        String akses = user.get(SessionManager.KEY_AKSES);
        if (akses.equals("1") || item_order.getStatus().equals("CANCEL") || item_order.getStatus().equals("DONE")) {
            lytbtn.setVisibility(View.GONE);
        }
        if (item_order.isOnTheSpot()) {
            lytbiodata.setVisibility(View.GONE);
            LinearLayout layout_ongkir = (LinearLayout) findViewById(R.id.layout_ongkir);
            layout_ongkir.setVisibility(View.GONE);
        }
        txtbiodata.setText("DETAIL BUYER \n\nAddress: " + item_order.getAddress() + "\nNo. Handphone : "+ item_order.getTelp());
        txtStatus.setText(item_order.getStatus().equals("") ? "PENDING" : item_order.getStatus());
        txtName.setText("Nama : " + item_order.getNama());
        textNotes.setText(item_order.getCatatan());
        txtTotal.setText(String.valueOf(formatduit.format(item_order.getTotal())));
        txtOngkir.setText(String.valueOf(formatduit.format(item_order.getTtlongkir())));
        txtSubtotal.setText(String.valueOf(formatduit.format(item_order.getTtlongkir() + item_order.getTotal())));
        String itemList = item_order.getItemDetail().toString();
        orderList.setText(itemList.substring(1, itemList.length() - 1).replace(", ", "\n"));
        orderList.setBackgroundColor(Color.parseColor("#ffffff"));
        btnAcpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AcceptOrder();

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(OrderDetail.this);
                alert.setTitle(R.string.app_name);
                alert.setIcon(R.drawable.ic_launcher);
                alert.setMessage("Cancel This Order ??");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final ProgressDialog loading = ProgressDialog.show(OrderDetail.this,"Send Data...","Please wait...",false,true);
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_CANCEL,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String s) {
                                        //Disimissing the progress dialog
                                        loading.dismiss();
                                        Toast.makeText(OrderDetail.this, s, Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError volleyError) {
                                        //Dismissing the progress dialog
                                        loading.dismiss();
                                        Toast.makeText(OrderDetail.this, "Server error", Toast.LENGTH_LONG).show();
                                    }
                                }){
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                //Converting Bitmap to String
                                //Creating parameters
                                Map<String,String> params = new Hashtable<String, String>();
                                //Adding parameters
                                params.put("order_id", ID);
                                params.put("gcm", item_order.getGcm_id());
                                params.put("key", Constant.KEY);
                                params.put("user_id", userid);
                                //returning parameters
                                return params;
                            }
                        };
                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 20, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        myapp.getInstance().addToRequestQueue(stringRequest);
                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });
                alert.show();
            }
        });
    }

    private void AcceptOrder() {
        URL_ACCEPT = Constant.URLADMIN + "api/accept.php";
        final ProgressDialog loading = ProgressDialog.show(OrderDetail.this,"Send data...","Please wait...",false,true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_ACCEPT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        Toast.makeText(OrderDetail.this, s, Toast.LENGTH_LONG).show();
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();
                        Toast.makeText(OrderDetail.this, "Server error", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();
                //Adding parameters
                params.put("order_id", ID);
                params.put("key", Constant.KEY);
                params.put("gcm", item_order.getGcm_id());
                //returning parameters
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 20, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        myapp.getInstance().addToRequestQueue(stringRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // app icon in action bar clicked; go home
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.open_main, R.anim.close_next);
    }

}
