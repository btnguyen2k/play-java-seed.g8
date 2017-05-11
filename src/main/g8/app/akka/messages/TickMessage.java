package akka.messages;

import org.apache.commons.lang3.builder.ToStringBuilder;

import utils.IdUtils;

/**
 * A message that encapsulates a "tick".
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.2
 */
public class TickMessage {
    /**
     * "Tick"'s unique id.
     */
    public final String id;

    /**
     * "Tick's" timestamp (UNIX timestamp, in milliseconds) when the tick is
     * fired.
     */
    public final long timestampMs = System.currentTimeMillis();

    public TickMessage() {
        id = IdUtils.nextId();
    }

    public TickMessage(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public long getTimestamp() {
        return this.timestampMs;
    }

    private String toString;

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        if (toString == null) {
            ToStringBuilder tsb = new ToStringBuilder(this);
            tsb.append("id", id).append("timestamp", timestampMs);
            toString = tsb.toString();
        }
        return toString;
    }
}
