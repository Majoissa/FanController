package com.majoissa.fancontroller;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    //definicion de las variables para als seekBar que controlaràn la velocidad de los ventiladores
    private SeekBar sbSpeed1;
    private SeekBar sbSpeed2;
    private SeekBar sbSpeed3;
    private SeekBar sbSpeed4;
    //definicion de las variables para ñps Switch que controlán el estado on/off de los ventiladores
    private Switch swOnOff1;
    private Switch swOnOff2;
    private Switch swOnOff3;
    private Switch swOnOff4;
    //definicion de los textview que mostraran informacion de los ventiladores
    private TextView tvSpeed1;
    private TextView tvSpeed2;
    private TextView tvSpeed3;
    private TextView tvSpeed4;
    private EditText etIPAddressA;

    //definicion de las variables para los editText en donde se introduciran los segmentos de la ip
    private EditText etIPAddressB;
    private EditText etIPAddressC;
    private EditText etIPAddressD;

    //definicion de la variable para el boton de confirmacion de la direccion ip
    private ImageButton ibtOk;
    //definicion de la variable textView que mostrará la direccion ip configurada
    private TextView tvIPAdressConfigurada;

    @Override
    protected void onCreate(Bundle savedInstanceState) { //este metodo se llama cuando se crea la actividad
        super.onCreate(savedInstanceState);
        //se establece el diseño de la interfaz de usuario para esta actividad
        setContentView(R.layout.activity_main);

        //getSupportActionBar().hide(); --> lo comento porque al app peta
        //configuro la ventana para que ocupe toda la pantalla
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //inicializacion de las variables de instancia con 'findViewById(...)'. Se conectan las variables de instancia con los elementos de la interfaz de usuario definidos en el archivo xml de diseño
        sbSpeed1 = findViewById(R.id.seekBar1);
        sbSpeed2 = findViewById(R.id.seekBar2);
        sbSpeed3 = findViewById(R.id.seekBar3);
        sbSpeed4 = findViewById(R.id.seekBar4);
        swOnOff1 = findViewById(R.id.switch1);
        swOnOff2 = findViewById(R.id.switch2);
        swOnOff3 = findViewById(R.id.switch3);
        swOnOff4 = findViewById(R.id.switch4);
        tvSpeed1 = findViewById(R.id.textView1);
        tvSpeed2 = findViewById(R.id.textView2);
        tvSpeed3 = findViewById(R.id.textView3);
        tvSpeed4 = findViewById(R.id.textView4);
        etIPAddressA = findViewById(R.id.editTextText);
        etIPAddressB = findViewById(R.id.editTextText2);
        etIPAddressC = findViewById(R.id.editTextText3);
        etIPAddressD = findViewById(R.id.editTextText4);
        ibtOk = findViewById(R.id.imageButton);
        tvIPAdressConfigurada = findViewById(R.id.textView);

        // Inicialment, els ventiladors estan parats. Els Switch ja estan en
        // "off" per configuració d'interfície i aquí desactivem les SeekBar i
        // posem els seus TextView de color gris per simular la desactivació.

            sbSpeed1.setEnabled(false);
            sbSpeed2.setEnabled(false);
            sbSpeed3.setEnabled(false);
            sbSpeed4.setEnabled(false);
            tvSpeed1.setTextColor(ContextCompat.getColor(this, R.color.text_desactivat));
            tvSpeed2.setTextColor(ContextCompat.getColor(this, R.color.text_desactivat));
            tvSpeed3.setTextColor(ContextCompat.getColor(this, R.color.text_desactivat));
            tvSpeed4.setTextColor(ContextCompat.getColor(this, R.color.text_desactivat));


        //asigno etiquetas a cada seekbar
        sbSpeed1.setTag(1);
        sbSpeed2.setTag(2);
        sbSpeed3.setTag(3);
        sbSpeed4.setTag(4);

        // Asignar el listener a los switches para gestionar cambios de estado
        FanSwitchStateChangeListener listener1 = new FanSwitchStateChangeListener(sbSpeed1, tvSpeed1, 1);
        swOnOff1.setOnCheckedChangeListener(listener1);

        FanSwitchStateChangeListener listener2 = new FanSwitchStateChangeListener(sbSpeed2, tvSpeed2, 2);
        swOnOff2.setOnCheckedChangeListener(listener2);

        FanSwitchStateChangeListener listener3 = new FanSwitchStateChangeListener(sbSpeed3, tvSpeed3, 3);
        swOnOff3.setOnCheckedChangeListener(listener3);

        FanSwitchStateChangeListener listener4 = new FanSwitchStateChangeListener(sbSpeed4, tvSpeed4, 4);
        swOnOff4.setOnCheckedChangeListener(listener4);

        //asigno listeners a los seekbar para gestionar cambios de velocidad
        FanSpeedAdjustmentListener seekBarListener = new FanSpeedAdjustmentListener();
        sbSpeed1.setOnSeekBarChangeListener(seekBarListener);
        sbSpeed2.setOnSeekBarChangeListener(seekBarListener);
        sbSpeed3.setOnSeekBarChangeListener(seekBarListener);
        sbSpeed4.setOnSeekBarChangeListener(seekBarListener);

        // Asignar el TextWatcher personalizado a cada EditText, para gestionar los cambios en la direccion IP
        IPAddressValidationWatcher textWatcher = new IPAddressValidationWatcher();
        etIPAddressA.addTextChangedListener(textWatcher);
        etIPAddressB.addTextChangedListener(textWatcher);
        etIPAddressC.addTextChangedListener(textWatcher);
        etIPAddressD.addTextChangedListener(textWatcher);

        //Configuracion del onFocusChangeListener para seleccionar el texto de un editText cuando recibe el foco
        View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && v instanceof EditText) {
                    ((EditText) v).selectAll();
                }
            }
        };

        etIPAddressA.setOnFocusChangeListener(focusChangeListener);
        etIPAddressB.setOnFocusChangeListener(focusChangeListener);
        etIPAddressC.setOnFocusChangeListener(focusChangeListener);
        etIPAddressD.setOnFocusChangeListener(focusChangeListener);

        //Configuracion del OnClickListener para el boton ibtOk para validar y establecer la direccion IP
        ibtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ipA = etIPAddressA.getText().toString();
                String ipB = etIPAddressB.getText().toString();
                String ipC = etIPAddressC.getText().toString();
                String ipD = etIPAddressD.getText().toString();
                if (isValidIPAddress(ipA) && isValidIPAddress(ipB) && isValidIPAddress(ipC) && isValidIPAddress(ipD)) {
                    String ipAddress = ipA + "." + ipB + "." + ipC + "." + ipD;
                    tvIPAdressConfigurada.setText(ipAddress);
                } else {
                    Toast.makeText(MainActivity.this, "Error: Dirección IP no válida", Toast.LENGTH_SHORT).show();
                }
            }

            private boolean isValidIPAddress(String ipPart) {
                try {
                    int ipNum = Integer.parseInt(ipPart);
                    return ipNum >= 0 && ipNum <= 255;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        });


    }
//Definicion de clase interna para manejar los cambios de texto en los edit text de la direccion ip
    private class IPAddressValidationWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // No es necesario implementar algo aquí
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // No es necesario implementar algo aquí
        }

        //metodo que verifica y reacciona a los cambios en los edittext
        @Override
        public void afterTextChanged(Editable s) {
            EditText[] editTexts = {etIPAddressA, etIPAddressB, etIPAddressC, etIPAddressD};
            for (int i = 0; i < editTexts.length; i++) {
                EditText currentEditText = editTexts[i];
                if (currentEditText.getText().hashCode() == s.hashCode()) {
                    String text = currentEditText.getText().toString();
                    if (!text.isEmpty() && (Integer.parseInt(text) < 0 || Integer.parseInt(text) > 255)) {
                        currentEditText.setBackgroundColor(Color.parseColor("#FFFFCCCC")); // Color de fondo rojo claro
                        Toast.makeText(MainActivity.this, "Error: El número debe estar entre 0 y 255", Toast.LENGTH_SHORT).show();
                    } else {
                        currentEditText.setBackgroundColor(Color.TRANSPARENT); // Restablece el color de fondo
                        if (text.length() == 3 && i < editTexts.length - 1) {
                            editTexts[i + 1].requestFocus(); // Pasar el foco al siguiente EditText
                        }
                        if (text.contains(".")) {
                            text = text.replace(".", "");
                            currentEditText.setText(text);
                            if (i < editTexts.length - 1) {
                                editTexts[i + 1].requestFocus();
                            }
                        }
                    }
                }
            }
        }
    }
    //definicion de clase interna para manejar los cambios de estado de los switches
    private class FanSwitchStateChangeListener implements CompoundButton.OnCheckedChangeListener {
        private SeekBar seekBar;
        private TextView textView;
        private int fanNumber;

        public FanSwitchStateChangeListener(SeekBar seekBar, TextView textView, int fanNumber) {
            this.seekBar = seekBar;
            this.textView = textView;
            this.fanNumber = fanNumber;
        }

        //gestiona los cambios de estado de los switch y actualiza la UI y la URL correspondiente
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            seekBar.setEnabled(isChecked);
            textView.setTextColor(isChecked ? Color.BLACK : ContextCompat.getColor(MainActivity.this, R.color.text_desactivat));

            // Construir la URL y enviar la petición HTTP
            String baseURL = "http://" + etIPAddressA.getText().toString() + "." +
                    etIPAddressB.getText().toString() + "." +
                    etIPAddressC.getText().toString() + "." +
                    etIPAddressD.getText().toString();
            String requestURL = baseURL + "?fan=" + fanNumber + "&state=" + (isChecked ? "on" : "off");
            if (isChecked) {
                requestURL += "&speed=" + seekBar.getProgress();
            }
            showToastWithUrl(requestURL);
        }

    }

    //configuracion de clase interna para manejar los cambios en las seekbar
    private class FanSpeedAdjustmentListener implements SeekBar.OnSeekBarChangeListener {

        //aqui se gestionan los cambios en las seekbar y se actualiza la URL
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                int fanNumber = (int) seekBar.getTag();
                String baseURL = "http://" + etIPAddressA.getText().toString() + "." +
                        etIPAddressB.getText().toString() + "." +
                        etIPAddressC.getText().toString() + "." +
                        etIPAddressD.getText().toString();
                String requestURL = baseURL + "?fan=" + fanNumber + "&state=on&speed=" + progress;
                showToastWithUrl(requestURL);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }

    //metodo para mostrar los toast con la URL generada
    private void showToastWithUrl(String urlStr) {
        runOnUiThread(() -> Toast.makeText(MainActivity.this, urlStr, Toast.LENGTH_LONG).show());
    }

}