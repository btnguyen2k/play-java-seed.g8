package akka;

import com.github.ddth.commons.utils.DateFormatUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import utils.IdUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
     * "Tick's" timestamp (UNIX timestamp, in milliseconds) when the tick is fired.
     */
    public final long timestampMs = System.currentTimeMillis();

    public final Date timestamp = new Date(timestampMs);

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

    public String getTimestampStr() {
        return getTimestampStr(DateFormatUtils.DF_ISO8601);
    }

    public String getTimestampStr(String dateTimeFormat) {
        return DateFormatUtils.toString(timestamp, dateTimeFormat);
    }

    public TickMessage addTag(String name, Object value) {
        tags.put(name, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        tsb.append("id", id).append("timestamp", timestampMs)
                .append("timestampStr", getTimestampStr()).append("tags", tags);
        return tsb.toString();
    }

}
