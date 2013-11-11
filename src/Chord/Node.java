import java.util.ArrayList;
import java.util.List;

/**
 * Created by bresiu on 11.11.13.
 */
public class Node {
    public List<Key> keys;

    String address;
    int hash;

    public Node(String address) {
        this.address = address;
        this.hash = address.hashCode();
        this.keys = new ArrayList<Key>();
    }

    // Dodajemy klucz do wezla
    private void addKey(String nameFile) {
        keys.add(new Key(nameFile));
    }

    // Tworzymy nowy klucz identyczny z tym do usuniecia,
    // Nastepnie usuwamy identyczny z listy
    private void deleteKey(String nameFile) {
        Key keyToRemove = new Key(nameFile);
        keys.remove(keyToRemove);
    }

    public String getAddress() {
        return address;
    }

    public int getHash() {
        return hash;
    }

    public List<Key> getKeys() {
        return keys;
    }
}
