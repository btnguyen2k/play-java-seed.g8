package utils;

import java.math.BigInteger;

import com.github.ddth.commons.utils.IdGenerator;

/**
 * ID generator utility class.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.2
 */
public class IdUtils {
    /**
     * Snowflake ID generator.
     */
    public final static IdGenerator idGen = IdGenerator.getInstance(IdGenerator.getMacAddr());

    /**
     * Generate a unique ID (128 bits).
     * 
     * @return
     */
    public static String nextId() {
        return idGen.generateId128Hex().toLowerCase();
    }

    /**
     * Generate a unique ID (64 bits).
     * 
     * @return
     */
    public static long nextIdAsLong() {
        return idGen.generateId64();
    }

    /**
     * Generate a unique ID (128 bits).
     * 
     * @return
     */
    public static BigInteger nextIdAsBigInteger() {
        return idGen.generateId128();
    }
}
