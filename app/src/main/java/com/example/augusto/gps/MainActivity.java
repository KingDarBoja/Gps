package com.example.augusto.gps;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

// Import files related to sql connection on mariadb

import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.enums.ObdProtocols;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private TextView tvLatitude, tvLongitude, tvTime, tvBtConnection, tvRpmObd;
    private LocationManager locationManager;
    private LocationListener listener;
    String lat,lon,tim,tim2,timOut,date;
    private String deviceAddress;
//    private ArrayList<String> deviceStrs = new ArrayList<>();
//    private ArrayList<String> devices = new ArrayList<>();
    private BluetoothSocket socket;
    private BluetoothAdapter btAdapter = null;
    private Set<BluetoothDevice> pairedDevices;
    private String TAG = "ElMensaje";
    Button btnPaired;
    ListView devicelist;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvBtConnection = (TextView) findViewById(R.id.btConnection);
        tvRpmObd = (TextView) findViewById(R.id.rpmOBD);

        btnPaired = (Button) findViewById(R.id.btnBluetooth);

        // Bluetooth code
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(btAdapter == null) {
            //Show a mensag. that thedevice has no bluetooth adapter
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();
        } else
        {
            if (btAdapter.isEnabled())
            { }
            else
            {
                //Ask to the user turn the bluetooth on
                Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnBTon,1);
            }
        }

//        pairedDevices = btAdapter.getBondedDevices();
//        if (pairedDevices.size() > 0)
//        {
//            // this for instruction add the paired devices into the list
//            for (BluetoothDevice device : pairedDevices)
//            {
//                deviceStrs.add(device.getName() + "\n" + device.getAddress());
//                devices.add(device.getAddress());
//            }
//        }
//
//        // Show the list of devices
//        final AlertDialog.Builder alertDialogBt = new AlertDialog.Builder(this);
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
//                android.R.layout.select_dialog_singlechoice,
//                deviceStrs.toArray(new String[deviceStrs.size()]));
//
//
//
//        alertDialogBt.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInt, int which) {
//                dialogInt.dismiss();
//                int position = ((AlertDialog) dialogInt).getListView().getCheckedItemPosition();
//                deviceAddress = devices.get(position);
//                System.out.println("Device Address: " + deviceAddress);
//                BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
//                BluetoothDevice device = btAdapter.getRemoteDevice(deviceAddress);
//                tvBtConnection.setText(deviceAddress);
//                UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
//                try {
//                    socket = device.createInsecureRfcommSocketToServiceRecord(uuid);
//                    socket.connect();
//                    new EchoOffCommand().run(socket.getInputStream(), socket.getOutputStream());
//                    new LineFeedOffCommand().run(socket.getInputStream(), socket.getOutputStream());
//                    new TimeoutCommand(125).run(socket.getInputStream(), socket.getOutputStream());
//                    new SelectProtocolCommand(ObdProtocols.AUTO).run(socket.getInputStream(), socket.getOutputStream());
//                    socket.getOutputStream();
//                } catch (IOException | InterruptedException e) {
//                    System.out.println("Error connecting the device: " + e.getMessage());
//                }
//
//
//            }
//        });
//
//        alertDialogBt.setTitle("Choose Bluetooth device");
//        alertDialogBt.show();
//
//        RPMCommand engineRpmCommand = new RPMCommand();
//        SpeedCommand speedCommand = new SpeedCommand();
//        while (!Thread.currentThread().isInterrupted())
//        {
//            try {
//                engineRpmCommand.run(socket.getInputStream(), socket.getOutputStream());
//            } catch (IOException | InterruptedException e) {
//                e.printStackTrace();
//            }
//            try {
//                speedCommand.run(socket.getInputStream(), socket.getOutputStream());
//            } catch (IOException | InterruptedException e) {
//                e.printStackTrace();
//            }
//            // TODO handle commands result
//            tvRpmObd.setText(engineRpmCommand.getFormattedResult());
//            Log.d(TAG, "RPM: " + engineRpmCommand.getFormattedResult());
//            Log.d(TAG, "Speed: " + speedCommand.getFormattedResult());
//        }

        // Connect to the selected device
//        btAdapter = BluetoothAdapter.getDefaultAdapter();
//        BluetoothDevice device = btAdapter.getRemoteDevice(deviceAddress);
//        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
//        try {
//            BluetoothSocket socket = device.createInsecureRfcommSocketToServiceRecord(uuid);
//            socket.connect();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        tvLatitude = (TextView) findViewById(R.id.latitudeField);
        tvLongitude = (TextView) findViewById(R.id.longitudeField);
        tvTime = (TextView) findViewById(R.id.timeField);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lat = Double.toString(location.getLatitude());
                lon = Double.toString(location.getLongitude());
                Date date = new Date(location.getTime());
//                DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
//                DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(getApplicationContext());
//                tim =  dateFormat.format(date) + "\n" + timeFormat.format(date);
//                tim2 = dateFormat.format(date) + " " + timeFormat.format(date);
                SimpleDateFormat outTime = new SimpleDateFormat("MMM dd yyyy HH:mm:ss");
                timOut = outTime.format(date);
                tvLatitude.setText(lat);
                tvLongitude.setText(lon);
                tvTime.setText(timOut);

                String method ="register";
//                BackgroundTask backgroundTask = new BackgroundTask(getBaseContext());
//                backgroundTask.execute(method,lat,lon,timOut);

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        configure_button();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                configure_button();
                break;
            default:
                break;
        }
    }


    void configure_button() {
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                        , 10);
            }
            return;
        }

        // this code won'textView execute IF permissions are not allowed, because in the line above there is return statement.
        locationManager.requestLocationUpdates("gps", 5000, 0, listener);
    }

    public void btnBtOnClick(View view) {
        final ArrayList<String> deviceStrs = new ArrayList<>();
        final ArrayList<String> devices = new ArrayList<>();
        pairedDevices = btAdapter.getBondedDevices();
        if (pairedDevices.size() > 0)
        {
            // this for instruction add the paired devices into the list
            for (BluetoothDevice device : pairedDevices)
            {
                deviceStrs.add(device.getName() + "\n" + device.getAddress());
                devices.add(device.getAddress());
            }
        }

        // Show the list of devices
        final AlertDialog.Builder alertDialogBt = new AlertDialog.Builder(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.select_dialog_singlechoice,
                deviceStrs.toArray(new String[deviceStrs.size()]));

        alertDialogBt.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInt, int which) {
                dialogInt.dismiss();
                int position = ((AlertDialog) dialogInt).getListView().getCheckedItemPosition();
                deviceAddress = devices.get(position);
                System.out.println("Device Address: " + deviceAddress);
                BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
                BluetoothDevice device = btAdapter.getRemoteDevice(deviceAddress);
                UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                try {
                    socket = device.createInsecureRfcommSocketToServiceRecord(uuid);
                    socket.connect();
                    Toast.makeText(getApplicationContext(), "Dispositivo OBD" + "\n" + "Puede proceder con obtener datos", Toast.LENGTH_LONG).show();
                    new EchoOffCommand().run(socket.getInputStream(), socket.getOutputStream());
                    new LineFeedOffCommand().run(socket.getInputStream(), socket.getOutputStream());
                    new TimeoutCommand(125).run(socket.getInputStream(), socket.getOutputStream());
                    new SelectProtocolCommand(ObdProtocols.AUTO).run(socket.getInputStream(), socket.getOutputStream());
                    socket.getOutputStream();
                    OBDDatos();
                } catch (IOException | InterruptedException e) {
                    Toast.makeText(getApplicationContext(), "No es un dispositivo OBD" + "\n" + "La conexión se cerrará automaticamente", Toast.LENGTH_LONG).show();
                }
            }
        });

        alertDialogBt.setTitle("Choose Bluetooth device");
        alertDialogBt.show();

    }

    public void OBDDatos() {
        RPMCommand engineRpmCommand = new RPMCommand();
        SpeedCommand speedCommand = new SpeedCommand();
        while (!Thread.currentThread().isInterrupted())
        {
            try {
                engineRpmCommand.run(socket.getInputStream(), socket.getOutputStream());
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            try {
                speedCommand.run(socket.getInputStream(), socket.getOutputStream());
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            // TODO handle commands result
            tvRpmObd.setText(engineRpmCommand.getFormattedResult());
            Log.d(TAG, "RPM: " + engineRpmCommand.getFormattedResult());
            Log.d(TAG, "Speed: " + speedCommand.getFormattedResult());
        }
    }
}