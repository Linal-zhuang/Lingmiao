package com.lynnsion.lmnpuht.Lynnsion.wifiUtil;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lynnsion.lmnpuht.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client extends Activity implements View.OnClickListener {

    TextView TextViewOutput;
    EditText EditTextMsg;
    private Socket mClient;
    private Button btnConnect;

    private TcpClientConnector connector = TcpClientConnector.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_wificlient_activity);

        Button BtnSend = (Button) this.findViewById(R.id.btnSendMsgClient);
        TextViewOutput = (TextView) this.findViewById(R.id.tvGetMsg);
        EditTextMsg = (EditText) this.findViewById(R.id.editMessageClient);

        BtnSend.setOnClickListener(this);


        btnConnect = (Button) findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(this);

        connector.setOnConnectLinstener(new TcpClientConnector.ConnectLinstener() {
            @Override
            public void onReceiveData(String data) {
//                Message msg = new Message();
//                Bundle bundle = new Bundle();
//                bundle.putString("data", data);
//                msg.setData(bundle);
//                msg.what = 1;
//                mHandler.sendMessage(msg);
                TextViewOutput.setText(data);
            }
        });
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Toast.makeText(Client.this, msg.getData().get("data") + "", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnConnect:
                String ip = "192.168.11.32";
                int port = 6666;
//                connector.creatConnect(ip, port);
                    connect(ip, port);
                break;
            case R.id.btnSendMsgClient:
                try {
                    String data = "d0 36 61 9E 00 02 12 ed 00 ee";
//                    connector.send(data);
                    send(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                TextViewOutput.setText("send msg");
                break;
            default:
                break;
        }
    }


    private void connect(final String mSerIP, final int mSerPort) {
        new Thread() {
            public void run() {
                try {
                    mClient = new Socket(mSerIP, mSerPort);
                    InputStream inputStream = null;
                    inputStream = mClient.getInputStream();
                    byte[] buffer = new byte[1024];
                    int len = -1;
                    while ((len = inputStream.read(buffer)) != -1) {
                        String data = new String(buffer, 0, len);
                        Message message = new Message();
                        message.what = 100;
                        Bundle bundle = new Bundle();
                        bundle.putString("data", data);
                        message.setData(bundle);
                        mHandler.sendMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    public void send(String data) throws IOException {
        OutputStream outputStream = mClient.getOutputStream();
        outputStream.write(data.getBytes());
    }




}
