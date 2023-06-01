import org.json.JSONArray;
import org.json.JSONObject;
import java.awt.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


public class NetworkUDPProtocol implements Runnable {
    public static final int PORT_1 = 7001;
    public static final int PORT_2 = 7002;
    public static final String HOST = "localhost";


    private final int remotePort;


    private DatagramSocket socket;


    byte[] receivingDataBuffer = new byte[1024];
    byte[] sendingDataBuffer = new byte[1024];


    private final NetworkEventListener listener;


    public NetworkUDPProtocol(boolean isServer, NetworkEventListener listener) {
        this.listener = listener;
        int localPort;
        if (isServer) {
            localPort = PORT_1;
            remotePort = PORT_2;
        } else {
            localPort = PORT_2;
            remotePort = PORT_1;
        }
        try {
            socket = new DatagramSocket(localPort);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }


    private void sendData(String data) {
        try {
            sendingDataBuffer = data.getBytes();
            var packet = new DatagramPacket(sendingDataBuffer, sendingDataBuffer.length, InetAddress.getByName(HOST), remotePort);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private final DatagramPacket inputPacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);


    @Override
    public void run() {
        handleEvents();
    }


    public void closeConnection() {
        var jsonObject = new JSONObject();
        jsonObject.put("command", NetworkCommands.CLOSE_CONNECTION);
        var res = jsonObject.toString();
        sendData(res);
        if (socket != null) {
            socket.close();
        }
    }


    public void clearObjects() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("command", NetworkCommands.CLEAR_OBJECTS);
        var res = jsonObject.toString();
        sendData(res);
    }


    public void sendObjectByIndex(int index, GraphicalObject object) {
        var jsonObject = new JSONObject();
        jsonObject.put("command", NetworkCommands.RESPONSE_OBJ_BY_INDEX);
        jsonObject.put("object", object.writeToJson());
        var type = object.getClass().getSimpleName();
        jsonObject.put("obj_type", type);
        jsonObject.put("index", index);
        var res = jsonObject.toString();
        sendData(res);
    }


    public void sendObjectsList(GraphicalObject[] objects) {
        var jsonObject = new JSONObject();
        jsonObject.put("command", NetworkCommands.RESPONSE_OBJ_LIST);
        var jsonArray = new JSONArray();
        for (int i = 0; i < objects.length; i++) {
            var object = objects[i];
            var obj = new JSONObject();
            obj.put("index", i);
            obj.put("obj_type", object.getClass().getSimpleName());
            obj.put("object", object.writeToJson());
            jsonArray.put(obj);
        }
        jsonObject.put("objects", jsonArray);
        var res = jsonObject.toString();
        sendData(res);
    }


    public void sendObjectsListSize(int size) {
        var jsonObject = new JSONObject();
        jsonObject.put("command", NetworkCommands.RESPONSE_OBJ_LIST_SIZE);
        jsonObject.put("obj_list_size", size);
        var res = jsonObject.toString();
        sendData(res);
    }


    public void requestObjectByIndex(int index) {
        var jsonObject = new JSONObject();
        jsonObject.put("command", NetworkCommands.REQUEST_OBJ_BY_INDEX);
        jsonObject.put("index", index);
        var res = jsonObject.toString();
        sendData(res);
    }


    public void requestObjectsList() {
        var jsonObject = new JSONObject();
        jsonObject.put("command", NetworkCommands.REQUEST_OBJ_LIST);
        var res = jsonObject.toString();
        sendData(res);
    }


    public void requestObjectsListSize() {
        var jsonObject = new JSONObject();
        jsonObject.put("command", NetworkCommands.REQUEST_OBJ_LIST_SIZE);
        var res = jsonObject.toString();
        sendData(res);
    }


    public void handleEvents() {
        eventLoop:
        while (true) {
            try {
                socket.receive(inputPacket);
                var line = new String(inputPacket.getData(), 0, inputPacket.getLength());
                var jsonObject = new JSONObject(line);
                var command = jsonObject.getString("command");
                switch (command) {
                    case NetworkCommands.CLOSE_CONNECTION -> {
                        listener.onEvent(new NetworkEvent.CloseConnection());
                        closeConnection();
                        break eventLoop;
                    }
                    case NetworkCommands.CLEAR_OBJECTS -> listener.onEvent(new NetworkEvent.ClearObjects());
                    case NetworkCommands.REQUEST_OBJ_BY_INDEX -> {
                        var index = jsonObject.getInt("index");
                        listener.onEvent(new NetworkEvent.RequestObjectByIndex(index));
                    }
                    case NetworkCommands.REQUEST_OBJ_LIST -> listener.onEvent(new NetworkEvent.RequestObjectList());
                    case NetworkCommands.REQUEST_OBJ_LIST_SIZE -> listener.onEvent(new NetworkEvent.RequestObjectListSize());
                    case NetworkCommands.RESPONSE_OBJ_BY_INDEX ->
                            listener.onEvent(new NetworkEvent.ResponseObjectByIndex(jsonObject.getInt("index"), jsonObject.getString("obj_type"), jsonObject.getString("object")));
                    case NetworkCommands.RESPONSE_OBJ_LIST -> {
                        var jsonArray = jsonObject.getJSONArray("objects");
                        var objects = new GraphicalObject[jsonArray.length()];
                        for (int i = 0; i < jsonArray.length(); i++) {
                            var obj = jsonArray.getJSONObject(i);
                            var object = switch (obj.getString("obj_type")) {
                                case "RightClick" -> new RightClick(
                                        100, 100, 100, 100, Color.RED, "http://github.com/k1yuchnikov/rcsp-labs/blob/master/src/main/resources/assets/RightClick.png"
                                );
                                case "LeftClick" -> new LeftClick(100, 100, 100, 100, Color.RED);
                                default ->
                                        throw new IllegalStateException("Unexpected value: " + obj.getString("obj_type"));
                            };
                            object.readFromJson(obj.getString("object"));
                            objects[i] = object;
                        }
                        listener.onEvent(new NetworkEvent.ResponseObjectList(objects));
                    }
                    case NetworkCommands.RESPONSE_OBJ_LIST_SIZE ->
                            listener.onEvent(new NetworkEvent.ResponseObjectListSize(jsonObject.getInt("obj_list_size")));
                    case NetworkCommands.RESPONSE_OBJ ->
                            listener.onEvent(new NetworkEvent.ResponseObject(jsonObject.getString("object"), jsonObject.getString("obj_type")));
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}


