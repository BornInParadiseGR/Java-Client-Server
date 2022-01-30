
import java.io.Serializable;

/**
 *
 * @author icsd17010
 */
public class TextFile implements Serializable {
    private String name, contents, creator;
    private boolean publicForAll;

    public TextFile(String name, String contents, String creator, boolean publicForAll) {
        this.name = name;
        this.contents = contents;
        this.creator = creator;
        this.publicForAll = publicForAll;
    }

    public String getName() {
        return name;
    }

    public String getContents() {
        return contents;
    }

    public String getCreator() {
        return creator;
    }

    public boolean isPublicForAll() {
        return publicForAll;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void setPublicForAll(boolean publicForAll) {
        this.publicForAll = publicForAll;
    }
    
}
