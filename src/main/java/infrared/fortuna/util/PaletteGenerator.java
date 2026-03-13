package infrared.fortuna.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PaletteGenerator
{
    public static byte[] generateTrimPalette(Color baseColor)
    {
        float[] hsb = Color.RGBtoHSB(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), null);

        BufferedImage image = new BufferedImage(8, 1, BufferedImage.TYPE_INT_ARGB);

        float baseBrightness = hsb[2];

        for (int i = 0; i < 8; i++)
        {
            float t = i / 7f;
            float brightness = baseBrightness * (1.0f - t);
            float saturation = Math.min(hsb[1] + (t * 0.15f), 1.0f);
            Color pixel = Color.getHSBColor(hsb[0], saturation, brightness);
            image.setRGB(i, 0, pixel.getRGB());
        }

        try
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image, "png", out);
            return out.toByteArray();
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to generate trim palette", e);
        }
    }
}