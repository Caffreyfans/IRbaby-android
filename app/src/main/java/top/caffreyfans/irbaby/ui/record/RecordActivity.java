package top.caffreyfans.irbaby.ui.record;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Observable;
import java.util.Observer;

import top.caffreyfans.irbaby.R;
import top.caffreyfans.irbaby.firmware_api.IRbabyApi;
import top.caffreyfans.irbaby.helper.ApplianceContract;
import top.caffreyfans.irbaby.helper.NotifyMsgEntity;
import top.caffreyfans.irbaby.helper.UdpNotifyManager;
import top.caffreyfans.irbaby.model.DeviceInfo;

public class RecordActivity extends AppCompatActivity
        implements Observer{

    private final static String TAG = RecordActivity.class.getSimpleName();

    private EditText meditText;
    private DeviceInfo mDeviceInfo;
    private String signalType;
    private IRbabyApi mIRbabyApi;
    private TextView mTextView;
    private Button test_btn;
    private Button save_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        UdpNotifyManager.getUdpNotifyManager().addObserver(this);
        Intent intent = getIntent();
        if (intent.hasExtra(ApplianceContract.DeviceSetting.DEVICE_INFO)) {
            mDeviceInfo = (DeviceInfo) intent.getSerializableExtra(ApplianceContract.DeviceSetting.DEVICE_INFO);
            this.setTitle(mDeviceInfo.getMac());

            mIRbabyApi = new IRbabyApi(this, mDeviceInfo, null);
            mIRbabyApi.enableSignal();
        }

        mTextView = (TextView) findViewById(R.id.raw_tv);
        test_btn = (Button) findViewById(R.id.test_bt);
        save_btn = (Button) findViewById(R.id.save_bt);

        test_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIRbabyApi.sendSignal("test", signalType, "file");
            }
        });

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSaveDialog();
            }
        });
    }

    private void showSaveDialog() {
       meditText = new EditText(this);
       meditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
       AlertDialog.Builder inputDialog =
               new AlertDialog.Builder(this);
       inputDialog.setTitle(getString(R.string.dialog_title)).setView(meditText);
       inputDialog.setPositiveButton(getString(R.string.dialog_save_button),
               new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       String fileName = meditText.getText().toString();
                       Log.d(TAG, "onClick: signalType = " + signalType);
                       mIRbabyApi.saveSignal(fileName, signalType);
                   }
               });
       inputDialog.setNegativeButton(getString(R.string.dialog_cancel_button),
               new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {

                   }
               });
       inputDialog.show();
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

    @Override
    public void update(Observable o, Object arg) {
        NotifyMsgEntity entity = (NotifyMsgEntity)arg;
        int code = (int)entity.getCode();
        JSONObject jsonObject;
        if (code == UdpNotifyManager.RECORD_RT) {
            try {
                jsonObject = new JSONObject(entity.getData().toString());
                mTextView.setText(jsonObject.getString ("params"));
                signalType = jsonObject.getJSONObject("params").getString("signal");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIRbabyApi.disableRecord();
    }
}
