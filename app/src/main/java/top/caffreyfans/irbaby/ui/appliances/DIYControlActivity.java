package top.caffreyfans.irbaby.ui.appliances;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import net.irext.webapi.utils.Constants;

import org.litepal.LitePal;

import java.util.List;

import top.caffreyfans.irbaby.R;
import top.caffreyfans.irbaby.firmware_api.IRbabyApi;
import top.caffreyfans.irbaby.helper.ApplianceContract;
import top.caffreyfans.irbaby.model.ApplianceInfo;
import top.caffreyfans.irbaby.model.DeviceInfo;

public class DIYControlActivity extends AppCompatActivity {

    private ApplianceInfo mApplianceInfo;
    private IRbabyApi mIRbabyApi;
    private DeviceInfo mDeviceInfo;
    private Button send_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diycontrol);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        if (intent.hasExtra(ApplianceContract.Control.APPLIANCE_INFO)) {
            mApplianceInfo = (ApplianceInfo) intent.getSerializableExtra(ApplianceContract.Control.APPLIANCE_INFO);
            this.setTitle(mApplianceInfo.getName());
            List<DeviceInfo> deviceInfos = LitePal.findAll(DeviceInfo.class);
            for (DeviceInfo deviceInfo : deviceInfos) {
                if (deviceInfo.getMac().equals(mApplianceInfo.getMac())) {
                    mDeviceInfo = deviceInfo;
                    mIRbabyApi = new IRbabyApi(this, mDeviceInfo, mApplianceInfo);
                }
            }
        }

        send_btn = (Button) findViewById(R.id.send_bt);
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIRbabyApi.sendSignal(mApplianceInfo.getFile(), mApplianceInfo.getSignal(), "file");
            }
        });
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
                return true;
            case  R.id.action_export:
                Intent intent = new Intent(this, ExportActivity.class);
                intent.putExtra(ApplianceContract.Control.APPLIANCE_INFO, mApplianceInfo);
                intent.putExtra(ApplianceContract.Control.EXPORT_TYPE, Constants.CategoryID.DIY);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
