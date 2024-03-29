package methk.presentationremote;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    // Widgets
    private Button btnPaired;
    private ListView devicesList;

    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> pairedDevices;
    public static String EXTRA_ADDRESS = "device_address";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize widgets
        btnPaired = findViewById(R.id.paired_dev_btn);
        devicesList = findViewById(R.id.paired_dev_listview);

        // Initialize bluetooth adapter
        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        if(myBluetooth == null) { // This device has not bluetooth adapter
            Toast.makeText(getApplicationContext(), "Bluetooth Adapter Not Available", Toast.LENGTH_LONG).show();
            finish();
        } else if(!myBluetooth.isEnabled()) { // This device has bluetooth adapter but turned off
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon, 1); // Intent to turn on bluetooth adapter
        }

        btnPaired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pairedDevicesList();
            }
        });

    }

    // List with paired bluetooth devices
    private void pairedDevicesList() {
        pairedDevices = myBluetooth.getBondedDevices();
        ArrayList list = new ArrayList();

        if (pairedDevices.size() > 0) // ArrayList with name and MAC address of paired devices
            for(BluetoothDevice bt : pairedDevices)
                list.add(bt.getName() + "\n" + bt.getAddress());
        else
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();


        // Display paired devices in the ListView
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        devicesList.setAdapter(adapter);
        devicesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick (AdapterView<?> av, View v, int arg2, long arg3) {
                // MAC address are last 17 characters of the textview clicked
                String info = ((TextView) v).getText().toString();
                String address = info.substring(info.length() - 17);

                Intent i = new Intent(MainActivity.this, Controller.class);

                // Send to next activity the MAC address of the chosen device
                i.putExtra(EXTRA_ADDRESS, address);
                startActivity(i);
            }
        });

    }
}
