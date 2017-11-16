package akka.cluster;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import akka.cluster.ddata.ORMultiMap;
import utils.IdUtils;

/**
 * Manage cluster's distributed data.
 * 
 * <p>
 * Cluster's distributed data model: {@link ORMultiMap}.
 * </p>
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.5
 */
public class DistributedDataManager {

    private static Cache<Long, DDGetResult> ddGetResponses = CacheBuilder.newBuilder()
            .expireAfterWrite(30, TimeUnit.SECONDS).maximumSize(81920).build();

    /**
     * Retrieve distributed-data's get response from cache.
     * 
     * @param id
     * @return
     */
    public static DDGetResult getResponse(long id) {
        return ddGetResponses.getIfPresent(id);
    }

    /**
     * Put distributed-data's get response to cache.
     * 
     * @param id
     * @param ddGetResult
     */
    public static void setResponse(long id, DDGetResult ddGetResult) {
        ddGetResponses.put(id, ddGetResult);
    }

    /**
     * A lock implemented using distributed data.
     */
    public static class DDLock implements Serializable {
        private static final long serialVersionUID = "template-v0.1.5".hashCode();

        public final long timestamp = System.currentTimeMillis();
        public final long expiry;
        public final String lockId;

        public DDLock(String lockId) {
            this(lockId, 60, TimeUnit.SECONDS);// 60 seconds
        }

        public DDLock(String lockId, long lockTimeout, TimeUnit logTimeoutUnit) {
            this.lockId = lockId;
            expiry = timestamp + logTimeoutUnit.toMillis(lockTimeout);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return lockId != null ? lockId.hashCode() : 0;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object obj) {
            return obj == this
                    || (obj instanceof DDLock && StringUtils.equals(lockId, ((DDLock) obj).lockId));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
            tsb.append("timestamp", timestamp).append("expiry", expiry).append("lock-id", lockId);
            return tsb.toString();
        }
    }

    /**
     * Encapsulate result from distributed data GET request.
     */
    public static class DDGetResult implements Serializable {
        private static final long serialVersionUID = "template-v0.1.5".hashCode();

        public static class DDGetNotFound extends DDGetResult {
            private static final long serialVersionUID = "template-v0.1.5".hashCode();

            public DDGetNotFound(long id, String key) {
                super(id, key);
            }
        }

        public static class DDGetError extends DDGetResult {
            private static final long serialVersionUID = "template-v0.1.5".hashCode();

            public DDGetError(long id, String key) {
                super(id, key);
            }
        }

        public static DDGetResult error(long id, String key) {
            return new DDGetError(id, key);
        }

        public static DDGetResult notFound(long id, String key) {
            return new DDGetNotFound(id, key);
        }

        public static DDGetResult ok(long id, String key, Collection<Object> value) {
            return new DDGetResult(id, key, value);
        }

        private long id;
        private String key;
        private Set<Object> value;

        public DDGetResult(long id, String key) {
            this.id = id;
            this.key = key;
        }

        public DDGetResult(long id, String key, Collection<Object> value) {
            this(id, key);
            this.value = value != null ? new HashSet<>(value) : new HashSet<>();
        }

        public long getId() {
            return id;
        }

        public DDGetResult setId(long id) {
            this.id = id;
            return this;
        }

        public boolean isError() {
            return this instanceof DDGetError;
        }

        public boolean isNotFound() {
            return this instanceof DDGetNotFound;
        }

        public boolean isNullOrEmpty() {
            return value == null || value.isEmpty();
        }

        public Collection<Object> getValue() {
            return value != null ? Collections.unmodifiableSet(value) : null;
        }

        public DDGetResult setValue(Collection<Object> value) {
            this.value = value != null ? new HashSet<>(value) : new HashSet<>();
            return this;
        }

        public Object singleValue() {
            if (value != null) {
                Iterator<Object> it = value.iterator();
                return it.hasNext() ? it.next() : null;
            }
            return null;
        }

        @SuppressWarnings("unchecked")
        public <T> T singleValueAs(Class<T> clazz) {
            Object singleValue = singleValue();
            return singleValue != null && clazz.isAssignableFrom(singleValue.getClass())
                    ? (T) singleValue : null;
        }

        public boolean valueContains(Object obj) {
            return value != null && value.contains(obj);
        }

        public String getKey() {
            return key;
        }

        public DDGetResult setKey(String key) {
            this.key = key;
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
            tsb.append("id", id).append("key", key).append("value", value);
            return tsb.toString();
        }
    }

    /**
     * Encapsulate tags attached to a distributed data record.
     */
    public static class DDTags implements Serializable {
        private static final long serialVersionUID = "template-v0.1.5".hashCode();

        public static DDTags EMPTY = new DDTags(0, null);

        public static DDTags empty() {
            return EMPTY;
        }

        private long id;
        private String key;

        public DDTags() {
        }

        public DDTags(String key) {
            this(IdUtils.nextIdAsLong(), key);
        }

        public DDTags(long id, String key) {
            this.id = id;
            this.key = key;
        }

        public long getId() {
            return id;
        }

        public DDTags setId(long id) {
            this.id = id;
            return this;
        }

        public String getKey() {
            return key;
        }

        public DDTags setKey(String key) {
            this.key = key;
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
            tsb.append("id", id).append("key", key);
            return tsb.toString();
        }
    }
}
