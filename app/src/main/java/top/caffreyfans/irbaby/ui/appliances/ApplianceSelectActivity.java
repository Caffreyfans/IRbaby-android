package top.caffreyfans.irbaby.ui.appliances;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.android.material.snackbar.Snackbar;

import net.irext.webapi.model.Brand;
import net.irext.webapi.model.Category;
import net.irext.webapi.utils.Constants;
import net.irext.webapi.utils.Constants.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import top.caffreyfans.irbaby.IRApplication;
import top.caffreyfans.irbaby.R;
import top.caffreyfans.irbaby.adapter.AppliancesSelectAdapter;
import top.caffreyfans.irbaby.helper.ApplianceContract;
import top.caffreyfans.irbaby.model.ApplianceInfo;

public class ApplianceSelectActivity extends AppCompatActivity {

    private final String TAG = ApplianceSelectActivity.class.getSimpleName();
    private ListView mListView;
    private IRApplication mApp;
    private ProgressBar mProgressBar;
    private Context mContext;
    private ApplianceInfo mApplianceInfo;
    private ContentID mContentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appliance_select);

        mContext = this;

        mListView = (ListView) findViewById(R.id.appliance_select_lv);
        mApp = (IRApplication) getApplication();
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        final Intent intent = getIntent();

        if (intent.hasExtra(ApplianceContract.Select.TITLE)) {
            String title = intent.getStringExtra(ApplianceContract.Select.TITLE);
            this.setTitle(title);
        }

        if (intent.hasExtra(ApplianceContract.Select.CONTENT_ID)) {
            mContentID = (ContentID) intent.getSerializableExtra(ApplianceContract.Select.CONTENT_ID);
        }

        if (intent.hasExtra(ApplianceContract.Select.APPLIANCE_INFO)) {
            mApplianceInfo = (ApplianceInfo) intent.getSerializableExtra(ApplianceContract.Select.APPLIANCE_INFO);
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        new FetchData().execute();
    }

    private class FetchData extends AsyncTask<Void, Void, List<String>> {

        private List<Category> mCategoryList;
        private List<Brand> mBrandList;
        private List<String> mListString = new ArrayList<>();

        @Override
        protected List<String> doInBackground(Void... voids) {
            switch (mContentID) {
                case LIST_CATEGORIES:
                    mCategoryList = mApp.mWeAPIs.listCategories();
                    for (Category category : mCategoryList) {
                        mListString.add(category.getName());
                    }
                    break;

                case LIST_BRANDS:
                    mBrandList = mApp.mWeAPIs.listBrands(mApplianceInfo.getCategory());
                    for (Brand brand : mBrandList) {
                        mListString.add(brand.getName());
                    }
                    break;

                    default: break;
            }
            return mListString;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mListView.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<String> stringList) {
            super.onPostExecute(stringList);
            mProgressBar.setVisibility(View.INVISIBLE);
            mListView.setVisibility(View.VISIBLE);
            AppliancesSelectAdapter appliancesSelectAdapter = new AppliancesSelectAdapter(mContext, stringList);
            mListView.setAdapter(appliancesSelectAdapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    switch (mContentID) {
                        case LIST_CATEGORIES:
                            mApplianceInfo.setCategory(CategoryID.values()[position].getValue());
                            Intent intent = new Intent(mContext, ApplianceSelectActivity.class);
                            intent.putExtra(ApplianceContract.Select.TITLE, getString(R.string.select_brand));
                            intent.putExtra(ApplianceContract.Select.CONTENT_ID, ContentID.LIST_BRANDS);
                            intent.putExtra(ApplianceContract.Select.APPLIANCE_INFO, mApplianceInfo);
                            startActivity(intent);
                            break;

                        case LIST_BRANDS:
                            CategoryID categoryID = CategoryID.values()[mApplianceInfo.getCategory() - 1];
                            mApplianceInfo.setBrand(mBrandList.get(position).getId());
                            mApplianceInfo.setName(mBrandList.get(position).getName());

                            switch (categoryID) {
                                case AIR_CONDITIONER:
                                    Intent intent1 = new Intent(mContext, ACControlActivity.class);
                                    intent1.putExtra(ApplianceContract.Control.IS_PARSE, true);
                                    intent1.putExtra(ApplianceContract.Control.APPLIANCE_INFO, mApplianceInfo);
                                    startActivity(intent1);
                                    break;
                            }

                    }

                }
            });
        }

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
