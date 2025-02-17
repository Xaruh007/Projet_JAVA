package gui;

import dao.UserDAO;
import dao.ParticipantDAO;
import model.Participant;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton;
    private Connection connection;

    public LoginFrame(Connection connection) {
        this.connection = connection;

        setTitle("Connexion");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel usernameLabel = new JLabel("Nom d'utilisateur :");
        usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Mot de passe :");
        passwordField = new JPasswordField();
        loginButton = new JButton("Se connecter");
        registerButton = new JButton("S'inscrire");

        mainPanel.add(usernameLabel);
        mainPanel.add(usernameField);
        mainPanel.add(passwordLabel);
        mainPanel.add(passwordField);
        mainPanel.add(new JLabel()); // Espace vide
        mainPanel.add(loginButton);
        mainPanel.add(new JLabel()); // Espace vide
        mainPanel.add(registerButton);

        add(mainPanel);

        loginButton.addActionListener(new LoginActionListener());
        registerButton.addActionListener(e -> {
            new RegisterFrame(connection).setVisible(true);
            dispose();
        });
    }

    private class LoginActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(
                    LoginFrame.this,
                    "Veuillez remplir tous les champs.",
                    "Erreur de connexion",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            try {
                UserDAO userDAO = new UserDAO(connection);
                String role = userDAO.authenticateUser(username, password);

                if (role != null) {
                    if ("ADMIN".equalsIgnoreCase(role)) {
                        new MainAdminFrame(connection, username).setVisible(true);
                    } else {
                        int userId = userDAO.getUserIdByUsername(username);
                        ParticipantDAO participantDAO = new ParticipantDAO(connection);
                        Participant participant = participantDAO.getParticipantByUserId(userId);

                        if (participant != null) {
                            System.out.println("Utilisateur connecté : " + participant.getNom());
                        }
                        new MainFrame(connection, username).setVisible(true);
                    }
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(
                        LoginFrame.this,
                        "Nom d'utilisateur ou mot de passe incorrect.",
                        "Erreur de connexion",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            } catch (SQLException | NoSuchAlgorithmException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                    LoginFrame.this,
                    "Une erreur s'est produite lors de la connexion.",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
}