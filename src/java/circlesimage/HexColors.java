package circlesimage;

/**
 * This class is an extension for the HexColors
 * class which has a method for get a HexColor
 * passing the color name
 * This is for cosmetics proposes
 */
public class HexColors extends engine.gfx.HexColors {

    public static int getHexColor(String colorName) {
        switch ( colorName ) {
            case "WHITE": default:
                return WHITE;
            case "BLACK":
                return BLACK;
            case "ALPHA":
                return ALPHA;
            case "GREY":
                return GREY;
            case "RED":
                return RED;
            case "GREEN":
                return GREEN;
            case "BLUE":
                return BLUE;
            case "YELLOW":
                return YELLOW;
            case "MAGENTA":
                return MAGENTA;
            case "CYAN":
                return CYAN;
            case "WINE":
                return WINE;
            case "FANCY_RED":
                return FANCY_RED;
            case "ORANGE":
                return ORANGE;
            case "TANGERINE":
                return TANGERINE;
            case "LEMON":
                return LEMON;
            case "MINT":
                return MINT;
            case "DARK_MINT":
                return DARK_MINT;
            case "FANCY_BLUE":
                return FANCY_BLUE;
            case "LIGHT_BLUE":
                return LIGHT_BLUE;
            case "DARK_BLUE":
                return DARK_BLUE;
            case "ROYAL_BLUE":
                return ROYAL_BLUE;
        }
    }

}
