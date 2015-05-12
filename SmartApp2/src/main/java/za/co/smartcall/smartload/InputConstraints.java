package za.co.smartcall.smartload;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.beans.Observable;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

public class InputConstraints {

	public static final String MSISDN_REG_FORMAT = "0\\d{9}";

    public static void noLeadingAndTrailingBlanks(final TextField textField) {
        textField.addEventFilter(KeyEvent.KEY_TYPED, createNoLeadingBlanksInputHandler());
        textField.focusedProperty().addListener((Observable observable) -> {
            textField.setText(textField.getText().trim());
        });
    }

    public static void noLeadingBlanks(final TextField textField) {
        textField.addEventFilter(KeyEvent.KEY_TYPED, createNoLeadingBlanksInputHandler());
    }

    public static void noBlanks(final TextField textField) {
        textField.addEventFilter(KeyEvent.KEY_TYPED, createNoBlanksInputHandler());
        textField.focusedProperty().addListener((Observable observable) -> {
            textField.setText(textField.getText().trim());
        });
    }

    public static void numbersOnly(final TextField textField) {
        numbersOnly(textField, Integer.MAX_VALUE);
    }

    public static void numbersOnly(final TextField textField, final Integer maxLenth) {
        textField.addEventFilter(KeyEvent.KEY_TYPED, createNumbersOnlyInputHandler(maxLenth));
        textField.focusedProperty().addListener((Observable observable) -> {
            textField.setText(textField.getText().trim());
        });
    }

    public static void lettersOnly(final TextField textField) {
        lettersOnly(textField, Integer.MAX_VALUE);
    }

    public static void lettersOnly(final TextField textField, final Integer maxLenth) {
        textField.addEventFilter(KeyEvent.KEY_TYPED, createLettersOnlyInputHandler(maxLenth));
        textField.focusedProperty().addListener((Observable observable) -> {
            textField.setText(textField.getText().trim());
        });
    }

    public static EventHandler<KeyEvent> createNoLeadingBlanksInputHandler() {
        return (KeyEvent event) -> {
            if (event.getSource() instanceof TextField) {
                TextField textField = (TextField) event.getSource();
                if (" ".equals(event.getCharacter()) && textField.getCaretPosition() == 0) {
                    event.consume();
                }
            }
        };
    }

    public static EventHandler<KeyEvent> createNumbersOnlyInputHandler(final Integer maxLength) {
        return createPatternInputHandler(maxLength, "[0-9.]");
    }

    public static EventHandler<KeyEvent> createLettersOnlyInputHandler(final Integer maxLength) {
        return createPatternInputHandler(maxLength, "[A-Za-z]");
    }

    public static EventHandler<KeyEvent> createNoBlanksInputHandler() {
        return (KeyEvent event) -> {
            if (event.getSource() instanceof TextField) {
                if (" ".equals(event.getCharacter())) {
                    event.consume();
                }
            }
        };
    }

    public static EventHandler<KeyEvent> createPatternInputHandler(final Integer maxLength, String pattern) {
        return (KeyEvent event) -> {
            if (event.getSource() instanceof TextField) {
                TextField textField = (TextField) event.getSource();
                if (textField.getText().length() >= maxLength || !event.getCharacter().matches(pattern)) {
                    event.consume();
                }
            }
        };
    }
    
    
	public static String convertTo0Format(String msisdn){
		msisdn = msisdn.trim();
		if (msisdn.length()== 11){
			msisdn = msisdn.replaceFirst("27", "0");
		}
		return msisdn;
	}
	
	public static boolean validateMsisdn(String sim){
		System.out.println(sim);
		 sim = convertTo0Format(sim);
		 Pattern p = Pattern.compile(MSISDN_REG_FORMAT);
		 Matcher m = p.matcher(sim);
		 return (m.matches());
	}
}
