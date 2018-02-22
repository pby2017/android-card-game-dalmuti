package dal.mycom.com.DMTC;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class JoinActivity extends AppCompatActivity {

    Intent toMyService;
    Intent fromMyClass;
    Intent toMyClass;

    EditText et1_roomName;
    Button bt1_join;
    Button bt2_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        Log.e("JoinActivity","onCreate");

        et1_roomName = (EditText)findViewById(R.id.et1_roomName);
        bt1_join = (Button)findViewById(R.id.bt1_join);
        bt2_cancel = (Button)findViewById(R.id.bt2_cancel);

        bt1_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String roomName = et1_roomName.getText().toString();

                if(!roomName.equals("")){

                    toMyService = new Intent(getApplicationContext(), MyService.class);

                    toMyService.putExtra("fromClass","RobbyActivity");
                    toMyService.putExtra("cmd","CMD_joinGame20");
                    toMyService.putExtra("roomName",roomName);

                    startService(toMyService);

                    finish();
                }
            }
        });

        bt2_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e("JoinActivity","onNewIntent");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("JoinActivity","onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("JoinActivity","onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("JoinActivity","onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("JoinActivity","onDestroy");
    }
}
