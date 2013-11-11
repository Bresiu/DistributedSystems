import java.util.ArrayList;
import java.util.List;

/**
 * Created by bresiu on 11.11.13.
 */
public class Chord {
    public List<Node> nodeList = new ArrayList<Node>();

    // Dolacz wezel do sieci
    public void join(String address) {
        Node node = new Node(address);
        nodeList.add(getCorrectPosition(address), node);
    }

    // Odlacz wezel od sieci, przekaz klucze nastepcy
    public void leave(String address) {
        for (int i = 0; i < size(); i++) {
            if (getNode(i).getAddress().equals(address)) {
                notifySuccessor(getSuccessor(getNode(i)), getNode(i).getKeys());
                nodeList.remove(i);
            }
        }
    }

    // Wstaw dane do sieci (do odpowiedniego wezla)
    public void insert(String fileName) {
        int hash = fileName.hashCode();
    }

    // Znajdz dane w sieci (odpowiedni wezel)
    public void find(String filename) {

    }

    private int getCorrectPosition(String address) {
        int hash = address.hashCode();
        int position = 0;
        while (hash < nodeList.get(position).getHash()) {
            position++;
        }
        return position;
    }

    private Node getNode(int i) {
        return nodeList.get(i);
    }

    private Node getSuccessor(Node node) {
        int position = nodeList.indexOf(node);
        if (position > 0) {
            return getNode(position - 1);
        }
        return node;
    }

    private void notifySuccessor(Node successor, List<Key> listOfKeys) {
        successor.keys.addAll(listOfKeys);
    }

    private int size() {
        return nodeList.size();
    }

    public static void main(String[] args) {

    }
}
