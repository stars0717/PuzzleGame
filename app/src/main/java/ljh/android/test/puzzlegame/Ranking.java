package ljh.android.test.puzzlegame;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by soldesk on 2016-09-06.
 */
public class Ranking extends AppCompatActivity{
    private int rank;
    private ArrayList<Integer> AL = new ArrayList();
    TextView rankResult1,rankResult2,rankResult3,rankResult4,rankResult5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ranking);
        rankResult1 = (TextView) findViewById(R.id.rankResult1);
        Intent intent = getIntent();    //PuzzleGameActivity에서 보낸 intent를 받는다.
        rank = intent.getIntExtra("rank",0);  // intent.putExtra("rank",data)를 받아오는데
                                                // getIntExtra("rank",999)여기에서 "rank"는 동일하게 0은 초기값을 의미한다.
                                                // 이렇게 intent로 받은 값을 rank에 담는다.
        rankResult1.setText("당신의 기록은 :"+rank+" 초 입니다.");  // rankResult1 textView에 ()안의 내용값을 출력한다.

    }
    // 종료 onClick
    public void exit(View v) {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
    }
    // 게음으로 돌아가기 onClick
    public void game(View view) {
        finish();
    }
}
