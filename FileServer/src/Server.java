import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author icsd17010
 */
public class Server {
    /* Sockets kai streams */
    private ServerSocket serverSocket;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    /* pylh tou server */
    private int PORT;
    private boolean running, connected;
    private String connectedUsername;

    public Server(int PORT) {
        this.PORT = PORT;
    }
    
    public void serverLoop() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server up and running!");
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        /* Kyrio loop tou server */
        while (running) {
            try {
                socket = serverSocket.accept();
                System.out.println("Incoming connection.");

                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
                connected = true;

                System.out.println("Client connected!");
                /* o server zhta username */
                out.writeObject("username?");
                out.flush();

                /* o server diabazei to username san object apo to stream kai to kanei cast se String*/
                Object obj = in.readObject();
                if (obj instanceof String)
                    connectedUsername = (String) obj;
                System.out.println("Username is: " + connectedUsername);

                /* loop pelath */
                while (connected) {
                    /* lambanoume leitourgia */
                    String option = "";
                    obj = in.readObject();
                    if (obj instanceof String)
                        option = (String) obj;

                    /* analoga thn leitourgia akolouthoume to katallhlo protokolo */
                    switch (option) {
                        /* dhmiourgia arxeiou */
                        case "create_file":
                        {
                            /* O server zhta antikeimeno arxeiou */
                            out.writeObject("send_file");
                            out.flush();
                            /* O server lambanei antikeimeno TextFile */
                            Object fileObject = in.readObject();
                            if (fileObject instanceof TextFile) {
                                TextFile newTextFile = (TextFile) fileObject;
                                /* dhmoiyrgia arxeioy kai apostolh apotelesmatos */
                                if (createFile(newTextFile)) {
                                    out.writeObject("success");
                                } else {
                                    out.writeObject("fail");
                                }
                                out.flush();
                            }
                            break;
                        }
                        /* tropopoihsh arxeiou */
                        case "edit_file":
                        {
                            /* O server zhta onoma arxeioy kai shmioyrgoy */
                            out.writeObject("send_file");
                            out.flush();
                            /* O server lambanei ta stoixeia */
                            Object fileObject = in.readObject();
                            if (fileObject instanceof TextFile) {
                                TextFile file = (TextFile) fileObject;
                                /* tropopoihsh arxeiou */
                                String result = editFile(file);
                                out.writeObject(result);
                                out.flush();
                            }
                            break;
                        }
                        /* anagnwsh arxeiou */
                        case "read_file":
                        {
                            /* O server zhta onoma arxeioy kai shmioyrgoy */
                            out.writeObject("send_name_creator");
                            out.flush();
                            /* O server lambanei ta stoixeia */
                            Object filenameObject = in.readObject();
                            if (filenameObject instanceof String) {
                                String filename = (String) filenameObject;
                                /* diabasma arxeiou */
                                TextFile textfile = readFile(filename);
                                if (textfile != null) {
                                    if (textfile.getCreator().equals(connectedUsername) || textfile.isPublicForAll()) {
                                        out.writeObject(textfile);
                                    } else {
                                        out.writeObject("denied");
                                    }
                                } else {
                                    out.writeObject("notfound");
                                }
                                out.flush();
                            }
                            break;
                        }
                        /* H leitourgia den anagnwrizetai */
                        default:
                        {
                            connected = false;
                        }
                    }
                }
                /* kleinoume sockets kai streams */
                out.close();
                socket.close();
            } catch (IOException ex) {
                connected = false;
                System.out.println("Client disconnected!");
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    /* Methodos poy ksekina ton server */
    public void startServer() {
        running = true;
        connected = false;
        serverLoop();
    }
    /* Methodos poy stamata ton server */
    public void stopServer() {
        running = false;
    }
    /* Methodos poy dhmoiyrgei neo arxeio */
    public boolean createFile(TextFile newTextFile) {
        try {
            /* to onoma einai ths morfhws dhmioyrgos_onomaarxeiou.dat */
            String filename = new String(newTextFile.getCreator() + "_" + newTextFile.getName() + ".dat");
            FileOutputStream fout = new FileOutputStream(new File(filename));
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            /* grafoume to antikeimeno sto arxeio */
            oos.writeObject(newTextFile);
            oos.flush();
            /* kleinoyme to object output stream kai to arxeio */
            oos.close();
            fout.close();
            /* Epityxhs shmioyrgia arxeiou */
            System.out.println("New File created: " + filename);
            return true;
        } catch(Exception e) {
            System.out.println("Error creating file!");
        }
        /* apotyxia dhmioyrgias arxeioy */
        return false;
    }
    /* Methodos poy antikathista to arxeio me kainoyrio (tropopoihsh) */
    public String editFile(TextFile replaceFile) {
        try {
            /* to onoma einai ths morfhws dhmioyrgos_onomaarxeiou.dat */
            String filename = replaceFile.getCreator() + "_" + replaceFile.getName() + ".dat";
            System.out.println("Searching for: " + filename);
            FileInputStream fin = new FileInputStream(new File(filename));
            ObjectInputStream ois = new ObjectInputStream(fin);
            /* Anagnwsh object apo arxeio */
            TextFile textFile = (TextFile) ois.readObject();
            if (!textFile.getCreator().equals(connectedUsername)) {
                return "denied";
            }
            /* kleinoume to object input stream  */
            ois.close();
            fin.close();
            /* Epityxhs anagnwsh arxeiou proxwrame sthn antikatastash */
            FileOutputStream fout = new FileOutputStream(new File(filename));
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            /* Egrafh neou object sto arxeio */
            oos.writeObject(replaceFile);
            oos.flush();
            /* Kleinoume to object output stream */
            oos.close();
            fout.close();
            return "success";
        } catch(Exception e) {
            System.out.println("Error editing file!");
        }
        /* Sfalma kata thn anagnwsh arxeioy */
        return "notfound";
    }
    /* Methodos poy diabazei object typoy TextFile apo arxeio */
    public TextFile readFile(String filename) {
        try {
            /* to onoma einai ths morfhws dhmioyrgos_onomaarxeiou.dat */
            filename = filename + ".dat";
            System.out.println("Searching for: " + filename);
            FileInputStream fin = new FileInputStream(new File(filename));
            ObjectInputStream ois = new ObjectInputStream(fin);
            /* Anagnwsh object apo arxeio */
            TextFile textFile = (TextFile) ois.readObject();
            /* kleinoume to object input stream  */
            ois.close();
            fin.close();
            /* Epityxhs anagnwsh arxeiou */
            return textFile;
        } catch(Exception e) {
            System.out.println("Error reading file!");
        }
        /* Sfalma kata thn anagnwsh arxeioy */
        return null;
    }
}
