package kq.miniproject.projectss.model;

public class Email {

    private Person sendTo;
    private String message;

    public Person getSendTo() {
        return sendTo;
    }

    public void setSendTo(Person sendTo) {
        this.sendTo = sendTo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Email(Person sendTo, String message) {
        this.sendTo = sendTo;
        this.message = message;
    }

    public Email() {
    }

}
