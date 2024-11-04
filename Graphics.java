import java.awt.Color;
import java.awt.Font;
//import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.imageio.ImageIO;

// Example to illustrate Java 2D graphics usage for Genetic Algorithms
// (C) Alexander del Pino

public class Graphics {

    public static void main(String[] args) {

        int height = 500;
        int width = 1000;
        int margin = 100; 
        
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2.setColor(Color.YELLOW);
        g2.fillRect(0, 0, width, height);

        int[] generations = new int[100]; 
        double[] bestFitness = new double[100];
        double[] averageFitness = new double[100];
        int dataCount = 0;

        try (BufferedReader br = new BufferedReader(new FileReader("fitness_data2.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (dataCount == 0) { 
                    dataCount++;
                    continue;
                }
                String[] values = line.split(",");
                generations[dataCount - 1] = Integer.parseInt(values[0]);
                bestFitness[dataCount - 1] = Double.parseDouble(values[1]);
                averageFitness[dataCount - 1] = Double.parseDouble(values[2]);
                dataCount++;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        
        g2.setColor(Color.BLACK);
        g2.drawLine(margin, height - margin, width - margin, height - margin); // X-axis
        g2.drawLine(margin, height - margin, margin, margin); // Y-axis

   
        g2.setColor(Color.RED);
        drawLine(g2, generations, bestFitness, width, height, margin);

        g2.setColor(Color.BLUE);
        drawLine(g2, generations, averageFitness, width, height, margin);

        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Serif", Font.PLAIN, 20));
        g2.drawString("Generations", width / 2, height - 10);
        g2.drawString("Fitness", 10, height / 2);

        g2.drawString("RED: Best Fitness", width - 150, 30);
        g2.drawString("BLUE: Avg Fitness", width - 150, 50);

        String folder = "/Users/manar/OneDrive - Technological University Dublin/GenAlgo";
        String filename = "Image.png";
        if (!new File(folder).exists()) {
            new File(folder).mkdirs();
        }

        try {
            ImageIO.write(image, "png", new File(folder + File.separator + filename));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    private static void drawLine(Graphics2D g2, int[] xValues, double[] yValues, int width, int height, int margin) {
        int maxValue = getMaxValue(yValues);
        for (int i = 0; i < xValues.length - 1; i++) {
            if (xValues[i] == 0 || xValues[i + 1] == 0) continue; // Skip if values are zero

            int x1 = margin + (xValues[i] * (width - 2 * margin) / xValues[xValues.length - 1]);
            int y1 = height - margin - (int)((yValues[i] / maxValue) * (height - 2 * margin));
            int x2 = margin + (xValues[i + 1] * (width - 2 * margin) / xValues[xValues.length - 1]);
            int y2 = height - margin - (int)((yValues[i + 1] / maxValue) * (height - 2 * margin));
            g2.drawLine(x1, y1, x2, y2);
        }
    }

    private static int getMaxValue(double[] values) {
        int max = Integer.MIN_VALUE;
        for (double value : values) {
            if (value > max) {
                max = (int) value;
            }
        }
        return max;
    }
}
