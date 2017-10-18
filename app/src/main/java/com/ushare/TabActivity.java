package com.ushare;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.ushare.adapter.TabAdapter;
import com.ushare.util.SessionManager;

import org.w3c.dom.Text;

import java.util.HashMap;

public class TabActivity extends AppCompatActivity {
    ViewPager pager;
    TabAdapter adapter;
    TabLayout tabs;
    Toolbar mToolbar;
    HashMap<String, String> user;
    String tipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("VOUCHER");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        LinearLayout layoutPoin = (LinearLayout) findViewById(R.id.layout_poin);
        SessionManager session = new SessionManager(getApplicationContext());
        user = session.getUserDetails();
        String akses = user.get(SessionManager.KEY_AKSES);
        tipe = getIntent().getStringExtra("tipe");

        adapter = new TabAdapter(getSupportFragmentManager());
        if (tipe.equals("history")) {
            layoutPoin.setVisibility(View.GONE);
            adapter.setFragment(new FragOrderProses(), "ORDER");
            adapter.setFragment(new FragmentHistory(), "HISTORY");
        } else if (tipe.equals("voucher")) {
            adapter.setFragment(new VoucherList(), "VOUCHER");
            if (akses.equals("2")) {
                layoutPoin.setVisibility(View.GONE);
                adapter.setFragment(new OrderVoucher(), "ORDER VOUCHER");
            } else {
                layoutPoin.setVisibility(View.VISIBLE);
                TextView poin = (TextView) findViewById(R.id.poin);
                poin.setText("MY POINTS : " + user.get(SessionManager.KEY_POIN));
                Button spin = (Button) findViewById(R.id.spin);
                spin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(TabActivity.this, SpinActivity.class));
                    }
                });
                adapter.setFragment(new MyVoucher(), "MY VOUCHER");
            }
        }
        pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(adapter);

        tabs = (TabLayout)findViewById(R.id.tabs);
        tabs.setupWithViewPager(pager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (tipe.equals("voucher")) {
            TextView poin = (TextView) findViewById(R.id.poin);
            poin.setText("MY POINTS : " + user.get(SessionManager.KEY_POIN));
        }
    }
}
