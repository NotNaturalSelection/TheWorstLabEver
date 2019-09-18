package source;

import java.io.Serializable;

public class Command implements Serializable {
    private static final long serialVersionUID = 1024;

    private String command;
    private Protagonist object;
    private String fileContent;
    private String login;
    private String password;

    public Command(String command, Protagonist object, String file) {
        this.command = command;
        this.object = object;
        this.fileContent = file;
    }

    public String getStringCommand() {
        return command;
    }

    public void setStringCommand(String command) {
        this.command = command;
    }

    public Protagonist getObject() {
        return object;
    }

    public void setObject(Protagonist object) {
        this.object = object;
    }

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String file) {
        this.fileContent = file;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
