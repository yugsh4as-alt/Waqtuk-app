package com.prayertimes.app.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.prayertimes.app.R;
import com.prayertimes.app.utils.AppPreferences;

import java.util.Locale;

/**
 * Fallback screen: enter latitude, longitude, and altitude manually.
 * Useful for users who know their exact coordinates (e.g. from a map).
 */
public class ManualCoordinatesActivity extends AppCompatActivity {

    private EditText etLat, etLon, etAlt, etLabel;
    private AppPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_coords);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("إدخال الإحداثيات");
        }

        prefs = new AppPreferences(this);
        etLat   = findViewById(R.id.et_lat);
        etLon   = findViewById(R.id.et_lon);
        etAlt   = findViewById(R.id.et_alt);
        etLabel = findViewById(R.id.et_label);

        // Pre-fill with current saved values
        if (prefs.isLocationSet()) {
            etLat.setText(String.valueOf(prefs.getLatitude()));
            etLon.setText(String.valueOf(prefs.getLongitude()));
            etAlt.setText(String.valueOf((int) prefs.getElevation()));
            etLabel.setText(prefs.getLocationName());
        }

        Button btnSave = findViewById(R.id.btn_save_coords);
        btnSave.setOnClickListener(v -> saveCoordinates());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }

    private void saveCoordinates() {
        String latStr = etLat.getText().toString().trim();
        String lonStr = etLon.getText().toString().trim();
        String altStr = etAlt.getText().toString().trim();
        String label  = etLabel.getText().toString().trim();

        if (latStr.isEmpty() || lonStr.isEmpty()) {
            Toast.makeText(this, "Latitude and Longitude are required", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double lat = Double.parseDouble(latStr);
            double lon = Double.parseDouble(lonStr);
            double alt = altStr.isEmpty() ? 0 : Double.parseDouble(altStr);

            if (lat < -90 || lat > 90) {
                Toast.makeText(this, "Latitude must be between -90 and 90", Toast.LENGTH_SHORT).show();
                return;
            }
            if (lon < -180 || lon > 180) {
                Toast.makeText(this, "Longitude must be between -180 and 180", Toast.LENGTH_SHORT).show();
                return;
            }
            if (label.isEmpty()) {
                label = String.format(Locale.US, "%.4f°, %.4f°", lat, lon);
            }

            prefs.saveLocation(lat, lon, alt, label);
            Toast.makeText(this, "Location saved: " + label, Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT).show();
        }
    }
}
