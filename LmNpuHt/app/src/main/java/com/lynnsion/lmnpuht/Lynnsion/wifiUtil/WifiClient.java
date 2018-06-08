package com.lynnsion.lmnpuht.Lynnsion.wifiUtil;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lynnsion.lmnpuht.R;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;


public class WifiClient extends AppCompatActivity implements View.OnClickListener {
    private EditText editText_ip, editText_data;
    private OutputStream outputStream = null;
    private Socket socket = null;
    private String ip;
    private String data;
    private boolean socketStatus = false;

    private Button btnConnect, btnSendMsg;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_wificlient_activity);


        editText_ip = (EditText) findViewById(R.id.appCompatEditTextIp);
        editText_data = (EditText) findViewById(R.id.editMessageClient);

        btnConnect = (Button) findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(WifiClient.this);
        btnSendMsg = (Button) findViewById(R.id.btnSendMsgClient);
        btnSendMsg.setOnClickListener(WifiClient.this);
    }

    public void connect(View view) {
        ip = editText_ip.getText().toString();
        ip = "192.168.11.32";
        final int port = 5555;
        if (ip == null) {
            Toast.makeText(WifiClient.this, "please input Server IP", Toast.LENGTH_SHORT).show();
        }

        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();

                if (!socketStatus) {

                    try {
                        socket = new Socket(ip, port);
                        if (socket == null) {
                        } else {
                            socketStatus = true;
                        }
                        outputStream = socket.getOutputStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }
        };
        thread.start();
    }



    public void send(View view) {
//        data = editText_data.getText().toString();
        data = "d0 36 61 9E 00 02 12 ed 00 ee";
        if (data == null) {
            Toast.makeText(WifiClient.this, "please input Sending Data", Toast.LENGTH_SHORT).show();
        } else {
            //在后面加上 '\0' ,是为了在服务端方便我们去解析；
            data = data + '\0';
        }

        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                if (socketStatus) {
                    try {
                        outputStream.write(data.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }
        };
        thread.start();
    }

    /*当客户端界面返回时，关闭相应的socket资源*/
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*关闭相应的资源*/
        try {
            outputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnConnect:
                connect(v);
                break;
            case R.id.btnSendMsgClient:
                send(v);
                Toast.makeText(WifiClient.this, socket+"", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }
}