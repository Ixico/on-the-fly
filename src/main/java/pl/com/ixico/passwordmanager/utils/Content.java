package pl.com.ixico.passwordmanager.utils;

public class Content {

    public static String help() {
        return """
                STEP 1
                1. Provide strong master password that fulfills the length requirement and make sure to remember it.
                2. Remember your checksum and verify it every time you provide master password to make sure it is correct.
                3. Press generate button and wait for your key to be generated.
                4. Make sure to use silent mode (right top corner) when you are in a public place.
                                
                STEP 2
                5. Enter service name to generate password for authentication process.
                6. Once you hit generate button, your password will be copied into your clipboard.
                7. Make sure to refresh your session if you are still using the app (you will be logged out since it expires).
                """;
    }

}
