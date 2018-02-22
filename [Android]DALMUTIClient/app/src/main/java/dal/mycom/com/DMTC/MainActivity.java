package dal.mycom.com.DMTC;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText et;
    EditText et1;
    Button bt;

    String hostIP = "";
    String hostNick = "";
    int port;

    boolean isConnected=false;

    Intent toMyService;
    Intent fromMyClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et = (EditText) findViewById(R.id.et);
        et1 = (EditText) findViewById(R.id.et1);
        bt = (Button) findViewById(R.id.bt);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hostIP = et.getText().toString().trim();
                hostNick = et1.getText().toString().trim();
                port = 7777;

                toMyService = new Intent(getApplicationContext(),MyService.class);

                toMyService.putExtra("fromClass","MainActivity");
                toMyService.putExtra("cmd","CMD_connect10");
                toMyService.putExtra("hostIP",hostIP);
                toMyService.putExtra("hostNick",hostNick);
                toMyService.putExtra("port",port);

                startService(toMyService);
            }
        });

        if(toMyService != null){
            stopService(toMyService);
            Log.e("MainActivity_onCreate","stopService");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {

        Log.e("MainActivity_onNewIntnt","before");
        super.onNewIntent(intent);
        Log.e("MainActivity_onNewIntnt","after");
        if(toMyService != null){
            stopService(toMyService);
            Log.e("MainActivity_onNewIntnt","stopService");
        }

        processCMD(intent);
    }

    private void processCMD(Intent intent) {

        if(intent.hasExtra("fromClass")){
            if(intent.getStringExtra("fromClass").equals("MyService")){
                if(intent.hasExtra("cmd")){
                    if(intent.getStringExtra("cmd").equals("CMD_connect_fail01")){
                        Toast.makeText(getApplicationContext(),"cannot connect this server",Toast.LENGTH_LONG).show();
                    } // cmd.equals("CMD_connect_fail01")
                } // has cmd
            } // fromClass.equals("MyService")
        } // has fromClass
    }

    @Override
    protected void onStart() {

        Log.e("MainActivity_onStart","before");
        super.onStart();
        Log.e("MainActivity_onStart","after");
        if(toMyService != null){
            stopService(toMyService);
            Log.e("MainActivity_onNewIntnt","stopService");
        }
    }

    @Override
    protected void onStop() {

        Log.e("MainActivity_onStop","before");
        super.onStop();
        Log.e("MainActivity_onStop","after");
    }

    @Override
    protected void onResume() {

        Log.e("MainActivity_onResume","before");
        super.onResume();
        Log.e("MainActivity_onResume","after");
    }

    @Override
    protected void onDestroy() {

        Log.e("MainActivity_onDestroy","before");
        super.onDestroy();
        Log.e("MainActivity_onDestroy","after");
        if(toMyService != null){
            stopService(toMyService);
            Log.e("MainActivity_onDestroy","stopService");
        }
    }
}
