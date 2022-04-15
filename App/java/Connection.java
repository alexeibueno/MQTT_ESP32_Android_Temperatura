package br.gov.sp.etec.app_mqtt_testenovo;

import android.content.Context;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

/**
 * Created by dinusha on 3/22/16.
 * Single instance
 */
public class Connection {

    private String cliendId;
    private String host;
    private int port;
    private MqttConnectOptions options;
    private MqttAndroidClient client;
    private Context context;
    private boolean tlsConnection;
    private ConnectionStatus status;

    private static Connection connection = null;

    public enum ConnectionStatus{
        CONNECTED,
        CONNECTING,
        DISCONNECTED,
        DISCONNECTING,
        ERROR,
        NONE
    }

    private Connection(){
        cliendId = null;
        host = null;
        port = 0;
        options = null;
        client = null;
        context = null;
        tlsConnection = false;
        status = ConnectionStatus.NONE;
    }

    public static Connection getInstance(){
        if(connection == null){
            connection = new Connection();
        }

        return connection;
    }

    public void createConnection(String clientId, String host, int port, MqttConnectOptions options, Context context, boolean tlsConnection){
        String uri = null;

        if(tlsConnection){
            uri = "ssl://" + host + ":" + port;
        }else{
            uri = "tcp://" + host + ":" + port;
        }

        this.cliendId = clientId;
        this.host = host;
        this.port = port;
        this.options = options;
        this.context = context;
        this.tlsConnection = tlsConnection;

        this.client = new MqttAndroidClient(context, uri, clientId);
    }

    public ConnectionStatus getStatus() {
        return status;
    }

    public void setStatus(ConnectionStatus status) {
        this.status = status;
    }

    public boolean isConnected(){
        return (this.status == ConnectionStatus.CONNECTED);
    }

    public MqttAndroidClient getClient() {
        return client;
    }

    public Context getContext() {
        return context;
    }

    public String getCliendId() {
        return cliendId;
    }


    public String getHost() {
        return host;
    }


    public int getPort() {
        return port;
    }

    public MqttConnectOptions getOptions() {
        return options;
    }

}
