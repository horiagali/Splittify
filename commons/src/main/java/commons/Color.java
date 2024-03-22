package commons;

import jakarta.persistence.Embeddable;

@Embeddable
public class Color {
    private int red;
    private int green;
    private int blue;
    private int alpha;


    /**
     * Creates a color
     * @param red red value
     * @param green green value
     * @param blue blue value
     * @param alpha opacity value
     */
    public Color(int red, int green, int blue, int alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public Color() {

    }

    /**
     * Getter
     * @return red value
     */
    public int getRed() {
        return red;
    }

    /**
     * Setter
     * @param red red value
     */
    public void setRed(int red) {
        this.red = red;
    }

    /**
     * Getter
     * @return green value
     */
    public int getGreen() {
        return green;
    }

    /**
     * Setter
     * @param green green value
     */
    public void setGreen(int green) {
        this.green = green;
    }

    /**
     * Getter
     * @return blue value
     */
    public int getBlue() {
        return blue;
    }

    /**
     * Setter
     * @param blue blue value
     */
    public void setBlue(int blue) {
        this.blue = blue;
    }

    /**
     * Getter
     * @return alpha value
     */
    public int getAlpha() {
        return alpha;
    }

    /**
     * Setter
     * @param alpha alpha value
     */
    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    /**
     * Method to get the hexadecimal representation of the color
     * @return hexadecimal representation of the color
     */
    public String toHex() {
        return String.format("#%02X%02X%02X%02X", red, green, blue, alpha);
    }

    /**
     * Method to get the RGB representation of the color
     * @return RGB representation
     */
    public String toRGB() {
        return String.format("RGB(%d, %d, %d)", red, green, blue);
    }

    /**
     * Method to get the RGBA representation of the color
     * @return RGBA representation
     */
    public String toRGBA() {
        return String.format("RGBA(%d, %d, %d, %.2f)", red, green, blue, alpha / 255.0);
    }

    /**
     * Equals method
     * @param other object to compare to
     * @return true iff same
     */
    public boolean equals(Color other) {
        return this.red == other.red && this.green == other.green &&
                this.blue == other.blue && this.alpha == other.alpha;
    }

    /**
     * Override toString method to provide a string representation of the color
     * @return string
     */
    @Override
    public String toString() {
        return "Color{" +
                "red=" + red +
                ", green=" + green +
                ", blue=" + blue +
                ", alpha=" + alpha +
                '}';
    }
}

