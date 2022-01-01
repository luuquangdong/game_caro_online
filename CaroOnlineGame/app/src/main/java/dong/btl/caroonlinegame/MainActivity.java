package dong.btl.caroonlinegame;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {

    private EditText etName;
    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        socket = Common.getSocket();

        etName = findViewById(R.id.etName);
        Button btConnect = findViewById(R.id.btConnect);
        btConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(socket.connected()) {
                    String nickName = etName.getText().toString();
                    if(nickName.equalsIgnoreCase("")){
                        Toast.makeText(MainActivity.this,"vui lòng nhập nick name", Toast.LENGTH_SHORT).show();
                    }else {
                        socket.emit("check user name", etName.getText().toString());
                    }
                }else{
                    Toast.makeText(MainActivity.this,"không kết nối được với server", Toast.LENGTH_SHORT).show();
                }
            }
        });
        socket.on("response check user", reponseCheckUser);

        socket.connect();
    }

    private Emitter.Listener reponseCheckUser = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                String result = (String) args[0];
                @Override
                public void run() {
                    if(result.equalsIgnoreCase("ok")){
                        Intent intent = new Intent(MainActivity.this, RoomActivity.class);
                        Common.username = etName.getText().toString();
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(MainActivity.this, "user name đã tồn tại", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    };
}
