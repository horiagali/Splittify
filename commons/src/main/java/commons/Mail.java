package commons;

public class Mail {
    private String toEmail;
    private String subject;
    private String text;

    /**
     * constructor
     * @param toEmail
     * @param subject
     * @param text
     */
    public Mail(String toEmail, String subject, String text) {
        this.toEmail = toEmail;
        this.subject = subject;
        this.text = text;
    }

    /**
     * empty constructor
     */
    public Mail() {
    }

    /**
     * getter for email
     * @return the email
     */
    public String getToEmail() {
        return toEmail;
    }

    /**
     * setter for email
     * @param toEmail the email
     */
    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }

    /**
     * getter for subject
     * @return the subject
     */

    public String getSubject() {
        return subject;
    }

    /**
     * setter for subject
     * @param subject
     */

    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * getter for text
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * setter for text
     * @param text
     */

    public void setText(String text) {
        this.text = text;
    }
}
