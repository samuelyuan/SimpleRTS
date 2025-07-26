package map;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class MapFileLoader {
    public static ArrayList<String> parseFile(String filename) {
        ArrayList<String> data = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = br.readLine()) != null) {
                data.add(line);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public static void saveFile(ArrayList<String> data, File file) {
        try {
            PrintWriter out = new PrintWriter(file);
            for (String str : data) {
                out.println(str);
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Overload for saving images
    public static void saveFile(BufferedImage im, File file) throws IOException {
        ImageIO.write(im, "png", file);
    }
} 