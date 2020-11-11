package testing;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Ejemplo para guardar una imagen en un fichero desde java. Código original de
 * webo_cs
 * @see "http://chuwiki.chuidiang.org/index.php?title=Guardar_imagen_en_fichero"
 */
public class GuardarGraficoComponente {
    /**
     * Main de ejemplo. Crea un BufferedImage, hace un dibujito y lo guarda en
     * un fichero jpg.
     *
     * @param args arguments
     */
    public static void main(String[] args) {
        File fichero = new File("foto.jpg");
        String formato = "jpg";

        // Creamos la imagen para dibujar en ella.
        BufferedImage imagen = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);

        // Hacemos el dibujo
        Graphics g = imagen.getGraphics();
        g.setColor(Color.red);
        g.fillRect(50, 50, 100, 100);
        g.setColor(Color.green);
        g.fillRect(0, 0, 50, 50);
        g.setColor(Color.yellow);
        g.fillOval(25, 25, 50, 50);

        // Escribimos la imagen en el archivo.
        try {
            ImageIO.write(imagen, formato, fichero);
        } catch (IOException e) {
            System.out.println("Error de escritura");
        }
    }

}
