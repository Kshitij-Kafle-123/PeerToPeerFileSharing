package data;

import java.io.Serializable;

public class Message implements Serializable {

    private static final long serialVersionUID = 1L;
    public String type, sender, content, recipient, key;

    public Message(String type, String sender, String content, String recipient, String key) {
        this.type = type;
        this.sender = sender;
        this.content = content;
        this.recipient = recipient;
        this.key = key;
    }

    @Override
    public String toString() {
        return "Message{" +
                "type='" + type + '\'' +
                ", sender='" + sender + '\'' +
                ", content='" + content + '\'' +
                ", recipient='" + recipient + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}

