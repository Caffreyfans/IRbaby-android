package top.caffreyfans.irbaby.ui.record;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Observable;
import java.util.Observer;
import java.util.regex.Pattern;

import org.litepal.LitePal;

import top.caffreyfans.irbaby.R;
import top.caffreyfans.irbaby.firmware_api.IRbabyApi;
import top.caffreyfans.irbaby.helper.ApplianceContract;
import top.caffreyfans.irbaby.helper.NotifyMsgEntity;
import top.caffreyfans.irbaby.helper.UdpNotifyManager;
import top.caffreyfans.irbaby.model.ApplianceInfo;
import top.caffreyfans.irbaby.model.DeviceInfo;

public class RecordActivity extends AppCompatActivity
        implements Observer{

    private final static String TAG = RecordActivity.class.getSimpleName();
    private static final Pattern SIGNAL_FILE_PATTERN = Pattern.compile("^[A-Za-z0-9._-]+$");

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
        updateActionButtons(false);

        test_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasRecordedSignal()) {
                    Toast.makeText(RecordActivity.this, R.string.record_signal_missing, Toast.LENGTH_SHORT).show();
                    return;
                }
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
                       String fileName = meditText.getText().toString().trim();
                       if (!isValidFileName(fileName)) {
                           Toast.makeText(RecordActivity.this, R.string.record_invalid_name, Toast.LENGTH_SHORT).show();
                           return;
                       }
                       if (hasDuplicateFileName(fileName)) {
                           Toast.makeText(RecordActivity.this, R.string.record_duplicate_name, Toast.LENGTH_SHORT).show();
                           return;
                       }
                       Log.d(TAG, "onClick: signalType = " + signalType);
                       mIRbabyApi.saveSignal(fileName, signalType);
                       Toast.makeText(RecordActivity.this, R.string.record_save_success, Toast.LENGTH_SHORT).show();
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

    private boolean hasRecordedSignal() {
        return !TextUtils.isEmpty(signalType);
    }

    private void updateActionButtons(boolean enabled) {
        test_btn.setEnabled(enabled);
        save_btn.setEnabled(enabled);
    }

    private boolean isValidFileName(String fileName) {
        return !TextUtils.isEmpty(fileName) && SIGNAL_FILE_PATTERN.matcher(fileName).matches();
    }

    private boolean hasDuplicateFileName(String fileName) {
        return LitePal.where("mac = ? and file = ?", mDeviceInfo.getMac(), fileName)
                .count(ApplianceInfo.class) > 0;
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
        if (code == UdpNotifyManager.RECORD_RT) {
            try {
                JSONObject jsonObject = new JSONObject(entity.getData().toString());
                JSONObject params = jsonObject.optJSONObject("params");
                if (params == null) {
                    mTextView.setText(entity.getData().toString());
                    signalType = null;
                    updateActionButtons(false);
                    return;
                }
                mTextView.setText(params.toString(2));
                signalType = params.optString("signal", null);
                updateActionButtons(hasRecordedSignal());

            } catch (JSONException e) {
                updateActionButtons(false);
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UdpNotifyManager.getUdpNotifyManager().deleteObserver(this);
        if (mIRbabyApi != null) {
            mIRbabyApi.disableRecord();
            mIRbabyApi.free();
        }
    }
}
