package it.turin.hermesclient.utils;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Metodi di utilita' per validare le stringhe degli indirizzi email usate dal
 * client.
 */
public final class EmailValidator {

    private static final Pattern EMAIL_REGEX = Pattern.compile("^[a-z0-9._%+\\-]+@[a-z0-9.\\-]+\\.[a-z]{2,}$");

    /**
     * Restituisce se un singolo indirizzo email rispetta il formato accettato.
     *
     * @param email indirizzo email da validare
     * @return {@code true} se l'indirizzo e' valido
     */
    public static boolean isValid(String email) {
        return EMAIL_REGEX.matcher(email).matches();
    }

    /**
     * Restituisce se tutti gli indirizzi email nella lista sono validi.
     *
     * @param emails indirizzi da validare
     * @return {@code true} se tutti gli indirizzi sono validi
     */
    public static boolean allValid(List<String> emails) {
        for (String mail : emails) {
            System.out.println("mail: " + mail);
            if (!isValid(mail)) return false;
        }
        return true;
    }

    /**
     * Trova tutti gli indirizzi non validi nella lista fornita.
     *
     * @param emails indirizzi da validare
     * @return indirizzi non validi
     */
    public static List<String> findInvalid(List<String> emails) {
        return emails.stream().filter(e -> !isValid(e)).toList();
    }
}

