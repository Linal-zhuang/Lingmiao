package com.lynnsion.lmnpuht.Lynnsion.stateMachine;

import android.os.Message;
import android.util.Log;

import com.lynnsion.lmnpuht.Lynnsion.stateMachine.util.State;
import com.lynnsion.lmnpuht.Lynnsion.stateMachine.util.StateMachine;

/**
 * Created by Lynnsion on 2018/4/17.
 */

@SuppressWarnings("SpellCheckingInspection")
public class NaviStateMachine extends StateMachine {

    private final static String TAG = NaviStateMachine.class.getSimpleName();

    private final static int MSG_REACHPOSE = 1;
    private final static int MSG_PLAYMUSICOVER = 2;
    private final static int MSG_PLAYSTART = 3;


    private UpdateUIListener listener = null;


    public NaviStateMachine() {
        super(TAG);

        addState(mDefaulteState, null);
        addState(mGoingState, mDefaulteState);
        addState(mPlayingMusicState, mDefaulteState);
        setInitialState(mGoingState);
        start();

    }

    public void registerListener(UpdateUIListener l) {
        this.listener = l;
    }

    private void notifyUI(String text) {
        if (listener != null) {
            listener.update(text);
        }
    }

    public void reachPose() {
        sendMessage(MSG_REACHPOSE);
    }

    public void playMusicOver() {
        sendMessage(MSG_PLAYMUSICOVER);
    }

//    public void playStart() {
//        sendMessage(MSG_PLAYMUSICOVER);
//    }

    private State mDefaulteState = new DefaultState();

    class DefaultState extends State {

        @Override
        public boolean processMessage(Message msg) {
            notifyUI("DefaultState: wrong command");
            return true;
        }
    }


    private State mPlayingMusicState = new PlayingMusicState();

    class PlayingMusicState extends State {
        @Override
        public void enter() {
            Log.d(TAG, "enter " + getName());
            notifyUI(getName());
        }

        @Override
        public boolean processMessage(Message msg) {
            switch (msg.what) {
                case MSG_PLAYMUSICOVER:
                    transitionTo(mGoingState);
                    break;
                default:
                    return false;
            }
            return true;
        }
    }

    private State mGoingState = new GoingState();

    class GoingState extends State {
        @Override
        public void enter() {
            Log.d(TAG, "enter " + getName());
            notifyUI(getName());
        }

        @Override
        public boolean processMessage(Message msg) {
            switch (msg.what) {
                case MSG_REACHPOSE:
                    transitionTo(mPlayingMusicState);
                    break;
                default:
                    return false;
            }
            return true;
        }
    }


}
