package com.example.game;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothConnectionManager {

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothAdapter bluetoothAdapter;
    private AcceptThread acceptThread;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;
    private Handler mainHandler;

    public BluetoothConnectionManager(Context context) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public void startServer() {
        acceptThread = new AcceptThread();
        acceptThread.start();
    }

    public void connectToDevice(BluetoothDevice device) {
        connectThread = new ConnectThread(device);
        connectThread.start();
    }

    public void sendScore(int score) {
        if (connectedThread != null) {
            connectedThread.write(("" + score).getBytes());
        }
    }

    public void receiveScore(ScoreCallback callback) {
        if (connectedThread != null) {
            connectedThread.setScoreCallback(callback);
        }
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket serverSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord("MyApp", MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            serverSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            while (true) {
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
                if (socket != null) {
                    manageConnectedSocket(socket);
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

        public void cancel() {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket socket;

        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            socket = tmp;
        }

        public void run() {
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
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
            try {
                socket.connect();
                manageConnectedSocket(socket);
            } catch (IOException e) {
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        }

        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void manageConnectedSocket(BluetoothSocket socket) {
        connectedThread = new ConnectedThread(socket);
        connectedThread.start();
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket socket;
        private final InputStream inputStream;
        private final OutputStream outputStream;
        private ScoreCallback scoreCallback;

        public ConnectedThread(BluetoothSocket socket) {
            this.socket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            inputStream = tmpIn;
            outputStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            while (true) {
                try {
                    bytes = inputStream.read(buffer);
                    String received = new String(buffer, 0, bytes);
                    // Handle incoming data
                    if (scoreCallback != null) {
                        mainHandler.post(() -> scoreCallback.onScoreReceived(Integer.parseInt(received)));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void setScoreCallback(ScoreCallback callback) {
            this.scoreCallback = callback;
        }

        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public interface ScoreCallback {
        void onScoreReceived(int score);
    }
}
