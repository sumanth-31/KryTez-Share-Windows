package krytez;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.plaf.ButtonUI;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.NoRouteToHostException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class KrytezShare extends JFrame implements ActionListener {
    JButton senditb, receiveitb;
    GridBagConstraints gbc;
    JLabel layout1;
    String fname1, fname;

    public class Server1 {

        ServerSocket socket;
        InetAddress inet;
        long data = 0;
        int ind = 0;
        GridBagLayout layout;
        GridBagConstraints gbc;
        JLabel uid, pass, panel;
        Socket servs;
        File[] files1;
        ArrayList<File> files;
        Dimension d;
        JFileChooser jf;
        LookAndFeel def;
        long startt, endt, totalt, maintrans = 0;
        float rate;
        double time, datad;
        boolean startflag = true;
        boolean errflag = false;
        JProgressBar cpb;

        Server1() {
            remove(layout1);
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            files = new ArrayList<>();
            String path = System.getProperty("user.dir");
            path += "\\backtry6.jpg";
            ImageIcon iimg = new ImageIcon(path);
            try {
                def = UIManager.getLookAndFeel();
            } catch (Exception e) {

            }
            /*
             * Image img = iimg.getImage(); setBounds(0, 0, d.height, d.width); Image
             * finalimg = img.getScaledInstance(d.height, d.width, Image.SCALE_SMOOTH); iimg
             * = new ImageIcon(finalimg);
             */
            panel = new JLabel(iimg, JLabel.CENTER);
            try {
                inet = InetAddress.getLocalHost();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(KrytezShare.this, "Error occurred while collecting ip address!", "ERROR!",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                add(layout1);
                return;
            }
            String ipadd;
            ipadd = inet.getHostAddress();
            if (ipadd.equals("127.0.0.1")) {
                JOptionPane.showMessageDialog(KrytezShare.this, "Connect to the network and try again !", "WARNING!",
                        JOptionPane.WARNING_MESSAGE);
                add(layout1);
                return;
            }
            uid = new JLabel("User Id: " + ipadd);
            pass = new JLabel("Password: ");
            layout = new GridBagLayout();
            gbc = new GridBagConstraints();
            panel.setLayout(layout);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.ipady = 20;
            gbc.ipadx = 10;
            gbc.gridwidth = 1;
            panel.add(uid, gbc);
            gbc.gridx = 0;
            gbc.gridy = 1;
            panel.add(pass, gbc);
            uid.setFont(font);
            uid.setForeground(Color.DARK_GRAY);
            pass.setForeground(Color.DARK_GRAY);
            pass.setFont(font);
            add(panel);
            setVisible(true);
            d = getSize();
            files = new ArrayList<>();
            if (select() == 1) {
                if (startServer() == 1) {
                } else
                    return;
            } else
                return;

        }

        Font font = new Font("courier", Font.PLAIN, 24);

        int generatePort() {
            Random random = new Random();
            int portno = random.nextInt(64511) + 1024;
            return portno;

        }

        void setUI(Component comp) {
            comp.setBackground(Color.BLACK);
            comp.setForeground(Color.DARK_GRAY);
            comp.setFont(font);
        }

        // Server connected
        int portno;

        int startServer() {
            try {
                portno = generatePort();
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    cpb = new JProgressBar();
                    cpb.setIndeterminate(true);
                    cpb.setStringPainted(true);
                    cpb.setString("Waiting for receiver");
                    cpb.setVisible(true);
                } catch (Exception e) {

                }
                gbc.gridx = 0;
                gbc.gridy = 2;
                panel.add(cpb, gbc);
                revalidate();
                repaint();
                SwingWorker sw = new SwingWorker<String, String>() {
                    protected String doInBackground() {
                        try {
                            socket = new ServerSocket(portno);
                            SwingUtilities.invokeAndWait(new Runnable() {
                                public void run() {
                                    pass.setText("Password: " + portno);
                                    JOptionPane.showMessageDialog(getParent(), "Server Started!",
                                            "Server Status Report", JOptionPane.INFORMATION_MESSAGE);
                                }

                            });
                            servs = socket.accept();
                            send();

                        } catch (IOException e2) {
                            JOptionPane.showMessageDialog(getParent(), "Error occured.Retrying!", "ERROR!",
                                    JOptionPane.ERROR_MESSAGE);
                            if (startServer() == 1) {
                                errflag = false;
                                return "noerr";
                            } else {
                                errflag = true;
                                return "err";
                            }
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(getParent(), "Error occured while connecting to port",
                                    "ERROR!", JOptionPane.ERROR_MESSAGE);
                            errflag = true;
                            return "err";
                        }

                        return "noerr";
                    }

                    public void done() {
                        try {
                            UIManager.setLookAndFeel(def);
                        } catch (Exception e) {

                        }
                        /*
                         * remove(panel); add(layout1); revalidate(); repaint();
                         */
                    }

                };
                sw.execute();
            } catch (NumberFormatException e1) {
                JOptionPane.showMessageDialog(KrytezShare.this, "Enter a number", "ERROR!", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e3) {
                JOptionPane.showMessageDialog(KrytezShare.this, "Error occured while starting server", "ERROR!",
                        JOptionPane.ERROR_MESSAGE);
            }
            if (errflag) {
                return 0;

            } else
                return 1;
        }

        int select() {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {

            }
            int j = ind;
            JFileChooser chooser = new JFileChooser();
            chooser.setMultiSelectionEnabled(true);
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            chooser.showOpenDialog(null);
            files1 = chooser.getSelectedFiles();
            for (File fl : files1) {
                files.add(fl);
                ind++;
            }
            try {
                UIManager.setLookAndFeel(def);
            } catch (Exception e) {

            }
            if (ind > 0) {
                for (; j < ind; j++) {
                    File f = files.get(j);
                    if (f.isDirectory()) {
                        calc(f);
                    } else
                        data += (long) (f.length());
                }
                KrytezShare.this.revalidate();
                KrytezShare.this.repaint();
                if (JOptionPane.showOptionDialog(getParent(),
                        "Do you want to select more files or do you want to send?", "Confirmation!",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                        new Object[] { "Select More files", "Send" }, null) == JOptionPane.YES_OPTION) {
                    select();
                    return 1;
                } else {
                    return 1;
                }
            } else {
                JOptionPane.showMessageDialog(getParent(), "You need to select at least one file to send.", "ERROR!",
                        JOptionPane.ERROR_MESSAGE);
                remove(panel);
                add(layout1);
                revalidate();
                repaint();
                return 0;
            }
        }

        public void calc(File df) {
            for (File dfa : df.listFiles()) {
                if (dfa.isDirectory()) {
                    calc(dfa);
                } else
                    this.data += dfa.length();
            }
        }

        JProgressBar pb;

        void send() {
            try {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                    pb = new JProgressBar(0, (int) ((double) data / 1000000));
                    pb.setStringPainted(true);
                    pb.setString("Transferring files...");
                    pb.setVisible(true);
                } catch (Exception e) {

                }
                panel.remove(cpb);
                gbc.gridx = 0;
                gbc.gridy = 2;
                gbc.gridwidth = 5;
                gbc.ipadx = 40;
                panel.add(pb, gbc);
                revalidate();
                repaint();

                SwingWorker sw = new SwingWorker<String, String>() {
                    protected String doInBackground() {
                        try {
                            datad = data / 1000000.0;
                            data /= 1000000;
                            int trans = 0;
                            String fullname = "file";
                            BufferedOutputStream bo = new BufferedOutputStream(servs.getOutputStream());
                            PrintWriter pw = new PrintWriter(bo, true);
                            BufferedReader br = new BufferedReader(new InputStreamReader(servs.getInputStream()));
                            for (File f : files) {
                                if (f.isDirectory()) {
                                    dirTrav(f);
                                    continue;
                                }
                                fullname = f.getName();
                                pw.println(f.length() + " " + fullname);
                                br.readLine();

                                if (startflag) {
                                    pw.println(datad);
                                    startflag = false;
                                    startt = System.nanoTime();
                                }
                                bo.flush();
                                FileInputStream fin = new FileInputStream(f);
                                byte[] buff = new byte[32768];
                                int length;
                                // publish(fullname, String.valueOf(trans));
                                br.readLine();
                                while ((length = fin.read(buff)) != -1) {
                                    bo.write(buff, 0, length);
                                    bo.flush();
                                    trans += length;
                                    if (trans >= 1048576) {
                                        maintrans += 1;
                                        trans = 0;
                                        publish(fullname, String.valueOf(maintrans));
                                    }
                                }
                                bo.flush();
                                br.readLine();
                                fin.close();
                            }
                            if (files.size() > 0)
                                publish("Finished", String.valueOf(data));
                            pw.println("Finished");
                            bo.flush();
                            bo.close();
                            servs.close();
                            socket.close();
                            endt = System.nanoTime();
                            totalt = endt - startt;
                            time = totalt / 1000000000.0;
                            rate = (float) (datad / time);
                            time = time * 10;
                            time = Math.floor(time) / 10;
                            if (time >= 1)
                                JOptionPane.showMessageDialog(getParent(),
                                        "Transfer successful!\nAverage transfer speed: " + rate + "MBps",
                                        "INFORMATION!", JOptionPane.INFORMATION_MESSAGE);
                            else
                                JOptionPane.showMessageDialog(getParent(), "Transfer successful!", "INFORMATION!",
                                        JOptionPane.INFORMATION_MESSAGE);
                            startflag = true;
                            try {
                                UIManager.setLookAndFeel(def);
                            } catch (Exception e) {

                            }

                        } catch (SocketException e) {
                            JOptionPane.showMessageDialog(getParent(),
                                    "Error occured while transferring.\nCheck for connection or storage permission or connection.",
                                    "ERROR!", JOptionPane.ERROR_MESSAGE);
                            e.printStackTrace();

                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(getParent(), "Error occured!Contact KryTez team for support.",
                                    "ERROR!", JOptionPane.ERROR_MESSAGE);
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    public void process(List<String> f) {
                        pb.setString("Transferring file: " + f.get(0) + " " + f.get(1) + "MB/" + data);
                        pb.setValue(Integer.parseInt(f.get(1)));
                    }

                    protected void done() {
                        try {
                            servs.close();
                            socket.close();
                        } catch (Exception e) {

                        }
                        panel.remove(pb);
                        setSize(d);
                        files.clear();
                        data = 0;
                        ind = 0;
                        pb.setString("Transferring files...");
                        remove(panel);
                        add(layout1);
                        revalidate();
                        repaint();

                    }

                };
                sw.execute();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(KrytezShare.this,
                        "Error occured!Contact KryTez team for support.\nConatct info at KryTezTechnologies.netlify.com",
                        "ERROR!", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }

        }

        public void dirTrav(File df) {

            try {
                DataOutputStream bo = new DataOutputStream(servs.getOutputStream());
                PrintWriter pw = new PrintWriter(bo, true);
                BufferedReader br = new BufferedReader(new InputStreamReader(servs.getInputStream()));
                pw.println("Dir");
                pw.flush();
                br.readLine();
                fname1 = df.getName();
                pw.println(fname1);
                pw.flush();
                br.readLine();
                for (File doc : df.listFiles()) {
                    if (doc.isDirectory()) {
                        dirTrav(doc);
                    } else {

                        FileInputStream is = new FileInputStream(doc);
                        fname1 = doc.getName();
                        pw.println(doc.length() + " " + fname1);
                        br.readLine();
                        if (startflag) {
                            pw.println(datad);
                            startflag = false;
                            startt = System.nanoTime();
                        }
                        int len;
                        long trans = 0;
                        byte buff[] = new byte[32768];
                        br.readLine();
                        while ((len = is.read(buff)) != -1) {
                            bo.write(buff, 0, len);
                            bo.flush();
                            trans += len;
                            if (trans >= 1048576) {
                                maintrans += 1;
                                SwingUtilities.invokeAndWait(new Runnable() {
                                    public void run() {

                                        pb.setString(
                                                "Transferring file: " + fname1 + " " + maintrans + "MB/" + data + "MB");
                                        pb.setValue((int) maintrans);
                                    }
                                });

                                trans = 0;
                            }
                        }
                        bo.flush();
                        br.readLine();
                        is.close();
                    }
                }
                pw.println("Dirend");
                pw.flush();
                br.readLine();
                bo.flush();
                return;

            } catch (Exception e) {
                e.printStackTrace();
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            JOptionPane.showMessageDialog(KrytezShare.this, "Error Occured!", "ERROR!",
                                    JOptionPane.ERROR_MESSAGE);
                        }

                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        }
    }

    class Client1 implements ActionListener {
        JTextField ip, port;
        String totaldata;
        float rate;
        int data;
        JButton conn, back;
        JLabel panel;
        JProgressBar pb;
        GridBagConstraints gbc;
        Font font;
        Dimension dim;
        long trans;
        Socket socket;

        Client1() {
            remove(layout1);
            InetAddress inet;
            try {
                inet = InetAddress.getLocalHost();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(KrytezShare.this, "Error occurred while collecting ip address!", "ERROR!",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                add(layout1);
                return;
            }
            String ipadd;
            ipadd = inet.getHostAddress();
            if (ipadd.equals("127.0.0.1")) {
                JOptionPane.showMessageDialog(KrytezShare.this, "Connect to the network and restart app!", "WARNING!",
                        JOptionPane.WARNING_MESSAGE);
                add(layout1);
                return;
            }

            String path = System.getProperty("user.dir");
            path += "\\backtry6.jpg";
            ImageIcon iimg = new ImageIcon(path);
            panel = new JLabel(iimg, JLabel.CENTER);
            ip = new JTextField("Enter username of sender");
            port = new JTextField("Enter password of sender");
            conn = new JButton("Connect");
            conn.addActionListener(this);
            pb = new JProgressBar();
            pb.setIndeterminate(true);
            pb.setStringPainted(true);
            back = new JButton("Back");
            pb.setString("Waiting for files...");
            font = new Font("courier", Font.PLAIN, 18);
            ip.setFont(font);
            port.setFont(font);
            conn.setFont(font);
            conn.setForeground(Color.DARK_GRAY);
            panel.setLayout(new GridBagLayout());
            gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.ipadx = 40;
            gbc.ipady = 30;
            gbc.insets = new Insets(10, 0, 0, 0);
            gbc.gridwidth = 4;
            panel.add(ip, gbc);
            gbc.gridx = 0;
            gbc.gridy = 2;
            panel.add(port, gbc);
            gbc.gridx = 0;
            gbc.gridy = 4;
            gbc.insets = new Insets(10, 0, 0, 0);
            panel.add(conn, gbc);
            gbc.gridy = 5;
            panel.add(back, gbc);
            back.setContentAreaFilled(false);
            back.setOpaque(false);
            back.setFont(font);
            back.addActionListener(this);
            back.setForeground(Color.DARK_GRAY);
            conn.setContentAreaFilled(false);
            conn.setOpaque(false);
            add(panel);
            setVisible(true);
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            dim = getSize();
        }

        SwingWorker sw;
        String ipadd, file;
        int portno;
        boolean finflag = false;
        boolean start = true;
        long starttime, endtime, totaltime;
        BufferedInputStream is;
        FileOutputStream fos;
        long maintrans = 0;
        double datad;

        void reset() {
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.ipadx = 40;
            gbc.ipady = 30;
            gbc.insets = new Insets(10, 0, 0, 0);
            gbc.gridwidth = 4;
            panel.add(ip, gbc);
            gbc.gridx = 0;
            gbc.gridy = 2;
            panel.add(port, gbc);
            gbc.gridx = 0;
            gbc.gridy = 4;
            gbc.insets = new Insets(10, 0, 0, 0);
            panel.add(conn, gbc);
            gbc.gridy = 5;
            panel.add(back, gbc);
            remove(panel);
            add(layout1);
            revalidate();
            repaint();
        }

        public void initUI() {

            reset();
            revalidate();
            repaint();
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == conn) {
                try {

                    ipadd = ip.getText();
                    portno = Integer.parseInt(port.getText());
                } catch (NumberFormatException e1) {

                    JOptionPane.showMessageDialog(getParent(), "Enter a proper User Id and password!", "ERROR!",
                            JOptionPane.ERROR_MESSAGE);
                    initUI();
                }
                panel.remove(ip);
                panel.remove(port);
                panel.remove(conn);
                panel.remove(back);
                gbc.gridy = 0;
                gbc.gridwidth = 8;
                gbc.ipadx = 120;
                panel.add(pb, gbc);
                gbc.gridy = 1;
                panel.add(back, gbc);
                revalidate();
                repaint();
                sw = new SwingWorker<String, String>() {

                    @Override
                    protected String doInBackground() throws Exception {
                        BufferedReader br;
                        BufferedOutputStream bo;
                        PrintWriter pw;
                        trans = 0;
                        maintrans = 0;
                        try {
                            socket = new Socket(ipadd, portno);
                            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            bo = new BufferedOutputStream(socket.getOutputStream());
                            pw = new PrintWriter(bo, true);
                            while (true) {
                                file = br.readLine();
                                String path = System.getProperty("user.dir");
                                File dir = new File(path + "\\Received Files");
                                if (dir.exists())
                                    ;
                                else if (dir.mkdir())
                                    ;
                                else {
                                    JOptionPane.showMessageDialog(KrytezShare.this,
                                            "Can't create folder to save files.Try running in administrator mode",
                                            "ERROR!", JOptionPane.ERROR_MESSAGE);
                                    reset();
                                    socket.close();
                                    return "err";
                                }
                                if (file.equals("Finished")) {
                                    br.close();
                                    socket.close();
                                    finflag = true;
                                    break;
                                } else if (file.equals("Dir")) {

                                    pw.println("done");
                                    file = br.readLine();
                                    pw.println("Done");
                                    pw.flush();
                                    dirTrav(path + "\\Received Files", file);
                                    continue;
                                }

                                String filedata[] = file.split(" ");
                                long filelen = Long.parseLong(filedata[0]);
                                file = file.substring(filedata[0].length());
                                long transferred = 0;
                                File f = new File(dir + "\\" + file);
                                byte[] buff = new byte[32768];
                                int len;
                                int i = 0;
                                pw.println("done");
                                pw.flush();
                                while (f.exists()) {
                                    f = new File(dir + "\\" + "(" + i + ")" + file);
                                    i++;
                                }
                                pw.println("done");
                                pw.flush();
                                if (start) {
                                    totaldata = br.readLine();
                                    datad = Double.parseDouble(totaldata);
                                    data = (int) datad;
                                    starttime = System.nanoTime();
                                    SwingUtilities.invokeAndWait(new Runnable() {
                                        public void run() {
                                            pb.setIndeterminate(false);
                                            pb.setMaximum((int) data);
                                            pb.setMinimum(0);
                                        }
                                    });
                                    start = false;
                                }
                                is = new BufferedInputStream(socket.getInputStream());
                                fos = new FileOutputStream(f);
                                publish(file);
                                if (filelen == 0) {
                                    fos.flush();
                                    fos.close();
                                    pw.println("received");
                                    pw.flush();
                                    continue;

                                }
                                while ((len = is.read(buff)) != -1) {

                                    fos.write(buff, 0, len);
                                    trans += len;
                                    if (trans >= 1048576) {
                                        maintrans += 1;
                                        trans = 0;
                                        publish(file);
                                    }
                                    transferred += len;
                                    if (transferred == filelen)
                                        break;
                                }
                                fos.flush();
                                fos.close();
                                pw.println("received");
                                pw.flush();
                            }
                        } catch (UnknownHostException e) {
                            JOptionPane.showMessageDialog(getParent(), "Enter a proper User Id and password!", "ERROR!",
                                    JOptionPane.ERROR_MESSAGE);
                            initUI();

                        } catch (NoRouteToHostException e) {
                            JOptionPane.showMessageDialog(getParent(), "Enter a proper User Id and password!", "ERROR!",
                                    JOptionPane.ERROR_MESSAGE);
                            initUI();

                        } catch (Exception e) {
                            e.printStackTrace();
                            JOptionPane.showMessageDialog(getParent(), "Error occured!Try checking connection!",
                                    "ERROR!", JOptionPane.ERROR_MESSAGE);
                            initUI();

                        }
                        endtime = System.nanoTime();
                        start = true;
                        totaltime = endtime - starttime;
                        return null;
                    }

                    public void process(List<String> f) {

                        pb.setString("Transferring: " + f.get(0) + " " + maintrans + "MB/" + (int) data + "MB");
                        pb.setValue((int) (maintrans));
                    }

                    public void done() {
                        initUI();
                        if (finflag) {

                            finflag = false;
                            double time = totaltime / 1000000000.0;
                            time = time * 10;
                            time = Math.floor(time) / 10;
                            rate = (float) (datad / time);
                            if (totaltime >= 1)
                                JOptionPane.showMessageDialog(getParent(),
                                        "Transfer successful!\nAverage transfer speed: " + rate
                                                + "MBps\nFiles will be saved in " + System.getProperty("user.dir")
                                                + "\\Received Files",
                                        "INFORMATION!", JOptionPane.INFORMATION_MESSAGE);
                            else
                                JOptionPane.showMessageDialog(
                                        getParent(), "Transfer successful!\nFiles will be saved in "
                                                + System.getProperty("user.dir") + "\\Received Files",
                                        "INFORMATION!", JOptionPane.INFORMATION_MESSAGE);

                        }
                        try {
                            is.close();
                            fos.close();
                            socket.close();
                        } catch (Exception e) {
                            // nothin
                        }
                        reset();
                        revalidate();
                        repaint();

                    }

                };
                sw.execute();
            } else if (e.getSource() == back) {
                try {
                    is.close();
                    fos.close();
                    socket.close();
                } catch (Exception e1) {
                    // nothin
                }
                reset();
            }
        }

        public void dirTrav(String path, String file) {
            File f = new File(path, file);
            int index = 0;
            while (f.exists()) {
                f = new File(path, "(" + index + ")" + file);
                index++;
            }
            if (!f.mkdir()) {
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            JOptionPane.showMessageDialog(KrytezShare.this,
                                    "Error Occured!\nCould not create folder.\nIf the app is installed in system drive, kindly run as administrator.",
                                    "ERROR!", JOptionPane.ERROR_MESSAGE);
                        }

                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            try {
                DataOutputStream bo = null;
                while (true) {
                    try {
                        bo = new DataOutputStream(socket.getOutputStream());
                    } catch (Exception ex1) {
                        // System.out.println(886);
                    }
                    PrintWriter pw = new PrintWriter(bo, true);
                    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    BufferedInputStream is = new BufferedInputStream(socket.getInputStream());
                    if ((fname = br.readLine()).equals("Dirend")) {
                        // System.out.println(fname);
                        pw.println("done");
                        pw.flush();
                        break;
                    }
                    // System.out.println(fname);
                    if (fname.equals("Dir")) {

                        pw.println("done");
                        pw.flush();
                        file = br.readLine();
                        pw.println("Done");
                        pw.flush();
                        dirTrav(f.getPath(), file);
                        continue;
                    }
                    String filedata[] = fname.split(" ");
                    long filelen = Long.parseLong(filedata[0]);
                    fname = fname.substring(filedata[0].length());
                    long transferred = 0;
                    // System.out.println(fname + " length is " + filelen);
                    pw.println("Done");
                    pw.flush();
                    File f1 = new File(f, fname);
                    // System.out.println(896);
                    if (start) {
                        totaldata = br.readLine();
                        datad = Double.parseDouble(totaldata);
                        data = (int) datad;
                        starttime = System.nanoTime();
                        SwingUtilities.invokeAndWait(new Runnable() {
                            public void run() {
                                pb.setIndeterminate(false);
                                pb.setMaximum((int) data);
                                pb.setMinimum(0);
                            }
                        });
                        start = false;
                    }
                    // System.out.println(911);
                    int len;
                    byte[] buff = new byte[32768];
                    FileOutputStream fos = new FileOutputStream(f1);
                    pw.println("done");
                    pw.flush();
                    long transd = 0;
                    // System.out.println(917);
                    if (filelen == 0) {

                        fos.flush();
                        fos.close();
                        pw.println("received");
                        pw.flush();
                        continue;
                    }
                    while ((len = is.read(buff)) != -1) {
                        fos.write(buff, 0, len);
                        fos.flush();
                        transd += len;
                        if (transd >= 1048576) {
                            maintrans += 1;
                            SwingUtilities.invokeAndWait(new Runnable() {
                                public void run() {

                                    pb.setString("Transferring file: " + fname + " " + maintrans + "MB/" + data + "MB");
                                    pb.setValue((int) maintrans);
                                }
                            });
                            transd = 0;
                        }
                        transferred += len;
                        if (transferred == filelen)
                            break;
                    }
                    fos.flush();
                    fos.close();
                    pw.println("received");
                    pw.flush();

                }

            } catch (Exception e) {
                e.printStackTrace();
                // System.out.println(952);
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            JOptionPane.showMessageDialog(KrytezShare.this, "Error Occured!", "ERROR!",
                                    JOptionPane.ERROR_MESSAGE);
                        }

                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    KrytezShare() {
        setTitle("KryTez Share");
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        JOptionPane.showMessageDialog(this,
                "If the app is installed in System drive(C:),\nplease run in administrator mode to share files.\nWithout admin permission,app can't write to c drive.");
        Font font = new Font("courier", Font.PLAIN, 24);
        senditb = new JButton("Send");
        senditb.addActionListener(this);
        senditb.setForeground(Color.DARK_GRAY);
        senditb.setFont(font);
        receiveitb = new JButton("Receive");
        receiveitb.addActionListener(this);
        receiveitb.setFont(font);
        receiveitb.setForeground(Color.DARK_GRAY);
        String path = System.getProperty("user.dir");
        path += "\\Background_2.jpg";
        ImageIcon icon = new ImageIcon(path);
        layout1 = new JLabel(icon, JLabel.CENTER);
        layout1.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.ipadx = 10;
        gbc.ipady = 20;
        layout1.add(senditb, gbc);
        gbc.gridy = 1;
        Insets inset = new Insets(20, 0, 0, 0);
        gbc.insets = inset;
        layout1.add(receiveitb, gbc);
        senditb.setContentAreaFilled(false);
        senditb.setOpaque(false);
        receiveitb.setContentAreaFilled(false);
        receiveitb.setOpaque(false);
        add(layout1);
        setVisible(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == senditb) {
            Server1 server = new Server1();
        } else {
            Client1 client = new Client1();
        }
    }
}