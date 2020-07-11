package top.caffreyfans.irbaby.ui.appliances;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.irext.webapi.bean.ACStatus;
import net.irext.webapi.model.RemoteIndex;
import net.irext.webapi.utils.Constants.*;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.List;
import top.caffreyfans.irbaby.IRApplication;
import top.caffreyfans.irbaby.MainActivity;
import top.caffreyfans.irbaby.R;
import top.caffreyfans.irbaby.firmware_api.IRbabyApi;
import top.caffreyfans.irbaby.helper.ApplianceContract;
import top.caffreyfans.irbaby.model.ApplianceInfo;
import top.caffreyfans.irbaby.model.DeviceInfo;


public class ACControlActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = ACControlActivity.class.getSimpleName();
    private TextView temperature_tv;
    private ImageView mode_img;
    private TextView wind_speed_tv;
    private TextView wind_direction_tv;
    private TextView mode_tv;
    private TextView swing_tv;
    private TextView remote_index_tv;
    private LinearLayout mLinearLayout;
    private ConstraintLayout ac_state_layout;
    private ConstraintLayout ac_parse_cl;
    private List<RemoteIndex> mRemoteIndexList;
    private ProgressBar mProgressBar;
    private int mRemoteIndex = 0;
    private boolean mIsParse = false;
    private ACStatus mACStatus;
    private ApplianceInfo mApplianceInfo;
    private DeviceInfo mDeviceInfo;
    private IRbabyApi mIRbabyApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appliance_ac_control);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mACStatus = new ACStatus();
        temperature_tv = (TextView) findViewById(R.id.ac_temperature_tv);
        mode_img = (ImageView) findViewById(R.id.ac_mode_img);
        mode_tv = (TextView) findViewById(R.id.ac_mode_tv);
        wind_speed_tv = (TextView) findViewById(R.id.ac_speed_tv);
        wind_direction_tv = (TextView) findViewById(R.id.ac_fan_direction_tv);
        mLinearLayout = (LinearLayout) findViewById(R.id.ac_control_ll);
        ac_state_layout = findViewById(R.id.ac_state_layout);
        swing_tv = (TextView) findViewById(R.id.ac_swing_tv);
        ac_parse_cl = (ConstraintLayout) findViewById(R.id.ac_parse_cl);
        remote_index_tv = (TextView) findViewById(R.id.ac_parse_index_tv);
        mProgressBar = (ProgressBar) findViewById(R.id.ac_control_pb);

        /* power button onclick listener */
        for (int i = 0; i < mLinearLayout.getChildCount(); i++) {
            ViewGroup viewGroup = (ViewGroup) mLinearLayout.getChildAt(i);
            for (int j = 0; j < viewGroup.getChildCount(); j++) {
                View view = (View) viewGroup.getChildAt(j);
                view.setOnClickListener(this);
            }
        }

        Intent intent = getIntent();

        if (intent.hasExtra(ApplianceContract.Control.APPLIANCE_INFO)) {
            mApplianceInfo = (ApplianceInfo) intent.getSerializableExtra(ApplianceContract.Control.APPLIANCE_INFO);
            this.setTitle(mApplianceInfo.getName());
            List<DeviceInfo> deviceInfos = LitePal.findAll(DeviceInfo.class);
            for (DeviceInfo deviceInfo : deviceInfos) {
                if (deviceInfo.getMac().equals(mApplianceInfo.getMac())) {
                    mDeviceInfo = deviceInfo;
                }
            }
        }

        if (intent.hasExtra(ApplianceContract.Control.IS_PARSE)) {
            mIsParse = intent.getBooleanExtra(ApplianceContract.Control.IS_PARSE, false);
            new FetchIndexData().execute();
        } else {

        }
        mIRbabyApi = new IRbabyApi(this, mDeviceInfo, mApplianceInfo);
        initACStatus();
        refreshAcStatusUI();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIRbabyApi.free();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ac_power_ctrl_btn:
                mACStatus.acPower = ACPower.values()[(mACStatus.acPower.ordinal() + 1) % ACPower.values().length] ;
                break;

            case R.id.ac_mode_ctrl_btn:
                mACStatus.acMode = ACMode.values()[(mACStatus.acMode.ordinal() + 1) % ACMode.values().length];
                break;

            case R.id.ac_fan_speed_ctrl_btn:
                mACStatus.acWindSpeed = ACWindSpeed.values()[(mACStatus.acWindSpeed.ordinal() + 1) % ACWindSpeed.values().length];

                break;

            case R.id.ac_fan_direction_ctrl_btn:
                mACStatus.acWindDir = ACWindDirection.values()[(mACStatus.acWindDir.ordinal() + 1) % ACWindDirection.values().length];
                break;

            case R.id.ac_fan_swing_ctrl_btn:
                mACStatus.acSwing = ACSwing.values()[(mACStatus.acSwing.ordinal() + 1) % ACSwing.values().length];
                break;

            case R.id.ac_temperature_subtract_ctrl_btn:
                if (mACStatus.acTemp.ordinal() -1 >= ACTemperature.TEMP_16.ordinal()) {
                    mACStatus.acTemp = ACTemperature.values()[mACStatus.acTemp.ordinal() - 1];
                }
                break;

            case R.id.ac_temperature_add_ctrl_btn:
                if (mACStatus.acTemp.ordinal() + 1 <= ACTemperature.TEMP_30.ordinal()) {
                    mACStatus.acTemp = ACTemperature.values()[mACStatus.acTemp.ordinal() + 1];
                }
                break;

            case R.id.ac_parse_previous_btn:
                if (mRemoteIndexList != null) {
                    if (mRemoteIndex - 1 >= 0 ) {
                        mRemoteIndex--;
                        mApplianceInfo.setFile(mRemoteIndexList.get(mRemoteIndex).getRemoteMap());
                    }
                }
                break;

            case R.id.ac_parse_save_btn:
                mIsParse = false;
                saveAppliance();
                break;

            case R.id.ac_parse_next_btn:
                if (mRemoteIndexList != null) {
                    if (mRemoteIndex + 1 < mRemoteIndexList.size()) {
                        mRemoteIndex++;
                        mApplianceInfo.setFile(mRemoteIndexList.get(mRemoteIndex).getRemoteMap());
                    }
                }
                break;
        }
        View parent = (View) v.getParent();
        if (parent.getId() != R.id.ac_parse_cl) {
            mIRbabyApi.sendIR(mACStatus);
        }
        refreshAcStatusUI();
    }

    private void initACStatus() {
        mACStatus.acPower = ACPower.POWER_ON;
        mACStatus.acMode = ACMode.MODE_AUTO;
        mACStatus.acTemp = ACTemperature.TEMP_23;
        mACStatus.acSwing = ACSwing.SWING_OFF;
        mACStatus.acWindDir = ACWindDirection.DIR_TOP;
        mACStatus.acWindSpeed = ACWindSpeed.SPEED_AUTO;
        mACStatus.acDisplay = 0;
        mACStatus.acSleep = 0;
        mACStatus.acTimer = 0;
    }

    private void refreshAcStatusUI() {
        if (mACStatus.acPower == ACPower.POWER_ON) {
            ac_state_layout.setVisibility(View.VISIBLE);
        } else {
            ac_state_layout.setVisibility(View.INVISIBLE);
        }

        int img_id;
        String mode_string;
        switch (mACStatus.acMode) {
            case MODE_COOL: img_id = R.drawable.ic_cool; mode_string = getString(R.string.mode_cool); break;
            case MODE_DEHUMIDITY: img_id = R.drawable.ic_water; mode_string = getString(R.string.mode_water); break;
            case MODE_FAN: img_id = R.drawable.ic_fan; mode_string = getString(R.string.mode_fan); break;
            case MODE_AUTO: img_id = R.drawable.ic_auto; mode_string = getString(R.string.mode_auto); break;
            case MODE_HEAT: img_id = R.drawable.ic_heat; mode_string = getString(R.string.mode_heat); break;
            default:
                throw new IllegalStateException("Unexpected value: " + mACStatus.acMode);
        }
        mode_img.setImageResource(img_id);
        mode_tv.setText(mode_string);

        temperature_tv.setText(String.valueOf(mACStatus.acTemp.ordinal() + 16));
        wind_speed_tv.setText(mACStatus.acWindSpeed.name());
        wind_direction_tv.setText(mACStatus.acWindDir.name());
        swing_tv.setText(mACStatus.acSwing.name());

        if (mIsParse) {
            ac_parse_cl.setVisibility(View.VISIBLE);
            remote_index_tv.setText(String.valueOf(mRemoteIndex));
        } else {
            ac_parse_cl.setVisibility(View.GONE);
        }
    }

    private class FetchIndexData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            IRApplication userApp = (IRApplication) getApplication();
            mRemoteIndexList = userApp.mWeAPIs.listRemoteIndexes(mApplianceInfo.getCategory(),
                    mApplianceInfo.getBrand(), null, null);
            mApplianceInfo.setFile(mRemoteIndexList.get(mRemoteIndex).getRemoteMap());
            try {
                JSONObject sendJson = new JSONObject();
                sendJson.put("cmd", "download");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    private void saveAppliance() {
        mApplianceInfo.save();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private ACStatus jsonToStatus(JSONObject jsonObject) {
        ACStatus acStatus = new ACStatus();
        try {
            acStatus.acPower = ACPower.values()[jsonObject.getInt("power")];
            acStatus.acTemp = ACTemperature.values()[jsonObject.getInt("temperature")];
            acStatus.acMode = ACMode.values()[jsonObject.getInt("mode")];
            acStatus.acSwing = ACSwing.values()[jsonObject.getInt("swing")];
            acStatus.acWindDir = ACWindDirection.values()[jsonObject.getInt("direction")];
            acStatus.acWindSpeed = ACWindSpeed.values()[jsonObject.getInt("speed")];
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return acStatus;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.appliance_control, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case  R.id.action_export:
                Intent intent = new Intent(this, ExportActivity.class);
                intent.putExtra(ApplianceContract.Control.APPLIANCE_INFO, mApplianceInfo);
                intent.putExtra(ApplianceContract.Control.EXPORT_TYPE, CategoryID.AIR_CONDITIONER);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
