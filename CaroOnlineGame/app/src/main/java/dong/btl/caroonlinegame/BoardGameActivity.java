package dong.btl.caroonlinegame;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class BoardGameActivity extends AppCompatActivity {

    private final int MAX_NUM_Row = 11;
    private final int MAX_NUM_Col = 12;

    private Socket socket;

    private boolean isMyTurn;
    private boolean amIOwner;
    private int roomID;
    private boolean ready;
    private boolean isGameRunning;

    private Drawable mySign;
    private Drawable competitorSign;

    // view
    private TextView myName;
    private TextView myTurn;
    private TextView myTime;
    private TextView competitorName;
    private TextView competitorTurn;
    private TextView competitorTime;
    private TextView tvReady;
    private Button btStart;
    private Button btReady;

    private ImageView [][] ivCells;
    private Drawable x, o;

    private CountDownTimer myTimer;
    private CountDownTimer compeTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_game);

        anhXa();
        loadImage();

        ivCells = new ImageView[MAX_NUM_Row][MAX_NUM_Col];

        socket = Common.getSocket();
        socket.on("someone join room", onJoinRoom);
        socket.on("already", onAlready);
        socket.on("response start game", onStartGame);
        socket.on("no danh", onCompetitorGo);
        socket.on("you win", onYouWin);
        socket.on("you lose", onYouLose);
        socket.on("someone left", onSomeoneLeft);
        socket.on("change turn", onChangeTurn);

        createBoardGame();
        thietLapTuIntent();
        anReadyStart();
        anTimeTurn();
        hienReadyStart();
    }

    @Override
    public void onBackPressed() {
        socket.emit("i quit", roomID);
        super.onBackPressed();
    }

    private void anhXa(){
        myName = findViewById(R.id.tvMyName);
        myTurn = findViewById(R.id.tvMyTurn);
        myTime = findViewById(R.id.tvMyTime);
        competitorName = findViewById(R.id.tvCompetitorName);
        competitorTime = findViewById(R.id.tvCompetitorTime);
        competitorTurn = findViewById(R.id.tvCompetitorTurn);
        tvReady = findViewById(R.id.tvReady);
        btStart = findViewById(R.id.btStart);
        btReady = findViewById(R.id.btReady);
    }

    public void loadImage(){
        x = this.getResources().getDrawable(R.drawable.x);
        o = this.getResources().getDrawable(R.drawable.o);
    }

    private Emitter.Listener onChangeTurn = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    anTimeTurn();
                    hienTimeTurn();
                    startCompeCountDown();
                }
            });
        }
    };

    private Emitter.Listener onSomeoneLeft = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(ready) {
                        ready = false;
                        tvReady.setVisibility(View.INVISIBLE);
                    }
                    competitorName.setText("Đang đợi đối thủ...");
                    if(!amIOwner){
                        // khách => chủ phòng
                        amIOwner = true;
                        btReady.setVisibility(View.INVISIBLE);
                        btStart.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    };

    private Emitter.Listener onCompetitorGo = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            final JSONObject jso = (JSONObject) args[0];
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        int x = jso.getInt("x");
                        int y = jso.getInt("y");
                        ivCells[x][y].setImageDrawable(competitorSign);

                        isMyTurn = true;
                        // changes turn
                        compeTimer.cancel();
                        anTimeTurn();
                        hienTimeTurn();
                        startMyCountDown();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener onYouWin = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    thongBao("Chúc mừng!!", "Bạn đã chiến thắng");
                    endGame();
                }
            });
        }
    };

    private Emitter.Listener onYouLose = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    thongBao("Ôi thôi", "Bạn đã thua rồi T_T");
                    endGame();
                }
            });
        }
    };

    private Emitter.Listener onStartGame = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            final boolean myTurn = (boolean) args[0];
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isMyTurn = myTurn;
                    startGame();
                }
            });
        }
    };

    private Emitter.Listener onAlready = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ready = true;
                    tvReady.setVisibility(View.VISIBLE);
                }
            });
        }
    };

    private Emitter.Listener onJoinRoom = new Emitter.Listener() {
        // hiển thị tên khách mới vào
        @Override
        public void call(Object... args) {
            final String guestName = (String) args[0];
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    competitorName.setText(guestName);
                }
            });
        }
    };

    public void thongBao(String title, String message){
        try {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setTitle(title);
            alertBuilder.setMessage(message);
            alertBuilder.show();
        }catch (WindowManager.BadTokenException ex){
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    private void endGame(){
        isMyTurn = false;
        isGameRunning = false;

        if(myTimer != null){
            myTimer.cancel();
        }
        if(compeTimer != null){
            compeTimer.cancel();
        }
        anTimeTurn();

        // hiện ready - start
        hienReadyStart();
    }

    private void thietLapTuIntent(){
        // thiết lập:
        // room id
        // owner room
        // my name
        isMyTurn = false;
        Intent intent = getIntent();
        this.roomID = intent.getIntExtra("room id", -1 );
        this.amIOwner = intent.getBooleanExtra("owner", false);
        String tenDoiThu = intent.getStringExtra("competitor name");

        competitorName.setText(tenDoiThu);
        myName.setText(Common.username);
    }

    private void startGame(){
        isGameRunning = true;
        //ẩn ready-start
        anReadyStart();

        if(isMyTurn) {
            //mình chơi trước là X màu, đối thủ chơi sau sau là O
            mySign = x;
            competitorSign = o;

        }else{//mình chơi sau là O, đối thủ chơi trước là X
            mySign = o;
            competitorSign = x;
        }
        clearBoard();
        //hiện đếm thời gian
        hienTimeTurn();
        // Bắt đầu tính giờ
        if(isMyTurn){
            startMyCountDown();
        }else{
            startCompeCountDown();
        }
        Toast toast = Toast.makeText(this, "Game start", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

    private void clearBoard(){
        for(int i=0; i<MAX_NUM_Row; i++){
            for(int j=0; j<MAX_NUM_Col; j++){
                ivCells[i][j].setImageDrawable(null);
            }
        }
    }

    private void createBoardGame(){
        // tinh kich thuoc cell
//        int sizeCell = Math.round(getWidthScreen()/MAX_NUM_Col);
        int cellSize = caculateCellSize();
        // lay out cua 1 cell va cho 1 hang
        // layout width va height cua 1 View
        LinearLayout.LayoutParams lpCell = new LinearLayout.LayoutParams(cellSize, cellSize);
        LinearLayout.LayoutParams lpRol = new LinearLayout.LayoutParams(cellSize*MAX_NUM_Col, cellSize);

        LinearLayout llBoardGame = findViewById(R.id.llBoardGame);
        for(int i=0; i < MAX_NUM_Row; i++){
            // linear chua 1 hang cell
            LinearLayout llRow = new LinearLayout(this);
            llRow.setLayoutParams(lpRol);
            for(int j=0; j < MAX_NUM_Col; j++){
                ivCells[i][j] = new ImageView(this);
                // set background, layout param cho cell
                ivCells[i][j].setBackgroundResource(R.drawable.bgcell);
                ivCells[i][j].setLayoutParams(lpCell);

                final int x = i;
                final int y = j;
                ivCells[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImageView iv = (ImageView) v;
                        if(isMyTurn && (iv.getDrawable() == null) && isGameRunning ){
                            // ngưng đếm giờ
                            myTimer.cancel();
                            iv.setImageDrawable(mySign);
                            // chuyển lượt
                            isMyTurn = false;
                            JSONObject jso = new JSONObject();
                            try {
                                jso.put("roomID",roomID);
                                jso.put("x", x);
                                jso.put("y", y);
                                socket.emit("go",jso);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                // them 1 cell vao hang
                llRow.addView(ivCells[i][j]);
            }
            // them hang vao board
            llBoardGame.addView(llRow);
        }
    }

    private int caculateCellSize(){
        DisplayMetrics dm = this.getResources().getSystem().getDisplayMetrics();
        int width = (int) dm.widthPixels/MAX_NUM_Col;
        int height = dm.heightPixels*8/12/MAX_NUM_Row;
        return (width < height) ? width : height;
    }

    public void startMyCountDown(){
        myTimer = new CountDownTimer(60*1000, 1000){
            @Override
            public void onTick(long millisUntilFinished) {
                int timeLeft = (int) millisUntilFinished/1000;
                myTime.setText(String.valueOf(timeLeft));
                if(!isMyTurn){
                    this.cancel();
                }
            }

            @Override
            public void onFinish() {
                socket.emit("toi het gio", roomID);
            }
        }.start();
    }

    public void startCompeCountDown(){
        compeTimer = new CountDownTimer(60*1000, 1000){
            @Override
            public void onTick(long millisUntilFinished) {
                int timeLeft = (int) millisUntilFinished/1000;
                competitorTime.setText(String.valueOf(timeLeft));
                if(isMyTurn){
                    this.cancel();
                }
            }
            @Override
            public void onFinish() {
                socket.emit("doi thu het gio", roomID);
            }
        }.start();
    }

    private void anReadyStart(){
        ready = false;
        tvReady.setVisibility(View.INVISIBLE);
        btReady.setVisibility(View.INVISIBLE);
        btStart.setVisibility(View.INVISIBLE);
    }

    private void anTimeTurn(){
        myTime.setVisibility(View.INVISIBLE);
        myTurn.setVisibility(View.INVISIBLE);
        competitorTurn.setVisibility(View.INVISIBLE);
        competitorTime.setVisibility(View.INVISIBLE);
    }

    private void hienReadyStart(){
        if(amIOwner){
            // chủ phòng thì hiện start
            btStart.setVisibility(View.VISIBLE);
        }else{
            // khách thì hiện ready
            btReady.setVisibility(View.VISIBLE);
        }
    }

    private void hienTimeTurn(){
        if(isGameRunning){
            if(isMyTurn){
                myTime.setVisibility(View.VISIBLE);
                myTurn.setVisibility(View.VISIBLE);
            }else{
                competitorTurn.setVisibility(View.VISIBLE);
                competitorTime.setVisibility(View.VISIBLE);
            }
        }
    }

    public void btStartGameClicked(View view) {
        if(ready == false){
            Toast.makeText(this, "người chơi chưa sãn sàng", Toast.LENGTH_SHORT).show();
        }else{
            socket.emit("start game", roomID );
        }
    }

    public void btReadyClicked(View view) {
        // hiển thị sẵn sàng
        // gửi thông báo cho server
        ready = true;
        tvReady.setVisibility(View.VISIBLE);
        socket.emit("i am ready", roomID);
    }
}
