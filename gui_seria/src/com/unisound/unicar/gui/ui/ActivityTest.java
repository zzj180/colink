package com.unisound.unicar.gui.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.framework.service.IMessageRouterCallback;
import com.unisound.unicar.framework.service.IMessageRouterService;
import com.unisound.unicar.gui.utils.ContactsUtil;
import com.unisound.unicar.gui.utils.Logger;

public class ActivityTest extends Activity implements OnClickListener {
    private final static String TAG = "MainActivity";
    private Context mContext;
    private IMessageRouterService mService;
    private Button mBtnConn, mBtnReg, mBtnSendEvent, mBtnSendMsg, btnWriteContactsInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_main);
        mContext = this;
        initView();
    }

    private void initView() {
        mBtnConn = (Button) findViewById(R.id.btnConn);
        mBtnConn.setOnClickListener(this);

        mBtnReg = (Button) findViewById(R.id.btnReg);
        mBtnReg.setOnClickListener(this);

        mBtnSendEvent = (Button) findViewById(R.id.btnSendEvent);
        mBtnSendEvent.setOnClickListener(this);

        mBtnSendMsg = (Button) findViewById(R.id.btnSendMsg);
        mBtnSendMsg.setOnClickListener(this);

        btnWriteContactsInfo = (Button) findViewById(R.id.btnWriteContactsInfo);
        btnWriteContactsInfo.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.btnConn:
                bindService(mContext);
                break;
            case R.id.btnReg:
                String systemCall =
                        "{\"type\":\"REG\",\"data\":{\"version\":\"v3.0\",\"moduleName\":\"GUI\",\"callNameList\":[{\"callName\":\"showRecognizerDialog\"},{\"callName\":\"inputContact\"},{\"callName\":\"selectContact\"}]}}";
                sendMsg(systemCall);
                break;
            case R.id.btnSendEvent:
                String eventMsg =
                        "{\"type\":\"EVENT\",\"data\":{\"moduleName\":\"GUI\",\"eventName\":\"PTT\"}}";
                sendMsg(eventMsg);
                break;
            case R.id.btnSendMsg:
                String sengMsg =
                        "{\"type\":\"RESP\",\"data\":{\"moduleName\":\"GUI\",\"callID\":\"1\",\"callName\":\"searchPOI\",\"params\":{\"searchPOI_state\":\"ok\",\"searchPOI_RES\":\"人民广场东门,人民广场西门\"}}}";
                sendMsg(sengMsg);
                break;
            // Test
            case R.id.btnWriteContactsInfo:
                writeContactsInfo(getApplicationContext());
                break;
            default:
                break;
        }
    }

    public void bindService(Context context) {
        try {
            Log("bindService");
            Intent i = new Intent("com.unisound.unicar.messagerouter.start");
            context.bindService(i, mConnection, Context.BIND_AUTO_CREATE);
        } catch (Exception e) {

        }
    }

    private void sendMsg(String msgJson) {
        if (mService != null) {
            try {
                mService.sendMessage(msgJson);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mService = IMessageRouterService.Stub.asInterface(service);
            try {
                Log("onServiceConnected");
                mService.registerCallback(mCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
            Log("onServiceDisconnected");
        }
    };


    private IMessageRouterCallback mCallback = new IMessageRouterCallback.Stub() {

        @Override
        public void onCallBack(String callBackJson) throws RemoteException {
            Log("onCallBack : " + callBackJson);
            SystemCallFunction systemCallFunction = new SystemCallFunction();
            systemCallFunction.showRecognizerDialog();
        }
    };

    public void unBindService(Context context) {
        try {
            Log("unBindService");
            context.unbindService(mConnection);
        } catch (Exception e) {

        }
    }

    private static void Log(String str) {
        Logger.d(TAG, "---" + str + "---");
    }

    private void writeContactsInfo(final Context ctx) {
        Thread t = new Thread() {
            @Override
            public void run() {
                ContactsUtil.testReadAllContacts(ctx);
            }
        };
        t.start();
    }

    class SystemCallFunction {
        public void showRecognizerDialog() {
            Log("showRecognizerDialog");
        }

        public void inputContact() {
            Log("inputContact");
        }

        public void selectContact() {
            Log("selectContact");
        }
    }

    interface CallFunction {
        public void callFunction(SystemCallFunction systemCallFunction);
    }

    class showRecognizerDialogFunction implements CallFunction {
        @Override
        public void callFunction(SystemCallFunction systemCallFunction) {
            systemCallFunction.showRecognizerDialog();
        }
    }

    class inputContactFuction implements CallFunction {
        @Override
        public void callFunction(SystemCallFunction systemCallFunction) {
            systemCallFunction.inputContact();
        }
    }

    class selectContactFuction implements CallFunction {
        @Override
        public void callFunction(SystemCallFunction systemCallFunction) {
            systemCallFunction.selectContact();
        }
    }
}
