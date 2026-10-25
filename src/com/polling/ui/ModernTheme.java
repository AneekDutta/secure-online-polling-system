// Package declaration for user interface components
package com.polling.ui;

// Import Color class for color definitions
import java.awt.Color;
// Import Component class
import java.awt.Component;
// Import Dimension class
import java.awt.Dimension;
// Import Font class for typographic styles
import java.awt.Font;
// Import Insets for margin and padding definitions
import java.awt.Insets;
// Import BorderFactory for border creations
import javax.swing.BorderFactory;
// Import JButton for component styling
import javax.swing.JButton;
// Import JLabel for component styling
import javax.swing.JLabel;
// Import JTextField for styling
import javax.swing.JTextField;
// Import Border interface
import javax.swing.border.Border;

// Central styling repository providing consistent colors, typography, and controls
public final class ModernTheme {

    // Private constructor preventing direct instantiation of utility class
    private ModernTheme() {
    // End of constructor
    }

    // Off-white primary application background color (Slate 50)
    public static final Color COLOR_BG_APP = new Color(248, 250, 252);

    // Pure white background for card panels and dialogs
    public static final Color COLOR_BG_CARD = new Color(255, 255, 255);

    // Muted off-white background for input fields and bars
    public static final Color COLOR_BG_INPUT = new Color(241, 245, 249);

    // Primary dark navy text color for headings and titles (Slate 900)
    public static final Color COLOR_TEXT_PRIMARY = new Color(15, 23, 42);

    // Secondary dark slate text color for labels and body (Slate 700)
    public static final Color COLOR_TEXT_SECONDARY = new Color(51, 65, 85);

    // Muted slate text color for hints and secondary metadata (Slate 500)
    public static final Color COLOR_TEXT_MUTED = new Color(100, 116, 139);

    // Restrained blue accent color for primary actions and highlights (Blue 600)
    public static final Color COLOR_ACCENT_BLUE = new Color(37, 99, 235);

    // Darker blue accent color for button hover states (Blue 700)
    public static final Color COLOR_ACCENT_BLUE_HOVER = new Color(29, 78, 216);

    // Subtle blue background tint for selection and active tabs (Blue 50)
    public static final Color COLOR_ACCENT_LIGHT = new Color(239, 246, 255);

    // Light border color for cards and separators (Slate 200)
    public static final Color COLOR_BORDER = new Color(226, 232, 240);

    // Emerald green for open status and success banners
    public static final Color COLOR_STATUS_OPEN = new Color(16, 149, 99);

    // Light green background tint for open status pill
    public static final Color COLOR_STATUS_OPEN_BG = new Color(236, 253, 245);

    // Crimson red for closed status and destructive warnings
    public static final Color COLOR_STATUS_CLOSED = new Color(220, 38, 38);

    // Light red background tint for closed status pill
    public static final Color COLOR_STATUS_CLOSED_BG = new Color(254, 242, 242);

    // Amber gold for tie indicators and notices
    public static final Color COLOR_STATUS_TIE = new Color(217, 119, 6);

    // Primary font family name
    private static final String FONT_FAMILY = "Segoe UI, -apple-system, BlinkMacSystemFont, Helvetica Neue, Sans-Serif";

    // Heading font (bold, 20pt)
    public static final Font FONT_HEADING = new Font(Font.SANS_SERIF, Font.BOLD, 20);

    // Subheading font (bold, 16pt)
    public static final Font FONT_SUBHEADING = new Font(Font.SANS_SERIF, Font.BOLD, 15);

    // Card title font (bold, 15pt)
    public static final Font FONT_CARD_TITLE = new Font(Font.SANS_SERIF, Font.BOLD, 14);

    // Standard regular body font (plain, 13pt)
    public static final Font FONT_BODY = new Font(Font.SANS_SERIF, Font.PLAIN, 13);

    // Standard bold body font (bold, 13pt)
    public static final Font FONT_BODY_BOLD = new Font(Font.SANS_SERIF, Font.BOLD, 13);

    // Small metadata font (plain, 11pt)
    public static final Font FONT_SMALL = new Font(Font.SANS_SERIF, Font.PLAIN, 11);

    // Small bold metadata font (bold, 11pt)
    public static final Font FONT_SMALL_BOLD = new Font(Font.SANS_SERIF, Font.BOLD, 11);

    // Factory method for creating a consistent card border with subtle outline and padding
    public static Border createCardBorder() {
        // Return compound border composed of outline and internal padding
        return BorderFactory.createCompoundBorder(
            // Subtle 1-pixel line border
            BorderFactory.createLineBorder(COLOR_BORDER, 1),
            // Internal 16-pixel empty margin padding
            BorderFactory.createEmptyBorder(16, 16, 16, 16)
        );
    // End of method
    }

    // Styles a button with the primary restrained blue aesthetic
    public static void stylePrimaryButton(JButton button) {
        // Set background color
        button.setBackground(COLOR_ACCENT_BLUE);
        // Set text foreground color to pure white
        button.setForeground(Color.WHITE);
        // Set typography to bold body font
        button.setFont(FONT_BODY_BOLD);
        // Remove focus rectangle
        button.setFocusPainted(false);
        // Enable filled background
        button.setContentAreaFilled(true);
        // Remove standard 3D beveled border
        button.setBorderPainted(false);
        // Enable opaque painting
        button.setOpaque(true);
        // Set comfortable button padding
        button.setMargin(new Insets(8, 16, 8, 16));
    // End of method
    }

    // Styles a button with a clean secondary neutral appearance
    public static void styleSecondaryButton(JButton button) {
        // Set background color to card white
        button.setBackground(COLOR_BG_CARD);
        // Set text foreground to dark slate
        button.setForeground(COLOR_TEXT_PRIMARY);
        // Set typography to bold body font
        button.setFont(FONT_BODY_BOLD);
        // Remove focus rectangle
        button.setFocusPainted(false);
        // Set clean 1-pixel border
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(7, 15, 7, 15)
        ));
        // Enable opaque painting
        button.setOpaque(true);
    // End of method
    }

    // Styles an input text field with uniform padding and subtle border
    public static void styleTextField(JTextField textField) {
        // Set background color to pure white
        textField.setBackground(Color.WHITE);
        // Set text foreground color
        textField.setForeground(COLOR_TEXT_PRIMARY);
        // Set caret color
        textField.setCaretColor(COLOR_TEXT_PRIMARY);
        // Set standard body font
        textField.setFont(FONT_BODY);
        // Apply consistent border with padding
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
    // End of method
    }
// End of class
}
