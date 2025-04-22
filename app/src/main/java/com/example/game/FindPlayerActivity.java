package com.example.game;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.Set;


public class FindPlayerActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter<String> discoveredDevicesAdapter;
    private ListView discoveredDevicesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Bluetooth", "Bluetooth is not available");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_player);

        // PERMISSION
        Log.d("Bluetooth", "Bluetooth permission - 1");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.BLUETOOTH_CONNECT, android.Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
            return;
        }

        discoveredDevicesListView = findViewById(R.id.discoveredDevicesListView);
        discoveredDevicesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        discoveredDevicesListView.setAdapter(discoveredDevicesAdapter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            // Gérer le cas où le Bluetooth n'est pas pris en charge
            Log.d("Bluetooth", "Bluetooth is not available");
            return;
        }
        Log.d("Bluetooth", "Bluetooth not null");



        if (!bluetoothAdapter.isEnabled()) {
            Log.d("Bluetooth", "Bluetooth is not enabled");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return; // if not permission granted, request permission
            }
            startActivityForResult(enableBtIntent, 1);
        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : pairedDevices) {
            discoveredDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
        }

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

        bluetoothAdapter.startDiscovery();
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                device.createBond();


                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                discoveredDevicesAdapter.add(device.getName() + "\n" + device.getAddress());

                discoveredDevicesListView.setOnItemClickListener((parent, view, position, id) -> {
                    String item = discoveredDevicesAdapter.getItem(position);
                    if (item != null && item.contains("\n")) {
                        String address = item.substring(item.indexOf("\n") + 1);
                        BluetoothDevice selectedDevice = bluetoothAdapter.getRemoteDevice(address);

                        Log.d("Bluetooth", "Selected device: " + selectedDevice.getName() + " - " + address);

                        // Exemple : démarrer une activité pour se connecter
                        SharedPreferences prefs = getSharedPreferences("SoloChallenge", MODE_PRIVATE);
                        // get next games from pref GAMES
                        String games = prefs.getString("GAMES", "");
                        String[] gamesArray = games.split(";");
                        Intent intent2;
                        if (gamesArray.length > 0 && !gamesArray[0].isEmpty()) {
                            String className = gamesArray[0]; // Exemple : "com.example.game.TicTacToeActivity"

                            try {
                                Class<?> activityClass = Class.forName(className);
                                intent2 = new Intent(FindPlayerActivity.this, activityClass);
                                intent2.putExtra("device_address", address);
                                startActivity(intent2);
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                                // Optionnel : afficher un message d'erreur ou fallback
                            }
                        }
                    }
                });

            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bluetoothAdapter != null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            bluetoothAdapter.cancelDiscovery();
        }
        unregisterReceiver(receiver);
    }
}
