package pl.com.ixico.passwordmanager.utils;

public class Content {

    public static String help() {
        return """
                STEP 1
                1. Use silent mode (right top corner) when you are in a public place.
                2. Provide master password that fulfills defined requirements and make sure to remember it.
                3. Remember your checksum and verify it every time you provide master password to make sure it is correct.
                
                STEP 2
                4. Enter service name to generate password for authentication process (case sensitive).
                5. Make sure to refresh your session if you are still using the app (you will be logged out since it expires).
                """;
    }

    public static String noTrivialSequencesTooltip() {
        return "Sequences like 'abcd', 'qwerty',\n '1qaz', 'aaaa', are forbidden";
    }
}
