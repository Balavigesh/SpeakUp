package speak_up;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.regex.Pattern;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;


public class AdminLogin extends JFrame{
    JLabel lbl_phone, lbl_password, lbl_passErr, lbl_phoneErr;
    JTextField txt_phone;
    JPasswordField txt_password;
    
    AdminLogin() {
        setSize(570, 500);
        setTitle("Speak Up");
        setBounds(420, 100, 570, 500);
        
        Color c1=new Color(50, 105, 168);
        Color c2=new Color(0,0,0);
        Color c3=new Color(255, 255, 255);
        Color c4=new Color(93, 253, 146);
        
        getContentPane().setBackground(c3);
        
        JLabel lbl_header = new JLabel("Admin Login");
        lbl_header.setForeground(c2);
        lbl_header.setBounds(200, 80, 300, 40);
        Font headerFont = new Font("serif", Font.BOLD, 25);
        lbl_header.setFont(headerFont);
        add(lbl_header);
        
        Font lblFont = new Font("Serif", Font.PLAIN, 18);
        
        lbl_phone = new JLabel("Phone    :");
        lbl_phone.setForeground(c2);
        lbl_phone.setBounds(130, 150, 70, 30);
        lbl_phone.setFont(lblFont);
        add(lbl_phone);
        
        lbl_phoneErr = new JLabel();
        lbl_phoneErr.setFont(new Font("serif", Font.PLAIN, 12));
        lbl_phoneErr.setBounds(250, 185, 150, 15);
        lbl_phoneErr.setForeground(Color.red);

        
        txt_phone = new JTextField();
        txt_phone.setBounds(250, 150, 150, 30);
        txt_phone.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                String phone = txt_phone.getText();
                lbl_phoneErr.setText("Enter a valid phone number.");
                if(!Pattern.matches("^[6-9]{1}[0-9]{0,9}$", phone)){
                    add(lbl_phoneErr);
                    validate();
                    repaint();
                }
                else{
                    remove(lbl_phoneErr);
                    validate();
                    repaint();
                }
            }
        });
        add(txt_phone);
        
        lbl_password = new JLabel("Password  :");
        lbl_password.setForeground(c2);
        lbl_password.setBounds(130, 210, 120, 30);
        lbl_password.setFont(lblFont);
        add(lbl_password);
        
        lbl_passErr = new JLabel();
        lbl_passErr.setFont(new Font("serif", Font.PLAIN, 12));
        lbl_passErr.setBounds(250, 260, 150, 15);
        lbl_passErr.setForeground(Color.red);

        
        txt_password = new JPasswordField();
        txt_password.setBounds(250, 210, 150, 30);
        
        txt_password.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                char[] password = txt_password.getPassword();
                if(password.length > 0){
                    remove(lbl_passErr);
                    validate();
                    repaint();
                }
            }
        });
        add(txt_password);
        
        JCheckBox check_password = new JCheckBox("Show Password");
        check_password.setForeground(c2);
        check_password.setBackground(c3);
        check_password.setBounds(250, 245, 150, 15);
        check_password.setFont(new Font("serif", Font.PLAIN, 13));
        check_password.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(check_password.isSelected()) 
                    txt_password.setEchoChar('\u0000');
                else
                    txt_password.setEchoChar((Character)UIManager.get("PasswordField.echoChar"));
            }
        } );
        add(check_password);
        
        JButton btn_submit = new JButton("Submit");
        btn_submit.setBackground(c4);
        btn_submit.setForeground(c2);
        btn_submit.setBorder(new LineBorder(c2));
        btn_submit.setBounds(280, 290, 100, 30);
        btn_submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean flag = true;
                String phone = txt_phone.getText();
                char password[] = txt_password.getPassword();
                if(phone.length() < 1){
                    lbl_phoneErr.setText("This field cannot be empty");
                    add(lbl_phoneErr);
                    validate();
                    repaint();
                    flag = false;
                } else if (!Pattern.matches("^[6-9]{1}[0-9]{9}$", phone)){
                    lbl_phoneErr.setText("Enter a valid phone number");
                    add(lbl_phoneErr);
                    validate();
                    repaint();
                    flag = false;
                }
                if(password.length < 1){
                    lbl_passErr.setText("This field cannot be empty");
                    add(lbl_passErr);
                    validate();
                    repaint();
                    flag = false;
                }
                if(flag) {
                    verifyUser(phone, password);
                }
            }
        });
        add(btn_submit);
        
        JButton btn_goBack = new JButton("Back");
        btn_goBack.setBackground(c1);
        btn_goBack.setForeground(c3);
        btn_goBack.setBounds(160, 290, 100, 30);
        
        btn_goBack.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                WelcomePage w1 = new WelcomePage();
            }
            
        });
        
        add(btn_goBack);
        
       
        setResizable(false);
        setLayout(null);
        setVisible(true);
    }
    
    void verifyUser(String phone, char[] password){
        try{
            Connection con = DbConnection.getConnection();
            Statement stmt = con.createStatement();
            String query;
            query = "select * from admin where phone='"+phone+"'";
            ResultSet user = stmt.executeQuery(query);
            
            String hashedPassword = HashPassword.toHexString(new String(password));
            if(user.next()){
                String actualPassword = user.getString("password");;
                if(actualPassword.equals(hashedPassword)){
                        AdminHomePage p1 = new AdminHomePage();
                        dispose();
                }
                else{
                    lbl_passErr.setText("Invalid Password");
                    add(lbl_passErr);
                    validate();
                    repaint();
                }
            }
            else{
                JTextArea lbl_noUser = new JTextArea("Account with given phone number does not exist.");
                lbl_noUser.setWrapStyleWord(true);
                lbl_noUser.setLineWrap(true);
                lbl_noUser.setOpaque(false);
                lbl_noUser.setEditable(false);

                lbl_noUser.setBounds(210, 320, 180, 70);
                lbl_noUser.setFont(new Font("serif", Font.PLAIN, 15));
                lbl_noUser.setForeground(Color.red);
                add(lbl_noUser);
                validate();
                repaint();
            }
            con.close();
        } catch(Exception ex) {
            System.out.println("exception occurred :" + ex);
        }
    }
}
