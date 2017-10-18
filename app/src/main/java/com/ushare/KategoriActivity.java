package com.ushare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ushare.adapter.SubAdapter;
import com.ushare.app.myapp;
import com.ushare.model.ItemSub;
import com.ushare.util.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class KategoriActivity extends AppCompatActivity {
    Toolbar mToolbar;
    GridView gridView;
    ArrayList<ItemSub> itemList;
    SubAdapter adapter;
    String URLKATE;
    int colorValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kategori);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("KATEGORI");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        gridView = (GridView) findViewById(R.id.gridView);
        colorValue = getIntent().getIntExtra("color", 0);
        LinearLayout layout = (LinearLayout)findViewById(R.id.activity_kategori);
        layout.setBackgroundColor(colorValue);
        itemList = new ArrayList<>();
        adapter = new SubAdapter(this, itemList, colorValue);
        gridView.setAdapter(adapter);
        URLKATE = Constant.URLAPI + "key=" + Constant.KEY + "&tag=" + Constant.TAG_SUB;
        daftarKategori();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                ItemSub item = (ItemSub) parent.getItemAtPosition(position);
                Intent intentlist = new Intent(KategoriActivity.this, ProdukActivity.class);
                intentlist.putExtra("id", item.getId());
                startActivity(intentlist);
            }
        });
    }

    private void daftarKategori() {
        JsonObjectRequest jsonKate = new JsonObjectRequest(URLKATE, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                itemList.clear();
                parseJsonKategory(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        jsonKate.setRetryPolicy(new DefaultRetryPolicy(5000, 20, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        myapp.getInstance().addToRequestQueue(jsonKate);
    }

    private void parseJsonKategory(JSONObject response) {
        try {
            JSONArray feedArray = response.getJSONArray("data");
            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject feedObj = (JSONObject) feedArray.get(i);
                ItemSub item = new ItemSub();
                item.setId(feedObj.getString("sub_id"));
                item.setName(feedObj.getString("nama_sub"));
                item.setImage(feedObj.getString("gambar"));
                itemList.add(item);
            }
        } catch (JSONException e) {
        }
        adapter.notifyDataSetChanged();
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
}
