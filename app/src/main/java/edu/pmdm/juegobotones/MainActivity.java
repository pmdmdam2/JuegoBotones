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
 * @author Rafa
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ArrayList<Button> botones=new ArrayList<Button>();
    private Timer timer;
    private Button btEmpezar;
    private MenuItem imMain;
    private int aciertos=0;
    private int periodo=1200;
    private MediaPlayer mediaPlayerJuego;
    private MediaPlayer mediaPlayerAcierto;
    private MediaPlayer mediaPlayerError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GridLayout grdColors = findViewById(R.id.grdColors);
        for(int i = 0; i<grdColors.getChildCount();i++){
            if(grdColors.getChildAt(i) instanceof Button && grdColors.getChildAt(i).getId()!=R.id.btEmpezar){
                botones.add((Button)grdColors.getChildAt(i));
                botones.get(i).setVisibility(View.GONE);
                ((Button)grdColors.getChildAt(i)).setOnClickListener(this);
            }
        }
        mediaPlayerAcierto= MediaPlayer.create(this, R.raw.acierto);
        mediaPlayerAcierto.setVolume(200,200);
        mediaPlayerError= MediaPlayer.create(this, R.raw.error);
        mediaPlayerError.setVolume(200,200);
        TextView tvState = findViewById(R.id.tvState);
        btEmpezar = findViewById(R.id.btEmpezar);
        btEmpezar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              aciertos = 0;
              Toast.makeText(MainActivity.this, getResources().getString(R.string.tres_verdes),Toast.LENGTH_LONG).show();
              mediaPlayerJuego= MediaPlayer.create(MainActivity.this, R.raw.juego_tronos);
              mediaPlayerJuego.setLooping(true);
              mediaPlayerJuego.setVolume(60,60);
              mediaPlayerJuego.start();
              tvState.setTextColor(getResources().getColor(R.color.purple_200));
              view.setEnabled(false);
              imMain.setEnabled(false);
              for (int i = 0; i < 16; i++)
                  botones.get(i).setVisibility(View.VISIBLE);
              timer = new Timer();
              timer.schedule(new TimerTask(){
                  @Override
                  public void run() {
                      final String[]  arrColores = getResources().getStringArray(R.array.arrColors);
                      if (aciertos < 3) {
                          runOnUiThread(new Runnable() {
                              @Override
                              public void run() {
                                  Random random = new Random();
                                  for (int i = 0; i < 16; i++) {
                                      int rnd = random.nextInt(16);
                                      botones.get(i).setTag(rnd);
                                      botones.get(i).setBackgroundColor(Color.parseColor(arrColores[rnd]));
                                  }
                                  tvState.setText(getResources().getString(R.string.captura_verde) + ":" + aciertos);
                              }
                          });
                      }else{
                          timer.cancel();
                          mediaPlayerJuego.stop();
                          runOnUiThread(new Runnable() {
                              @Override
                              public void run() {
                                  for (int i = 0; i < 16; i++)
                                      botones.get(i).setVisibility(View.GONE);

                                  tvState.setText(getResources().getString(R.string.captura_verde) + ":" + aciertos);
                                  tvState.setTextColor(Color.parseColor(arrColores[0]));
                                  Toast.makeText(MainActivity.this, getResources().getString(R.string.conseguido),
                                          Toast.LENGTH_LONG).show();
                                  btEmpezar.setEnabled(true);
                                  imMain.setEnabled(true);
                              }
                          });
                      }
                  }
              },0,periodo);
            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view.getTag().toString().equals("0") && aciertos<3) {
            mediaPlayerAcierto.start();
            aciertos++;
            Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(this, R.anim.hyperspace);
            view.startAnimation(hyperspaceJumpAnimation);
        }else{
            mediaPlayerError.start();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        imMain = menu.findItem(R.id.imMain);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.imBaja:
                this.periodo=1200;
                break;
            case R.id.imMedia:
                this.periodo=1000;
                break;
            case R.id.imAlta:
                this.periodo=800;
                break;
        }
        return true;
    }
}