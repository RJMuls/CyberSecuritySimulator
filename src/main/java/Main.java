
import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.*;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class Main {
    private static int securityScore = 0;
    private static String accountPassword = "";
    private static String cloudPassword = "";
    private static String uName = "";
    private static String realName = "";
    private static boolean cloudBackupEnabled = false;
    private static int firewallScore = 0;
    private static int securityRunthroughs = 0;
    private static boolean twoFactorEnabled = false;
    private static boolean savedPassword = false;
    private static boolean hardwareBackupEnabled = false;
    private static ScoreDatabase database = new ScoreDatabase();
    private static List<String> userWeaknesses = new ArrayList<>();

    private static int evaluatePassword(String password) {
        if (password.length() >= 10 && password.matches(".*[A-Z].*") && password.matches(".*[0-9].*")
                && password.matches(".*[!@#$%^&*()].*"))
            return 25;
        if (password.length() >= 6 && password.matches(".*[A-Z].*") && password.matches(".*[0-9].*"))
            return 10;
        return 0;
    }

    private static void showScreen(JFrame frame, JPanel panel) {
        frame.getContentPane().removeAll();
        frame.add(panel);
        frame.revalidate();
        frame.repaint();
    }

    private static void showStartScreen(JFrame frame) {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        JLabel titleLabel = new JLabel("Cyber Security Simulator", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);
        JButton createAccountButton = new JButton("Create Account");
        createAccountButton.setToolTipText("Start creating your secure account with password protection");
        createAccountButton.addActionListener(e -> showNameEntryScreen(frame));
        createAccountButton.setPreferredSize(new Dimension(200, 40));

        buttonPanel.add(createAccountButton);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        showScreen(frame, mainPanel);
    }

    private static void showNameEntryScreen(JFrame frame) {
        JPanel panel = new JPanel(new GridLayout(3, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel instructionLabel = new JLabel(
                "<html><center>Please enter your real name.<br>This will be used to save your score in the database.</center></html>");
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JTextField nameField = new JTextField();
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));

        panel.add(instructionLabel);
        panel.add(new JLabel("Your Name:"));
        panel.add(nameField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Enter Your Name", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String enteredName = nameField.getText().trim();
            if (enteredName.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                showNameEntryScreen(frame);
                return;
            }
            realName = enteredName;
            showCreateAccountScreen(frame);
        } else {
            showStartScreen(frame);
        }
    }

    private static void showWelcomeScreen(JFrame frame) {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        // Title
        JLabel titleLabel = new JLabel("Security Guidelines", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        // Content panel with centered text
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] guidelines = {
                "Strong Password Requirements:",
                "• 10 characters long, 1 uppercase, 1 number, 1 special character",
                "",
                "Security Considerations:",
                "• Saving your password will reduce your security score",
                "• Backing up your data increases your security score",
                "• Using the same password for account and cloud will penalize you",
                "",
                "Additional Security Features:",
                "• Two Factor Authentication increases your security score",
                "• Security questions provide additional protection",
                "",
                "Firewall Configuration:",
                "• Tests your knowledge of network security",
                "• Unnecessary ports reduce your score",
                "• Essential ports increase your score"
        };

        for (String guideline : guidelines) {
            JLabel label = new JLabel(guideline, SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.PLAIN, 14));
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            if (guideline.contains("Requirements:") || guideline.contains("Considerations:") ||
                    guideline.contains("Features:") || guideline.contains("Configuration:")) {
                label.setFont(new Font("Arial", Font.BOLD, 16));
                label.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
            } else if (!guideline.isEmpty()) {
                label.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
            } else {
                label.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
            }
            contentPanel.add(label);
        }

        // Logout button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JButton logoutButton = new JButton("Start New Session");
        logoutButton.setPreferredSize(new Dimension(200, 40));
        logoutButton.addActionListener(e -> {
            securityScore = 0;
            accountPassword = "";
            cloudPassword = "";
            uName = "";
            realName = "";
            cloudBackupEnabled = false;
            firewallScore = 0;
            securityRunthroughs = 0;
            securityScore = 0;
            twoFactorEnabled = false;
            savedPassword = false;
            hardwareBackupEnabled = false;
            userWeaknesses.clear();
            showStartScreen(frame);
        });

        buttonPanel.add(logoutButton);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        showScreen(frame, mainPanel);
    }

    private static void showCreateAccountScreen(JFrame frame) {
        JPanel panel = new JPanel(new GridLayout(7, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel warningLabel = new JLabel("<html><div style='text-align: center; color: red; font-weight: bold;'>" +
                "WARNING<br>" +
                "DO NOT use your real username or password!<br>" +
                "This is a simulation - use fake credentials only.</div></html>");
        warningLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();

        panel.add(warningLabel);
        panel.add(new JLabel("Enter Username (FAKE):"));
        panel.add(usernameField);
        panel.add(new JLabel("Enter Password (FAKE):"));
        panel.add(passwordField);
        panel.add(new JLabel("Confirm Password:"));
        panel.add(confirmPasswordField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Create Account", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            if ((new String(passwordField.getPassword())).equals(new String(confirmPasswordField.getPassword()))) {
                String username = usernameField.getText();
                accountPassword = new String(passwordField.getPassword());

                if (username.isEmpty() || accountPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Username and password must not be empty.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    showCreateAccountScreen(frame);
                    return;
                }

                try {
                    securityScore += evaluatePassword(accountPassword);

                    uName = username;
                    twoFactorEnabled = false;

                    showPasswordSavePrompt(frame);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(frame, "Error creating account.", "Error", JOptionPane.ERROR_MESSAGE);
                    showCreateAccountScreen(frame);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Passwords must match.", "Error", JOptionPane.ERROR_MESSAGE);
                showStartScreen(frame);
            }
        } else {
            showStartScreen(frame);
        }
    }

    private static void showPasswordSavePrompt(JFrame frame) {
        int savePassword = JOptionPane.showConfirmDialog(frame,
                "Do you want to save your password?", "Save Password", JOptionPane.YES_NO_OPTION);

        if (savePassword == JOptionPane.YES_OPTION) {
            securityScore -= 10;
            savedPassword = true;
            userWeaknesses.add("Saved password (security risk)");
        } else {
            securityScore += 10;
        }

        showBackupPrompt(frame);
    }

    private static void showBackupPrompt(JFrame frame) {
        int externalBackup = JOptionPane.showConfirmDialog(frame,
                "Do you want to back up your data externally?", "External Backup", JOptionPane.YES_NO_OPTION);
        if (!(externalBackup == JOptionPane.YES_OPTION)) {
            int backupData = JOptionPane.showConfirmDialog(frame,
                    "Do you want to back up your data in the cloud?", "Cloud Backup", JOptionPane.YES_NO_OPTION);

            if (backupData == JOptionPane.YES_OPTION) {
                cloudBackupEnabled = true;
                JPasswordField cloudPasswordField = new JPasswordField();
                JPanel cloudPanel = new JPanel(new GridLayout(2, 2));
                cloudPanel.add(new JLabel("Enter Cloud Password:"));
                cloudPanel.add(cloudPasswordField);

                int result = JOptionPane.showConfirmDialog(frame, cloudPanel,
                        "Create Cloud Password", JOptionPane.OK_CANCEL_OPTION);

                if (result == JOptionPane.OK_OPTION) {
                    cloudPassword = new String(cloudPasswordField.getPassword());
                    if (cloudPassword.isEmpty()) {
                        JOptionPane.showMessageDialog(frame,
                                "Password must not be empty. Backup failed.", "Error", JOptionPane.ERROR_MESSAGE);
                        cloudBackupEnabled = false;
                        securityScore -= 10;
                    } else {
                        securityScore += evaluatePassword(cloudPassword) / 2.5;
                        if (cloudPassword.equals(accountPassword)) {
                            securityScore -= 15;
                            JOptionPane.showMessageDialog(frame,
                                    "Warning: Cloud password is the same as account password! Security Score penalized.");
                        }
                    }
                }
                JOptionPane.showMessageDialog(frame, "Account created successfully! Security Score: " + securityScore);
                showSecurityConfigScreen(frame);
            } else {
                cloudBackupEnabled = false;
                securityScore -= 10;
                userWeaknesses.add("No backup enabled (data loss risk)");
                JOptionPane.showMessageDialog(frame, "Account created successfully! Security Score: " + securityScore);
                showSecurityConfigScreen(frame);
            }
        } else {
            hardwareBackupEnabled = true;
            securityScore += 10;
            JOptionPane.showMessageDialog(frame, "Account created successfully! Security Score: " + securityScore);
            showSecurityConfigScreen(frame);
        }
    }

    private static void showSecurityConfigScreen(JFrame frame) {
        securityRunthroughs++;
        JPanel securityPanel = new JPanel(new GridLayout(0, 1));
        JLabel securityLabel = new JLabel("Security Configuration");
        securityLabel.setToolTipText("Configure additional security features to protect your account");
        securityPanel.add(securityLabel);

        if (!twoFactorEnabled) {
            show2FAPrompt(frame);
        } else {
            showSecurityQuestionPrompt(frame);
        }
    }

    private static void show2FAPrompt(JFrame frame) {
        int enable2FA = JOptionPane.showConfirmDialog(frame,
                "Enable Two-Factor Authentication (2FA)?", "Security Setup", JOptionPane.YES_NO_OPTION);
        if (enable2FA == JOptionPane.YES_OPTION) {
            twoFactorEnabled = true;
            securityScore += 10;
        } else {
            userWeaknesses.add("2FA not enabled (authentication weakness)");
        }
        showSecurityQuestionPrompt(frame);
    }

    private static void showSecurityQuestionPrompt(JFrame frame) {
        int addSecurityQuestion = JOptionPane.showConfirmDialog(frame,
                "Set up a security question?", "Security Setup", JOptionPane.YES_NO_OPTION);

        if (addSecurityQuestion == JOptionPane.YES_OPTION) {
            showSecurityQuestionSetupScreen(frame);
        } else {
            showFirewallScreen(frame);
        }
    }

    private static void showSecurityQuestionSetupScreen(JFrame frame) {
        JTextField question = new JTextField();
        JTextField answer = new JTextField();

        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("Security Question:"));
        panel.add(question);
        panel.add(new JLabel("Answer:"));
        panel.add(answer);

        int result = JOptionPane.showConfirmDialog(frame, panel,
                "Set Security Question", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            if (question.getText().isEmpty() || answer.getText().isEmpty()) {
                JOptionPane.showMessageDialog(frame,
                        "Security question and answer must not be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                showSecurityQuestionSetupScreen(frame);
            } else {
                securityScore += 10;
                JOptionPane.showMessageDialog(frame, "Security question set up successfully.");
                showWifiScreen(frame);
            }
        } else {
            showWifiScreen(frame);
        }
    }

    private static void showWifiScreen(JFrame frame) {
        JTextField wifiInput = new JPasswordField();
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Wifi Configuration"));
        panel.add(wifiInput);

        int result = JOptionPane.showConfirmDialog(frame, panel,
                "S3curePassw*rd", JOptionPane.OK_CANCEL_OPTION);

        if (wifiInput.getText().equals("S3curePassw*rd")) {
            securityScore += 10;
            JOptionPane.showMessageDialog(frame, "Wifi configured successfully.");
            showFirewallScreen(frame);
        } else {
            JOptionPane.showMessageDialog(frame,
                    "Wrong Password", "Error", JOptionPane.ERROR_MESSAGE);
            showWifiScreen(frame);
        }
    }

    private static void showFirewallScreen(JFrame frame) {
        firewallScore = -5;
        LinkedHashMap<String, Integer> ports = new LinkedHashMap<>();
        LinkedHashMap<String, String> portDescriptions = new LinkedHashMap<>();

        // Define ports and their security implications
        ports.put("HTTP (80)", -5);
        portDescriptions.put("HTTP (80)", "Unsecured web traffic - recommended to use HTTPS instead");

        ports.put("HTTPS (443)", 5);
        portDescriptions.put("HTTPS (443)", "Secure encrypted web traffic - recommended for web services");

        ports.put("FTP (21)", -5);
        portDescriptions.put("FTP (21)", "Unsecured file transfer - vulnerable to attacks");

        ports.put("SSH (22)", 0);
        portDescriptions.put("SSH (22)", "Secure shell access - necessary for remote administration if needed");

        ports.put("SMTP (25)", -5);
        portDescriptions.put("SMTP (25)", "Email server port - should be secured if needed");

        ports.put("Remote Desktop (3389)", -5);
        portDescriptions.put("Remote Desktop (3389)", "Remote access - high security risk if exposed");

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        HashMap<String, JCheckBox> checkBoxes = new HashMap<>();

        for (String service : ports.keySet()) {
            JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel serviceLabel = new JLabel(service);
            serviceLabel.setToolTipText(portDescriptions.get(service));

            JCheckBox box = new JCheckBox("Allow", true);
            box.setToolTipText(portDescriptions.get(service));

            itemPanel.add(serviceLabel);
            JPanel checkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            checkPanel.add(box);

            panel.add(itemPanel);
            panel.add(checkPanel);
            checkBoxes.put(service, box);
        }

        // Enable tooltips globally
        ToolTipManager.sharedInstance().setInitialDelay(100);
        ToolTipManager.sharedInstance().setDismissDelay(10000);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Configure Firewall", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            processFirewallSettings(checkBoxes, ports);

            // Auto-save the score
            int totalScore = securityScore + firewallScore;
            database.addUserScore(realName, totalScore, userWeaknesses);

            JOptionPane.showMessageDialog(frame,
                    "Firewall setup complete! Your score: " + firewallScore +
                            "\nTotal Score: " + totalScore + " (automatically saved)");
            showAccountDetails(frame);
            showDashboard(frame);
        }
    }

    private static void processFirewallSettings(HashMap<String, JCheckBox> checkBoxes,
            LinkedHashMap<String, Integer> ports) {
        firewallScore = 0;
        for (String service : ports.keySet()) {
            boolean allowed = checkBoxes.get(service).isSelected();
            int scoreImpact = ports.get(service);
            if (!allowed) {
                firewallScore += Math.abs(scoreImpact);
            } else {
                firewallScore += (scoreImpact > 0 ? scoreImpact : 0);
            }
        }
    }

    private static void showDashboard(JFrame frame) {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JLabel titleLabel = new JLabel("Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        JButton accountButton = new JButton("View Account Details");
        accountButton.setToolTipText("See your account security status and settings");
        JButton startGameButton = new JButton("Change Firewall Settings");
        startGameButton.setToolTipText("Configure network security through port management");
        JButton restartButton = new JButton("Restart");
        restartButton.setToolTipText("Start over with a new configuration");
        JButton viewScoresButton = new JButton("View High Scores");
        viewScoresButton.setToolTipText("View top scores from all users");
        JButton clearScoresButton = new JButton("Clear Scoreboard");
        clearScoresButton.setToolTipText("Clear all saved scores from the database");

        accountButton.setPreferredSize(new Dimension(250, 40));
        startGameButton.setPreferredSize(new Dimension(250, 40));
        restartButton.setPreferredSize(new Dimension(250, 40));
        viewScoresButton.setPreferredSize(new Dimension(250, 40));
        clearScoresButton.setPreferredSize(new Dimension(250, 40));

        accountButton.addActionListener(e -> showAccountDetails(frame));
        startGameButton.addActionListener(e -> showFirewallScreen(frame));
        restartButton.addActionListener(e -> showWelcomeScreen(frame));
        viewScoresButton.addActionListener(e -> showHighScores(frame));
        clearScoresButton.addActionListener(e -> clearScoreboard(frame));

        buttonPanel.add(accountButton, gbc);
        buttonPanel.add(startGameButton, gbc);
        buttonPanel.add(viewScoresButton, gbc);
        buttonPanel.add(clearScoresButton, gbc);
        buttonPanel.add(restartButton, gbc);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        showScreen(frame, mainPanel);
    }

    private static void showAccountDetails(JFrame frame) {
        StringBuilder details = new StringBuilder();
        details.append("<html><ul>");
        details.append("<li><b>Username:</b> ").append(uName).append("</li>");
        details.append("<li><b>2FA Enabled:</b> ").append(twoFactorEnabled).append("</li>");
        details.append("<li><b>Saved Password:</b> ").append(savedPassword ? "Yes" : "No").append("</li>");
        details.append("<li><b>Cloud Backup Enabled:</b> ").append(cloudBackupEnabled ? "Yes" : "No").append("</li>");
        details.append("<li><b>Security Score:</b> ").append(securityScore).append("</li>");
        details.append("<li><b>Firewall Score:</b> ").append(firewallScore).append("</li>");
        details.append("<li><b>Total Score:</b> ").append(securityScore + firewallScore).append("</li>");
        if (!userWeaknesses.isEmpty()) {
            details.append("<li><b>Areas for Improvement:</b><ul>");
            for (String weakness : userWeaknesses) {
                details.append("<li>").append(weakness).append("</li>");
            }
            details.append("</ul></li>");
        }
        details.append("</ul></html>");
        JOptionPane.showMessageDialog(frame, details.toString(), "Account Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void saveCurrentScore(JFrame frame) {
        if (realName.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No user logged in!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int totalScore = securityScore + firewallScore;
        database.addUserScore(realName, totalScore, userWeaknesses);
        JOptionPane.showMessageDialog(frame,
                "Score saved successfully for " + realName + "!\nTotal Score: " + totalScore,
                "Score Saved", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void showHighScores(JFrame frame) {
        List<UserScore> topScores = database.getTopScores(10);

        if (topScores.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No scores saved yet!", "High Scores",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder scores = new StringBuilder();
        scores.append("<html><h3>Top 10 High Scores</h3><ol>");

        for (UserScore score : topScores) {
            scores.append("<li><b>").append(score.getUsername()).append(":</b> ")
                    .append(score.getFinalScore()).append(" points");
            if (!score.getWeaknesses().isEmpty()) {
                scores.append("<br><small>Areas that need improvement: ").append(score.getWeaknesses().size())
                        .append("</small>");
            }
            scores.append("</li>");
        }
        scores.append("</ol></html>");

        JOptionPane.showMessageDialog(frame, scores.toString(), "High Scores", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void clearScoreboard(JFrame frame) {
        // First prompt for admin password
        JPasswordField passwordField = new JPasswordField();
        JPanel passwordPanel = new JPanel(new GridLayout(2, 1));
        passwordPanel.add(new JLabel("Enter admin password:"));
        passwordPanel.add(passwordField);
        
        int passwordResult = JOptionPane.showConfirmDialog(frame, passwordPanel,
                "Admin Authentication Required", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (passwordResult != JOptionPane.OK_OPTION) {
            return; // User cancelled
        }
        
        String enteredPassword = new String(passwordField.getPassword());
        if (!enteredPassword.equals("Room230")) {
            JOptionPane.showMessageDialog(frame, "Incorrect admin password!", 
                    "Access Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // If password is correct, proceed with confirmation
        int confirm = JOptionPane.showConfirmDialog(frame,
                "Are you sure you want to clear all scores from the database?\nThis action cannot be undone.",
                "Clear Scoreboard", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            database.clearDatabase();
            JOptionPane.showMessageDialog(frame, "Scoreboard has been cleared successfully!",
                    "Scoreboard Cleared", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private static void setUIStyle() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("Button.arc", 15);
            UIManager.put("Button.background", new Color(70, 130, 180));
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Panel.background", new Color(240, 240, 240));
            UIManager.put("OptionPane.background", new Color(240, 240, 240));
            UIManager.put("OptionPane.messageFont", new Font("Arial", Font.PLAIN, 14));
            UIManager.put("Label.font", new Font("Arial", Font.PLAIN, 14));
            UIManager.put("Button.font", new Font("Arial", Font.BOLD, 14));
            UIManager.put("TextField.font", new Font("Arial", Font.PLAIN, 14));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        setUIStyle();
        JFrame frame = new JFrame("Cyber Security Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 700);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(240, 240, 240));
        showStartScreen(frame);
        frame.setVisible(true);
    }
}
