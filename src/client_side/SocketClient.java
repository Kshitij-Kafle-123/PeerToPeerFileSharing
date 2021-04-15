package client_side;

import data.Download;
import data.Message;
import data.Upload;

import javax.swing.*;

import compression.DeCompress;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;

public class SocketClient implements Runnable {

    public int port;
    public String serverAddr;
    public Socket socket;
    public ChatFrame ui;
    public ObjectInputStream In;
    public ObjectOutputStream Out;

    public SocketClient(ChatFrame frame) throws IOException {
        ui = frame;
        this.serverAddr = ui.serverAddress;
        this.port = ui.port;
        socket = new Socket(InetAddress.getByName(serverAddr), port);

        Out = new ObjectOutputStream(socket.getOutputStream());
        Out.flush();
        In = new ObjectInputStream(socket.getInputStream());

    }

    @Override
    public void run() {
        boolean keepRunning = true;
        while (keepRunning) {
            try {
                Message msg = (Message) In.readObject();
                System.out.println("Incoming : " + msg.toString());
                 if (msg.type.equals("newuser")) {
                    if (!msg.content.equals(ui.username)) {
                        boolean exists = false;
                        for (int i = 0; i < ui.defaultListModel.getSize(); i++) {
                            if (ui.defaultListModel.getElementAt(i).equals(msg.content)) {
                                exists = true;
                                break;
                            }
                        }
                        if (!exists) {
                            ui.defaultListModel.addElement(msg.content);
                        }
                    }
                } else if (msg.type.equals("signout")) {
                    if (msg.content.equals(ui.username)) {

                        ui.btnConnect.setEnabled(true);

                        ui.textField.setEditable(true);
                        ui.textField_1.setEditable(true);
                        ui.textField_2.setEditable(true);
                        for (int i = 1; i < ui.defaultListModel.size(); i++) {
                            ui.defaultListModel.removeElementAt(i);
                        }
                        ui.clientThread.stop();
                    } else {
                        ui.defaultListModel.removeElement(msg.content);

                    }
                } else if (msg.type.equals("upload_req")) {

                    if (JOptionPane.showConfirmDialog(ui, ("Accept '"
                            + msg.content + "' from " + msg.sender + " ?")) == 0) {

                        JFileChooser jf = new JFileChooser();
                        File file= new File(msg.content);
                        
                        DeCompress decompress=new DeCompress(file.getPath());
                        decompress.decodeFile();
                        jf.setSelectedFile(new File(msg.content));
                        int returnVal = jf.showSaveDialog(ui);

                        String saveTo = jf.getSelectedFile().getPath();
                        if (saveTo != null
                                && returnVal == JFileChooser.APPROVE_OPTION) {
                            Download dwn = new Download(saveTo, ui);
                            Thread t = new Thread(dwn);
                            t.start();

                            send(new Message("upload_res", ui.username,
                                    ("" + dwn.port), msg.sender, msg.key));
                        } else {
                            send(new Message("upload_res", ui.username, "NO",
                                    msg.sender, msg.key));
                        }
                    } else {
                        send(new Message("upload_res", ui.username, "NO",
                                msg.sender, msg.key));
                    }
                } else if (msg.type.equals("upload_res")) {
                    if (!msg.content.equals("NO")) {
                        int port = Integer.parseInt(msg.content);
                        String addr = msg.sender;
                        Upload upl = new Upload(addr, port, ui.file, ui);
                        Thread t = new Thread(upl);
                        t.start();
                    }

                }

            } catch (Exception ex) {
                keepRunning = true;

                ui.btnConnect.setEnabled(true);
                ui.textField.setEditable(true);
                ui.textField_1.setEditable(true);
                ui.textField_2.setEditable(true);
                for (int i = 1; i < ui.defaultListModel.size(); i++) {
                    ui.defaultListModel.removeElementAt(i);
                }
                ui.clientThread.stop();
                System.out.println("Exception SocketClient run()");
                ex.printStackTrace();
            }
        }
    }
    public void send(Message msg) {
        try {
            Out.writeObject(msg);
            Out.flush();
            System.out.println("Outgoing : " + msg.toString());

            if (msg.type.equals("message") && !msg.content.equals(".bye")) {
                String msgTime = (new Date()).toString();

            }
        } catch (IOException ex) {
            System.out.println("Exception SocketClient send()");
        }
    }

    public void closeThread(Thread t) {
        t = null;
    }
}
