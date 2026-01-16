
import java.io.*;

public class SayExample {

    public static void sayPhrase(String word) {
        try {
            // Use bash to run the macOS "say" command
            ProcessBuilder builder = new ProcessBuilder("bash", "-c", "say " + word);
            builder.start(); // no need to read output; "say" speaks it aloud
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
