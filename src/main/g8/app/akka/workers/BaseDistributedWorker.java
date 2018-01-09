package akka.workers;

import com.github.ddth.dlock.IDLock;
import com.github.ddth.dlock.IDLockFactory;
import com.github.ddth.dlock.LockResult;
import com.github.ddth.dlock.impl.inmem.InmemDLock;

import akka.cluster.BaseClusterActor;

/**
 * Base class to implement distributed-workers.
 *
 * <p>
 * This worker is almost the same as {@link BaseWorker} accept that, if supplied a
 * distributed-implementation of {@link IDLockFactory}, it can achieve the same behavior as
 * {@link BaseClusterActor}.
 * </p>
 *
 * <p>
 * Note: this class looks up {@link IDLockFactory} from
 * {@code IRegistry.getBean(IDlockFactory.class)}
 * </p>
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v2.6.r5
 */
public abstract class BaseDistributedWorker extends BaseWorker {

    private IDLockFactory lockFactory;
    private IDLock lock;

    protected IDLockFactory getDLockFactory() {
        return lockFactory;
    }

    protected IDLock getDLock() {
        return lock;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initDLock() {
        lockFactory = getRegistry().getBean(IDLockFactory.class);
        String lockName = getActorName();
        lock = lockFactory != null ? lockFactory.createLock(lockName) : new InmemDLock(lockName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean lock(String lockId, long durationMs) {
        return lock.lock(lockId, durationMs) == LockResult.SUCCESSFUL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean unlock(String lockId) {
        LockResult result = lock.unlock(lockId);
        return result == LockResult.SUCCESSFUL || result == LockResult.NOT_FOUND;
    }

}
