package ljh.android.test.puzzlegame;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.PrintWriter;


public class PuzzleGameActivity extends AppCompatActivity {
    private Button startBtn, resetBtn, pauseBtn, gradeBtn;
    private ImageView imgView[] = new ImageView[9];
    Bitmap imgPiece[] = new Bitmap[9];
    int img[] = new int[9];
    Bitmap mainImg;
    int[] mainList = {R.drawable.muzi_9, R.drawable.muzi_28,R.drawable.muzi_35,R.drawable.muzi_39,R.drawable.muzi_41,R.drawable.muzi_55};
    ImageView oriImg;
    private int MAIN_WIDTH;
    private int MAIN_HEIGHT;
    private int PIECE_WIDTH;
    private int PIECE_HEIGHT;
    boolean isPaused = false;
    private int timeCount = 0;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle_game);
        imgView[0] = (ImageView) findViewById(R.id.img01);
        imgView[1] = (ImageView) findViewById(R.id.img02);
        imgView[2] = (ImageView) findViewById(R.id.img03);
        imgView[3] = (ImageView) findViewById(R.id.img04);
        imgView[4] = (ImageView) findViewById(R.id.img05);
        imgView[5] = (ImageView) findViewById(R.id.img06);
        imgView[6] = (ImageView) findViewById(R.id.img07);
        imgView[7] = (ImageView) findViewById(R.id.img08);
        imgView[8] = (ImageView) findViewById(R.id.img09);
        startBtn = (Button) findViewById(R.id.startBtn);
        resetBtn = (Button) findViewById(R.id.resetBtn);
        pauseBtn = (Button) findViewById(R.id.pause);
        gradeBtn = (Button) findViewById(R.id.gradeBtn);
        oriImg = (ImageView) findViewById(R.id.oriImg);

        // reset버튼 클릭시
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int timeCount = 0;
                suffleImg();
                for (int i = 0; i < imgView.length; i++) {
                    imgView[i].setOnClickListener(click);
                }
            }
        });

        // pause버튼 클릭시
        pauseBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (isPaused) {     //isPaused가 true면
                    checkTime();      // checkTime메소드 실행
                    pauseBtn.setText("Pause");  //pause버튼의 텍스트를 Pause로 변경
                    isPaused = false;   //isPaused 를 false로 변경
                } else {        // isPaused가 false면
                    handler.removeMessages(0);  // handler의 메세지를 지운다(타이머를 멈춤)
                    pauseBtn.setText("ReStart"); // pause의 텍스트를 ReStart로 변경
                    isPaused = true;  //isPaused를 true로 변경
                }
            }
        }); //  버튼을 일시정지 <-> 다시시작으로 변경시키는 온클릭 리스너

        // start버튼 클릭시
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                suffleImg(); // suffleImg 메소드 실행
                for (int i = 0; i < imgView.length; i++) {
                    imgView[i].setOnClickListener(click); // 이미지를 클릭할때 click 온클릭리스너가 실행
                }
            }
        });

        // grade버튼 클릭시
        gradeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkClear()) {         //chechClear()가 true면
                    int data = timeCount;   // timeCount를 data에 넣는다
                    Intent intent = new Intent(getApplicationContext(),Ranking.class); //intent를 새로운 Ranking클래스로 intent한다
                    intent.putExtra("rank",data); //data를 rank라는 이름에 담아서 intent한다.
                    startActivity(intent); //intent를 실행한다.
                } else {        //checkClear()가 false면
                    Toast.makeText(getApplicationContext(),"퍼즐을 먼저 맞춰주세요",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void main(View view) {
        finish();
    }

    // 메인사진을 9등분해서 imgView에 담는다.
    private void pieceInit() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                imgPiece[i * 3 + j] = Bitmap.createBitmap(mainImg, j * PIECE_WIDTH, i * PIECE_HEIGHT, PIECE_WIDTH, PIECE_HEIGHT);
            }
        }
        for (int i = 0; i < 9; i++) {
            imgView[i].setImageBitmap(imgPiece[i]);
            img[i] = i;
        }
    }

    // 메인사진 리스트에서 랜덤으로 한장을 가져온다.
    private Bitmap getMainImg() {
        int random_index = (int) (Math.random() * mainList.length);
        return BitmapFactory.decodeResource(getApplicationContext().getResources(), mainList[random_index]);
    }

    // true 일떄 onCreate, false 일때 onPause
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        LinearLayout layout = (LinearLayout) findViewById(R.id.parent_layout);
        mainImg = getMainImg();
        mainImg = Bitmap.createScaledBitmap(mainImg, layout.getWidth(), layout.getHeight(), true);
        oriImg.setImageBitmap(mainImg);
        MAIN_WIDTH = mainImg.getWidth();
        MAIN_HEIGHT = mainImg.getHeight();
        PIECE_WIDTH = MAIN_WIDTH / 3;
        PIECE_HEIGHT = MAIN_HEIGHT / 3;
        pieceInit();
    }

    public View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = -1;
            for (int i = 0; i < imgView.length; i++) {
                if (imgView[i] == v) {
                    index = i;
                    break;
                }
            }
            slide_img(index); // 이미지이동 (index값을 넣고  그 값에 해당하는 이동이 이루어짐)
            checkClear();  // 다 맞춰졌는지 확인
        }
    };

    private void suffleImg() {
        pieceInit();
        int int_tmp;
        imgPiece[8] = null;

        for (int i = 0; i < 300; i++) {     // 이미지 9조각을 랜덤으로 섞는다.
            int target = (int) (Math.random() * 9);
            int_tmp = img[0];
            img[0] = img[target];
            img[target] = int_tmp;
        }
        for (int i = 0; i < imgView.length; i++) {      // 이미지가 null인 이미지의 배경색을 흰색으로
            imgView[i].setImageBitmap(imgPiece[img[i]]);
            if (imgPiece[img[i]] == null) {
                imgView[i].setBackgroundColor(Color.argb(255, 255, 255, 255));
            }
        }
        timeCount = 0;  // 카운트 값을 0으로
        if (handler == null) {      //
            handler = new Handler();
            checkTime(); // 카운트 시작
        }
    }

    private boolean checkClear() {      // 퍼즐을 다 맞췄는지 확인
        for (int i = 0; i < img.length; i++) {
            if (img[i] != i) return false;  // 실행되면 return값을 false로
        }
        return true;
    }

    private boolean slide_img(int touched_index) {
        boolean availableSlide = false;
        int target_index = -1;

        if (touched_index - 3 >= 0) { // 위로 갈수있는지
            if (img[touched_index - 3] == 8) {
                availableSlide = true;
                target_index = touched_index - 3;
            }
        }
        if (touched_index + 3 <= 8) { // 아래로 갈수있는지
            if (img[touched_index + 3] == 8) {
                availableSlide = true;
                target_index = touched_index + 3;
            }
        }
        if (touched_index - 1 >= touched_index / 3 * 3) { //왼쪽으로 갈수있는지
            if (img[touched_index - 1] == 8) {
                availableSlide = true;
                target_index = touched_index - 1;
            }
        }
        if (touched_index + 1 <= touched_index / 3 * 3 + 2) { //오른쪽으로 갈수있는지
            if (img[touched_index + 1] == 8) {
                availableSlide = true;
                target_index = touched_index + 1;
            }
        }
        if (availableSlide && target_index != -1) {
            int tmp = img[touched_index];
            img[touched_index] = img[target_index];
            img[target_index] = tmp;

            imgView[touched_index].setImageBitmap(imgPiece[img[touched_index]]);
            imgView[touched_index].setBackgroundColor(Color.argb(255, 255, 255, 255)); // 불투명하게
            imgView[target_index].setImageBitmap(imgPiece[img[target_index]]);
            imgView[target_index].setBackgroundColor(Color.argb(0, 255, 255, 255)); // 투명하게
        }
        return availableSlide;
    }
    // 버튼 GameStart를 타이머 형식으로 변경해서 카운트
    private void checkTime() {
        handler.postDelayed(new Runnable() {
            public void run() {
                timeCount++;
                checkTime();
                if (timeCount >= 0) {
                    startBtn.setText(timeCount + " 초");
                }
                if (checkClear()) {
                    handler.removeMessages(0);
                    startBtn.setText(timeCount + "초 클리어");
                }
            }
        }, 1000);
    }
}
