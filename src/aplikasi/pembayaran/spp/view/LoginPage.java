package aplikasi.pembayaran.spp.view;

import aplikasi.pembayaran.spp.model.Koneksi;
import aplikasi.pembayaran.spp.controller.UserController;
import aplikasi.pembayaran.spp.model.User;

import javax.swing.*;
import java.awt.*;

public class LoginPage extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnExit;
    
    private UserController userController;

    public LoginPage() {
        this.userController = new UserController();
        
        setTitle("Login - Aplikasi Pembayaran SPP");
        setSize(450, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        // 🔹 Title
        JLabel lblTitle = new JLabel("APLIKASI PEMBAYARAN SPP", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(40, 120, 200));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(lblTitle, BorderLayout.NORTH);

        // 🔹 Panel form
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 0; gbc.gridy = 0;
        panelForm.add(lblUser, gbc);

        txtUsername = new JTextField(20);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1; gbc.gridy = 0;
        panelForm.add(txtUsername, gbc);

        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 0; gbc.gridy = 1;
        panelForm.add(lblPass, gbc);

        txtPassword = new JPasswordField(20);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1; gbc.gridy = 1;
        panelForm.add(txtPassword, gbc);

        add(panelForm, BorderLayout.CENTER);

        // 🔹 Panel tombol
        JPanel panelBtn = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panelBtn.setBackground(new Color(230, 230, 230));

        btnLogin = new JButton("LOGIN");
        btnLogin.setPreferredSize(new Dimension(120, 35));
        btnLogin.setBackground(new Color(46, 204, 113));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogin.setFocusPainted(false);
        panelBtn.add(btnLogin);

        btnExit = new JButton("KELUAR");
        btnExit.setPreferredSize(new Dimension(120, 35));
        btnExit.setBackground(new Color(231, 76, 60));
        btnExit.setForeground(Color.WHITE);
        btnExit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnExit.setFocusPainted(false);
        panelBtn.add(btnExit);

        add(panelBtn, BorderLayout.SOUTH);

        // 🔹 Event tombol
        btnExit.addActionListener(e -> System.exit(0));
        btnLogin.addActionListener(e -> loginProses());
        
        // Enter key untuk login
        txtPassword.addActionListener(e -> loginProses());
    }

    // 🔹 Method login
    private void loginProses() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan Password tidak boleh kosong!",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validasi login via UserController
        User user = userController.login(username, password);
        
        if (user != null) {
            String role = user.getRole();
            
            JOptionPane.showMessageDialog(this,
                    "Login berhasil sebagai " + role + "!\nSelamat datang, " + user.getNamaLengkap(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            dispose();

            // 🔹 Arahkan ke dashboard sesuai role
            switch (role) {
                case "Kepsek":
                    DashboardKepsek dashKepsek = new DashboardKepsek(user);
                    dashKepsek.setVisible(true);
                    break;
                case "Admin":
                    DashboardAdmin dashAdmin = new DashboardAdmin(user);
                    dashAdmin.setVisible(true);
                    break;
                case "TU":
                    DashboardTU dashTU = new DashboardTU(username);
                    dashTU.setVisible(true);
                    break;
                case "Siswa":
                    DashboardSiswa dashSiswa = new DashboardSiswa(user);
                    dashSiswa.setVisible(true);
                    break;
                default:
                    JOptionPane.showMessageDialog(this,
                            "Role tidak dikenali: " + role,
                            "Error", JOptionPane.ERROR_MESSAGE);
                    break;
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Username atau Password salah!",
                    "Login Gagal", JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
            txtPassword.requestFocus();
        }
    }
}