package top.zloop.mobile.biz.article.websocket;

import android.util.Log;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class SocketIOClient {

    private static final String TAG = "SocketIOClient";

    private static SocketIOClient instance;

//    public static final String SOCKET_URL = "http://10.1.29.229:9003/";
    public static final String SOCKET_URL = "https://socket-io-chat.now.sh/";

    private Socket mSocket;

    private OnConnectListener mListener;

    private SocketIOClient() {
        if(mSocket==null){
            try {
                IO.Options opts = new IO.Options();
//                opts.path = "/notify";
//                opts.query = "scanId=a3f453c91f4bb12ddfd963a277670c33";
                opts.transports = new String[]{"websocket"};
                opts.timeout = 10 * 1000;
//                opts.reconnection = true;
//                opts.reconnectionAttempts = 5;
//                opts.reconnectionDelayMax = 1000; //重连等待时间
//                opts.secure = true;
//                opts.forceNew = true;
                opts.hostnameVerifier = new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                };

//                mSocket = IO.socket(SOCKET_URL);
                mSocket = IO.socket(SOCKET_URL, opts);

            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    public static SocketIOClient getInstance() {
        if (instance == null) {
            instance = new SocketIOClient();
        }
        return instance;
    }

    public Socket getSocket(){
        return mSocket;
    }



    public SocketIOClient connect() {
        mSocket.on(Socket.EVENT_CONNECT,onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT,onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectTimeout);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.connect();
        return this;
    }

    public SocketIOClient setOnConnectListener(OnConnectListener listener) {
        mListener = listener;
        return this;
    }


    public void disconnect() {
        mSocket.disconnect();
        mSocket.off();
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG, "connected");
            Log.d(TAG, "mSocket.id()?:"+mSocket.id());
            Log.d(TAG, "mSocket.connected()?:"+mSocket.connected());
            if (mListener != null) mListener.onConnect();
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG, "diconnected");
            if (mListener != null) mListener.onDisconnect();
        }
    };

    private Emitter.Listener onConnectTimeout = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e(TAG, "connecting timeout");
            if (mListener != null) mListener.onConnectTimeout();
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e(TAG, "connecting error");
            if (mListener != null) mListener.onConnectError();
        }
    };




}
