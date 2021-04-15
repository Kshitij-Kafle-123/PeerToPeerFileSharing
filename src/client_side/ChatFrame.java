package client_side;


import data.Message;
import server_side.SocketServer;

import javax.swing.*;

import compression.Compress;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import file.FileIO;

public class ChatFrame extends JFrame {
    public JOptionPane optionPane;
    private JFrame frame;
    public JTextField textField;
    public JTextField textField_1;
    public JTextField textField_2;
    public JButton btnConnect;
    public DefaultListModel<String> defaultListModel;
    public File file;
    public JList jList1;
    public SocketClient client;
    public int port;
    public String serverAddress;
    public String username;
    public Thread clientThread;
    public JFileChooser fileChooser;
    public JButton sendButton;
    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ChatFrame window = new ChatFrame();
                    window.frame.setResizable(false);
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public ChatFrame() {
        initialize();
        defaultListModel.addElement("All");
        jList1.setSelectedIndex(0);
        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        }); {

        }
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 770, 562);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JPanel panel = new JPanel();
        panel.setForeground(Color.BLUE);
        panel.setBounds(0, 0, 754, 660);
        frame.getContentPane().add(panel);
        panel.setLayout(null);

        JLabel labelUserName = new JLabel("Enter User Name");
        labelUserName.setFont(new Font("Tahoma", Font.PLAIN, 14));
        labelUserName.setBounds(40, 11, 107, 36);
        labelUserName.setBackground(Color.GREEN);
        panel.add(labelUserName);

        JLabel labelIp = new JLabel("IP Address");
        labelIp.setFont(new Font("Tahoma", Font.PLAIN, 14));
        labelIp.setBackground(Color.GREEN);
        labelIp.setBounds(40, 52, 84, 36);
        panel.add(labelIp);

        JLabel labelPort = new JLabel("Port Number");
        labelPort.setFont(new Font("Tahoma", Font.PLAIN, 14));
        labelPort.setBackground(Color.GREEN);
        labelPort.setBounds(40, 91, 107, 36);
        panel.add(labelPort);

        textField = new JTextField();
        textField.setBounds(157, 21, 311, 26);
        panel.add(textField);
        textField.setColumns(10);

        textField_1 = new JTextField();
        textField_1.setColumns(10);
        textField_1.setBounds(157, 62, 311, 26);
        panel.add(textField_1);

        textField_2 = new JTextField("13000");
        textField_2.setColumns(10);
        textField_2.setBounds(157, 101, 311, 26);
        panel.add(textField_2);

        btnConnect = new JButton("Connect");
        btnConnect.setForeground(Color.BLUE);
        btnConnect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                connectActionPerformed(e);
            }
        });

        btnConnect.setFont(new Font("Tahoma", Font.PLAIN, 18));
        btnConnect.setBounds(489, 101, 140, 26);
        panel.add(btnConnect);


        /*
         * JScrollPane chatScrollPane = new JScrollPane(); chatScrollPane.setBounds(10,
         * 149, 347, 314); panel.add(chatScrollPane);
         */
        JScrollPane chatScrollPaneRight = new JScrollPane();
        chatScrollPaneRight.setBounds(385, 149, 359, 303);
        panel.add(chatScrollPaneRight);

        jList1 = new JList();
        jList1.setModel((defaultListModel = new DefaultListModel<String>()));
        chatScrollPaneRight.setViewportView(jList1);

        fileChooser = new JFileChooser();
        fileChooser.setBounds(10, 149, 347, 314);
        panel.add(fileChooser);
        fileChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
					selectFile(e);
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }
        });
        optionPane = new JOptionPane();

        sendButton = new JButton("Send File");
        sendButton.setBackground(Color.WHITE);
        sendButton.setFont(new Font("Tahoma", Font.PLAIN, 13));
        sendButton.setBounds(241, 463, 107, 23);
        panel.add(sendButton);
        sendButton.setEnabled(false);
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendFile(e);
            }
        });
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                handleClosing();
            }
        });
    }




    private void connectActionPerformed(ActionEvent e) {
        serverAddress = textField_1.getText();
        port = Integer.parseInt(textField_2.getText());
        username = textField.getText();
        if (!serverAddress.isEmpty() && !textField_2.getText().isEmpty() && !username.isEmpty()) {
            try {
                client = new SocketClient(this);
                clientThread = new Thread(client);
                clientThread.start();
                client.send(new Message("login", username, "", "", ""));
                textField.setEnabled(false);
                textField_1.setEnabled(false);
                
                textField_2.setEnabled(false);
                btnConnect.setEnabled(false);
                JOptionPane.showMessageDialog(null, "Connection successful");
                System.out.println("Connected");
            } catch (Exception ex) {
                System.out.println(ex);
                JOptionPane.showMessageDialog(null, "Connection error");
            }

        }
    }
    private void selectFile(ActionEvent e) throws FileNotFoundException {
        file = fileChooser.getSelectedFile();
       String filePath=file.getPath();
    
        String data = null;
        try {
           
        	Compress compress=new Compress(filePath);
        	compress.encodeFile();
        	String desiredFilePath=compress.getOutputFileName();
        	//System.out.println(desiredFilePath);
            sendButton.setEnabled(true);
             FileIO fileIo=new FileIO(desiredFilePath);
             
            File myObj = new File(desiredFilePath);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                data = myReader.nextLine();
            }
            System.out.println(data);
            myReader.close();
        } catch (Exception ex) {
            System.out.println("An error occurred.");
            ex.printStackTrace();
        }
      //  String compression = HuffmanSolution.encode(data);
//        String s = HuffmanSolution.decode(compression);
   //     System.out.println(compression);

    }
    private void sendFile(ActionEvent e) {

        long size = file.length();
        if (size < 120 * 1024 * 1024) {
            client.send(new Message("upload_req", username, file.getName(),
                    jList1.getSelectedValue().toString(), ""));
        }
    }

    private void handleClosing() {
        String ObjButtons[] = {"Yes","No"};
        int PromptResult = JOptionPane.showOptionDialog(null,"Are you sure you want to exit?"," ",JOptionPane.DEFAULT_OPTION,JOptionPane.WARNING_MESSAGE,null,ObjButtons,ObjButtons[1]);
        if(PromptResult==JOptionPane.YES_OPTION)
        {
            if (client!=null) {
                try {
                    client.send(new Message("message", username, ".bye",
                            "SERVER", ""));
                    clientThread.stop();
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            }
            System.exit(0);
        }
    }
}
