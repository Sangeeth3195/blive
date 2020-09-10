package com.blive.activity;

import androidx.appcompat.app.ActionBar;
import android.view.MenuItem;


public class BaseScannerActivity extends BaseBackActivity {
    public void setupToolbar() {
        setTitle("QR Scan");
        final ActionBar ab = getSupportActionBar();
        if(ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
