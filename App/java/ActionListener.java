package br.gov.sp.etec.app_mqtt_testenovo;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;

/**
 * Created by dinusha on 3/22/16.
 */
public class ActionListener implements IMqttActionListener {
    private Connection connection;
    private Context context;
    private Action action;

    TextView texto;

    private static final String TAG = "ActionListener";

    public enum Action {
        CONNECT, DISCONNECT, SUBSCRIBE, UNSUBSCRIBE, PUBLISH
    }

    public ActionListener(Connection con, Action a, Context ctx, TextView texto){
        connection = con;
        context = ctx;
        action = a;
        this.texto = texto;
    }

    @Override
    public void onSuccess(IMqttToken asyncActionToken){
        //Toast.makeText(context, asyncActionToken.toString(), Toast.LENGTH_SHORT).show();

        switch(action){
            case CONNECT:
                onConnect(asyncActionToken);
                break;
            case DISCONNECT:
                onDisconnect(asyncActionToken);
                break;
            case SUBSCRIBE:
                onSubscribe(asyncActionToken);
                break;
            case UNSUBSCRIBE:
                onUnsubscribe(asyncActionToken);
                break;
            case PUBLISH:
                onPublish(asyncActionToken);
                break;

        }
    }

    public void onDisconnect(IMqttToken asyncActionToken){
        texto.setText("Desconectado...");
    }

    public void onConnect(IMqttToken asyncActionToken){
        Log.d(TAG, "onConnect");
        Toast.makeText(context, "Successfully Connected", Toast.LENGTH_SHORT).show();

        connection.setStatus(Connection.ConnectionStatus.CONNECTED);
    }

    public void onSubscribe(IMqttToken asyncActionToken){
        Log.d(TAG, "onSubscribe");
        Toast.makeText(context, "Successfully Subscribed", Toast.LENGTH_SHORT).show();

        asyncActionToken.getClient().setCallback(new RecebeDoMqtt(context, texto));
    }

    public void onUnsubscribe(IMqttToken asyncActionToken){
        Log.d(TAG, "onUnsubscribe");
    }

    public void onPublish(IMqttToken asyncActionToken){
        Log.d(TAG, "onPublish");
    }

    @Override
    public void onFailure(IMqttToken asyncActionToken, Throwable exception){
        //Toast.makeText(context, asyncActionToken.toString(), Toast.LENGTH_SHORT).show();

        switch(action){
            case CONNECT:
                onConnect(exception);
                break;
            case DISCONNECT:
                onDisconnect(exception);
                break;
            case SUBSCRIBE:
                onSubscribe(exception);
                break;
            case UNSUBSCRIBE:
                onUnsubscribe(exception);
                break;
            case PUBLISH:
                onPublish(exception);
                break;

        }
    }

    //OnFailure Handlers

    public void onDisconnect(Throwable exception){
        Log.d(TAG, "onDisconnectFailed");
    }

    public void onConnect(Throwable exception){
        Log.d(TAG, "onConnectFailed");


    }

    public void onSubscribe(Throwable exception){
        Log.d(TAG, "onSubscribeFailed");

    }

    public void onUnsubscribe(Throwable exception){
        Log.d(TAG, "onUnsubscribeFailed");
    }

    public void onPublish(Throwable exception){
        Log.d(TAG, "onPublishFailed");
    }

}
