package dong.btl.caroonlinegame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class RoomAdapter extends BaseAdapter {

    private List<RoomInfo> roomsInfor;
    private Context context;
    private int layout;

    public RoomAdapter(Context context, int layout,List<RoomInfo> roomsInfor) {
        this.roomsInfor = roomsInfor;
        this.context = context;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return roomsInfor.size();
    }

    @Override
    public Object getItem(int position) {
        return roomsInfor.get(position);
    }

    @Override
    public long getItemId(int position) {
        return roomsInfor.get(position).getRoomID();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // memory của view
        ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();
            // hỗ trợ chuyển layout.xml -> View java
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // thổi layout vào cho view
            convertView = inflater.inflate(this.layout, parent, false);
            // lấy các text view trong convertView ra
            holder.tvDescription = convertView.findViewById(R.id.tvDescription);
            holder.tvPlayerNum = convertView.findViewById(R.id.tvNum);
            // thực hiện ghi nhớ
            convertView.setTag(holder);
        }else{ //nếu view khởi tạo rồi thì lấy lại ký ức ra
            holder = (ViewHolder) convertView.getTag();
        }

        RoomInfo roomInfo = roomsInfor.get(position);

        // gán giá trị cho các view
        String des = "Phòng " + roomInfo.getRoomID() + ": " + roomInfo.getRoomName();
        String num = "Số người chơi: " + roomInfo.getPlayerNum();
        holder.tvDescription.setText(des);
        holder.tvPlayerNum.setText(num);

        return convertView;
    }

    private class ViewHolder{
        TextView tvDescription;
        TextView tvPlayerNum;
    }
}
