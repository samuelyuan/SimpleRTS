package map;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import utils.PathResolver;
import utils.Logger;

public class FileUtils {
    public static ArrayList<String> parseFile(String filename) {
        ArrayList<String> data = new ArrayList<String>();
        try {
            String resolvedPath = PathResolver.resolveMapPath(filename);
            try (BufferedReader br = new BufferedReader(new FileReader(resolvedPath))) {
                String line;
                while ((line = br.readLine()) != null) {
                    data.add(line);
                }
            }
        } catch (Exception e) {
            Logger.error("Failed to load file: " + filename + ", error: " + e.getMessage());
            e.printStackTrace();
        }
        return data;
    }

    public static void saveFile(ArrayList<String> data, File file) {
        try (PrintWriter out = new PrintWriter(file)) {
            for (String str : data) {
                out.println(str);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Overload for saving images
    public static void saveFile(BufferedImage im, File file) throws IOException {
        ImageIO.write(im, "png", file);
    }
}