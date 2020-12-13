package edu.pmdm.juegobotones;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Actividad principal. La finalidad es utilizar un GridLayout como interfaz principal de la
 * aplicación. Se trata de un sencillo juego que consiste en tocar botones de color verde, hasta
 * tres botones. También se incluye un menú desde el que se puede seleccionar la velicidad con la
 * que se cambian los colores de los botones. Como multimedia y animación se incluye:
 * - Giro y rotación del botón de color verde sobre el que se toca
 * - Multimedia: música de fondo del juego, sonido de error al tocar un botón que no es verde
 * y sonido al acertar en el toque del botón de color verde
 *
 * @author Rafa
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ArrayList<Button> botones = new ArrayList<Button>();
    private Timer timer;
    private Button btEmpezar;
    private MenuItem imMain;
    private int aciertos = 0;
    private int periodo = 1200;
    private MediaPlayer mediaPlayerJuego;
    private MediaPlayer mediaPlayerAcierto;
    private MediaPlayer mediaPlayerError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //se obtiene la rejilla
        GridLayout grdColors = findViewById(R.id.grdColors);
        //se obtienen los botones que hay dentro de la rejilla, se añaden al array de botones,
        //se ocultan los botones y se les suscribe al evento OnClick...
        for (int i = 0; i < grdColors.getChildCount(); i++) {
            if (grdColors.getChildAt(i) instanceof Button && grdColors.getChildAt(i).getId() != R.id.btEmpezar) {
                botones.add((Button) grdColors.getChildAt(i));
                botones.get(i).setVisibility(View.GONE);
                ((Button) grdColors.getChildAt(i)).setOnClickListener(this);
            }
        }
        //se configura el sonido de acierto, tocar el verde
        mediaPlayerAcierto = MediaPlayer.create(this, R.raw.acierto);
        mediaPlayerAcierto.setVolume(200, 200);
        //se configura el sonido de error, no tocar el verde
        mediaPlayerError = MediaPlayer.create(this, R.raw.error);
        mediaPlayerError.setVolume(200, 200);
        //se obtiene la referencia al TextView que muestra los aciertos
        TextView tvState = findViewById(R.id.tvState);
        //se obtiene la referencia al botón de emepezar
        btEmpezar = findViewById(R.id.btEmpezar);
        //se crea un evento Click para el botón empezar
        btEmpezar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //se reinician los aciertos
                aciertos = 0;
                //se muestra un toast de comienza el juego
                Toast.makeText(MainActivity.this, getResources().getString(R.string.tres_verdes), Toast.LENGTH_LONG).show();
                //se configura l música de fondo del juego
                mediaPlayerJuego = MediaPlayer.create(MainActivity.this, R.raw.juego_tronos);
                mediaPlayerJuego.setLooping(true);
                mediaPlayerJuego.setVolume(60, 60);
                //se inicia la música de fondo del juego
                mediaPlayerJuego.start();
                tvState.setTextColor(getResources().getColor(R.color.purple_200));
                //se desactiva el botón empezar
                view.setEnabled(false);
                //se desactiva el menú del juego
                imMain.setEnabled(false);
                //se muestran los botones de la rejilla
                for (int i = 0; i < 16; i++)
                    botones.get(i).setVisibility(View.VISIBLE);
                //se construye un temporizador que cada cierto período de tiempo cambiará aleatoriamente
                //los colores de fondo de los botones
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        final String[] arrColores = getResources().getStringArray(R.array.arrColors);
                        if (aciertos < 3) {
                            //hilo de ejecución con acceso a los componentes del hilo principal
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //se obtienen y asignan los colores aleatorias de los botones
                                    Random random = new Random();
                                    for (int i = 0; i < 16; i++) {
                                        int rnd = random.nextInt(16);
                                        botones.get(i).setTag(rnd);
                                        botones.get(i).setBackgroundColor(Color.parseColor(arrColores[rnd]));
                                    }
                                    //se actualizan los aciertos
                                    tvState.setText(getResources().getString(R.string.captura_verde) + ":" + aciertos);
                                }
                            });
                        } else {
                            //se detiene el temporizador
                            timer.cancel();
                            //se detiene la música de fondo
                            mediaPlayerJuego.stop();
                            //hilo de ejecución para acceder a los componentes del hilo principal
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //se ocultan los botones
                                    for (int i = 0; i < 16; i++)
                                        botones.get(i).setVisibility(View.GONE);
                                    //se actualizan los aciertos
                                    tvState.setText(getResources().getString(R.string.captura_verde) + ":" + aciertos);
                                    tvState.setTextColor(Color.parseColor(arrColores[0]));
                                    //se muestra el mensaje de enhorabuena
                                    Toast.makeText(MainActivity.this, getResources().getString(R.string.conseguido),
                                            Toast.LENGTH_LONG).show();
                                    //se activa el botón para comenzar a jugar de nuevo
                                    btEmpezar.setEnabled(true);
                                    //se activa el menú de la aplicación
                                    imMain.setEnabled(true);
                                }
                            });
                        }
                    }
                }, 0, periodo);
            }
        });
    }

    /**
     * Método de evento para los botones de la rejilla, menos el botón empezar
     * @param view Origen del evento
     */
    @Override
    public void onClick(View view) {
        //se comprueba si el botón sobre el que se ha pulsado es el verde
        if (view.getTag().toString().equals("0") && aciertos < 3) {
            //se pone en marcha el sonido de acierto
            mediaPlayerAcierto.start();
            //se incrementan los aciertos
            aciertos++;
            //se carga la animación de girar y rotar
            Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(this, R.anim.hyperspace);
            //se aplica la animación al botón
            view.startAnimation(hyperspaceJumpAnimation);
        } else {
            //se pone en marcha el sonido de error
            mediaPlayerError.start();
        }
    }

    /**
     * Método de callback para crear el menú de la aplicación
     * @param menu Objeto de tipo Menu donde se cargar el menú desplegado
     * @return boolean True si se ha implementado el método, false en caso contrario
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        imMain = menu.findItem(R.id.imMain);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    /**
     * Método de callback que es llamado cuando se pulsa sobre una opción de menú
     * @param item Elemento de menú sobre el que se ha pulsado
     * @return True si se ha implementado el método, false en caso contrario
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.imBaja:
                this.periodo = 1200;
                break;
            case R.id.imMedia:
                this.periodo = 1000;
                break;
            case R.id.imAlta:
                this.periodo = 800;
                break;
        }
        return true;
    }
}