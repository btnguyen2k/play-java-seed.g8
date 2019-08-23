package utils;

import org.apache.commons.lang3.StringUtils;
import play.api.mvc.DiscardingCookie;
import play.mvc.Http;
import scala.Option;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

/**
 * Easy API to work with cookies.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v2.7.r1
 */
public class CookieJar {
    private ConcurrentMap<String, Http.Cookie> jar = new ConcurrentHashMap<>();

    public CookieJar(Http.Cookies cookies) {
        cookies.forEach(cookie -> jar.put(cookie.name(), cookie));
    }

    /**
     * Return all cookies as an array.
     *
     * @return
     */
    public Http.Cookie[] asArray() {
        return jar.values().toArray(new Http.Cookie[0]);
    }

    /**
     * Return all cookies as a collection.
     *
     * @return
     */
    public Collection<Http.Cookie> asCollection() {
        return jar.values();
    }

    /**
     * Get cookie associated with a key.
     *
     * @param key
     * @return
     */
    public Http.Cookie get(String key) {
        return jar.get(key);
    }

    /**
     * Set a cookie entry.
     *
     * <ul>
     *     <li>If {@code value} is {@code empty}, it is equivalent to {@code remove(key)}.</li>
     *     <li>Otherwise, cookie entry is set, existing value (if any) will be overridden.</li>
     * </ul>
     *
     * @param key
     * @param value
     * @return
     */
    public CookieJar set(String key, String value) {
        if (!StringUtils.isBlank(key)) {
            if (StringUtils.isBlank(value)) {
                return remove(key);
            }
            set(key, Http.Cookie.builder(key, value).build());
        }
        return this;
    }

    /**
     * Set a cookie entry.
     *
     * <ul>
     *     <li>If {@code cookie} is {@code null}, it is equivalent to {@code remove(key)}.</li>
     *     <li>Otherwise, cookie entry is set, existing value (if any) will be overridden.</li>
     * </ul>
     *
     * @param key
     * @param cookie
     * @return
     */
    public CookieJar set(String key, Http.Cookie cookie) {
        if (!StringUtils.isBlank(key)) {
            if (cookie == null) {
                return remove(key);
            }
            jar.put(key, cookie);
        }
        return this;
    }

    /**
     * Remove the cookie associated with a key.
     *
     * @param key
     * @return
     */
    public CookieJar remove(String key) {
        if (!StringUtils.isBlank(key)) {
            jar.put(key, DiscardingCookie.apply(key, "/", Option.apply(null), false).toCookie().asJava());
        }
        return this;
    }

    /**
     * Remove a specified cookie.
     *
     * @param cookie
     * @return
     */
    public CookieJar remove(Http.Cookie cookie) {
        if (cookie != null) {
            String key = cookie.name();
            Option<String> domain = Option.apply(cookie.domain());
            jar.put(key, DiscardingCookie.apply(key, cookie.path(), domain, cookie.secure()).toCookie().asJava());
        }
        return this;
    }

    /**
     * Performs the given action for each cookie.
     *
     * @param action
     */
    public void forEach(Consumer<? super Http.Cookie> action) {
        jar.values().forEach(action);
    }
}
