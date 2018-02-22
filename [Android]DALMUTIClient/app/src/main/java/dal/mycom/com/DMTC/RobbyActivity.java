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

import java.util.ArrayList;

import dal.mycom.com.DMTC.CreateActivity;
import dal.mycom.com.DMTC.JoinActivity;
import dal.mycom.com.DMTC.R;

public class RobbyActivity extends AppCompatActivity {

    ArrayList<String> robbyList;
    ArrayAdapter robbyAdapter;
    ArrayList<String> chatList;
    ArrayAdapter chatAdapter;

    Intent toMyService;
    Intent toMyClass;
    Intent fromMyClass;

    ScrollView sv2_chatList;
    ListView lv1_robbyList;
    ListView lv2_chatList;
    Button bt1_create;
    Button bt2_join;
    Button bt3_refresh;
    Button bt4_sendChat;
    EditText et1_inputTalk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robby);
        Log.e("RobbyActivity","onCreate");

        robbyList = new ArrayList<>();
        chatList = new ArrayList<>();

        toMyService = null;
        fromMyClass = null;

        sv2_chatList = (ScrollView)findViewById(R.id.sv2_chatList);
        lv1_robbyList = (ListView)findViewById(R.id.lv1_robbyList);
        lv2_chatList = (ListView)findViewById(R.id.lv2_chatList);
        bt1_create = (Button)findViewById(R.id.bt1_create);
        bt2_join = (Button)findViewById(R.id.bt2_join);
        bt3_refresh = (Button)findViewById(R.id.bt3_refresh);
        bt4_sendChat = (Button)findViewById(R.id.bt4_sendChat);
        et1_inputTalk = (EditText)findViewById(R.id.et1_inputTalk);

        bt1_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toMyClass = new Intent(getApplicationContext(),CreateActivity.class);
                startActivity(toMyClass);
            }
        });

        bt2_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toMyClass = new Intent(getApplicationContext(),JoinActivity.class);
                startActivity(toMyClass);
            }
        });

        bt3_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toMyService = new Intent(getApplicationContext(),MyService.class);

                toMyService.putExtra("fromClass","RobbyActivity");
                toMyService.putExtra("cmd","CMD_pushRefreshBtn20");

                startService(toMyService);
            }
        });

        bt4_sendChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!et1_inputTalk.getText().toString().equals("")){
                    toMyService = new Intent(getApplicationContext(),MyService.class);

                    toMyService.putExtra("fromClass","RobbyActivity");
                    toMyService.putExtra("cmd","CMD_pushSendChatBtn20");
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
        Log.e("RobbyActivity","onStart");
        fromMyClass = getIntent();
        if(fromMyClass != null){
            if(fromMyClass.hasExtra("fromClass")){
                processCMD(fromMyClass);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("RobbyActivity","onStop");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e("RobbyActivity","onNewIntent");

        if(intent != null){
            if(intent.hasExtra("fromClass")){
                processCMD(intent);
            }
        }
    }

    private void processCMD(Intent intent) {

        if(intent.getStringExtra("fromClass").equals("MyService")){
            Log.e("RobbyActivity_fromClass","MyService");
            if(intent.hasExtra("cmd")){
                if(intent.getStringExtra("cmd").equals("CMD_startRobbyActivity02")){
                    Log.e("RobbyActivity","cmd - CMD_startRobbyActivity02");

                    robbyList = null;
                    robbyList=(ArrayList<String>)intent.getSerializableExtra("robbyList");
                    setRobbyListView(lv1_robbyList, robbyList);

                    chatList = null;
                    chatList = new ArrayList<>();
                    setChatListView(lv2_chatList, chatList);
                } // cmd.equals("CMD_startRobbyActivity02")

                if(intent.getStringExtra("cmd").equals("CMD_Return_loadRobbyActivity_NewMember02")){
                    Log.e("RobbyActivity","cmd - CMD_Return_loadRobbyActivity_NewMember02");

                    robbyList = null;
                    robbyList=(ArrayList<String>)intent.getSerializableExtra("robbyList");
                    setRobbyListView(lv1_robbyList, robbyList);

                    chatList = null;
                    chatList = (ArrayList<String>)intent.getSerializableExtra("chatList2");
                    setChatListView(lv2_chatList, chatList);
                } // cmd.equals("CMD_Return_loadRobbyActivity_NewMember02")

                if(intent.getStringExtra("cmd").equals("CMD_Return_loadRobbyActivity_ExitMember02")){
                    Log.e("RobbyActivity_cmd","CMD_Return_loadRobbyActivity_ExitMember02");

                    robbyList = null;
                    robbyList=(ArrayList<String>)intent.getSerializableExtra("robbyList");
                    setRobbyListView(lv1_robbyList, robbyList);

                    chatList = null;
                    chatList = (ArrayList<String>)intent.getSerializableExtra("chatList2");
                    setChatListView(lv2_chatList, chatList);
                } // CMD_Return_loadRobbyActivity_ExitMember02

/*********************************************************************************************************************/
/* 준비버튼 관련 부분이지만, 방만들기와 게임방 접속시 변경할 내용으로 수정해야 함.*/

                if(intent.getStringExtra("cmd").equals("CMD_Return_loadRobbyActivity_pushJoinBtn02")){
                    Log.e("RobbyActivity_cmd","CMD_Return_loadRobbyActivity_pushJoinBtn02");

                    robbyList = null;
                    robbyList = (ArrayList<String>)intent.getSerializableExtra("robbyList");

                    setRobbyListView(lv1_robbyList, robbyList);
                } // CMD_Return_loadRobbyActivity_pushJoinBtn02

/*********************************************************************************************************************/

                if(intent.getStringExtra("cmd").equals("CMD_Return_loadRobbyActivity_pushRefreshBtn02")){
                    Log.e("RobbyActivity_cmd","CMD_Return_loadRobbyActivity_pushRefreshBtn02");

                    robbyList = null;
                    robbyList=(ArrayList<String>)intent.getSerializableExtra("robbyList");
                    setRobbyListView(lv1_robbyList, robbyList);

                    chatList = null;
                    chatList = (ArrayList<String>)intent.getSerializableExtra("chatList2");
                    setChatListView(lv2_chatList, chatList);
                } // CMD_Return_loadRobbyActivity_pushRefreshBtn02

                if(intent.getStringExtra("cmd").equals("CMD_Return_loadRobbyActivity_pushSendChatBtn02")){
                    Log.e("RobbyActivity_cmd","CMD_Return_loadRobbyActivity_pushSendChatBtn02");

                    chatList = null;
                    chatList = (ArrayList<String>)intent.getSerializableExtra("chatList2");

                    setChatListView(lv2_chatList, chatList);
                } // CMD_Return_loadRobbyActivity_pushSendChatBtn02

            } // has cmd
        } // fromClass.equals("MyService")
    }

    private void setRobbyListView(ListView lv, ArrayList<String> arrayList) {
        robbyAdapter = new ArrayAdapter(getApplicationContext(),R.layout.listview_custom,arrayList);
        lv.setAdapter(robbyAdapter);

        Log.e("RobbyActivity","robbyList.size() : "+robbyList.size());
    }

    private void setChatListView(ListView lv, ArrayList<String> arrayList) {
        chatAdapter = new ArrayAdapter(getApplicationContext(),R.layout.listview_custom,arrayList);
        lv.setAdapter(chatAdapter);

        Log.e("RobbyActivity","chatList.size() : "+chatList.size());
    }
}
