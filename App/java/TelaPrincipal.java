package br.gov.sp.etec.app_mqtt_testenovo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.MqttException;

public class TelaPrincipal extends AppCompatActivity {

    private final String TAG = "MQTT_CLIENT";
    private Connection connection;

    TextView tv_temperatura;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_principal);

        tv_temperatura = (TextView)findViewById(R.id.tv_temperatura);

        connection = Connection.getInstance();

        conecta();
    }

    public void conecta_clique(View v){
        conecta();
    }

    private void conecta(){

        String clientId = MqttClient.generateClientId();

        connection.createConnection(clientId, "34.203.227.54", 1883, null, this.getApplicationContext(), false);

        MqttConnectOptions options = new MqttConnectOptions();
        String topic = "alexei/temperatura";
        byte[] payload = "some payload".getBytes();
        options.setWill(topic, payload, 1, false);

        try {
            IMqttToken token = connection.getClient().connect(options);
            connection.setStatus(Connection.ConnectionStatus.CONNECTING);

            ActionListener listener = new ActionListener(connection, ActionListener.Action.CONNECT, this.getApplicationContext(), tv_temperatura);
            token.setActionCallback(listener);

            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                    Toast.makeText(getApplicationContext(), "CONECTADO", Toast.LENGTH_SHORT).show();

                    mqttSubscribe();

                    asyncActionToken.getClient().setCallback(new RecebeDoMqtt(getApplicationContext(), tv_temperatura));
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(getApplicationContext(), "Erro e conexão", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void mqttSubscribe(){
        //TÓPICO ******************************************************************
        final String topic = "alexei/temperatura";

        int qos = 1;

        try {
            IMqttToken subToken = connection.getClient().subscribe(topic, qos);
            ActionListener listener = new ActionListener(connection, ActionListener.Action.SUBSCRIBE, this.getApplicationContext(), tv_temperatura);
            subToken.setActionCallback(listener);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    //Toast.makeText(getApplicationContext(), "Subscribed OK" + topic, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }
}
