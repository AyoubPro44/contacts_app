package boulahri.app.models;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.beans.property.SimpleStringProperty;

public record Contact(SimpleStringProperty name, SimpleStringProperty telephone, SimpleStringProperty email, SimpleStringProperty address) {
    public boolean isValidEmail() {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email.get());
        return email != null && matcher.matches();
    }

    public String toString() {
        return name.get() + ";" + telephone.get() + ";" + email.get() + ";" + address.get();
    }
}
