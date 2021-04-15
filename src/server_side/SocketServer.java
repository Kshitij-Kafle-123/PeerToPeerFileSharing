package server_side;

import data.Message;

import java.io.*;
import java.net.*;

class ServerThread extends Thread {

    public SocketServer server = null;
    public Socket socket = null;
    public int ID = -1;
    public String username = "";
    public ObjectInputStream streamIn = null;
    public ObjectOutputStream streamOut = null;
    public ServerFrame ui;

    public ServerThread(SocketServer _server, Socket _socket) {
        super();
        server = _server;
        socket = _socket;
        ID = socket.getPort();
        ui = _server.ui;
    }

    public void send(Message msg) {
        try {
            streamOut.writeObject(msg);
            streamOut.flush();
        } catch (IOException ex) {
            System.out.println("Exception [SocketClient : send(...)]");
        }
    }

    public int getID() {
        return ID;
    }

    public void run() {
        ui.jTextArea1.append("\nServer Thread " + ID + " running.");
        while (true) {
            try {
                Message msg = (Message) streamIn.readObject();
                server.handle(ID, msg);
            } catch (Exception ioe) {
                System.out.println(ID + " ERROR reading: " + ioe.getMessage());
//                server.remove(ID);
               // stop();
                
            }
        }
    }

    public void open() throws IOException {
        streamOut = new ObjectOutputStream(socket.getOutputStream());
        streamOut.flush();
        streamIn = new ObjectInputStream(socket.getInputStream());
    }

    public void close() throws IOException {
        if (socket != null)
            socket.close();
        if (streamIn != null)
            streamIn.close();
        if (streamOut != null)
            streamOut.close();
    }
}

public class SocketServer implements Runnable {

    public ServerThread clients[];
    public ServerSocket server = null;
    public Thread thread = null;
    public int clientCount = 0, port = 13000;
    public ServerFrame ui;

    public SocketServer(ServerFrame frame) {

        clients = new ServerThread[50]; // Max Clients
        ui = frame;

        try {
            server = new ServerSocket(port);
            port = server.getLocalPort();
            ui.jTextArea1.append("Server started. IP : "
                    + InetAddress.getLocalHost() + ", Port : "
                    + server.getLocalPort());
            start();
        } catch (IOException ioe) {
            ui.jTextArea1.append("Can not bind to port : " + port
                    + "\nRetrying");
            ui.RetryStart(0);
        }
    }

    public SocketServer(ServerFrame frame, int Port) {

        clients = new ServerThread[500];
        ui = frame;
        port = Port;

        try {
            server = new ServerSocket(port);
            port = server.getLocalPort();
            ui.jTextArea1.append("\nServer started. IP : "
                    + InetAddress.getLocalHost() + ", Port : "
                    + server.getLocalPort());
            start();
        } catch (IOException ioe) {
            ui.jTextArea1.append("\nCan not bind to port " + port + ": "
                    + ioe.getMessage());
        }
    }

    public void run() {
        while (thread != null) {
            try {
                ui.jTextArea1.append("\nWaiting for a Peers ...");
                addThread(server.accept());
            } catch (Exception ioe) {
                ui.jTextArea1.append("\nServer accept error: \n");
                ui.RetryStart(0);
            }
        }
    }

    public void start() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    @SuppressWarnings("deprecation")
    public void stop() {
        if (thread != null) {
            thread.stop();
            thread = null;
        }
    }

    private int findClient(int ID) {
        for (int i = 0; i < clientCount; i++) {
            if (clients[i].getID() == ID) {
                return i;
            }
        }
        return -1;
    }

    public synchronized void handle(int ID, Message msg) {
        if (msg.content.equals(".bye")) {
            Announce("signout", "SERVER", msg.sender);
            remove(ID);
        } else {
            if (msg.type.equals("login")) {
                if (findUserThread(msg.sender) == null) {
                    clients[findClient(ID)].username = msg.sender;
//                    clients[findClient(ID)].send(new Message("login",
//                            "SERVER", "TRUE", msg.sender, msg.key));
                    Announce("newuser", "SERVER", msg.sender);
                    SendUserList(msg.sender);
                } else {
                    clients[findClient(ID)].send(new Message("login", "SERVER",
                            "FALSE", msg.sender, msg.key));
                }
            } else if (msg.type.equals("message")) {
                if (msg.recipient.equals("All")) {
                    Announce("message", msg.sender, msg.content);
                } else {
                    findUserThread(msg.recipient).send(
                            new Message(msg.type, msg.sender, msg.content,
                                    msg.recipient, msg.key));
                    clients[findClient(ID)].send(new Message(msg.type,
                            msg.sender, msg.content, msg.recipient, msg.key));
                }
            } else if (msg.type.equals("upload_req")) {
                if (msg.recipient.equals("All")) {
                    clients[findClient(ID)].send(new Message("message",
                            "SERVER", "Uploading to 'All' forbidden",
                            msg.sender, msg.key));
                } else {
                    findUserThread(msg.recipient).send(
                            new Message("upload_req", msg.sender, msg.content,
                                    msg.recipient, msg.key));
                }
            } else if (msg.type.equals("upload_res")) {
                if (!msg.content.equals("NO")) {
                    String IP = findUserThread(msg.sender).socket
                            .getInetAddress().getHostAddress();
                    findUserThread(msg.recipient).send(
                            new Message("upload_res", IP, msg.content,
                                    msg.recipient, msg.key));
                } else {
                    findUserThread(msg.recipient).send(
                            new Message("upload_res", msg.sender, msg.content,
                                    msg.recipient, msg.key));
                }
            }
        }
    }


    public void Announce(String type, String sender, String content) {
        Message msg = new Message(type, sender, content, "All", "");
        for (int i = 0; i < clientCount; i++) {
            clients[i].send(msg);
        }
    }

    public void SendUserList(String toWhom) {
        for (int i = 0; i < clientCount; i++) {
            findUserThread(toWhom).send(
                    new Message("newuser", "SERVER", clients[i].username,
                            toWhom, ""));
        }
    }

    public ServerThread findUserThread(String usr) {
        for (int i = 0; i < clientCount; i++) {
            if (clients[i].username.equals(usr)) {
                return clients[i];
            }
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    public synchronized void remove(int ID) {
        int pos = findClient(ID);
        if (pos >= 0) {
            ServerThread toTerminate = clients[pos];
            ui.jTextArea1.append("\nRemoving Peer thread " + ID + " at "
                    + pos);
            if (pos < clientCount - 1) {
                for (int i = pos + 1; i < clientCount; i++) {
                    clients[i - 1] = clients[i];
                }
            }
            clientCount--;
            try {
                toTerminate.close();
            } catch (IOException ioe) {
                ui.jTextArea1.append("\nError closing thread: " + ioe);
            }
            toTerminate.stop();
        }
    }

    private void addThread(Socket socket) {
        if (clientCount < clients.length) {
            ui.jTextArea1.append("\nPeer accepted: " + socket);
            clients[clientCount] = new ServerThread(this, socket);
            try {
                clients[clientCount].open();
                clients[clientCount].start();
                clientCount++;
                System.out.println(clientCount);
            } catch (IOException ioe) {
                ui.jTextArea1.append("\nError opening thread: " + ioe);
            }
        } else {
            ui.jTextArea1.append("\nPeer refused: maximum " + clients.length
                    + " reached.");
        }
    }
}

