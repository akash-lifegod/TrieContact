import java.util.Objects;

public class Contact {
    private String name;
    private String phone;
    private String email;

    public Contact(String name, String phone, String email) {
        this.name = name != null ? name.trim() : "";
        this.phone = phone != null ? phone.trim() : "";
        this.email = email != null ? email.trim() : "";
    }

    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }

    public String toCSV() {
        return escape(name) + "," + escape(phone) + "," + escape(email);
    }
    private String escape(String s) {
        return s == null ? "" : s.replace(",", " ");
    }

    @Override
    public String toString() {
        return name + " - " + phone + " - " + email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Contact)) return false;
        Contact c = (Contact) o;
        return name.equalsIgnoreCase(c.name) &&
               phone.equals(c.phone) &&
               email.equalsIgnoreCase(c.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name.toLowerCase(), phone, email.toLowerCase());
    }
}
