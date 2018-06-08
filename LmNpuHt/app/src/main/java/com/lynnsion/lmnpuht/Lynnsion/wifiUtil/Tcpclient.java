package com.lynnsion.lmnpuht.Lynnsion.wifiUtil;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lynnsion.lmnpuht.R;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Tcpclient extends Activity implements View.OnClickListener {
    public static final String TAG = "testServer";

    TextView TextViewOutput;
    EditText EditTextMsg;
    String msg;

    private String ip;
    private String data;
    private boolean socketStatus = false;
    private Socket socket = null;
    private OutputStream outputStream = null;

    private Button btnConnect;

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
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSendMsg:
                break;
            case R.id.btnConnect:
                connect(v);
                break;

            case R.id.btnSendMsgClient:
                msg = "d0 36 61 9E 00 02 12 ed 00 ee";

                if (msg.length() == 0) {
                    TextViewOutput.append("cannot send blank msg\n");
                    return;
                }

                Thread thread = new Thread(new NetThread(), "thread1");
                thread.start();

                EditTextMsg.setText("");
                break;
            default:
                break;
        }

    }


    public void connect(View view) {
        ip = "192.168.11.32";
        final int port = 5555;
        if (ip == null) {
            Toast.makeText(this, "please input Server IP", Toast.LENGTH_SHORT).show();
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


    class NetThread implements Runnable {
        @Override
        public void run() {
            String ip = "192.168.11.32";
            final int port = 5555;
            try {
                Socket socket = new Socket(ip, port);
                PrintWriter pw = new PrintWriter(socket.getOutputStream());

                TextViewOutput.post(new ChangeText("Sending:" + msg));
                pw.println(msg);
                pw.flush();
                Tcpclient.this.runOnUiThread(new ChangeText("...finished!\n"));

                Scanner scan = new Scanner(socket.getInputStream());
                String ret = scan.nextLine();
                TextViewOutput.post(new ChangeText("Return:" + ret + "\n"));

                pw.close();
                scan.close();

                socket.close();
            } catch (Exception EE) {
                EE.printStackTrace();
            }
        }
    }

    class ChangeText implements Runnable {
        String text;

        ChangeText(String text) {
            this.text = text;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            TextViewOutput.append(text);
        }
    }

    private void Chongdian(){

    }


}


