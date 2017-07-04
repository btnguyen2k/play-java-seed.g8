package akka;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import utils.IdUtils;

/**
 * A message that encapsulates a "tick".
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.2
 */
public class TickMessage implements Serializable {

    private static final long serialVersionUID = "template-v0.1.2".hashCode();

    /**
     * "Tick"'s unique id.
     */
    public final String id;

    /**
     * "Tick's" timestamp (UNIX timestamp, in milliseconds) when the tick is
     * fired.
     */
    public final long timestampMs = System.currentTimeMillis();

    public final Map<String, Object> tags = new HashMap<>();

    public TickMessage() {
        id = IdUtils.nextId();
    }

    public TickMessage(Map<String, Object> tags) {
        this();
        if (tags != null) {
            this.tags.putAll(tags);
        }
    }

    public TickMessage(String id) {
        this.id = id;
    }

    public TickMessage(String id, Map<String, Object> tags) {
        this(id);
        if (tags != null) {
            this.tags.putAll(tags);
        }
    }

    public String getId() {
        return this.id;
    }

    public long getTimestamp() {
        return this.timestampMs;
    }

    public TickMessage addTag(String name, Object value) {
        tags.put(name, value);
        return this;
    }

    // private String toString;

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        // if (toString == null) {
        ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        tsb.append("id", id).append("timestamp", timestampMs).append("tags", tags);
        // toString = tsb.toString();
        return tsb.toString();
        // }
        // return toString;
    }

}
