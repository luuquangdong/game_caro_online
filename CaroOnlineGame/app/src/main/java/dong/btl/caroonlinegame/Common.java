package dong.btl.caroonlinegame;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class Common {

    private static Socket socket;
    public static String username;
    private Common(){}

    public static Socket getSocket(){
        if(socket == null){
            try {
                socket = IO.socket("http://192.168.1.115:8787/");
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return socket;
    }

}
