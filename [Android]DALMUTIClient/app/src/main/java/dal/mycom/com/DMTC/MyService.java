package dal.mycom.com.DMTC;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class MyService extends Service {

    String hostIP;
    String hostNick;
    int port;
    int nowState; /*1 = Main, 2 = robby, 2 = create, 2 = join, 3 = room, 4 = in game*/
    boolean isConnected;
    String IPHName;
    String roomName;
    Boolean master;
    Boolean canStart;
    String nowPlayer;
    String lastPlayer;
    int nowCardNumber;
    int nowCardCount;
    String showhDeck;
    HashMap<Integer, Integer> hDeck;

    String sendCMD;
    String receiveCMD;

    ArrayList<String> robbyList;
    ArrayList<String> chatList2;
    ArrayList<String> gamerList;
    ArrayList<String> chatList3;

    Intent toMyClass;

    ArrayList<Thread> threadManage;
    Socket socketManage;

    public MyService() {}

    @Override
    public void onCreate() {

        super.onCreate();
        port = 7777;
        nowState = 1;
        isConnected = false;
        roomName="";
        master=false;

        sendCMD = "";
        receiveCMD = "";

        robbyList = new ArrayList<>();
        chatList2 = new ArrayList<>();
        gamerList = new ArrayList<>();
        chatList3 = new ArrayList<>();

        toMyClass = null;

        threadManage = new ArrayList<>();
        socketManage = null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent == null){
            Log.e("MyService_restart","restart from strong exit");
            return Service.START_STICKY; /*START_STICKY, START_NOT_STICKY, START_REDELIVER_INTENT */
        }else{
            processCMD(intent);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.e("MyService_onDestroy","before!");
        super.onDestroy();
        Log.e("MyService_onDestroy","after!");

        Log.e("MyService_onDestroy","CMD_thread_socket_close");
        if(!threadManage.isEmpty()){
            for(Thread th : threadManage){
                if(th != null){
                    Log.e("MyService_onDestroy","th.interrupt : "+th.getName());
                    th.interrupt();
                }
            }
            threadManage.clear();
        }

        if(socketManage != null){
            try{
                Log.e("MyService_onDestroy","socketManage.close()");
                socketManage.close();
                socketManage = null;
            }catch (Exception ex){
                ex.printStackTrace();
                Log.e("MyService_onDestroy","socketManage.close() fail!");
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void processCMD(Intent intent) {

        if(intent.hasExtra("fromClass")){
            if(intent.getStringExtra("fromClass").equals("MainActivity")){
                if(intent.hasExtra("cmd")){
                    if(intent.getStringExtra("cmd").equals("CMD_connect10")){
                        hostIP = intent.getStringExtra("hostIP");
                        hostNick = intent.getStringExtra("hostNick");
                        port = intent.getIntExtra("port",7777); // 7777 -> 10000 : for default value test

                        if(!isConnected){
                            new Thread(){
                                public void run(){
                                    try {
                                        Socket s = new Socket();
                                        s.connect(new InetSocketAddress(hostIP,port),2000);
                                        socketManage = s;
                                        Thread sender = new Thread(new ClientSender(s, hostNick));
                                        Thread receiver = new Thread(new ClientReceiver(s));
                                        threadManage.add(sender);
                                        threadManage.add(receiver);
                                        sender.start();
                                        receiver.start();
                                        isConnected = true;
                                    } catch(Exception ex) {
                                        ex.printStackTrace();
                                        Log.e("MyService_error","s.connect & isConnected = "+isConnected);
                                        processCMD("CMD_connect_fail");
                                    }finally {
                                        Log.e("MyService_connth","connect thread exit");
                                    }
                                }
                            }.start();
                        } // !isConnected
                    } // cmd.equals("CMD_connect10")

                } // has cmd
            } // fromClass.equals("MainActivity")

            if(intent.getStringExtra("fromClass").equals("RobbyActivity")){
                Log.e("MyService_fromClass","RobbyActivity");

                if(intent.hasExtra("cmd")){
                    if(intent.getStringExtra("cmd").equals("CMD_pushRefreshBtn20")){
                        Log.e("MyService_CMD","CMD_pushRefreshBtn20");

                        processCMD("CMD_pushRefreshBtn2");
                    } // cmd.equals("CMD_pushRefreshBtn20")

                    if(intent.getStringExtra("cmd").equals("CMD_pushSendChatBtn20")){
                        Log.e("MyService_CMD","CMD_pushSendChatBtn20");

                        if(intent.hasExtra("chat")){
                            processCMD("CMD_pushSendChatBtn2",intent.getStringExtra("chat"));
                        }// has chat

                    } // cmd.equals("CMD_pushSendChatBtn20")

                    if (intent.getStringExtra("cmd").equals("CMD_createGame20")) {
                        Log.e("MyService_CMD", "CMD_createGame20");

                        processCMD("CMD_createGame", intent.getStringExtra("roomName"));
                    } // cmd.equals("CMD_createGame20")

                    if (intent.getStringExtra("cmd").equals("CMD_joinGame20")) {
                        Log.e("MyService_CMD", "CMD_joinGame20");

                        processCMD("CMD_joinGame", intent.getStringExtra("roomName"));
                    } // cmd.equals("CMD_joinGame20")

                } // has cmd
            } // fromClass.equals("RobbyActivity")

            if(intent.getStringExtra("fromClass").equals("RoomActivity")){
                Log.e("MyService","fromClass - RoomActivity");

                if(intent.hasExtra("cmd")) {
                    if(intent.getStringExtra("cmd").equals("CMD_ExitMember30")) {
                        Log.e("MyService_CMD", "CMD_ExitMember30");

                        processCMD("CMD_ExitMember3");
                    } // cmd.equals("CMD_ExitMember30")

                    if(intent.getStringExtra("cmd").equals("CMD_pushStartBtn30")){
                        Log.e("MyService_CMD","CMD_pushStartBtn30");

                        processCMD("CMD_pushStartBtn");
                    } // CMD_pushStartBtn30

/*********************************************************************************************************************/
/* 준비버튼 관련 부분이지만, 방만들기와 게임방 접속시 변경할 내용으로 수정해야 함.*/

                    if(intent.getStringExtra("cmd").equals("CMD_pushReadyBtn30")){
                        Log.e("MyService_CMD","CMD_pushReadyBtn30");

                        processCMD("CMD_pushReadyBtn");
                    } // cmd.equals("CMD_pushReadyBtn30")

/*********************************************************************************************************************/

                    if(intent.getStringExtra("cmd").equals("CMD_pushRefreshBtn30")){
                        Log.e("MyService_CMD","CMD_pushRefreshBtn30");

                        processCMD("CMD_pushRefreshBtn3");
                    } // cmd.equals("CMD_pushRefreshBtn30")

                    if(intent.getStringExtra("cmd").equals("CMD_pushSendChatBtn30")){
                        Log.e("MyService_CMD","CMD_pushSendChatBtn30");

                        if(intent.hasExtra("chat")){
                            processCMD("CMD_pushSendChatBtn3",intent.getStringExtra("chat"));
                        }// has chat

                    } // cmd.equals("CMD_pushSendChatBtn30")

                } // has cmd
            } // fromClass.equals("RoomActivity")

            if(intent.getStringExtra("fromClass").equals("GameActivity")) {
                Log.e("MyService", "fromClass - GameActivity");

                if(intent.getStringExtra("cmd").equals("CMD_pushSubmitBtn40")){
                    String submitMsg = "CMD_Client_pushSubmitBtn4"+
                            ":cardNumber"+intent.getStringExtra("cardNumber")+
                            ":cardCount"+intent.getStringExtra("cardCount");
                    if(intent.hasExtra("jokerCount")){
                        submitMsg += ":jokerCount"+intent.getStringExtra("jokerCount");
                    }
                    processCMD(submitMsg);
                } // cmd.equals("CMD_pushSubmitBtn40")

                if(intent.getStringExtra("cmd").equals("CMD_pushPassBtn40")){
                    Log.e("MyService", "cmd - CMD_pushPassBtn40");

                    processCMD("CMD_pushPassBtn4");
                } // cmd.equals("CMD_pushPassBtn40")
            } // fromClass.equals("GameActivity")

        } // has fromClass

    } // processCMD(Intent intent)

    public void processCMD(String CMD) {

        if(CMD.equals("CMD_thread_socket_close")){
            Log.e("MyService_processCMD","CMD_thread_socket_close");
            if(!threadManage.isEmpty()){
                for(Thread th : threadManage){
                    if(th != null){
                        Log.e("MyService_processCMD","th.interrupt : "+th.getName());
                        th.interrupt();
                    }
                }
                threadManage.clear();
            }

            if(socketManage != null){
                try{
                    Log.e("MyService_processCMD","socketManage.close()");
                    socketManage.close();
                    socketManage = null;
                }catch (Exception ex){
                    ex.printStackTrace();
                    Log.e("MyService_processCMD","socketManage.close() fail!");
                }
            }

            processCMD("CMD_connect_fail");
        } // CMD_thread_socket_close

        if(CMD.equals("CMD_connect_fail")){
            Log.e("MyService_processCMD","CMD_connect_fail");

            Intent toMyClass = new Intent(getApplicationContext(), MainActivity.class);
            toMyClass.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_NO_HISTORY);
            toMyClass.putExtra("fromClass", "MyService");
            toMyClass.putExtra("cmd","CMD_connect_fail01");
            startActivity(toMyClass);
        } // CMD_connect_fail

        if(CMD.equals("CMD_startRobbyActivity")){
            Log.e("MyService_processCMD","CMD_startRobbyActivity");

            processCMD("CMD_changeActivity_2");
            canStart = false;

            Intent toMyClass = new Intent(getApplicationContext(), RobbyActivity.class);
            toMyClass.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            toMyClass.putExtra("fromClass", "MyService");
            toMyClass.putExtra("cmd","CMD_startRobbyActivity02");
            toMyClass.putExtra("robbyList", robbyList);
            startActivity(toMyClass);
        } // CMD_startRobbyActivity

        if(CMD.equals("CMD_startRoomActivity")){
            Log.e("MyService_processCMD","CMD_startRoomActivity");

            processCMD("CMD_changeActivity_3");
            canStart = false;

            Intent toMyClass = new Intent(getApplicationContext(), RoomActivity.class);
            toMyClass.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            toMyClass.putExtra("fromClass", "MyService");
            toMyClass.putExtra("cmd","CMD_startRoomActivity03");
            toMyClass.putExtra("roomName", roomName);
            toMyClass.putExtra("gamerList", gamerList);
            toMyClass.putExtra("master",master);
            toMyClass.putExtra("canStart",canStart);

            startActivity(toMyClass);
        } // CMD_startRoomActivity

        if(CMD.equals("CMD_startGameActivity")){
            Log.e("MyService_processCMD","CMD_startGameActivity");

            processCMD("CMD_changeActivity_4");
            canStart = false;

            Intent toMyClass = new Intent(getApplicationContext(), GameActivity.class);
            toMyClass.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            toMyClass.putExtra("fromClass", "MyService");
            toMyClass.putExtra("cmd","CMD_startGameActivity04");
            toMyClass.putExtra("roomName", roomName);
            toMyClass.putExtra("IPHName", IPHName);
            toMyClass.putExtra("nowPlayer",nowPlayer);
            toMyClass.putExtra("lastPlayer",lastPlayer);
            toMyClass.putExtra("nowCardNumber",nowCardNumber);
            toMyClass.putExtra("nowCardCount",nowCardCount);
            toMyClass.putExtra("showhDeck",showhDeck);
            toMyClass.putExtra("hDeck",hDeck);

            startActivity(toMyClass);
        } // CMD_startGameActivity

        if(CMD.equals("CMD_loadRobbyActivity_NewMember")){
            Log.e("MyService_processCMD","CMD_loadRobbyActivity_NewMember");

            toMyClass = new Intent(getApplicationContext(), RobbyActivity.class);
            toMyClass.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TOP);
            toMyClass.putExtra("fromClass", "MyService");
            toMyClass.putExtra("cmd", "CMD_Return_loadRobbyActivity_NewMember02");
            toMyClass.putExtra("robbyList",robbyList);
            toMyClass.putExtra("chatList2",chatList2);

            startActivity(toMyClass);
        } // CMD_loadRobbyActivity_NewMember

        if(CMD.equals("CMD_loadRoomActivity_NewMember")){
            Log.e("MyService_processCMD","CMD_loadRoomActivity_NewMember");

            canStart = false;

            toMyClass = new Intent(getApplicationContext(), RoomActivity.class);

            toMyClass.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TOP);
            toMyClass.putExtra("fromClass", "MyService");
            toMyClass.putExtra("cmd", "CMD_Return_loadRoomActivity_NewMember03");
            toMyClass.putExtra("gamerList",gamerList);
            toMyClass.putExtra("chatList3",chatList3);
            toMyClass.putExtra("canStart",canStart);

            startActivity(toMyClass);
        } // CMD_loadRoomActivity_NewMember

/*********************************************************************************************************************/
/* 준비버튼 관련 부분이지만, 방만들기와 게임방 접속시 변경할 내용으로 수정해야 함.*/

        if(CMD.equals("CMD_loadRoomActivity_pushReadyBtn")){
            Log.e("MyService_processCMD","CMD_loadRoomActivity_pushReadyBtn");

            toMyClass = new Intent(getApplicationContext(), RoomActivity.class);

            toMyClass.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TOP);
            toMyClass.putExtra("fromClass", "MyService");
            toMyClass.putExtra("cmd", "CMD_Return_loadRoomActivity_pushReadyBtn03");
            toMyClass.putExtra("gamerList",gamerList);
            toMyClass.putExtra("canStart",canStart);

            startActivity(toMyClass);
        } // CMD_loadRoomActivity_pushReadyBtn

/*********************************************************************************************************************/

        if(CMD.equals("CMD_loadRobbyActivity_pushRefreshBtn")){
            Log.e("MyService_processCMD","CMD_loadRobbyActivity_pushRefreshBtn");

            toMyClass = new Intent(getApplicationContext(), RobbyActivity.class);

            toMyClass.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TOP);
            toMyClass.putExtra("fromClass", "MyService");
            toMyClass.putExtra("cmd", "CMD_Return_loadRobbyActivity_pushRefreshBtn02");
            toMyClass.putExtra("robbyList",robbyList);
            toMyClass.putExtra("chatList2",chatList2);

            startActivity(toMyClass);
        } // CMD_loadRobbyActivity_pushRefreshBtn

        if(CMD.equals("CMD_loadRobbyActivity_pushSendChatBtn")){
            Log.e("MyService_processCMD","CMD_loadRobbyActivity_pushSendChatBtn");

            toMyClass = new Intent(getApplicationContext(), RobbyActivity.class);

            toMyClass.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TOP);
            toMyClass.putExtra("fromClass", "MyService");
            toMyClass.putExtra("cmd", "CMD_Return_loadRobbyActivity_pushSendChatBtn02");
            toMyClass.putExtra("chatList2",chatList2);

            startActivity(toMyClass);
        } // CMD_loadRobbyActivity_pushSendChatBtn

        if(CMD.equals("CMD_loadRoomActivity_pushSendChatBtn")){
            Log.e("MyService_processCMD","CMD_loadRoomActivity_pushSendChatBtn");

            toMyClass = new Intent(getApplicationContext(), RoomActivity.class);

            toMyClass.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TOP);
            toMyClass.putExtra("fromClass", "MyService");
            toMyClass.putExtra("cmd", "CMD_Return_loadRoomActivity_pushSendChatBtn03");
            toMyClass.putExtra("chatList3",chatList3);

            startActivity(toMyClass);
        } // CMD_loadRoomActivity_pushSendChatBtn

        if(CMD.equals("CMD_loadRobbyActivity_ExitMember")){
            Log.e("MyService_processCMD","CMD_loadRobbyActivity_ExitMember");

            toMyClass = new Intent(getApplicationContext(), RobbyActivity.class);

            toMyClass.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TOP);
            toMyClass.putExtra("fromClass", "MyService");
            toMyClass.putExtra("cmd", "CMD_Return_loadRobbyActivity_ExitMember02");
            toMyClass.putExtra("robbyList",robbyList);
            toMyClass.putExtra("chatList2",chatList2);

            startActivity(toMyClass);
        } // CMD_loadRobbyActivity_ExitMember

        if(CMD.equals("CMD_loadRoomActivity_ExitMember")){
            Log.e("MyService_processCMD","CMD_loadRoomActivity_ExitMember");

            toMyClass = new Intent(getApplicationContext(), RoomActivity.class);

            toMyClass.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TOP);
            toMyClass.putExtra("fromClass", "MyService");
            toMyClass.putExtra("cmd", "CMD_Return_loadRoomActivity_ExitMember03");
            toMyClass.putExtra("master",master);
            toMyClass.putExtra("gamerList",gamerList);
            toMyClass.putExtra("chatList3",chatList3);
            toMyClass.putExtra("canStart",canStart);

            startActivity(toMyClass);
        } // CMD_loadRoomActivity_ExitMember

        if(CMD.equals("CMD_loadGameActivity_pushPassBtn")){
            Log.e("MyService_processCMD","CMD_loadGameActivity_pushPassBtn");

            toMyClass = new Intent(getApplicationContext(), GameActivity.class);

            toMyClass.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TOP);
            toMyClass.putExtra("fromClass", "MyService");
            toMyClass.putExtra("cmd", "CMD_Return_loadGameActivity_pushPassBtn04");
            toMyClass.putExtra("nowPlayer",nowPlayer);
            toMyClass.putExtra("lastPlayer",lastPlayer);
            toMyClass.putExtra("nowCardNumber",nowCardNumber);
            toMyClass.putExtra("nowCardCount",nowCardCount);

            startActivity(toMyClass);

        } // CMD_loadGameActivity_pushPassBtn

        if(CMD.equals("CMD_pushStartBtn")){
            Log.e("MyService_processCMD","CMD_pushStartBtn");

            sendCMD = "CMD_Client_pushStartBtn";
        } // CMD_pushStartBtn

/*********************************************************************************************************************/
/* 준비버튼 관련 부분이지만, 방만들기와 게임방 접속시 변경할 내용으로 수정해야 함.*/
        if(CMD.equals("CMD_pushReadyBtn")){
            Log.e("MyService_processCMD","CMD_pushReadyBtn");

            sendCMD = "CMD_Client_pushReadyBtn";
        } // CMD_pushReadyBtn

/*********************************************************************************************************************/

        if(CMD.equals("CMD_pushRefreshBtn2")){
            Log.e("MyService_processCMD","CMD_pushRefreshBtn2");

            sendCMD = "CMD_Client_pushRefreshBtn2";
        } // CMD_pushRefreshBtn2

        if(CMD.equals("CMD_ExitMember3")){
            Log.e("MyService_processCMD","CMD_ExitMember3");

            sendCMD = "CMD_Client_ExitMember3";
        }

        if(CMD.equals("CMD_pushPassBtn4")){
            Log.e("MyService_processCMD","CMD_pushPassBtn4");

            sendCMD = "CMD_Client_pushPassBtn4";
        }

        if(CMD.contains("CMD_Client_pushSubmitBtn4")){
            Log.e("MyService_processCMD",CMD);

            sendCMD = CMD;
        }

        if(CMD.contains("CMD_changeActivity")){
            if(CMD.substring(CMD.indexOf("e_")+2).equals("1")){
                Log.e("MyService_processCMD","CMD_changeActivity_1");

                nowState = 1;
            }
            if(CMD.substring(CMD.indexOf("e_")+2).equals("2")){
                Log.e("MyService_processCMD","CMD_changeActivity_2");

                nowState = 2;
            }
            if(CMD.substring(CMD.indexOf("e_")+2).equals("3")){
                Log.e("MyService_processCMD","CMD_changeActivity_3");

                nowState = 3;
            }
            if(CMD.substring(CMD.indexOf("e_")+2).equals("4")){
                Log.e("MyService_processCMD","CMD_changeActivity_4");

                nowState = 4;
            }
        } // CMD_changeActivity
    }

    public void processCMD(String CMD, String CMDvalue1){
        if(CMD.equals("CMD_pushSendChatBtn2")){
            sendCMD = "CMD_Client_pushSendChatBtn2"+":"+CMDvalue1;
        } // CMD_pushSendChatBtn2

        if(CMD.equals("CMD_pushSendChatBtn3")){
            sendCMD = "CMD_Client_pushSendChatBtn3"+":"+CMDvalue1;
        } // CMD_pushSendChatBtn3

        if(CMD.equals("CMD_createGame")){
            sendCMD = "CMD_Client_createGame2"+":"+CMDvalue1;
        } // CMD_createGame

        if(CMD.equals("CMD_joinGame")){
            sendCMD = "CMD_Client_joinGame2"+":"+CMDvalue1;
        } // CMD_createGame
    }

    class ClientSender extends Thread{
        Socket s;
        String name;

        DataOutputStream dout;

        public ClientSender(Socket s, String name) {
            this.s = s;
            this.name = name;

            try{
                dout = new DataOutputStream((s.getOutputStream()));
            }catch(Exception ex){
                ex.printStackTrace();
                Log.e("MyService_error!","dout = new ()");
            }
        }

        public void run() {
            try{
                dout.writeUTF(name);

                while(dout != null)
                {
                    if(!sendCMD.equals("")){
                        Log.e("MyService_sendRun()","Listen. sendCMD not empty.");
                        dout.writeUTF(sendCMD);
                        sendCMD = "";
                        Log.e("MyService_sendRun()","Listen not from sendCMD=\"\". sendCMD empty.");
                    }
                }
            }catch(Exception e) {
                e.printStackTrace();
                Log.e("MyService_error!","sender run()");
            }finally {
                Log.e("MyService_snd","thread exit");
                Log.e("MyService_ClientSender","CMD_thread_socket_close!");
                processCMD("CMD_thread_socket_close");
            }
        }
    }

    class ClientReceiver extends Thread {
        Socket s;

        DataInputStream din;
        ObjectInputStream oin;

        String receiveStr;
        String CMDvalue;

        public ClientReceiver(Socket s) {
            this.s = s;
            try{
                din = new DataInputStream((s.getInputStream()));
            }catch(Exception ex){
                ex.printStackTrace();
                Log.e("MyService_error!","din = new ()");
                Log.e("MyService_CliReceiver","CMD_thread_socket_close!");
                processCMD("CMD_thread_socket_close");
            }
        }

        public void processReceiveCMD(String CMD) {
            try{
                if(CMD.equals("CMD_Server_InitMember2")) {
                    Log.e("MyService_ReceiveCMD","CMD_Server_InitMember2");

                    din = new DataInputStream(s.getInputStream());
                    IPHName = din.readUTF();

                    oin = new ObjectInputStream(s.getInputStream());
                    robbyList = null;
                    robbyList = (ArrayList<String>)oin.readObject();

                    processCMD("CMD_startRobbyActivity");
                } // CMD_Server_InitMember2

                if(CMD.equals("CMD_Server_NewMember2")) {
                    Log.e("MyService_ReceiveCMD","CMD_Server_NewMember2");

                    din = new DataInputStream(s.getInputStream());
                    robbyList.add(din.readUTF());
                    chatList2.add(din.readUTF());

                    processCMD("CMD_loadRobbyActivity_NewMember");
                } // CMD_Server_NewMember2

                if(CMD.equals("CMD_Server_cannotCreateGame")){
                    Log.e("MyService","CMD_Server_cannotCreateGame");
                    oin = new ObjectInputStream(s.getInputStream());
                    robbyList = (ArrayList<String>)oin.readObject();
                    master = false;
                    processCMD("CMD_startRobbyActivity");
                } // CMD_Server_cannotCreateGame

                if(CMD.equals("CMD_Server_cannotJoinGame")){
                    Log.e("MyService","CMD_Server_cannotJoinGame");
                    oin = new ObjectInputStream(s.getInputStream());
                    robbyList = (ArrayList<String>)oin.readObject();
                    master = false;
                    processCMD("CMD_startRobbyActivity");
                } // CMD_Server_cannotCreateGame

                if(CMD.equals("CMD_Server_canCreateGame")){
                    Log.e("MyService","CMD_Server_canCreateGame");
                    din = new DataInputStream(s.getInputStream());
                    roomName = din.readUTF();
                    oin = new ObjectInputStream(s.getInputStream());
                    gamerList = (ArrayList<String>)oin.readObject();
                    master = true;
                    processCMD("CMD_startRoomActivity");
                }

                if(CMD.equals("CMD_Server_canJoinGame")){
                    Log.e("MyService","CMD_Server_canJoinGame");
                    din = new DataInputStream(s.getInputStream());
                    roomName = din.readUTF();
                    oin = new ObjectInputStream(s.getInputStream());
                    gamerList = (ArrayList<String>)oin.readObject();
                    master = false;
                    processCMD("CMD_startRoomActivity");
                } // CMD_Server_canJoinGame

/*********************************************************************************************************************/
/* 준비버튼 관련 부분이지만, 방만들기와 게임방 접속시 변경할 내용으로 수정해야 함.*/

                if(CMD.equals("CMD_Server_pushStartBtn3")){
                    Log.e("MyService_ReceiveCMD","CMD_Server_pushStartBtn3");

                    din = new DataInputStream(s.getInputStream());
                    nowPlayer = din.readUTF();
                    lastPlayer = din.readUTF();
                    nowCardNumber = Integer.parseInt(din.readUTF());
                    nowCardCount = Integer.parseInt(din.readUTF());
                    showhDeck = din.readUTF();
                    oin = new ObjectInputStream(s.getInputStream());
                    hDeck = (HashMap<Integer, Integer>)oin.readObject();

                    processCMD("CMD_startGameActivity");
                } // CMD_Server_pushStartBtn3

                if(CMD.equals("CMD_Server_pushReadyBtn3")){
                    Log.e("MyService_ReceiveCMD","CMD_Server_pushReadyBtn3");

                    din = new DataInputStream(s.getInputStream());
                    CMDvalue = din.readUTF();
                    if(CMDvalue.equals("CMD_Server_canStartBtn3")){
                        canStart = true;
                        CMDvalue = din.readUTF();
                    } else if(CMDvalue.equals("CMD_Server_cannotStartBtn3")){
                        canStart = false;
                        CMDvalue = din.readUTF();
                    }

                    if(gamerList.contains(CMDvalue)) {
                        int indexOfCMDvalue1 = gamerList.indexOf(CMDvalue);
                        gamerList.remove(CMDvalue);
                        gamerList.add(indexOfCMDvalue1, din.readUTF());
                    }

                    processCMD("CMD_loadRoomActivity_pushReadyBtn");
                } // CMD_Server_pushReadyBtn3

/*********************************************************************************************************************/

                if(CMD.equals("CMD_Server_pushRefreshBtn2")){
                    Log.e("MyService_ReceiveCMD","CMD_Server_pushRefreshBtn2");

                    oin = new ObjectInputStream(s.getInputStream());
                    robbyList = null;
                    robbyList = (ArrayList<String>)oin.readObject();

                    processCMD("CMD_loadRobbyActivity_pushRefreshBtn");
                } // CMD_Server_pushRefreshBtn2

                if(CMD.equals("CMD_Server_pushSendChatBtn2")){
                    Log.e("MyService_ReceiveCMD","CMD_Server_pushSendChatBtn2");

                    din = new DataInputStream(s.getInputStream());
                    chatList2.add(din.readUTF());

                    processCMD("CMD_loadRobbyActivity_pushSendChatBtn");
                } // CMD_Server_pushSendChatBtn2

                if(CMD.equals("CMD_Server_pushSendChatBtn3")){
                    Log.e("MyService_ReceiveCMD","CMD_Server_pushSendChatBtn3");

                    din = new DataInputStream(s.getInputStream());
                    chatList3.add(din.readUTF());

                    processCMD("CMD_loadRoomActivity_pushSendChatBtn");
                } // CMD_Server_pushSendChatBtn2

                if(CMD.equals("CMD_Server_JoinMember3")){
                    Log.e("MyService_ReceiveCMD","CMD_Server_JoinMember3");

                    din = new DataInputStream(s.getInputStream());
                    gamerList.add(din.readUTF());
                    chatList3.add(din.readUTF());

                    processCMD("CMD_loadRoomActivity_NewMember");
                } // CMD_Server_JoinMember3

                if(CMD.equals("CMD_Server_ExitMember2")){
                    Log.e("MyService_ReceiveCMD","CMD_Server_ExitMember2");

                    din = new DataInputStream(s.getInputStream());
                    robbyList.remove(din.readUTF());
                    chatList2.add(din.readUTF());

                    processCMD("CMD_loadRobbyActivity_ExitMember");
                } // CMD_Server_ExitMember2

                if(CMD.equals("CMD_Server_ExitMember3")){
                    Log.e("MyService_ReceiveCMD","CMD_Server_ExitMember3");

                    din = new DataInputStream(s.getInputStream());
                    gamerList.remove(din.readUTF());
                    String tmp = din.readUTF();
                    if(tmp.equals("CMD_Server_changeMaster3")) {
                        CMDvalue = din.readUTF();
                        if (gamerList.contains(CMDvalue)) {
                            gamerList.remove(CMDvalue);
                            gamerList.add(din.readUTF());
                        }
                        if(din.readUTF().equals("master")){
                            master = true;
                            if(din.readUTF().equals("canStart")){
                                canStart = true;
                            }else{
                                canStart = false;
                            }
                        }else{
                            master = false;
                        }
                        tmp = din.readUTF();
                    }
                    chatList3.add(tmp);

                    processCMD("CMD_loadRoomActivity_ExitMember");
                } // CMD_Server_ExitMember3

                if(CMD.equals("CMD_Server_pushSubmitBtnMyTurn4")){
                    Log.e("MyService_ReceiveCMD","CMD_Server_pushSubmitBtnMyTurn4");

                    din = new DataInputStream(s.getInputStream());
                    nowPlayer = din.readUTF();
                    lastPlayer = din.readUTF();
                    nowCardNumber = Integer.parseInt(din.readUTF());
                    nowCardCount = Integer.parseInt(din.readUTF());
                    showhDeck = din.readUTF();
                    oin = new ObjectInputStream(s.getInputStream());
                    hDeck = (HashMap<Integer, Integer>)oin.readObject();

                    processCMD("CMD_startGameActivity");
                } // CMD_Server_pushSubmitBtnMyTurn4

                if(CMD.equals("CMD_Server_pushSubmitBtn4")){
                    Log.e("MyService_ReceiveCMD","CMD_Server_pushSubmitBtn4");

                    din = new DataInputStream(s.getInputStream());
                    nowPlayer = din.readUTF();
                    lastPlayer = din.readUTF();
                    nowCardNumber = Integer.parseInt(din.readUTF());
                    nowCardCount = Integer.parseInt(din.readUTF());

                    processCMD("CMD_startGameActivity");
                } // CMD_Server_pushSubmitBtn4

                if(CMD.equals("CMD_Server_pushPassBtn4")){
                    Log.e("MyService_ReceiveCMD","CMD_Server_pushPassBtn4");

                    din = new DataInputStream(s.getInputStream());
                    nowPlayer = din.readUTF();
                    lastPlayer = din.readUTF();
                    nowCardNumber = Integer.parseInt(din.readUTF());
                    nowCardCount = Integer.parseInt(din.readUTF());

                    processCMD("CMD_loadGameActivity_pushPassBtn");
                } // CMD_Server_pushPassBtn4
            }catch(Exception ex){
                ex.printStackTrace();
                Log.e("error!!",CMD);
            }

        }

        public void run() {
            try{
                Log.e("MyService_receiveStr","try-catch start & oin = "+din.getClass());
                while(din != null) {
                    din = new DataInputStream(s.getInputStream());
                    receiveStr = din.readUTF();
                    Log.e("MyService_receiveStr",receiveStr);
                    processReceiveCMD(receiveStr);
                }
            }catch(Exception e) {
                e.printStackTrace();
                Log.e("MyService_error!","receiver run() & before server command : "+receiveStr);
                Log.e("MyService_CliReceiver","CMD_thread_socket_close!");
                processCMD("CMD_thread_socket_close");
            }finally {
                Log.e("MyService_rcv","thread exit");
            }
        }
    }
}
