import java.nio.file.*;
import java.io.*;
import java.util.*;

public class ContactManager {
    private final List<Contact> contacts = new ArrayList<>();
    private final Trie trie = new Trie();
    private final Path filePath;

    public ContactManager(String fileName) {
        this.filePath = Paths.get(fileName);
        loadFromFile();
    }

    private void loadFromFile() {
        if (!Files.exists(filePath)) return;
        try (BufferedReader br = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 3) {
                    Contact c = new Contact(parts[0].trim(), parts[1].trim(), parts[2].trim());
                    contacts.add(c);
                    trie.insert(c.getName(), c);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized boolean addContact(Contact c) {
        for (Contact existing : contacts) {
            if (existing.getName().equalsIgnoreCase(c.getName()) &&
                existing.getPhone().equals(c.getPhone())) {
                return false;
            }
        }
        contacts.add(c);
        trie.insert(c.getName(), c);
        appendToFile(c);
        return true;
    }

    private void appendToFile(Contact c) {
        try {
            Path parent = filePath.getParent();
            if (parent != null) Files.createDirectories(parent);
            try (BufferedWriter bw = Files.newBufferedWriter(filePath, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                bw.write(c.toCSV());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Contact> search(String prefix) { return trie.searchPrefix(prefix); }

    public List<Contact> getAllContacts() { return Collections.unmodifiableList(contacts); }
}
