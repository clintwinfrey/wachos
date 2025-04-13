package gov.mil.navy.nswcdd.wachos.tools;

public class Color {

    private final int r, g, b, a;
    /**
     * The color white. In the default sRGB space.
     */
    public final static Color white = new Color(255, 255, 255);

    /**
     * The color white. In the default sRGB space.
     *
     * @since 1.4
     */
    public final static Color WHITE = white;

    /**
     * The color light gray. In the default sRGB space.
     */
    public final static Color lightGray = new Color(192, 192, 192);

    /**
     * The color light gray. In the default sRGB space.
     *
     * @since 1.4
     */
    public final static Color LIGHT_GRAY = lightGray;

    /**
     * The color gray. In the default sRGB space.
     */
    public final static Color gray = new Color(128, 128, 128);

    /**
     * The color gray. In the default sRGB space.
     *
     * @since 1.4
     */
    public final static Color GRAY = gray;

    /**
     * The color dark gray. In the default sRGB space.
     */
    public final static Color darkGray = new Color(64, 64, 64);

    /**
     * The color dark gray. In the default sRGB space.
     *
     * @since 1.4
     */
    public final static Color DARK_GRAY = darkGray;

    /**
     * The color black. In the default sRGB space.
     */
    public final static Color black = new Color(0, 0, 0);

    /**
     * The color black. In the default sRGB space.
     *
     * @since 1.4
     */
    public final static Color BLACK = black;

    /**
     * The color red. In the default sRGB space.
     */
    public final static Color red = new Color(255, 0, 0);

    /**
     * The color red. In the default sRGB space.
     *
     * @since 1.4
     */
    public final static Color RED = red;

    /**
     * The color pink. In the default sRGB space.
     */
    public final static Color pink = new Color(255, 175, 175);

    /**
     * The color pink. In the default sRGB space.
     *
     * @since 1.4
     */
    public final static Color PINK = pink;

    /**
     * The color orange. In the default sRGB space.
     */
    public final static Color orange = new Color(255, 200, 0);

    /**
     * The color orange. In the default sRGB space.
     *
     * @since 1.4
     */
    public final static Color ORANGE = orange;

    /**
     * The color yellow. In the default sRGB space.
     */
    public final static Color yellow = new Color(255, 255, 0);

    /**
     * The color yellow. In the default sRGB space.
     *
     * @since 1.4
     */
    public final static Color YELLOW = yellow;

    /**
     * The color green. In the default sRGB space.
     */
    public final static Color green = new Color(0, 255, 0);

    /**
     * The color green. In the default sRGB space.
     *
     * @since 1.4
     */
    public final static Color GREEN = green;

    /**
     * The color magenta. In the default sRGB space.
     */
    public final static Color magenta = new Color(255, 0, 255);

    /**
     * The color magenta. In the default sRGB space.
     *
     * @since 1.4
     */
    public final static Color MAGENTA = magenta;

    /**
     * The color cyan. In the default sRGB space.
     */
    public final static Color cyan = new Color(0, 255, 255);

    /**
     * The color cyan. In the default sRGB space.
     *
     * @since 1.4
     */
    public final static Color CYAN = cyan;

    /**
     * The color blue. In the default sRGB space.
     */
    public final static Color blue = new Color(0, 0, 255);

    /**
     * The color blue. In the default sRGB space.
     *
     * @since 1.4
     */
    public final static Color BLUE = blue;

    /**
     * Creates an opaque sRGB color with the specified red, green, and blue
     * values in the range (0 - 255). The actual color used in rendering depends
     * on finding the best match given the color space available for a given
     * output device. Alpha is defaulted to 255.
     *
     * @throws IllegalArgumentException if <code>r</code>, <code>g</code> or
     * <code>b</code> are outside of the range 0 to 255, inclusive
     * @param r the red component
     * @param g the green component
     * @param b the blue component
     */
    public Color(int r, int g, int b) {
        this(r, g, b, 255);
    }

    /**
     * Creates an sRGB color with the specified red, green, blue, and alpha
     * values in the range (0 - 255).
     *
     * @throws IllegalArgumentException if <code>r</code>, <code>g</code>,
     * <code>b</code> or <code>a</code> are outside of the range 0 to 255,
     * inclusive
     * @param r the red component
     * @param g the green component
     * @param b the blue component
     * @param a the alpha component
     */
    public Color(int r, int g, int b, int a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }
    
    /**
     * Creates a Color from a hexadecimal String such as #FF0000
     *
     * @param color the hex string to convert
     */
    public Color(String color) {
        Color c = Color.decode(color);
        this.r = c.r;
        this.g = c.g;
        this.b = c.b;
        this.a = c.a;
    }

    /**
     * Converts this Color to a hexadecimal String
     *
     * @return a hexadecimal String representation
     */
    public String toHex() {
        return "#" + String.format("%02X%02X%02X", r, g, b);
    }

    /**
     * Returns the red component in the range 0-255 in the default sRGB space.
     *
     * @return the red component.
     */
    public int getRed() {
        return r;
    }

    /**
     * Returns the green component in the range 0-255 in the default sRGB space.
     *
     * @return the green component.
     */
    public int getGreen() {
        return g;
    }

    /**
     * Returns the blue component in the range 0-255 in the default sRGB space.
     *
     * @return the blue component.
     */
    public int getBlue() {
        return b;
    }

    /**
     * Returns the alpha component in the range 0-255.
     *
     * @return the alpha component.
     */
    public int getAlpha() {
        return a;
    }

    /**
     * Converts a <code>String</code> to an integer and returns the specified
     * opaque <code>Color</code>. This method handles string formats that are
     * used to represent octal and hexadecimal numbers.
     *
     * @param nm a <code>String</code> that represents an opaque color as a
     * 24-bit integer
     * @return the new <code>Color</code> object.
     * @exception NumberFormatException if the specified string cannot be
     * interpreted as a decimal, octal, or hexadecimal integer.
     * @since JDK1.1
     */
    public static Color decode(String nm) throws NumberFormatException {
        Integer intval = Integer.decode(nm);
        int i = intval.intValue();
        return new Color((i >> 16) & 0xFF, (i >> 8) & 0xFF, i & 0xFF);
    }
}
