import java.io.*;
import java.net.*;
import javax.swing.JOptionPane;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author icsd17010
 */
public class Client {
    private String username;
    private String SERVER_IP;
    private int SERVER_PORT;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    
    public Client(String username, String SERVER_IP, int SERVER_PORT) {
        this.username = username;
        this.SERVER_IP = SERVER_IP;
        this.SERVER_PORT = SERVER_PORT;
        this.connect();
    }

    public String getUsername() {
        return username;
    }
    
    public void connect() {
        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            Object obj = in.readObject();
            String message = "";
            if (obj instanceof String) {
                message = (String) obj;
                if (message.equals("username?")) {
                    out.writeObject(username);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void disconnect() {
        try {
            out.close();
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void createFile(String filename, String contents, boolean privateFile) {
        TextFile newTextFile = new TextFile(filename, contents, this.username, !privateFile);
        
        try {
            out.writeObject("create_file");

            String answer = (String) in.readObject();
            if (answer.equals("send_file")) {
                out.writeObject(newTextFile);
                out.flush();

                String result = (String) in.readObject();
                if (result.equals("success"))
                    JOptionPane.showMessageDialog(null, "Το αρχείο δημιουργήθηκε με επιτυχία!");
                else
                    JOptionPane.showMessageDialog(null, "Υπήρξε σφάλμα κατά την δημιουγία του αρχείου!");
            }
        } catch(IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void editFile(String name, String creator, String newContents, boolean privateFile) {
        TextFile textFileToEdit = new TextFile(name, newContents, creator, !privateFile);
        try {
            out.writeObject("edit_file");

            String answer = (String) in.readObject();
            if (answer.equals("send_file")) {
                out.writeObject(textFileToEdit);
                out.flush();
                Object result = in.readObject();
                if (result instanceof String && ((String) result).equals("success")) {
                    JOptionPane.showMessageDialog(null, "Το αρχείο τροποποιήθηκε με επιτυχία!");
                } else if (result instanceof String && ((String) result).equals("denied")) {
                    JOptionPane.showMessageDialog(null, "Δεν έχετε πρόσβαση σε αυτό το αρχείο!");
                } else if (result instanceof String && ((String) result).equals("notfound")) {
                    JOptionPane.showMessageDialog(null, "Το αρχείο δεν βρέθηκε!");
                } else {
                    JOptionPane.showMessageDialog(null, "Προέκυψε άγνωστο σφάλμα!");
                }
            }
        } catch(IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public TextFile readFile(String name, String creator) {
        String filename = creator + "_" + name;
        try {
            out.writeObject("read_file");

            String answer = (String) in.readObject();
            if (answer.equals("send_name_creator")) {
                out.writeObject(filename);
                out.flush();
                Object result = in.readObject();
                if (result instanceof TextFile) {
                    return (TextFile) result;
                } else if (result instanceof String && ((String) result).equals("denied")) {
                    JOptionPane.showMessageDialog(null, "Δεν έχετε πρόσβαση σε αυτό το αρχείο!");
                } else if (result instanceof String && ((String) result).equals("notfound")) {
                    JOptionPane.showMessageDialog(null, "Το αρχείο δεν βρέθηκε!");
                } else {
                    JOptionPane.showMessageDialog(null, "Προέκυψε άγνωστο σφάλμα!");
                }
            }
        } catch(IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
