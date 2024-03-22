package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ColorTest {
    @Test
    public void testToHex() {
        Color color = new Color(255, 0, 0, 255);
        assertEquals("#FF0000FF", color.toHex());
    }

    @Test
    public void testToRGB() {
        Color color = new Color(255, 0, 0, 255);
        assertEquals("RGB(255, 0, 0)", color.toRGB());
    }

    @Test
    public void testToRGBA() {
        Color color = new Color(255, 0, 0, 255);
        assertEquals("RGBA(255, 0, 0, 1.00)", color.toRGBA());
    }

    @Test
    public void testEquals() {
        Color color1 = new Color(255, 0, 0, 255);
        Color color2 = new Color(255, 0, 0, 255);
        Color color3 = new Color(0, 255, 0, 255);

        assertTrue(color1.equals(color2));
        assertFalse(color1.equals(color3));
    }

    @Test
    public void testToString() {
        Color color = new Color(255, 0, 0, 255);
        assertEquals("Color{red=255, green=0, blue=0, alpha=255}", color.toString());
    }

    @Test
    public void testGetters() {
        Color color = new Color(100, 150, 200, 255);

        assertEquals(100, color.getRed());
        assertEquals(150, color.getGreen());
        assertEquals(200, color.getBlue());
        assertEquals(255, color.getAlpha());
    }

    @Test
    public void testSetters() {
        Color color = new Color();

        color.setRed(100);
        color.setGreen(150);
        color.setBlue(200);
        color.setAlpha(255);

        assertEquals(100, color.getRed());
        assertEquals(150, color.getGreen());
        assertEquals(200, color.getBlue());
        assertEquals(255, color.getAlpha());
    }
}
