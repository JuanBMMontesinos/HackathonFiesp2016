package online.f4hupcaregroup.peoplecare;

import java.io.Serializable;

public class Client implements Serializable {

    private String warning;

    public String getShowWarning() {
        return showWarning;
    }

    public void setShowWarning(String showWarning) {
        this.showWarning = showWarning;
    }

    private String showWarning;

    public String getWarning() {
        return warning;
    }

    public void setWarning(String warning) {
        this.warning = warning;
    }
}
