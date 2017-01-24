package org.nem.chat.client.form;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;
import org.nem.chat.client.service.Chat;

/**
 *
 * @author Nemanja Marjanović
 */
public class LoginPanel extends JPanel {

    private final Chat chat;
    private final JTextField username;
    private final JButton login;
    private final JButton logout;

    public LoginPanel(final Chat chat) {

        this.chat = chat;
        this.username = new JTextField();
        this.login = new JButton("Login");
        this.logout = new JButton("Logout");

        this.login.addActionListener((e) -> {
            this.chat.login(this.username.getText());
            this.changeLoggedState(true);
        });

        this.logout.addActionListener((e) -> {
            this.chat.logout();
            this.changeLoggedState(false);
        });

        this.initComponents();
    }

    private void initComponents() {
        this.logout.setVisible(false);
        setLayout(new MigLayout("", "[grow][][]", "[grow]"));
        add(this.username, new CC().growX().growY());
        add(this.login, new CC().alignX("right").hideMode(1));
        add(this.logout, new CC().alignX("right").hideMode(1).wrap());
    }

    private void changeLoggedState(final boolean logged) {
        this.login.setVisible(!logged);
        this.username.setVisible(!logged);
        this.logout.setVisible(logged);
    }

}
