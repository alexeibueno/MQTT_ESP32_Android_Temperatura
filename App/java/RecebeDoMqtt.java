package br.gov.sp.etec.app_mqtt_testenovo;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Created by dinusha on 3/22/16.
 */
public class RecebeDoMqtt implements MqttCallback {
    private Context context;
    TextView texto;

    private final String TAG = "MQTT_CLIENT";

    public RecebeDoMqtt(Context ctx, TextView texto){
        context = ctx;
        this.texto = texto;
    }

    @Override
    public void connectionLost(Throwable cause){
        texto.setText("Sem Conexão");
    }

    //RECEBE A MENAGEM DO MQTT**********************************************
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        //Toast.makeText(context, "Message : "+topic +" - "+ new String(message.getPayload()), Toast.LENGTH_LONG).show();
        texto.setText("Temperatura: "+new String(message.getPayload()));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token){

    }
}
