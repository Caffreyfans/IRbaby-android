package top.caffreyfans.irbaby.ui.appliances;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import net.irext.webapi.utils.Constants;

import top.caffreyfans.irbaby.R;
import top.caffreyfans.irbaby.helper.ApplianceContract;
import top.caffreyfans.irbaby.model.ApplianceInfo;

public class ExportActivity extends AppCompatActivity {

    private ApplianceInfo mApplianceInfo;
    private String MAC;
    private String File;
    private String Config;
    private Constants.CategoryID categoryID;

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
            MAC = mApplianceInfo.getMac();
            File = mApplianceInfo.getFile();
        }

        if (intent.hasExtra(ApplianceContract.Control.EXPORT_TYPE)) {
            categoryID = (Constants.CategoryID) intent.getSerializableExtra(ApplianceContract.Control.EXPORT_TYPE);
            switch (categoryID) {
                case AIR_CONDITIONER:
                    Config = getACConfig();
                    break;
                case DIY:
                    Config = getDIYConfig();
                    break;
                default:
                    break;

            }
        }

        TextView mqttTV = (TextView) findViewById(R.id.mqtt_tv);
        mqttTV.setText(Config);
    }

    private String getACConfig() {
        String output = String.format(
                "climate:\n" +
                        "  - platform: mqtt\n" +
                        "    name: just you like!\n" +
                        "    modes:\n" +
                        "      - \"auto\"\n" +
                        "      - \"heat\"\n" +
                        "      - \"cool\"\n" +
                        "      - \"fan_only\"\n" +
                        "      - \"dry\"\n" +
                        "      - \"off\"\n" +
                        "    swing_modes:\n" +
                        "      - \"on\"\n" +
                        "      - \"off\"\n" +
                        "    max_temp: 30\n" +
                        "    min_temp: 16\n" +
                        "    fan_modes:\n" +
                        "      - \"auto\"\n" +
                        "      - \"low\"\n" +
                        "      - \"medium\"\n" +
                        "      - \"high\"\n" +
                        String.format("    mode_command_topic: \"/IRbaby/%s/send/ir/local/%s/mode\"\n", MAC, File) +
                        String.format("    mode_state_topic: \"/IRbaby/%s/state/%s/mode\"\n", MAC, File) +
                        String.format("    temperature_command_topic: \"/IRbaby/%s/send/ir/%s/temperature\"\n", MAC, File) +
                        String.format("    temperature_state_topic: \"/IRbaby/%s/state/%s/temperature\"\n", MAC, File) +
                        String.format("    fan_mode_command_topic: \"/IRbaby/%s/send/ir/%s/fan\"\n", MAC, File) +
                        String.format("    fan_mode_state_topic: \"/IRbaby/%s/state/%s/fan\"\n", MAC, File) +
                        String.format("    swing_mode_command_topic: \"/IRbaby/%s/send/ir/%s/swing\"\n", MAC, File) +
                        String.format("    swing_mode_state_topic: \"/IRbaby/%s/state/%s/swing\"\n", MAC, File) +
                        "    precision: 1.0");
        return output;
    }

    private String getDIYConfig() {
        String output = "switch:\n" +
                " - platform: mqtt\n" +
                String.format("   command_topic: \"/IRbaby/%s/send/ir/file/%s/skr\"", MAC, File);
        return output;
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
