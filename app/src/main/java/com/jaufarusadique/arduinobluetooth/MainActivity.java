package com.jaufarusadique.arduinobluetooth;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    ImageView retryButton, switchButton;
    TextView connectionStatus;

    BluetoothAdapter        bluetoothAdapter;
    Set<BluetoothDevice>    pairedDevices;
    BluetoothDevice         result  = null;
    BluetoothSocket         socket;
    String                  myArduinoDevice = "HC-05";
    OutputStream            outputStream;

    String on   = "a";
    String off  = "b";
    boolean switchOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        retryButton = findViewById(R.id.retryButton);
        switchButton = findViewById(R.id.switchButton);
        connectionStatus = findViewById(R.id.connectionStatus);

        bluetoothAdapter    = BluetoothAdapter.getDefaultAdapter();
        if(!bluetoothAdapter.isEnabled()){
            bluetoothAdapter.enable();
            boolean isEnabled = false;
            do{
                if(bluetoothAdapter.isEnabled()){
                    isEnabled = true;
                }
            }while (!isEnabled);
        }
        if(bluetoothAdapter.isEnabled()){
            pairedDevices = bluetoothAdapter.getBondedDevices();
            for(BluetoothDevice bt : pairedDevices){
                if(myArduinoDevice.equals(bt.getName())){
                    result = bt;
                    break;
                }
            }
            connect();
        }

    }

    private void connect() {
        try {
            socket = (BluetoothSocket) result.getClass().getMethod("createRfcommSocket",new Class[]{int.class}).invoke(result,1);
            socket.connect();
            outputStream = socket.getOutputStream();
            connectionStatus.setText("Connected");
            retryButton.setVisibility(View.INVISIBLE);
        } catch (IllegalAccessException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            connectionStatus.setText("Disconnected");
            retryButton.setVisibility(View.VISIBLE);
        } catch (InvocationTargetException e) {
             Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
             connectionStatus.setText("Disconnected");
             retryButton.setVisibility(View.VISIBLE);
        } catch (NoSuchMethodException e) {
             Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
             connectionStatus.setText("Disconnected");
             retryButton.setVisibility(View.VISIBLE);
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            connectionStatus.setText("Disconnected");
            retryButton.setVisibility(View.VISIBLE);
        }
    }

    private void dataToSend(String data){
        byte[] b = data.getBytes();
        try {
            outputStream.write(b);
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            connectionStatus.setText("Disconnected");
            retryButton.setVisibility(View.VISIBLE);
        }
    }

    public void sendData(View view) {
        if(switchOn){
            dataToSend(off);
            switchButton.setColorFilter(Color.argb(255,255,0,0));
            switchOn = false;

        }
        if(!switchOn){
            dataToSend(on);
            switchButton.setColorFilter(Color.argb(255,0,255,0));
            switchOn = true;
        }
    }

    public void reConnect(View view) {
        connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothAdapter.disable();
    }
}
