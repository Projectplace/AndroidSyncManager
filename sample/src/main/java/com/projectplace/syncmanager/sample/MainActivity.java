package com.projectplace.syncmanager.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.projectplace.syncmanager.SyncFetch;
import com.projectplace.syncmanager.SyncObject;
import com.projectplace.syncmanager.SyncUpload;
import com.projectplace.syncmanager.sample.models.Item;
import com.projectplace.syncmanager.sample.sync.MySyncManager;
import com.projectplace.syncmanager.sample.sync.SyncFetchItems;
import com.projectplace.syncmanager.sample.sync.SyncFetchLogin;
import com.projectplace.syncmanager.sample.sync.SyncUploadItem;

import java.util.ArrayList;
import java.util.List;

import retrofit.RetrofitError;

public class MainActivity extends AppCompatActivity implements SyncObject.SyncListener {

    private TextView mAccessToken;
    private TextView mExpiresIn;
    private TextView mItems;
    private List<Item> mItemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MySharedPreferences.getInstance().clearTokens();

        mAccessToken = (TextView) findViewById(R.id.access_token);
        mExpiresIn = (TextView) findViewById(R.id.expires_in);
        mItems = (TextView) findViewById(R.id.items);

        findViewById(R.id.get_access_token_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MySyncManager.getInstance().fetch(new SyncFetchLogin());
            }
        });

        findViewById(R.id.expire_access_token_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MySharedPreferences prefs = MySharedPreferences.getInstance();
                prefs.setTokens(prefs.getAccessToken(), prefs.getRefreshToken(), 0);
                renderText();
            }
        });

        findViewById(R.id.clear_access_token_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MySharedPreferences prefs = MySharedPreferences.getInstance();
                prefs.clearTokens();
                renderText();
            }
        });

        findViewById(R.id.get_items_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MySyncManager.getInstance().fetch(new SyncFetchItems());
            }
        });

        findViewById(R.id.add_items_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MySyncManager.getInstance().upload(new SyncUploadItem(String.format("Item %s", mItemList.size() + 1)));
            }
        });

        MySyncManager.getInstance().registerSyncListener(this);
        MySyncManager.getInstance().setLogsEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MySyncManager.getInstance().unregisterSyncListener(this);
    }

    @Override
    public void onFetchDone(SyncFetch syncFetch) {
        if (syncFetch instanceof SyncFetchItems) {
            if (syncFetch.isSuccess()) {
                // Normally the items would have been save to DB and loaded by a Loader, but for test just add them here
                mItemList.clear();
                mItemList.addAll(((SyncFetchItems) syncFetch).getItems());
            } else {
                RetrofitError error = syncFetch.getError();
                if (error.getResponse() != null) {
                    mItems.setText(error.getResponse().getReason());
                }
            }
        }

        renderText();
    }

    @Override
    public void onUploadDone(SyncUpload syncUpload) {
        if (syncUpload instanceof SyncUploadItem) {
            if (syncUpload.isSuccess()) {
                Item item = ((SyncUploadItem) syncUpload).getItem();
                mItemList.add(item);
                Toast.makeText(this, String.format("Item added: %s", item.getName()), Toast.LENGTH_SHORT).show();
            }
        }

        renderText();
    }

    private void renderText() {
        String accessToken = MySharedPreferences.getInstance().getAccessToken();
        mAccessToken.setText(accessToken != null ? accessToken : "null");
        mExpiresIn.setText(MySharedPreferences.getInstance().getAccessTokenExpiresIn() / 1000 + " seconds");

        mItems.setText(mItemList.size() == 0 ? "No items added" : "");
        for (Item item : mItemList) {
            mItems.setText(mItems.getText() + "\n" + item.getName());
        }
    }
}
