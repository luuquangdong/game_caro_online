package dong.btl.caroonlinegame;

public class RoomInfo {
    private int roomID;
    private String roomName;
    private int playerNum;

    public RoomInfo(int roomID, String roomName, int playerNum) {
        this.roomID = roomID;
        this.roomName = roomName;
        this.playerNum = playerNum;
    }

    public int getRoomID() {
        return roomID;
    }

    public String getRoomName() {
        return roomName;
    }

    public int getPlayerNum() {
        return playerNum;
    }
}
