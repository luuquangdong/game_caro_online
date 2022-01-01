package dong.btl.caroonlinegame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class CreateRoomActivity extends AppCompatActivity {

    private Socket socket;
    private EditText etRoomName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);

        socket = Common.getSocket();
        socket.on("create room response", createRoomHandle);

        etRoomName = findViewById(R.id.etRoomName);
    }

    Emitter.Listener createRoomHandle = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            // chua test
            final int roomID = (int) args[0];
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                        Intent intent = new Intent(CreateRoomActivity.this, BoardGameActivity.class);
                        intent.putExtra("room id", roomID );
                        // là chủ phòng
                        intent.putExtra("owner", true);

                        intent.putExtra("competitor name", "đang đợi đối thủ...");
                        startActivity(intent);
                        finish();
                }
            });
        }
    };

    public void btCreateRoomClicked(View view) {
        socket.emit("create room", etRoomName.getText().toString());
    }
}