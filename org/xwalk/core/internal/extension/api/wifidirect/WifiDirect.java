package org.xwalk.core.internal.extension.api.wifidirect;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.GroupInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xwalk.core.internal.extension.XWalkExtensionWithActivityStateListener;

public class WifiDirect extends XWalkExtensionWithActivityStateListener {
    private static final String CMD_CANCEL_CONNECT = "cancelConnect";
    private static final String CMD_CONNECT = "connect";
    private static final String CMD_DISCONNECT = "disconnect";
    private static final String CMD_DISCOVER_PEERS = "discoverPeers";
    private static final String CMD_GET_CONNECTION_INFO = "getConnectionInfo";
    private static final String CMD_GET_PEERS = "getPeers";
    private static final String CMD_INIT = "init";
    private static final String ERROR_BUSY = "WifiP2pManager.BUSY";
    private static final String ERROR_DEFAULT = "WifiP2pManager.ERROR";
    private static final String ERROR_GENERAL_ERROR_MSG_STEM = "Android WiFi Direct error: ";
    private static final String ERROR_INVALID_CALL_NO_DATA_MSG = "Error: Invalid connect API call - data === null";
    private static final String ERROR_NO_SERVICE_REQUESTS = "WifiP2pManager.NO_SERVICE_REQUESTS";
    private static final String ERROR_P2P_UNSUPPORTED = "WifiP2pManager.P2P_UNSUPPORTED";
    private static final String ERROR_REASON_CODE_STEM = "WifiP2pManager reasonCode: ";
    private static final String EVENT_CONNECTION_CHANGED = "connectionchanged";
    private static final String EVENT_DISCOVERY_STOPPED = "discoverystoppedevent";
    private static final String EVENT_PEERS_CHANGED = "peerschanged";
    private static final String EVENT_THIS_DEVICE_CHANGED = "thisdevicechanged";
    private static final String EVENT_WIFI_STATE_CHANGED = "wifistatechanged";
    public static final String JS_API_PATH = "jsapi/wifidirect_api.js";
    private static final String NAME = "xwalk.experimental.wifidirect";
    private static final String STATE_AVAILABLE = "available";
    private static final String STATE_CONNECTED = "connected";
    private static final String STATE_FAILED = "failed";
    private static final String STATE_INVITED = "invited";
    private static final String STATE_UNAVAILABLE = "unavailable";
    private static final String TAG = "WifiDirect";
    private static final String TAG_ASYNC_CALL_ID = "asyncCallId";
    private static final String TAG_CMD = "cmd";
    private static final String TAG_CONNECTED = "connected";
    private static final String TAG_DATA = "data";
    private static final String TAG_ENABLED = "enabled";
    private static final String TAG_ERROR = "error";
    private static final String TAG_ERROR_CODE = "errorCode";
    private static final String TAG_EVENT_NAME = "eventName";
    private static final String TAG_FALSE = "false";
    private static final String TAG_GROUP_FORMED = "groupFormed";
    private static final String TAG_IS_SERVER = "isServer";
    private static final String TAG_MAC = "MAC";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_NAME = "name";
    private static final String TAG_SERVER_IP = "serverIP";
    private static final String TAG_STATUS = "status";
    private static final String TAG_TRUE = "true";
    private static final String TAG_TYPE = "type";
    private Activity mActivity = null;
    private Channel mChannel = null;
    private IntentFilter mIntentFilter;
    private WifiP2pManager mManager = null;
    private BroadcastReceiver mReceiver = null;
    private boolean mReceiverRegistered = false;

    class C03815 extends BroadcastReceiver {
        C03815() {
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            JSONObject out = new JSONObject();
            try {
                if ("android.net.wifi.p2p.STATE_CHANGED".equals(action)) {
                    Object obj;
                    JSONObject access$400 = WifiDirect.this.setEventData(out, WifiDirect.EVENT_WIFI_STATE_CHANGED);
                    String str = WifiDirect.TAG_ENABLED;
                    if (intent.getIntExtra("wifi_p2p_state", -1) == 2) {
                        obj = WifiDirect.TAG_TRUE;
                    } else {
                        obj = WifiDirect.TAG_FALSE;
                    }
                    access$400.put(str, obj);
                } else if ("android.net.wifi.p2p.PEERS_CHANGED".equals(action)) {
                    WifiDirect.this.setEventData(out, WifiDirect.EVENT_PEERS_CHANGED);
                } else if ("android.net.wifi.p2p.CONNECTION_STATE_CHANGE".equals(action)) {
                    WifiDirect.this.setEventData(out, WifiDirect.EVENT_CONNECTION_CHANGED).put("connected", ((NetworkInfo) intent.getParcelableExtra("networkInfo")).isConnected());
                } else if ("android.net.wifi.p2p.THIS_DEVICE_CHANGED".equals(action)) {
                    WifiDirect.this.convertDeviceToJSON(WifiDirect.this.setEventData(out, WifiDirect.EVENT_THIS_DEVICE_CHANGED), (WifiP2pDevice) intent.getParcelableExtra("wifiP2pDevice"));
                } else if ("android.net.wifi.p2p.DISCOVERY_STATE_CHANGE".equals(action)) {
                    if (intent.getIntExtra("discoveryState", -1) == 1) {
                        WifiDirect.this.setEventData(out, WifiDirect.EVENT_DISCOVERY_STOPPED);
                    } else {
                        return;
                    }
                }
                WifiDirect.this.broadcastMessage(out.toString());
            } catch (JSONException e) {
                WifiDirect.this.printErrorMessage(e);
            }
        }
    }

    public WifiDirect(String jsApiContent, Activity activity) {
        super(NAME, jsApiContent, activity);
        this.mActivity = activity;
    }

    private ActionListener createCallActionListener(final int instanceID, final JSONObject jsonOutput) {
        return new ActionListener() {
            public void onSuccess() {
                try {
                    jsonOutput.put(WifiDirect.TAG_DATA, true);
                } catch (JSONException e) {
                    WifiDirect.this.printErrorMessage(e);
                }
                WifiDirect.this.postMessage(instanceID, jsonOutput.toString());
            }

            public void onFailure(int reasonCode) {
                WifiDirect.this.setError(jsonOutput, "", reasonCode);
                WifiDirect.this.postMessage(instanceID, jsonOutput.toString());
            }
        };
    }

    private void disconnect(final int instanceID, final JSONObject jsonOutput) {
        if (this.mManager != null && this.mChannel != null) {
            this.mManager.requestGroupInfo(this.mChannel, new GroupInfoListener() {
                public void onGroupInfoAvailable(WifiP2pGroup group) {
                    if (group != null) {
                        WifiDirect.this.mManager.removeGroup(WifiDirect.this.mChannel, WifiDirect.this.createCallActionListener(instanceID, jsonOutput));
                    }
                }
            });
        }
    }

    private void handleMessage(final int instanceID, String message) {
        try {
            JSONObject jsonInput = new JSONObject(message);
            String cmd = jsonInput.getString(TAG_CMD);
            String asyncCallId = jsonInput.getString(TAG_ASYNC_CALL_ID);
            final JSONObject jsonOutput = new JSONObject();
            jsonOutput.put(TAG_ASYNC_CALL_ID, asyncCallId);
            if (cmd.equals(CMD_DISCOVER_PEERS)) {
                this.mManager.discoverPeers(this.mChannel, createCallActionListener(instanceID, jsonOutput));
            } else if (cmd.equals(CMD_GET_PEERS)) {
                this.mManager.requestPeers(this.mChannel, new PeerListListener() {
                    public void onPeersAvailable(WifiP2pDeviceList peers) {
                        try {
                            jsonOutput.put(WifiDirect.TAG_DATA, WifiDirect.this.convertListToJSON(peers));
                        } catch (JSONException e) {
                            WifiDirect.this.printErrorMessage(e);
                        }
                        WifiDirect.this.postMessage(instanceID, jsonOutput.toString());
                    }
                });
            } else if (cmd.equals(CMD_INIT)) {
                jsonOutput.put(TAG_DATA, init());
                postMessage(instanceID, jsonOutput.toString());
            } else if (cmd.equals(CMD_GET_CONNECTION_INFO)) {
                this.mManager.requestConnectionInfo(this.mChannel, new ConnectionInfoListener() {
                    public void onConnectionInfoAvailable(WifiP2pInfo info) {
                        try {
                            JSONObject data = new JSONObject();
                            jsonOutput.put(WifiDirect.TAG_DATA, data);
                            data.put(WifiDirect.TAG_GROUP_FORMED, info.groupFormed);
                            if (info.groupFormed) {
                                data.put(WifiDirect.TAG_IS_SERVER, info.isGroupOwner);
                                data.put(WifiDirect.TAG_SERVER_IP, info.isGroupOwner ? "" : info.groupOwnerAddress.toString().replace("/", ""));
                            }
                            WifiDirect.this.postMessage(instanceID, jsonOutput.toString());
                        } catch (JSONException e) {
                            WifiDirect.this.printErrorMessage(e);
                        }
                    }
                });
            } else if (cmd.equals(CMD_CONNECT)) {
                JSONObject dev = jsonInput.getJSONObject(TAG_DATA);
                if (dev == null) {
                    setError(jsonOutput, ERROR_INVALID_CALL_NO_DATA_MSG, 0);
                    postMessage(instanceID, jsonOutput.toString());
                    return;
                }
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = dev.getString(TAG_MAC);
                config.wps.setup = 0;
                this.mManager.connect(this.mChannel, config, createCallActionListener(instanceID, jsonOutput));
            } else if (cmd.equals(CMD_CANCEL_CONNECT)) {
                this.mManager.cancelConnect(this.mChannel, createCallActionListener(instanceID, jsonOutput));
            } else if (cmd.equals(CMD_DISCONNECT)) {
                disconnect(instanceID, jsonOutput);
            }
        } catch (JSONException e) {
            printErrorMessage(e);
        }
    }

    private JSONObject setEventData(JSONObject out, String eventName) throws JSONException {
        out.put(TAG_EVENT_NAME, eventName);
        JSONObject data = new JSONObject();
        out.put(TAG_DATA, data);
        return data;
    }

    private boolean init() {
        if (this.mActivity == null) {
            return false;
        }
        this.mManager = (WifiP2pManager) this.mActivity.getSystemService("wifip2p");
        this.mChannel = this.mManager.initialize(this.mActivity, this.mActivity.getMainLooper(), null);
        this.mReceiver = new C03815();
        this.mIntentFilter = new IntentFilter();
        this.mIntentFilter.addAction("android.net.wifi.p2p.STATE_CHANGED");
        this.mIntentFilter.addAction("android.net.wifi.p2p.PEERS_CHANGED");
        this.mIntentFilter.addAction("android.net.wifi.p2p.CONNECTION_STATE_CHANGE");
        this.mIntentFilter.addAction("android.net.wifi.p2p.THIS_DEVICE_CHANGED");
        this.mIntentFilter.addAction("android.net.wifi.p2p.DISCOVERY_STATE_CHANGE");
        if (!this.mReceiverRegistered) {
            this.mActivity.registerReceiver(this.mReceiver, this.mIntentFilter);
            this.mReceiverRegistered = true;
        }
        return true;
    }

    private String convertStateToString(int state) {
        switch (state) {
            case 0:
                return "connected";
            case 1:
                return STATE_INVITED;
            case 2:
                return STATE_FAILED;
            case 3:
                return STATE_AVAILABLE;
            case 4:
                return STATE_UNAVAILABLE;
            default:
                return "";
        }
    }

    private void convertDeviceToJSON(JSONObject ob, WifiP2pDevice peer) throws JSONException {
        ob.put(TAG_MAC, peer.deviceAddress);
        ob.put(TAG_NAME, peer.deviceName);
        ob.put("type", peer.primaryDeviceType);
        ob.put("status", convertStateToString(peer.status));
    }

    private JSONArray convertListToJSON(WifiP2pDeviceList peers) throws JSONException {
        JSONArray arr = new JSONArray();
        for (WifiP2pDevice peer : peers.getDeviceList()) {
            JSONObject ob = new JSONObject();
            convertDeviceToJSON(ob, peer);
            arr.put(ob);
        }
        return arr;
    }

    protected void printErrorMessage(JSONException e) {
        Log.e(TAG, e.toString());
    }

    private String convertReasonCodeToString(int reasonCode) {
        switch (reasonCode) {
            case 0:
                return ERROR_DEFAULT;
            case 1:
                return ERROR_P2P_UNSUPPORTED;
            case 2:
                return ERROR_BUSY;
            case 3:
                return ERROR_NO_SERVICE_REQUESTS;
            default:
                return ERROR_REASON_CODE_STEM + reasonCode;
        }
    }

    protected void setError(JSONObject out, String errorMessage, int reasonCode) {
        JSONObject data = new JSONObject();
        JSONObject error = new JSONObject();
        try {
            out.put(TAG_DATA, data);
            String str = TAG_MESSAGE;
            if (errorMessage.isEmpty()) {
                errorMessage = ERROR_GENERAL_ERROR_MSG_STEM + convertReasonCodeToString(reasonCode);
            }
            error.put(str, errorMessage);
            error.put(TAG_ERROR_CODE, reasonCode);
            data.put(TAG_ERROR, error);
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }
    }

    public void onMessage(int instanceID, String message) {
        if (!message.isEmpty()) {
            handleMessage(instanceID, message);
        }
    }

    public void onActivityStateChange(Activity activity, int newState) {
        if (this.mReceiver != null) {
            switch (newState) {
                case 3:
                    this.mActivity = activity;
                    if (!this.mReceiverRegistered) {
                        this.mActivity.registerReceiver(this.mReceiver, this.mIntentFilter);
                        this.mReceiverRegistered = true;
                        return;
                    }
                    return;
                case 4:
                    if (this.mReceiverRegistered) {
                        this.mActivity.unregisterReceiver(this.mReceiver);
                        this.mReceiverRegistered = false;
                    }
                    this.mActivity = null;
                    return;
                default:
                    return;
            }
        }
    }

    public String onSyncMessage(int instanceID, String message) {
        return null;
    }
}
