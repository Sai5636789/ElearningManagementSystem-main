package com.elearning.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.ImageIcon;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.net.URL;

public final class ImageLoader {

    private static final Logger log = LoggerFactory.getLogger(ImageLoader.class);

    private ImageLoader() {
    }

    public static ImageIcon getIcon(String name) {
        URL url = ImageLoader.class.getResource("/icons/" + name);
        if (url == null) {
            log.warn("Icon not found: /icons/{}", name);
            return new ImageIcon();
        }
        return new ImageIcon(url);
    }

    public static ImageIcon getScaledIcon(String name, int width, int height) {
        ImageIcon icon = getIcon(name);
        if (icon.getImage() == null) {
            return icon;
        }
        Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    public static ImageIcon createCircularAvatar(BufferedImage master, int diameter) {
        if (master == null) {
            return new ImageIcon();
        }
        BufferedImage mask = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = mask.createGraphics();
        applyQualityRenderingHints(g2);
        g2.fill(new Ellipse2D.Double(0, 0, diameter, diameter));
        g2.dispose();

        BufferedImage image = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
        g2 = image.createGraphics();
        applyQualityRenderingHints(g2);
        int width = image.getWidth();
        int height = image.getHeight();
        g2.drawImage(master, 0, 0, width, height, null);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_IN));
        g2.drawImage(mask, 0, 0, null);
        g2.dispose();

        return new ImageIcon(image);
    }

    private static void applyQualityRenderingHints(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }
}
