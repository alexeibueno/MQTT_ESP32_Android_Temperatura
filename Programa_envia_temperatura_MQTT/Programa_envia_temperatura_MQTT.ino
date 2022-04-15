// ---- Parâmetros do sensor de temperatura
#define ADC_VREF_mV    5000.0 // in millivolt
#define ADC_RESOLUTION 4095.0

//Pino de leitura analógica do sensor
//Pode-se utilizar o GPIO36, 39, 34 e 35 simultaneamente com Wifi
#define PIN_LM35       36 

//Include do WiFi e MQTT
#include <WiFi.h>
#include <PubSubClient.h>

/* Constantes do MQTT */
#define ID_MQTT  "esp32_mqtt"
#define TOPICO_PUBLISH_TEMPERATURA "alexei/temperatura"

const char* SSID = "Apolo"; //Nome da rede WI-FI que deseja se conectar
const char* PASSWORD = "08111979"; //Senha da rede WI-FI que deseja se conectar
 
const char* BROKER_MQTT = "34.203.227.54"; //URL do broker MQTT que se deseja utilizar
int BROKER_PORT = 1883; // Porta do Broker MQTT
   
//Variáveis e objetos globais
WiFiClient espClient; // Cria o objeto espClient
PubSubClient MQTT(espClient); // Instancia o Cliente MQTT passando o objeto espClient

/* Prototypes */
float faz_leitura_temperatura(void);
void initWiFi(void);
void initMQTT(void);
void mqtt_callback(char* topic, byte* payload, unsigned int length);
void reconnectMQTT(void);
void reconnectWiFi(void);
void VerificaConexoesWiFIEMQTT(void);

/* Função: faz a leitura de temperatura */
float faz_leitura_temperatura(void)
{
    // leitura do sensor
    int adcVal = analogRead(PIN_LM35);
    // converte leitura em millivolt
    float milliVolt = adcVal * (ADC_VREF_mV / ADC_RESOLUTION);
    // converte voltagem em n °C
    float tempC = milliVolt / 10;
    // retorna
    return tempC;
}

/* Função: inicializa e conecta-se na rede WI-FI */
void initWiFi(void) 
{
    delay(10);
    Serial.println("------Conexao WI-FI------");
    Serial.print("Conectando-se na rede: ");
    Serial.println(SSID);
    Serial.println("Aguarde");
      
    reconnectWiFi();
}

/* Função: reconecta-se ao broker MQTT (caso ainda não esteja conectado ou em caso de a conexão cair */
void reconnectMQTT(void) {
    while (!MQTT.connected()){
        Serial.print("* Tentando se conectar ao Broker MQTT: ");
        Serial.println(BROKER_MQTT);
        if (MQTT.connect(ID_MQTT)){
            Serial.println("Conectado com sucesso ao broker MQTT!");            
        }else{
            Serial.println("Falha ao reconectar no broker, nova tentativa em 2s");
            delay(2000);
        }
    }
}

/* Função: verifica o estado das conexões WiFI e ao broker MQTT. 
 *         Em caso de desconexão (qualquer uma das duas), a conexão é refeita. */
void VerificaConexoesWiFIEMQTT(void)
{
    if (!MQTT.connected()) 
        reconnectMQTT(); //se não há conexão com o Broker, a conexão é refeita      
     reconnectWiFi(); //se não há conexão com o WiFI, a conexão é refeita
}

/* Função: reconecta-se ao WiFi */
void reconnectWiFi(void) 
{
    //se já está conectado a rede WI-FI, nada é feito. 
    //Caso contrário, são efetuadas tentativas de conexão
    if (WiFi.status() == WL_CONNECTED)
        return;          
    WiFi.begin(SSID, PASSWORD); // Conecta na rede WI-FI      
    while (WiFi.status() != WL_CONNECTED) {
        delay(100);
        Serial.print(".");
    }    
    Serial.println();
    Serial.print("Conectado com sucesso na rede ");
    Serial.print(SSID);
    Serial.println("IP obtido: ");
    Serial.println(WiFi.localIP());
}

/* Função: inicializa parâmetros de conexão MQTT */
void initMQTT(void) 
{
    MQTT.setServer(BROKER_MQTT, BROKER_PORT);   //informa qual broker e porta deve ser conectado    
}


void setup() {
  Serial.begin(115200);    
  /* Inicializa a conexao wi-fi */
  initWiFi();
  /* Inicializa a conexao ao broker MQTT */
  initMQTT();  
}

/***************************************************************************/
void loop() {
    char temperatura_str[10] = {0};
     
    /* garante funcionamento das conexões WiFi e ao broker MQTT */
    VerificaConexoesWiFIEMQTT();
    
    /* Compoe as strings a serem enviadas pro dashboard (campos texto) */
    sprintf(temperatura_str,"%.2f", faz_leitura_temperatura());
 
    Serial.println(temperatura_str);
 
    /*  Envia as strings ao dashboard MQTT */
    MQTT.publish(TOPICO_PUBLISH_TEMPERATURA, temperatura_str, true);

    /* keep-alive da comunicação com broker MQTT */
    MQTT.loop();
 
    /* Refaz o ciclo após 2 segundos */
    delay(2000);
}
