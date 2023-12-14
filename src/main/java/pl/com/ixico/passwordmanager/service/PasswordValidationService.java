package pl.com.ixico.passwordmanager.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PasswordValidationService {

    private static final Integer MAX_COMMON_CHARACTERS_WITH_FORBIDDEN = 4;

    private static final Integer MINIMUM_PASSWORD_LENGTH = 16;
    private static final List<String> FORBIDDEN_SEQUENCES = forbiddenSequences();


    public boolean caseRequirementFulfilled(String password) {
        return !password.toLowerCase().equals(password) && !password.toUpperCase().equals(password);
    }

    public boolean complexityRequirementFulfilled(String password) {
        return !StringUtils.isAlphanumeric(password) && containsDigit(password);
    }

    private boolean containsDigit(String password) {
        return password.chars().anyMatch(Character::isDigit);
    }

    public boolean lengthRequirementFulfilled(String password) {
        return password.length() >= MINIMUM_PASSWORD_LENGTH;
    }

    public boolean noTrivialSequencesRequirementFulfilled(String password) {
        if (password.isBlank()) return false;
        return FORBIDDEN_SEQUENCES.stream().noneMatch(forbiddenSequence ->
                longestSubstring(password.toLowerCase(), forbiddenSequence) > MAX_COMMON_CHARACTERS_WITH_FORBIDDEN) ||
                hasConsecutiveCharacters(password);
    }

    private boolean hasConsecutiveCharacters(String password) {
        int count = 0;
        for (int i = 0; i < password.length() - 1; i++) {
            if (password.charAt(i) == password.charAt(i + 1)) {
                count++;
            } else {
                count = 0;
            }
            if (count == MAX_COMMON_CHARACTERS_WITH_FORBIDDEN - 1) {
                return true;
            }
        }
        return false;
    }

    private int longestSubstring(String first, String second) {
        int maxLen = 0;
        int fl = first.length();
        int sl = second.length();
        int[][] table = new int[fl][sl];
        for (int i = 0; i < fl; i++) {
            for (int j = 0; j < sl; j++) {
                if (first.charAt(i) == second.charAt(j)) {
                    if (i == 0 || j == 0) {
                        table[i][j] = 1;
                    } else {
                        table[i][j] = table[i - 1][j - 1] + 1;
                    }
                    if (table[i][j] > maxLen) {
                        maxLen = table[i][j];
                    }
                }
            }
        }
        return maxLen;
    }

    private static List<String> forbiddenSequences() {
        var forbiddenList = new ArrayList<>(
                List.of(
                        "1234567890-=", "qwertyuiop[]", "asdfghjkl;'\\", "zxcvbnm,./", "abcdefghijklmnopqrstuvwxyz",
                        "1qaz", "2wsx", "3edc", "4rfv", "5tgb", "6yhn", "7ujm", "8ik,", "9ol.", "0p;/"
                )
        );
        forbiddenList.addAll(forbiddenList.stream().map(StringUtils::reverse).toList());
        return forbiddenList;
    }

}
