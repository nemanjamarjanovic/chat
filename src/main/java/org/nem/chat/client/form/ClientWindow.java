package org.nem.chat.client.form;

import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.Timer;
import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;
import org.nem.chat.client.service.Chat;


/**
 *
 * @author Nemanja Marjanović
 */
public class ClientWindow extends JFrame {

    private final Chat chat;
    private final LoginPanel loginPanel;
    private final UsersPanel usersPanel;
    private final SessionsPanel sessionsPanel;

    public ClientWindow() throws IOException {

        this.chat = new Chat();
        this.loginPanel = new LoginPanel(this.chat);
        this.usersPanel = new UsersPanel(this.chat);
        this.sessionsPanel = new SessionsPanel(this.chat);

        new Timer(100, (e) -> {
            setTitle(this.chat.title());
        }).start();

        this.initComponents();
    }

    private void initComponents() {
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLayout(new MigLayout("", "[150][grow]", "[30][grow]"));
        add(this.loginPanel, new CC().spanX(2).growX().wrap());
        add(this.usersPanel, new CC().growX().growY());
        add(this.sessionsPanel, new CC().growX().growY());

    }

    @Override
    public void dispose() {
        this.chat.logout();
        super.dispose();
    }

    public static void main(String[] args) throws Exception {
        new ClientWindow().setVisible(true);
    }

}
