package aplikasi.pembayaran.spp.view;

import aplikasi.pembayaran.spp.model.User;
import aplikasi.pembayaran.spp.controller.UserController;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Dashboard untuk Siswa/Orang Tua
 * Role: READ-ONLY - cek tagihan, riwayat pembayaran, update profil
 *
 * Fitur Siswa/Orang Tua:
 * - Cek tagihan SPP bulanan
 * - Lihat riwayat pembayaran
 * - Update profil (nama, no HP, password)
 * - Download kwitansi pembayaran
 * - Lihat jadwal pembayaran
 */
public class DashboardSiswa extends JFrame implements ActionListener {
    
    // User yang sedang login
    private User currentUser;
    
    // Controller
    private UserController userController;
    
    // Main components
    private JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel contentPanel;
    private JPanel sidebarPanel;
    private JLabel userInfoLabel;
    private JLabel timeLabel;
    
    // Menu buttons
    private JButton btnDashboard;
    private JButton btnTagihan;
    private JButton btnRiwayatPembayaran;
    private JButton btnProfil;
    private JButton btnLogout;
    
    // Content panels
    private JPanel dashboardContentPanel;
    private JPanel tagihanContentPanel;
    private JPanel riwayatContentPanel;
    private JPanel profilContentPanel;
    
    // Timer untuk update waktu
    private Timer timeUpdateTimer;
    
    /**
     * Constructor - Setup dashboard untuk Siswa/Orang Tua
     */
    public DashboardSiswa(User user) {
        this.currentUser = user;
        this.userController = new UserController();
        
        initComponents();
        setupUI();
        setupEventHandlers();
        startTimeUpdate();
        showDashboardContent(); // Default show dashboard
        
        System.out.println("🎓 Dashboard Siswa loaded untuk: " + user.getNamaLengkap());
    }
    
    /**
     * Method untuk inisialisasi komponen UI
     */
    private void initComponents() {
        // Set properties window
        setTitle("SPP Payment System - Dashboard Siswa/Orang Tua");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Full screen
        
        // Main layout
        setLayout(new BorderLayout());
        
        // Create main panels
        createHeaderPanel();
        createSidebarPanel();
        createContentPanel();
        
        // Setup main panel
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    /**
     * Method untuk membuat header panel
     */
    private void createHeaderPanel() {
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(155, 89, 182)); // Purple color untuk siswa
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        headerPanel.setPreferredSize(new Dimension(0, 80));
        
        // Title di kiri
        JLabel titleLabel = new JLabel("🎓 DASHBOARD SISWA/ORANG TUA");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        
        // User info & time di kanan
        JPanel rightPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        rightPanel.setOpaque(false);
        
        userInfoLabel = new JLabel("👤 " + currentUser.getNamaLengkap() + " (" + currentUser.getRole() + ")");
        userInfoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        userInfoLabel.setForeground(Color.WHITE);
        userInfoLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        timeLabel = new JLabel();
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        timeLabel.setForeground(new Color(255, 255, 255, 200));
        timeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        rightPanel.add(userInfoLabel);
        rightPanel.add(timeLabel);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);
    }
    
    /**
     * Method untuk membuat sidebar menu
     */
    private void createSidebarPanel() {
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(new Color(142, 68, 173)); // Darker purple
        sidebarPanel.setPreferredSize(new Dimension(250, 0));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        
        // Menu buttons
        btnDashboard = createMenuButton("🏠 Dashboard", "Halaman utama dengan ringkasan");
        btnTagihan = createMenuButton("💰 Cek Tagihan SPP", "Lihat tagihan SPP bulanan");
        btnRiwayatPembayaran = createMenuButton("📋 Riwayat Pembayaran", "Histori pembayaran SPP");
        btnProfil = createMenuButton("👤 Profil", "Update data profil");
        
        // Spacer
        sidebarPanel.add(Box.createVerticalStrut(20));
        sidebarPanel.add(createSeparator());
        sidebarPanel.add(Box.createVerticalStrut(20));
        
        btnLogout = createMenuButton("🚪 Logout", "Keluar dari aplikasi");
        btnLogout.setBackground(new Color(231, 76, 60)); // Red color
        
        // Set dashboard as active by default
        setActiveButton(btnDashboard);
    }
    
    /**
     * Method untuk membuat menu button
     */
    private JButton createMenuButton(String text, String tooltip) {
        JButton button = new JButton("<html><div style='text-align: left; padding: 5px;'>" + text + "</div></html>");
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(155, 89, 182));
        button.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(230, 50));
        button.setToolTipText(tooltip);
        button.addActionListener(this);
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.getBackground() != new Color(125, 60, 152)) { // Not active
                    button.setBackground(new Color(165, 105, 189));
                }
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.getBackground() != new Color(125, 60, 152)) { // Not active
                    button.setBackground(new Color(155, 89, 182));
                }
            }
        });
        
        sidebarPanel.add(button);
        sidebarPanel.add(Box.createVerticalStrut(5));
        
        return button;
    }
    
    /**
     * Method untuk membuat separator
     */
    private JSeparator createSeparator() {
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(255, 255, 255, 100));
        separator.setMaximumSize(new Dimension(200, 1));
        return separator;
    }
    
    /**
     * Method untuk membuat content panel
     */
    private void createContentPanel() {
        contentPanel = new JPanel(new CardLayout());
        contentPanel.setBackground(new Color(236, 240, 241)); // Light gray
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create different content panels
        createDashboardContent();
        createTagihanContent();
        createRiwayatContent();
        createProfilContent();
        
        // Add to CardLayout
        contentPanel.add(dashboardContentPanel, "DASHBOARD");
        contentPanel.add(tagihanContentPanel, "TAGIHAN");
        contentPanel.add(riwayatContentPanel, "RIWAYAT");
        contentPanel.add(profilContentPanel, "PROFIL");
    }
    
    /**
     * Method untuk membuat dashboard content (halaman utama)
     */
    private void createDashboardContent() {
        dashboardContentPanel = new JPanel(new BorderLayout());
        dashboardContentPanel.setBackground(new Color(236, 240, 241));
        
        // Title
        JLabel titleLabel = new JLabel("Selamat Datang di Portal Siswa/Orang Tua");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
//        titleLabel.setFore
                
titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(155, 89, 182));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Info siswa panel
        JPanel infoSiswaPanel = new JPanel(new BorderLayout());
        infoSiswaPanel.setBackground(Color.WHITE);
        infoSiswaPanel.setBorder(BorderFactory.createTitledBorder("👤 Informasi Siswa"));
        infoSiswaPanel.setPreferredSize(new Dimension(0, 150));
        
        JPanel infoDetailPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        infoDetailPanel.setBackground(Color.WHITE);
        infoDetailPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Data siswa dummy
        infoDetailPanel.add(new JLabel("NIS:"));
        infoDetailPanel.add(new JLabel("2024001"));
        infoDetailPanel.add(new JLabel("Kelas:"));
        infoDetailPanel.add(new JLabel("XII IPA 1"));
        
        infoDetailPanel.add(new JLabel("Nama:"));
        infoDetailPanel.add(new JLabel("Ahmad Rizky Pratama"));
        infoDetailPanel.add(new JLabel("Tahun Ajaran:"));
        infoDetailPanel.add(new JLabel("2024/2025"));
        
        infoDetailPanel.add(new JLabel("Orang Tua:"));
        infoDetailPanel.add(new JLabel("Budi Rizky"));
        infoDetailPanel.add(new JLabel("No. Telepon:"));
        infoDetailPanel.add(new JLabel("081234567890"));
        
        infoSiswaPanel.add(infoDetailPanel, BorderLayout.CENTER);
        
        // Status cards panel
        JPanel statusPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statusPanel.setOpaque(false);
        
        // Create status cards
        statusPanel.add(createStatusCard("💰 Status SPP", "LUNAS", "Bulan Maret 2024", new Color(46, 204, 113)));
        statusPanel.add(createStatusCard("🎁 Potongan", "Rp 25,000", "Beasiswa Prestasi", new Color(155, 89, 182)));
        statusPanel.add(createStatusCard("📋 Tunggakan", "Rp 0", "Tidak ada tunggakan", new Color(52, 152, 219)));
        
        // Recent activities panel
        JPanel recentPanel = new JPanel(new BorderLayout());
        recentPanel.setBackground(Color.WHITE);
        recentPanel.setBorder(BorderFactory.createTitledBorder("📋 Aktivitas Terbaru"));
        recentPanel.setPreferredSize(new Dimension(0, 200));
        
        String[] recentActivities = {
            "✅ Pembayaran SPP Maret 2024 telah diterima",
            "🎁 Mendapat potongan beasiswa prestasi sebesar Rp 25,000",
            "📄 Kwitansi pembayaran Maret 2024 tersedia untuk download",
            "💰 Tagihan SPP April 2024 sudah dapat dibayarkan",
            "📧 Reminder: Batas pembayaran SPP tanggal 10 setiap bulan"
        };
        
        JList<String> activityList = new JList<>(recentActivities);
        activityList.setFont(new Font("Arial", Font.PLAIN, 12));
        activityList.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        recentPanel.add(new JScrollPane(activityList), BorderLayout.CENTER);
        
        // Layout dashboard content
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(infoSiswaPanel, BorderLayout.CENTER);
        
        JPanel middlePanel = new JPanel(new BorderLayout());
        middlePanel.setOpaque(false);
        middlePanel.add(statusPanel, BorderLayout.CENTER);
        middlePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        dashboardContentPanel.add(topPanel, BorderLayout.NORTH);
        dashboardContentPanel.add(middlePanel, BorderLayout.CENTER);
        dashboardContentPanel.add(recentPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Method untuk membuat status card
     */
    private JPanel createStatusCard(String title, String value, String description, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Icon panel
        JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        iconPanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel("●");
        iconLabel.setFont(new Font("Arial", Font.BOLD, 30));
        iconLabel.setForeground(color);
        iconPanel.add(iconLabel);
        
        // Text panel
        JPanel textPanel = new JPanel(new GridLayout(3, 1, 0, 5));
        textPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        titleLabel.setForeground(new Color(127, 140, 141));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 18));
        valueLabel.setForeground(new Color(52, 73, 94));
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        descLabel.setForeground(new Color(149, 165, 166));
        
        textPanel.add(titleLabel);
        textPanel.add(valueLabel);
        textPanel.add(descLabel);
        
        card.add(iconPanel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    /**
     * Method untuk membuat tagihan content
     */
    private void createTagihanContent() {
        tagihanContentPanel = new JPanel(new BorderLayout());
        tagihanContentPanel.setBackground(new Color(236, 240, 241));
        
        // Title
        JLabel titleLabel = new JLabel("💰 Cek Tagihan SPP");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(230, 126, 34));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Current month panel
        JPanel currentMonthPanel = new JPanel(new BorderLayout());
        currentMonthPanel.setBackground(new Color(46, 204, 113, 50));
        currentMonthPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(46, 204, 113), 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        currentMonthPanel.setPreferredSize(new Dimension(0, 120));
        
        JLabel currentMonthLabel = new JLabel("✅ SPP MARET 2024 - SUDAH LUNAS");
        currentMonthLabel.setFont(new Font("Arial", Font.BOLD, 18));
        currentMonthLabel.setForeground(new Color(39, 174, 96));
        currentMonthLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel paidInfoLabel = new JLabel("Dibayar tanggal: 05 Maret 2024 | Jumlah: Rp 125,000 (setelah potongan Rp 25,000)");
        paidInfoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        paidInfoLabel.setForeground(new Color(52, 73, 94));
        paidInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        currentMonthPanel.add(currentMonthLabel, BorderLayout.CENTER);
        currentMonthPanel.add(paidInfoLabel, BorderLayout.SOUTH);
        
        // Upcoming payment panel
        JPanel upcomingPanel = new JPanel(new BorderLayout());
        upcomingPanel.setBackground(new Color(52, 152, 219, 50));
        upcomingPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        upcomingPanel.setPreferredSize(new Dimension(0, 120));
        
        JLabel upcomingLabel = new JLabel("📅 SPP APRIL 2024 - DAPAT DIBAYAR");
        upcomingLabel.setFont(new Font("Arial", Font.BOLD, 18));
        upcomingLabel.setForeground(new Color(41, 128, 185));
        upcomingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel upcomingInfoLabel = new JLabel("Nominal: Rp 150,000 | Potongan: Rp 25,000 | Yang harus dibayar: Rp 125,000");
        upcomingInfoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        upcomingInfoLabel.setForeground(new Color(52, 73, 94));
        upcomingInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        upcomingPanel.add(upcomingLabel, BorderLayout.CENTER);
        upcomingPanel.add(upcomingInfoLabel, BorderLayout.SOUTH);
        
        // Payment schedule table
        JPanel schedulePanel = new JPanel(new BorderLayout());
        schedulePanel.setBackground(Color.WHITE);
        schedulePanel.setBorder(BorderFactory.createTitledBorder("📅 Jadwal Pembayaran Tahun Ajaran 2024/2025"));
        
        String[] scheduleColumns = {"Bulan", "Nominal SPP", "Potongan", "Total Bayar", "Batas Bayar", "Status"};
        Object[][] scheduleData = {
            {"Januari 2024", "Rp 150,000", "Rp 25,000", "Rp 125,000", "10 Jan 2024", "✅ Lunas"},
            {"Februari 2024", "Rp 150,000", "Rp 25,000", "Rp 125,000", "10 Feb 2024", "✅ Lunas"},
            {"Maret 2024", "Rp 150,000", "Rp 25,000", "Rp 125,000", "10 Mar 2024", "✅ Lunas"},
            {"April 2024", "Rp 150,000", "Rp 25,000", "Rp 125,000", "10 Apr 2024", "💰 Belum Bayar"},
            {"Mei 2024", "Rp 150,000", "Rp 25,000", "Rp 125,000", "10 Mei 2024", "⏳ Belum Jatuh Tempo"},
            {"Juni 2024", "Rp 150,000", "Rp 25,000", "Rp 125,000", "10 Jun 2024", "⏳ Belum Jatuh Tempo"},
        };
        
        DefaultTableModel scheduleTableModel = new DefaultTableModel(scheduleData, scheduleColumns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only untuk siswa
            }
        };
        
        JTable scheduleTable = new JTable(scheduleTableModel);
        scheduleTable.setFont(new Font("Arial", Font.PLAIN, 12));
        scheduleTable.setRowHeight(25);
        scheduleTable.getTableHeader().setBackground(new Color(230, 126, 34));
        scheduleTable.getTableHeader().setForeground(Color.WHITE);
        scheduleTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane scheduleScrollPane = new JScrollPane(scheduleTable);
        schedulePanel.add(scheduleScrollPane, BorderLayout.CENTER);
        
        // Layout
        JPanel alertsPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        alertsPanel.setOpaque(false);
        alertsPanel.add(currentMonthPanel);
        alertsPanel.add(upcomingPanel);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(alertsPanel, BorderLayout.CENTER);
        
        tagihanContentPanel.add(topPanel, BorderLayout.NORTH);
        tagihanContentPanel.add(schedulePanel, BorderLayout.CENTER);
    }
    
    /**
     * Method untuk membuat riwayat content
     */
    private void createRiwayatContent() {
        riwayatContentPanel = new JPanel(new BorderLayout());
        riwayatContentPanel.setBackground(new Color(236, 240, 241));
        
        // Title
        JLabel titleLabel = new JLabel("📋 Riwayat Pembayaran SPP");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 152, 219));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createTitledBorder("🔍 Filter Riwayat"));
        
        filterPanel.add(new JLabel("Tahun:"));
        JComboBox<String> tahunCombo = new JComboBox<>(new String[]{"2024", "2023", "2022"});
        filterPanel.add(tahunCombo);
        
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(new JLabel("Status:"));
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Semua", "Lunas", "Belum Bayar"});
        filterPanel.add(statusCombo);
        
        JButton btnFilterRiwayat = new JButton("🔍 Filter");
        btnFilterRiwayat.setBackground(new Color(52, 152, 219));
        btnFilterRiwayat.setForeground(Color.WHITE);
        btnFilterRiwayat.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        filterPanel.add(btnFilterRiwayat);
        
        // Riwayat table
        String[] riwayatColumns = {"Bulan/Tahun", "Tanggal Bayar", "Nominal SPP", "Potongan", "Jumlah Bayar", "Metode", "Status", "Kwitansi"};
        Object[][] riwayatData = {
            {"Maret 2024", "05/03/2024", "Rp 150,000", "Rp 25,000", "Rp 125,000", "Cash", "✅ Lunas", "📄 Download"},
            {"Februari 2024", "03/02/2024", "Rp 150,000", "Rp 25,000", "Rp 125,000", "Transfer", "✅ Lunas", "📄 Download"},
            {"Januari 2024", "08/01/2024", "Rp 150,000", "Rp 25,000", "Rp 125,000", "Cash", "✅ Lunas", "📄 Download"},
            {"Desember 2023", "07/12/2023", "Rp 150,000", "Rp 0", "Rp 150,000", "Transfer", "✅ Lunas", "📄 Download"},
            {"November 2023", "10/11/2023", "Rp 150,000", "Rp 0", "Rp 150,000", "Cash", "✅ Lunas", "📄 Download"},
        };
        
        DefaultTableModel riwayatTableModel = new DefaultTableModel(riwayatData, riwayatColumns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only
            }
        };
        
        JTable riwayatTable = new JTable(riwayatTableModel);
        riwayatTable.setFont(new Font("Arial", Font.PLAIN, 11));
        riwayatTable.setRowHeight(25);
        riwayatTable.getTableHeader().setBackground(new Color(52, 152, 219));
        riwayatTable.getTableHeader().setForeground(Color.WHITE);
        riwayatTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        JScrollPane riwayatScrollPane = new JScrollPane(riwayatTable);
        
        // Summary panel
        JPanel summaryPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(BorderFactory.createTitledBorder("📊 Ringkasan Pembayaran"));
        summaryPanel.setPreferredSize(new Dimension(0, 80));
        
        summaryPanel.add(createSummaryCard("Total Dibayar", "Rp 1,875,000", new Color(46, 204, 113)));
        summaryPanel.add(createSummaryCard("Total Potongan", "Rp 75,000", new Color(155, 89, 182)));
        summaryPanel.add(createSummaryCard("Jumlah Transaksi", "5", new Color(52, 152, 219)));
        summaryPanel.add(createSummaryCard("Status", "100% Lunas", new Color(46, 204, 113)));
        
        // Layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(filterPanel, BorderLayout.CENTER);
        
        riwayatContentPanel.add(topPanel, BorderLayout.NORTH);
        riwayatContentPanel.add(riwayatScrollPane, BorderLayout.CENTER);
        riwayatContentPanel.add(summaryPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Method untuk membuat summary card
     */
    private JPanel createSummaryCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        titleLabel.setForeground(new Color(127, 140, 141));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 16));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    /**
     * Method untuk membuat potongan content
     */
    private void createPotonganContent() {
//        potonganContentPanel = new JPanel(new BorderLayout());
//        potonganContentPanel.setBackground(new Color(236, 240, 241));
//        
        // Title
        JLabel titleLabel = new JLabel("🎁 Potongan SPP & Beasiswa");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(155, 89, 182));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Current discount panel
        JPanel currentDiscountPanel = new JPanel(new BorderLayout());
        currentDiscountPanel.setBackground(new Color(155, 89, 182, 50));
        currentDiscountPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(155, 89, 182), 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        currentDiscountPanel.setPreferredSize(new Dimension(0, 100));
        
        JLabel discountTitleLabel = new JLabel("🎉 SELAMAT! Anda mendapat potongan SPP");
        discountTitleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        discountTitleLabel.setForeground(new Color(125, 60, 152));
        discountTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel discountInfoLabel = new JLabel("Jenis: Beasiswa Prestasi | Jumlah: Rp 25,000/bulan | Berlaku sampai: Juni 2024");
        discountInfoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        discountInfoLabel.setForeground(new Color(52, 73, 94));
        discountInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        currentDiscountPanel.add(discountTitleLabel, BorderLayout.CENTER);
        currentDiscountPanel.add(discountInfoLabel, BorderLayout.SOUTH);
        
        // Discount history table
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBackground(Color.WHITE);
        historyPanel.setBorder(BorderFactory.createTitledBorder("📋 Riwayat Potongan"));
        
        String[] potonganColumns = {"Periode", "Jenis Potongan", "Nominal", "Persentase", "Status", "Keterangan"};
        Object[][] potonganData = {
            {"Jan - Jun 2024", "Beasiswa Prestasi", "Rp 25,000", "16.7%", "✅ Aktif", "Juara 1 Olimpiade Matematika"},
            {"Jul - Des 2023", "Beasiswa Kurang Mampu", "Rp 75,000", "50%", "✅ Selesai", "Bantuan ekonomi keluarga"},
            {"Jan - Jun 2023", "Potongan Khusus", "Rp 15,000", "10%", "✅ Selesai", "Anak guru"},
        };
        
        DefaultTableModel potonganTableModel = new DefaultTableModel(potonganData, potonganColumns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable potonganTable = new JTable(potonganTableModel);
        potonganTable.setFont(new Font("Arial", Font.PLAIN, 12));
        potonganTable.setRowHeight(25);
        potonganTable.getTableHeader().setBackground(new Color(155, 89, 182));
        potonganTable.getTableHeader().setForeground(Color.WHITE);
        
        historyPanel.add(new JScrollPane(potonganTable), BorderLayout.CENTER);
        
        // Info panel
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(new Color(52, 152, 219, 50));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        infoPanel.setPreferredSize(new Dimension(0, 80));
        
        JLabel infoLabel = new JLabel("ℹ️ Informasi: Potongan SPP diberikan berdasarkan prestasi akademik, kondisi ekonomi, atau kriteria khusus lainnya");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        infoLabel.setForeground(new Color(41, 128, 185));
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        infoPanel.add(infoLabel, BorderLayout.CENTER);
        
        // Layout
//        potonganContentPanel.add(titleLabel, BorderLayout.NORTH);
//        potonganContentPanel.add(currentDiscountPanel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(currentDiscountPanel, BorderLayout.NORTH);
        centerPanel.add(historyPanel, BorderLayout.CENTER);
        centerPanel.add(infoPanel, BorderLayout.SOUTH);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
//        potonganContentPanel.add(centerPanel, BorderLayout.CENTER);
    }
    
    /**
     * Method untuk membuat profil content
     */
    private void createProfilContent() {
        profilContentPanel = new JPanel(new BorderLayout());
        profilContentPanel.setBackground(new Color(236, 240, 241));
        
        // Title
        JLabel titleLabel = new JLabel("👤 Profil & Pengaturan Akun");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Split panel
        JSplitPane profilSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        profilSplitPane.setDividerLocation(500);
        
        // Left panel - Profile form
        JPanel profileFormPanel = new JPanel(new BorderLayout());
        profileFormPanel.setBackground(Color.WHITE);
        profileFormPanel.setBorder(BorderFactory.createTitledBorder("✏️ Edit Profil"));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Form fields
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("NIS:"), gbc);
        gbc.gridx = 1;
        JTextField nisProfileField = new JTextField("2024001", 15);
        nisProfileField.setEditable(false);
        formPanel.add(nisProfileField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Nama Lengkap:"), gbc);
        gbc.gridx = 1;
        JTextField namaProfileField = new JTextField("Ahmad Rizky Pratama", 15);
        formPanel.add(namaProfileField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("No. Telepon:"), gbc);
        gbc.gridx = 1;
        JTextField teleponField = new JTextField("081234567890", 15);
        formPanel.add(teleponField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Password Lama:"), gbc);
        gbc.gridx = 1;
        JPasswordField passwordLamaField = new JPasswordField(15);
        formPanel.add(passwordLamaField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Password Baru:"), gbc);
        gbc.gridx = 1;
        JPasswordField passwordBaruField = new JPasswordField(15);
        formPanel.add(passwordBaruField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Konfirmasi Password:"), gbc);
        gbc.gridx = 1;
        JPasswordField konfirmasiPasswordField = new JPasswordField(15);
        formPanel.add(konfirmasiPasswordField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        
        JButton btnUpdateProfil = new JButton("💾 Update Profil");
        btnUpdateProfil.setBackground(new Color(46, 204, 113));
        btnUpdateProfil.setForeground(Color.WHITE);
        btnUpdateProfil.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JButton btnResetForm = new JButton("🔄 Reset");
        btnResetForm.setBackground(new Color(149, 165, 166));
        btnResetForm.setForeground(Color.WHITE);
        btnResetForm.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        buttonPanel.add(btnUpdateProfil);
        buttonPanel.add(btnResetForm);
        
        profileFormPanel.add(formPanel, BorderLayout.CENTER);
        profileFormPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Right panel - Account info
        JPanel accountInfoPanel = new JPanel(new BorderLayout());
        accountInfoPanel.setBackground(Color.WHITE);
        accountInfoPanel.setBorder(BorderFactory.createTitledBorder("ℹ️ Informasi Akun"));
        
        JPanel infoDetailPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        infoDetailPanel.setBackground(Color.WHITE);
        infoDetailPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Account info
        infoDetailPanel.add(new JLabel("Username:"));
        infoDetailPanel.add(new JLabel("siswa_2024001"));
        infoDetailPanel.add(new JLabel("Role:"));
        infoDetailPanel.add(new JLabel("Siswa"));
        infoDetailPanel.add(new JLabel("Bergabung:"));
        infoDetailPanel.add(new JLabel("Juli 2023"));
        infoDetailPanel.add(new JLabel("Last Login:"));
        infoDetailPanel.add(new JLabel("01 April 2024, 14:30"));
        infoDetailPanel.add(new JLabel("Status Akun:"));
        infoDetailPanel.add(new JLabel("✅ Aktif"));
        infoDetailPanel.add(new JLabel("Akses Portal:"));
        infoDetailPanel.add(new JLabel("Read-Only"));
        
        // Security tips panel
        JPanel securityPanel = new JPanel(new BorderLayout());
        securityPanel.setBackground(new Color(241, 196, 15, 50));
        securityPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(241, 196, 15), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel securityTipsLabel = new JLabel("<html><b>🔒 Tips Keamanan:</b><br>" +
                "• Jangan berbagi password dengan orang lain<br>" +
                "• Gunakan password yang kuat (min. 6 karakter)<br>" +
                "• Logout setelah selesai menggunakan sistem<br>" +
                "• Laporkan jika ada aktivitas mencurigakan</html>");
        securityTipsLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        securityTipsLabel.setForeground(new Color(149, 117, 29));
        
        securityPanel.add(securityTipsLabel, BorderLayout.CENTER);
        
        accountInfoPanel.add(infoDetailPanel, BorderLayout.CENTER);
        accountInfoPanel.add(securityPanel, BorderLayout.SOUTH);
        
        // Add panels to split pane
        profilSplitPane.setLeftComponent(profileFormPanel);
        profilSplitPane.setRightComponent(accountInfoPanel);
        
        profilContentPanel.add(titleLabel, BorderLayout.NORTH);
        profilContentPanel.add(profilSplitPane, BorderLayout.CENTER);
    }
    
    /**
     * Method untuk setup UI additional properties
     */
    private void setupUI() {
        // Set icon
        try {
            setIconImage(Toolkit.getDefaultToolkit().getImage("assets/siswa_icon.png"));
        } catch (Exception e) {
            // Use default icon
        }
    }
    
    /**
     * Method untuk setup event handlers
     */
    private void setupEventHandlers() {
        // Window closing event
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                handleLogout();
            }
        });
    }
    
    /**
     * Method untuk start timer update waktu
     */
    private void startTimeUpdate() {
        timeUpdateTimer = new Timer(1000, e -> {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            timeLabel.setText("🕐 " + now.format(formatter));
        });
        timeUpdateTimer.start();
    }
    
    /**
     * Method untuk set active button
     */
    private void setActiveButton(JButton activeButton) {
        // Reset all buttons
        JButton[] buttons = {btnDashboard, btnTagihan, btnRiwayatPembayaran, btnProfil};
        for (JButton btn : buttons) {
            if (btn != btnLogout) {
                btn.setBackground(new Color(155, 89, 182));
            }
        }
        
        // Set active button
        activeButton.setBackground(new Color(125, 60, 152)); // Darker purple active color
    }
    
    /**
     * Method untuk switch content panel
     */
    private void showContent(String contentName) {
        CardLayout cardLayout = (CardLayout) contentPanel.getLayout();
        cardLayout.show(contentPanel, contentName);
    }
    
    // Content switcher methods
    private void showDashboardContent() {
        showContent("DASHBOARD");
        setActiveButton(btnDashboard);
    }
    
    private void showTagihanContent() {
        showContent("TAGIHAN");
        setActiveButton(btnTagihan);
    }
    
    private void showRiwayatContent() {
        showContent("RIWAYAT");
        setActiveButton(btnRiwayatPembayaran);
    }
    
    
    private void showProfilContent() {
        showContent("PROFIL");
        setActiveButton(btnProfil);
    }
    
    /**
     * Method untuk handle logout
     */
    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Apakah Anda yakin ingin logout?",
            "Konfirmasi Logout",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Stop timer
            if (timeUpdateTimer != null) {
                timeUpdateTimer.stop();
            }
            
            System.out.println("🎓 Siswa " + currentUser.getNamaLengkap() + " logout");
            
            // Tutup window dan kembali ke login
            dispose();
            new LoginPage().setVisible(true);
        }
    }
    
    /**
     * Action listener untuk button clicks
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnDashboard) {
            showDashboardContent();
        } else if (e.getSource() == btnTagihan) {
            showTagihanContent();
        } else if (e.getSource() == btnRiwayatPembayaran) {
            showRiwayatContent();
        } else if (e.getSource() == btnProfil) {
            showProfilContent();
        } else if (e.getSource() == btnLogout) {
            handleLogout();
        }
    }
    
    /**
     * Main method untuk testing dashboard
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
//                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
            } catch (Exception e) {
                System.out.println("⚠️ Tidak bisa set look and feel");
            }
            
            // Create dummy user untuk testing
//            User testUser = new User("siswa", "siswa123", "Siswa", "Ahmad Rizky Pratama", "081234567893");
//            new DashboardSiswa(testUser).setVisible(true);
        });
    }
}