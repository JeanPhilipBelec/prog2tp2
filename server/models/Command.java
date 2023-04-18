package server.models;

import java.io.Serializable;

public class Command implements Serializable {
    String command;
    String arguments;

    public Command(String command, String arguments){
        this.command = command;
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        return (this.command + " " + this.arguments);
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setArguments(String arguments) {
        this.arguments = arguments;
    }

    public String getArguments() {
        return arguments;
    }

    public String getCommand() {
        return command;
    }
}
