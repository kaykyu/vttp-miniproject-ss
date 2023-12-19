package kq.miniproject.projectss.model;

import java.io.Serializable;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class Person implements Serializable {

    @Size(min = 3, max = 20, message = "Name must be between 3 and 20 characters")
    @NotEmpty(message = "Name must not be blank")
    private String name;

    @Email(message = "Must be valid Email format")
    @NotEmpty(message = "Email must not be blank")
    @Size(max = 50, message = "Email must be less than 50 characters")
    private String email;

    private String uniqueId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public Person() {
    }

    public Person(String name, String email) {
        this.name = name;
        this.email = email;
        this.uniqueId = null;
    }

    @Override
    public String toString() {
        return "%s - %s: %s".formatted(uniqueId, name, email);
    }

}
