package top.caffreyfans.irbaby.ui.appliances;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import top.caffreyfans.irbaby.R;
import top.caffreyfans.irbaby.helper.ApplianceContract;
import top.caffreyfans.irbaby.model.ApplianceInfo;

public class ExportActivity extends AppCompatActivity {

    private ApplianceInfo mApplianceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();

        if (intent.hasExtra(ApplianceContract.Control.APPLIANCE_INFO)) {
            mApplianceInfo = (ApplianceInfo) intent.getSerializableExtra(ApplianceContract.Control.APPLIANCE_INFO);
        }

        TextView mqttTV = (TextView) findViewById(R.id.mqtt_tv);
        String mac = mApplianceInfo.getMac();
        String file = mApplianceInfo.getFile();
        String output = String.format(
                "climate:\n" +
                "  - platform: mqtt\n" +
                "    name: just you like!\n" +
                "    modes:\n" +
                "      - \"heat\"\n" +
                "      - \"cool\"\n" +
                "      - \"auto\"\n" +
                "      - \"fan\"\n" +
                "      - \"dry\"\n" +
                "      - \"off\"\n" +
                "    swing_modes:\n" +
                "      - \"on\"\n" +
                "      - \"off\"\n" +
                "    max_temp: 30\n" +
                "    min_temp: 16\n" +
                "    fan_modes:\n" +
                "      - \"high\"\n" +
                "      - \"medium\"\n" +
                "      - \"low\"\n" +
                "      - \"auto\"\n" +
                String.format("    mode_command_topic: \"/IRbaby/%s/set/%s/mode\"\n", mac, file) +
                String.format("    temperature_command_topic: \"/IRbaby/%s/set/%s/temperature\"\n", mac, file) +
                String.format("    fan_mode_command_topic: \"/IRbaby/%s/set/%s/fan\"\n", mac, file) +
                String.format("    swing_mode_command_topic: \"/IRbaby/%s/set/%s/swing\"\n", mac, file) +
                "    precision: 1.0");
        mqttTV.setText(output);
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
