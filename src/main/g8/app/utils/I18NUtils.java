package utils;

import java.util.Arrays;
import java.util.Collections;

import play.i18n.Lang;
import play.i18n.Messages;
import play.i18n.MessagesApi;
import play.mvc.Http.RequestHeader;

/**
 * I18N utility class.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.0
 */
public class I18NUtils {
    /**
     * Render a message.
     * 
     * <p>
     * Message key format: {@code key||param1||param2...}
     * </p>
     * 
     * @param messages
     * @param msgKey
     * @return
     */
    public static String renderMessage(Messages messages, String msgKey) {
        if (msgKey.contains("||")) {
            String[] tokens = msgKey.split("\\\\|\\\\|");
            return messages.at(tokens[0], (Object[]) Arrays.copyOfRange(tokens, 1, tokens.length));
        }
        return msgKey;
    }

    /**
     * Get a messages context for the given request.
     * 
     * @param messagesApi
     * @param requestHeader
     * @return
     */
    public static Messages calcMesages(MessagesApi messagesApi, RequestHeader requestHeader) {
        return messagesApi.preferred(requestHeader);
    }

    /**
     * Get a messages context for the given language.
     *
     * @param messagesApi
     * @param lang
     * @return
     */
    public static Messages calcMesages(MessagesApi messagesApi, Lang lang) {
        return messagesApi.preferred(Collections.singleton(lang));
    }
}
