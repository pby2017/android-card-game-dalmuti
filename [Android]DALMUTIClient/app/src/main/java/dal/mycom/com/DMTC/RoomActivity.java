package dal.mycom.com.DMTC;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class RoomActivity extends AppCompatActivity {

    ArrayList<String> gamerList;
    ArrayAdapter gameAdapter;
    ArrayList<String> chatList;
    ArrayAdapter chatAdapter;

    Intent toMyService;
    Intent fromMyClass;

    TextView tv_roomName;
    ScrollView sv2_chatList;
    ListView lv1_gamerList;
    ListView lv2_chatList;
    Button bt1_start;
    Button bt2_ready;
    Button bt3_refresh;
    Button bt4_sendChat;
    EditText et1_inputTalk;

    Boolean canStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        Log.e("RoomActivity","onCreate");

        gamerList = new ArrayList<>();
        chatList = new ArrayList<>();

        toMyService = null;
        fromMyClass = null;

        tv_roomName = (TextView)findViewById(R.id.tv_roomName);
        sv2_chatList = (ScrollView)findViewById(R.id.sv2_chatList);
        lv1_gamerList = (ListView)findViewById(R.id.lv1_gamerList);
        lv2_chatList = (ListView)findViewById(R.id.lv2_chatList);
        bt1_start = (Button)findViewById(R.id.bt1_start);
        bt2_ready = (Button)findViewById(R.id.bt2_ready);
        bt3_refresh = (Button)findViewById(R.id.bt3_refresh);
        bt4_sendChat = (Button)findViewById(R.id.bt4_sendChat);
        et1_inputTalk = (EditText)findViewById(R.id.et1_inputTalk);

        canStart = false;

        bt1_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(canStart){
//                    Toast.makeText(getApplicationContext(),"all players ready now.",Toast.LENGTH_LONG).show();
                    toMyService = new Intent(getApplicationContext(), MyService.class);

                    toMyService.putExtra("fromClass","RoomActivity");
                    toMyService.putExtra("cmd","CMD_pushStartBtn30");

                    startService(toMyService);
                }else{
                    Toast.makeText(getApplicationContext(),"all players need ready.",Toast.LENGTH_LONG).show();
                }
            }
        });

        bt2_ready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toMyService = new Intent(getApplicationContext(), MyService.class);

                toMyService.putExtra("fromClass","RoomActivity");
                toMyService.putExtra("cmd","CMD_pushReadyBtn30");

                startService(toMyService);
            }
        });

        bt3_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toMyService = new Intent(getApplicationContext(), MyService.class);

                toMyService.putExtra("fromClass","RoomActivity");
                toMyService.putExtra("cmd","CMD_pushRefreshBtn30");

                startService(toMyService);
            }
        });

        bt4_sendChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!et1_inputTalk.getText().toString().equals("")){
                    toMyService = new Intent(getApplicationContext(), MyService.class);

                    toMyService.putExtra("fromClass","RoomActivity");
                    toMyService.putExtra("cmd","CMD_pushSendChatBtn30");
                    toMyService.putExtra("chat",et1_inputTalk.getText().toString());
                    et1_inputTalk.setText("");

                    startService(toMyService);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("RoomActivity","onStart");

        fromMyClass = getIntent();
        if(fromMyClass != null){
            if(fromMyClass.hasExtra("fromClass")){
                processCMD(fromMyClass);
            }
        }
    }

    private void processCMD(Intent intent) {
        if(intent.getStringExtra("fromClass").equals("MyService")){
            if(intent.hasExtra("cmd")){
                if(intent.getStringExtra("cmd").equals("CMD_startRoomActivity03")){
                    setRoomName(intent.getStringExtra("roomName"));

                    setStartBtn((Boolean)intent.getSerializableExtra("master"));
                    setCanStart((Boolean)intent.getSerializableExtra("canStart"));

                    gamerList = null;
                    gamerList = (ArrayList<String>)intent.getSerializableExtra("gamerList");
                    setGamerListView(lv1_gamerList, gamerList);

                    chatList = null;
                    chatList = new ArrayList<>();
                    setChatListView(lv2_chatList, chatList);
                } // cmd.equals("CMD_startRoomActivity03")

                if(intent.getStringExtra("cmd").equals("CMD_Return_loadRoomActivity_NewMember03")){
                    Log.e("RoomActivity","cmd - CMD_Return_loadRoomActivity_NewMember03");

                    setCanStart((Boolean)intent.getSerializableExtra("canStart"));

                    gamerList = null;
                    gamerList=(ArrayList<String>)intent.getSerializableExtra("gamerList");
                    setGamerListView(lv1_gamerList, gamerList);

                    chatList = null;
                    chatList = (ArrayList<String>)intent.getSerializableExtra("chatList3");
                    setChatListView(lv2_chatList, chatList);
                } // cmd.equals("CMD_Return_loadRoomActivity_NewMember03")

                if(intent.getStringExtra("cmd").equals("CMD_Return_loadRoomActivity_ExitMember03")){
                    Log.e("RoomActivity_cmd","CMD_Return_loadRoomActivity_ExitMember03");
                    setStartBtn((Boolean)intent.getSerializableExtra("master"));
                    setCanStart((Boolean)intent.getSerializableExtra("canStart"));

                    gamerList = null;
                    gamerList=(ArrayList<String>)intent.getSerializableExtra("gamerList");
                    setGamerListView(lv1_gamerList, gamerList);

                    chatList = null;
                    chatList = (ArrayList<String>)intent.getSerializableExtra("chatList3");
                    setChatListView(lv2_chatList, chatList);
                } // CMD_Return_loadRoomActivity_ExitMember03

/*********************************************************************************************************************/
/* 준비버튼 관련 부분이지만, 방만들기와 게임방 접속시 변경할 내용으로 수정해야 함.*/

                if(intent.getStringExtra("cmd").equals("CMD_Return_loadRoomActivity_pushStartBtn03")){
                    Log.e("RoomActivity_cmd","CMD_Return_loadRoomActivity_pushStartBtn03");


                } // CMD_Return_loadRoomActivity_pushStartBtn03

                if(intent.getStringExtra("cmd").equals("CMD_Return_loadRoomActivity_pushReadyBtn03")){
                    Log.e("RoomActivity_cmd","CMD_Return_loadRoomActivity_pushReadyBtn03");
                    setCanStart((Boolean)intent.getSerializableExtra("canStart"));

                    gamerList = null;
                    gamerList = (ArrayList<String>)intent.getSerializableExtra("gamerList");

                    setGamerListView(lv1_gamerList, gamerList);
                } // CMD_Return_loadRoomActivity_pushReadyBtn03

/*********************************************************************************************************************/

                if(intent.getStringExtra("cmd").equals("CMD_Return_loadRoomActivity_pushRefreshBtn03")){
                    Log.e("RoomActivity_cmd","CMD_Return_loadRoomActivity_pushRefreshBtn03");

                    gamerList = null;
                    gamerList=(ArrayList<String>)intent.getSerializableExtra("gamerList");
                    setGamerListView(lv1_gamerList, gamerList);

                    chatList = null;
                    chatList = (ArrayList<String>)intent.getSerializableExtra("chatList3");
                    setChatListView(lv2_chatList, chatList);
                } // CMD_Return_loadRoomActivity_pushRefreshBtn03

                if(intent.getStringExtra("cmd").equals("CMD_Return_loadRoomActivity_pushSendChatBtn03")){
                    Log.e("RoomActivity_cmd","CMD_Return_loadRoomActivity_pushSendChatBtn03");

                    chatList = null;
                    chatList = (ArrayList<String>)intent.getSerializableExtra("chatList3");

                    setChatListView(lv2_chatList, chatList);
                } // CMD_Return_loadRoomActivity_pushSendChatBtn03
            } // has cmd
        } // fromClass.equals("MyService")

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("RoomActivity","onStop");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("RoomActivity","onDestroy");

        toMyService = new Intent(getApplicationContext(), MyService.class);

        toMyService.putExtra("fromClass","RoomActivity");
        toMyService.putExtra("cmd","CMD_ExitMember30");

        startService(toMyService);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e("RoomActivity","onNewIntent");

        if(intent != null){
            if(intent.hasExtra("fromClass")){
                processCMD(intent);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("RoomActivity","onResume");

    }

    private void setRoomName(String roomName){
        tv_roomName.setText(roomName);
    }

    private void setStartBtn(Boolean master){
        if(master){
            bt1_start.setVisibility(View.VISIBLE);
        }else{
            bt1_start.setVisibility(View.INVISIBLE);
        }
    }

    private void setCanStart(Boolean canStart){
        this.canStart = canStart;
    }

    private void setGamerListView(ListView lv, ArrayList<String> arrayList) {
        gameAdapter = new ArrayAdapter(getApplicationContext(),R.layout.listview_custom,arrayList);
        if(gameAdapter == null){
            Log.e("RoomActivity","gameAdapter is null");
        }else{
            Log.e("RoomActivity","gameAdapter is not null - "+gameAdapter.getCount());
        }
        if(lv == null){
            Log.e("RoomActivity","lv is null");
        }else{
            Log.e("RoomActivity","lv is not null");
        }
        lv.setAdapter(gameAdapter);
    }

    private void setChatListView(ListView lv, ArrayList<String> arrayList) {
        chatAdapter = new ArrayAdapter(getApplicationContext(),R.layout.listview_custom,arrayList);
        lv.setAdapter(chatAdapter);
    }
}
