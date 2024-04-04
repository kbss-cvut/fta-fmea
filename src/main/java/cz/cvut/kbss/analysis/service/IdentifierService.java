package cz.cvut.kbss.analysis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.net.URI;
import java.security.SecureRandom;
import java.util.Random;

@Service
@Slf4j
public class IdentifierService {
    private final int RANDOM_BOUND = 10000;

    private final Random RANDOM = new Random();

    private final Random SECURE_RANDOM = new SecureRandom();


    public URI composeIdentifier(String prefix, String fragment) {
        return URI.create(prefix + "/" + fragment);
    }

    /**
     * Generates a pseudo-unique OWL key using current system time and a random generator.
     *
     * @return OWL key
     */
    public String generateKey() {
        String key = Long.toString(System.nanoTime());
        return key.concat(Integer.toString(RANDOM.nextInt(RANDOM_BOUND)));
    }

    /**
     * Generates a number for uri using  a random generator.
     *
     * @return String number
     */
    public String generateRandomURINumber() {
        return Integer.toString(RANDOM.nextInt(RANDOM_BOUND));
    }

    /**
     * Generates a token for setting password using a secure random generator.
     *
     * @return String number
     */
    public String generateRandomToken() {
        int length = 20;
        return String.format("%"+length+"s", new BigInteger(length*5/*base 32,2^5*/, SECURE_RANDOM)
                .toString(32)).replace('\u0020', '0');
    }

    public URI generateNewInstanceUri(String typeUri) {
        return generateNewUri(typeUri + "/instance");
    }

    public URI generateNewUri(String prefix) {
        return URI.create(prefix + generateKey());
    }
}
