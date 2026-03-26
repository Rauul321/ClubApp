package utilities;

/**
 * Utility class for format validation.
 */
public class FormatValidationUtils {

    /**
     * Validates if the given email is in a correct format.
     * @param email Email address to validate.
     * @return true if the email format is valid, false otherwise.
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) return false;

        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

}
