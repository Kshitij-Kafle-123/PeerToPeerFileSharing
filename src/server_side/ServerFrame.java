package server_side;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.swing.*;


import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

public class ServerFrame extends JFrame {

    public SocketServer server;
    private JButton jButton1;
    private JScrollPane jScrollPane1;
    public JTextArea jTextArea1;
    public JButton jButton2;
    public ServerFrame() {
        initComponents();
    }

    public boolean isWin32() {
        return System.getProperty("os.name").startsWith("Windows");
    }

    private void initComponents() {

        jButton1 = new JButton();
        jButton2 = new JButton();
        jButton2.setEnabled(false);
        jScrollPane1 = new JScrollPane();
        jTextArea1 = new JTextArea();
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Server Side");

        jButton1.setText("Start Server");
        jButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTextArea1 = new javax.swing.JTextArea();

        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Consolas", 0, 12));
        jTextArea1.setRows(25);

        jButton2.setText("Stop Server");
        jButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                server.stop();
                jTextArea1.append("\nServer stopped !!! ");
                jButton2.setEnabled(false);
                jButton1.setEnabled(true);
            }
        });
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
                getContentPane());
        layout.setHorizontalGroup(
                layout.createParallelGroup(Alignment.TRAILING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap(46, Short.MAX_VALUE)
                                .addComponent(jTextArea1, GroupLayout.PREFERRED_SIZE, 537, GroupLayout.PREFERRED_SIZE)
                                .addGap(18)
                                .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
                        .addGroup(Alignment.LEADING, layout.createSequentialGroup()
                                .addGap(187)
                                .addComponent(jButton1)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(jButton2, GroupLayout.PREFERRED_SIZE, 91, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(238, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(17)
                                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(jButton1)
                                        .addComponent(jButton2))
                                .addGap(18)
                                .addGroup(layout.createParallelGroup(Alignment.TRAILING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE)
                                                .addContainerGap())
                                        .addComponent(jTextArea1, GroupLayout.PREFERRED_SIZE, 285, GroupLayout.PREFERRED_SIZE)))
        );
        getContentPane().setLayout(layout);

        pack();
    }

    private void jButton1ActionPerformed(ActionEvent evt) {
        server = new SocketServer(this);
        jButton1.setEnabled(false);
        jButton2.setEnabled(true);
    }

    public void RetryStart(int port) {
        if (server != null) {
            server.stop();
        }
        server = new SocketServer(this, port);
    }

    public static void main(String args[]) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.out.println("Look & Feel Exception");
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {

               ServerFrame serverFrame =  new ServerFrame();
               serverFrame.setVisible(true);
               serverFrame.setResizable(false);
            }
        });
    }
}

