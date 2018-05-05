package com.lynnsion.lmnpuht.Lynnsion.wifiUtil;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lynnsion.lmnpuht.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * Created by Lynnsion on 2018/4/28.
 */

public class WifiService extends AppCompatActivity implements View.OnClickListener {

    private EditText editMessage;
    private Button btnStartServer, btnSendMsg;

    private StringBuffer stringBuffer = new StringBuffer();

    private ServerSocket serverSocket = null;

    private InputStream inputStream;

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    editMessage.setText(msg.obj.toString());
                    break;

                case 2:
                    editMessage.setText(msg.obj.toString());
                    stringBuffer.setLength(0);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_wifiserver_activity);

        initLayout();

        receiverData();
    }

    /*
        服务器端接收数据
        需要注意以下一点：
        服务器端应该是多线程的，因为一个服务器可能会有多个客户端连接在服务器上；
     */
    private void receiverData() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                 /*指明服务器端的端口号*/
                try {
                    serverSocket = new ServerSocket(8000);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                GetIpAddress.getLocalIpAddress(serverSocket);

                Message message_1 = handler.obtainMessage();
                message_1.what = 1;
                message_1.obj = "IP:" + GetIpAddress.getIP() + " PORT: " + GetIpAddress.getPort();
                handler.sendMessage(message_1);

                while (true) {
                    Socket socket = null;
                    try {
                        socket = serverSocket.accept();
                        inputStream = socket.getInputStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    new ServerThread(socket, inputStream).start();

                }
            }
        };
        thread.start();
    }

    private void initLayout() {

        editMessage = (EditText) findViewById(R.id.editMessage);
        btnSendMsg = (Button) findViewById(R.id.btnSendMsg);
        btnSendMsg.setOnClickListener(this);

        btnStartServer = (Button) findViewById(R.id.btnStartServer);
        btnStartServer.setOnClickListener(this);
    }

    class ServerThread extends Thread {

        private Socket socket;
        private InputStream inputStream;
        private StringBuffer stringBuffer = WifiService.this.stringBuffer;


        public ServerThread(Socket socket, InputStream inputStream) {
            this.socket = socket;
            this.inputStream = inputStream;
        }

        @Override
        public void run() {
            int len;
            byte[] bytes = new byte[20];
            boolean isString = false;

            try {
                //在这里需要明白一下什么时候其会等于 -1，其在输入流关闭时才会等于 -1，
                //并不是数据读完了，再去读才会等于-1，数据读完了，最结果也就是读不到数据为0而已；
                while ((len = inputStream.read(bytes)) != -1) {
                    for (int i = 0; i < len; i++) {
                        if (bytes[i] != '\0') {
                            stringBuffer.append((char) bytes[i]);
                        } else {
                            isString = true;
                            break;
                        }
                    }
                    if (isString) {
                        Message message_2 = handler.obtainMessage();
                        message_2.what = 2;
                        message_2.obj = stringBuffer;
                        handler.sendMessage(message_2);
                        isString = false;
                    }

                }
                //当这个异常发生时，说明客户端那边的连接已经断开
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    inputStream.close();
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }

        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSendMsg:
                break;
            case R.id.btnStartServer:
                editMessage.setText("IP:" + GetIpAddress.getIP() + " PORT: " + GetIpAddress.getPort());
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
