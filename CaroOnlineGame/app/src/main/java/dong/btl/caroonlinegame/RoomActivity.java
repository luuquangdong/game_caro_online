package dong.btl.caroonlinegame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class RoomActivity extends AppCompatActivity {

    private RoomAdapter roomAdapter;
    private ListView lvRooms;
    private Socket socket;
    List<RoomInfo> rooms;

    private boolean canJoinRoom = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        // sự kiện của socket
        socket = Common.getSocket();
//        Log.i("ABC", String.valueOf(socket.connected()));
        socket.on("reponse rooms infor", showRoom);
        socket.on("reponse join room", joinRoomHandle);
        //---------
        rooms = new ArrayList<>();
        roomAdapter = new RoomAdapter(this, R.layout.room_item, rooms);
        lvRooms = findViewById(R.id.lvRooms);
        lvRooms.setAdapter(roomAdapter);

        lvRooms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(canJoinRoom){
                    socket.emit("join room",id);
                    canJoinRoom = false;
                }else{
                    Toast.makeText(RoomActivity.this, "đợi tí...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        getRoomsInfor();
    }

    private void getRoomsInfor(){
        socket.emit("get rooms infor");
    }

    private  Emitter.Listener joinRoomHandle = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            final JSONObject jso = (JSONObject) args[0];

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String status = jso.getString("status");
                        if(status.equalsIgnoreCase("OK")){
                            //neu duoc -> vao
                            String competitorName = jso.getString("competitorName");
                            int roomID = jso.getInt("roomID");

                            Intent intent = new Intent(RoomActivity.this, BoardGameActivity.class);
                            intent.putExtra("competitor name",competitorName);
                            intent.putExtra("room id", roomID);
                            //không phải chủ phòng
                            intent.putExtra("owner", false);

                            startActivity(intent);

                        }else if(status.equalsIgnoreCase("FULL")){
                            // phòng đã đầy
                            Toast.makeText(RoomActivity.this, "phòng không đã đầy, vui lòng chọn phòng khác", Toast.LENGTH_SHORT).show();
                        }else {
                            // phòng không tồn tại
                            Toast.makeText(RoomActivity.this, "phòng không tồn tại", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    canJoinRoom = true;
                }
            });
        }
    };

    private Emitter.Listener showRoom = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //xóa dữ liệu cũ
                    //thêm dữ liệu mới
                    //thông báo cho list view
                    rooms.clear();
                    rooms.addAll( handleJSArr((JSONArray) args[0]));
                    roomAdapter.notifyDataSetChanged();
                }
            });

        }
    };

    private List<RoomInfo> handleJSArr(JSONArray arr){
        List<RoomInfo> result = new ArrayList<>();
        try {
//            JSONArray jsa = new JSONArray(arr);
            JSONArray jsa = arr;
            for(int i=0; i<jsa.length(); i++){
                JSONObject jso = jsa.getJSONObject(i);
                // lấy giá trị json ra
                int id = jso.getInt("id");
                String name = jso.getString("name");
                int num = jso.getInt("playerNum");

                RoomInfo room = new RoomInfo(id, name, num);

                result.add(room);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        Log.i("ABC", String.valueOf(result.size()));
        return result;
    }

    public void btCreateRoomClicked(View view) {
        Intent intent = new Intent(this, CreateRoomActivity.class);
        startActivity(intent);
    }

    public void btUpdateClicked(View view) {
        getRoomsInfor();
    }
}
