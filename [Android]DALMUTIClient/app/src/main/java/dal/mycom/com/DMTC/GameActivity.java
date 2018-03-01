package dal.mycom.com.DMTC;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class GameActivity extends AppCompatActivity {

    TextView tv1_nowPlayer, tv2_nowPlayer, tv1_nowCard, tv2_nowCard, tv1_myCard, tv2_myCard, tv_jokerCount;
    CheckBox cb_joker;
    LinearLayout llh_jokerCount;
    EditText et_jokerCount, et_cardNum, et_cardCount;
    Button bt_submit, bt_pass;
    ScrollView sv_finish;
    ListView lv_finish;

    int cardNumber, cardCount, jokerCount;
    String IPHName, nowPlayer, lastPlayer;
    int nowCardNumber, nowCardCount;
    String showhDeck;
    HashMap<Integer, Integer> hDeck;
    ArrayList<String> finishList;
    ArrayAdapter finishAdapter;

    Intent toMyService;
    Intent toMyClass;
    Intent fromMyClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Log.e("GameActivity","onCreate");

        tv1_nowPlayer = (TextView)findViewById(R.id.tv1_nowPlayer);
        tv2_nowPlayer = (TextView)findViewById(R.id.tv2_nowPlayer);
        tv1_nowCard = (TextView)findViewById(R.id.tv1_nowCard);
        tv2_nowCard = (TextView)findViewById(R.id.tv2_nowCard);
        tv1_myCard = (TextView)findViewById(R.id.tv1_myCard);
        tv2_myCard = (TextView)findViewById(R.id.tv2_myCard);
        cb_joker = (CheckBox)findViewById(R.id.cb_joker);
        llh_jokerCount = (LinearLayout)findViewById(R.id.llh_jokerCount);
        tv_jokerCount = (TextView)findViewById(R.id.tv_jokerCount);
        et_jokerCount = (EditText) findViewById(R.id.et_jokerCount);
        et_cardNum = (EditText)findViewById(R.id.et_cardNum);
        et_cardCount = (EditText)findViewById(R.id.et_cardCount);
        bt_submit = (Button)findViewById(R.id.bt_submit);
        bt_pass = (Button)findViewById(R.id.bt_pass);
        sv_finish = (ScrollView)findViewById(R.id.sv_finish);
        lv_finish = (ListView)findViewById(R.id.lv_finish);

        llh_jokerCount.setVisibility(View.INVISIBLE);

        Intent intent = getIntent();

        cb_joker.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(cb_joker.isChecked()){
                    llh_jokerCount.setVisibility(View.VISIBLE);
                }else{
                    llh_jokerCount.setVisibility(View.INVISIBLE);
                }
            }
        });

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!nowPlayer.equals(IPHName)){
                    Toast.makeText(getApplicationContext(),"Not your turn.",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(et_cardNum.getText() == null || et_cardNum.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(),"Empty card number.",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(et_cardCount.getText() == null || et_cardCount.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(),"Empty card count.",Toast.LENGTH_SHORT).show();
                    return;
                }
                cardNumber = Integer.parseInt(et_cardNum.getText().toString());
                cardCount = Integer.parseInt(et_cardCount.getText().toString());
                if(cardNumber < 0 || cardNumber > 13) {
                    Toast.makeText(getApplicationContext(),"Invalid card number.",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(cardCount < 0 || cardCount > cardNumber) {
                    Toast.makeText(getApplicationContext(),"Invalid card count.",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!hDeck.containsKey(Integer.valueOf(cardNumber))){
                    Toast.makeText(getApplicationContext(),"you have not this card!!!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(hDeck.get(Integer.valueOf(cardNumber)).intValue() < cardCount){
                    Toast.makeText(getApplicationContext(),"you have not this card enough!!!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(cb_joker.isChecked()){
                    if(et_jokerCount.getText() == null || et_jokerCount.getText().toString().equals("")){
                        Toast.makeText(getApplicationContext(),"Empty joker count.",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    jokerCount = Integer.parseInt(et_jokerCount.getText().toString());
                    if(jokerCount <= 0 || jokerCount > 2){
                        Toast.makeText(getApplicationContext(),"Invalid joker count.",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(cardCount >= 13){
                        Toast.makeText(getApplicationContext(),"you cannot use Joker for normal card!!!",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(hDeck.get(Integer.valueOf(13)).intValue() < jokerCount) {
                        Toast.makeText(getApplicationContext(),"you have not Joker enough!!!",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(nowCardCount !=0 || nowCardNumber != 0){
                        if(cardNumber >= nowCardNumber || (cardCount+jokerCount) != nowCardCount){
                            Toast.makeText(getApplicationContext(),"you can not submit this card!!!",Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    // 조커를 사용했을 때, intent 발생.

                    toMyService = new Intent(getApplicationContext(), MyService.class);

                    toMyService.putExtra("fromClass","GameActivity");
                    toMyService.putExtra("cmd","CMD_pushSubmitBtn40");
                    toMyService.putExtra("jokerCount",Integer.toString(jokerCount));
                    toMyService.putExtra("cardNumber",Integer.toString(cardNumber));
                    toMyService.putExtra("cardCount",Integer.toString(cardCount));

                    startService(toMyService);
                }else{
                    if(nowCardCount !=0 || nowCardNumber != 0){
                        if(cardNumber >= nowCardNumber || cardCount != nowCardCount){
                            Toast.makeText(getApplicationContext(),"you can not submit this card!!!",Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    // 조커를 사용하지 않았을 때, intent 발생.

                    toMyService = new Intent(getApplicationContext(), MyService.class);

                    toMyService.putExtra("fromClass","GameActivity");
                    toMyService.putExtra("cmd","CMD_pushSubmitBtn40");
                    toMyService.putExtra("cardNumber",Integer.toString(cardNumber));
                    toMyService.putExtra("cardCount",Integer.toString(cardCount));

                    startService(toMyService);
                }
                if(cb_joker.isChecked()){
                    et_jokerCount.setText("");
                    cb_joker.setChecked(false);
                }
                et_cardNum.setText("");
                et_cardCount.setText("");
            }
        });

        bt_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!nowPlayer.equals(IPHName)){
                    Toast.makeText(getApplicationContext(),"Not your turn.",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(nowCardNumber == 0 || nowCardCount == 0){
                    Toast.makeText(getApplicationContext(),"you are first turn.",Toast.LENGTH_SHORT).show();
                    return;
                }
                // 패스일 때, intent 발생.
                toMyService = new Intent(getApplicationContext(), MyService.class);

                toMyService.putExtra("fromClass","GameActivity");
                toMyService.putExtra("cmd","CMD_pushPassBtn40");

                startService(toMyService);
            }
        });

        processCMD(getIntent());
    }

    void processCMD(Intent intent){
        if(intent.getStringExtra("fromClass").equals("MyService")){
            if(intent.getStringExtra("cmd").equals("CMD_startGameActivity04")){
                IPHName = intent.getStringExtra("IPHName");
                nowPlayer = intent.getStringExtra("nowPlayer");
                lastPlayer = intent.getStringExtra("lastPlayer");
                nowCardNumber = intent.getIntExtra("nowCardNumber", 0);
                nowCardCount = intent.getIntExtra("nowCardCount", 0);
                showhDeck = intent.getStringExtra("showhDeck");
                hDeck = (HashMap<Integer, Integer>)intent.getSerializableExtra("hDeck");
                finishList = (ArrayList<String>)intent.getSerializableExtra("finishList");
                if(nowCardNumber == 0 || nowCardCount == 0){
                    lastPlayer = "";
                }

                tv2_nowPlayer.setText(nowPlayer);
                tv2_nowCard.setText(Integer.toString(nowCardNumber)+"("+Integer.toString(nowCardCount)+") - "+lastPlayer);
                tv2_myCard.setText(showhDeck);
                if(finishList.size() != 0){
                    finishAdapter = new ArrayAdapter(getApplicationContext(), R.layout.listview_custom, finishList);
                    lv_finish.setAdapter(finishAdapter);
                }
            } // cmd.equals("CMD_startGameActivity04")

            if(intent.getStringExtra("cmd").equals("CMD_Return_loadGameActivity_pushPassBtn04")){
                nowPlayer = intent.getStringExtra("nowPlayer");
                nowCardNumber = intent.getIntExtra("nowCardNumber", 0);
                nowCardCount = intent.getIntExtra("nowCardCount", 0);
                lastPlayer = intent.getStringExtra("lastPlayer");
                if(nowCardNumber == 0 || nowCardCount == 0){
                    lastPlayer = "";
                }

                tv2_nowPlayer.setText(nowPlayer);
                tv2_nowCard.setText(Integer.toString(nowCardNumber)+"("+Integer.toString(nowCardCount)+") - "+lastPlayer);
            } // cmd.equals("CMD_Return_loadGameActivity_pushPassBtn04")
        } // fromClass.equals("MyService")
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("GameActivity","onStart");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e("GameActivity","onNewIntent");

        processCMD(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("GameActivity","onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("GameActivity","onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("GameActivity","onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("GameActivity","onPause");
    }
}
