package org.remus.simpleoauthconfigclient.service;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;

@Component
public class CommandPrompt implements PromptProvider {

    private Session session;

    public CommandPrompt(Session session) {

        this.session = session;
    }
    @Override
    public AttributedString getPrompt() {
        try {
            return session.isLoggedIn() ?
                    new AttributedString(new URL(session.getEndpoint()).getHost()+":> ", AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW)) :
                    new AttributedString("not-connected:> ", AttributedStyle.DEFAULT.foreground(AttributedStyle.RED));
        } catch (MalformedURLException e) {
            // ignore
        }
        return new AttributedString("not-connected:> ", AttributedStyle.DEFAULT.foreground(AttributedStyle.RED));

    }
}
