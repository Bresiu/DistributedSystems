/**
 * Created by bresiu on 11.11.13.
 */
public class Key {

    String fileName;
    int hash;

    public Key(String fileName) {
        this.fileName = fileName;
        this.hash = this.fileName.hashCode();
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getHash() {
        return hash;
    }

    public void setHash(int hash) {
        this.hash = hash;
    }
}
