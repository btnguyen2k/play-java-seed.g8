package akka;

import akka.actor.ActorPath;
import akka.actor.UntypedAbstractActor;
import com.google.inject.Provider;
import modules.registry.IRegistry;
import modules.registry.RegistryGlobal;
import play.Logger;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Base class to implement Akka actors.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.5
 */
public class BaseActor extends UntypedAbstractActor {

    protected Map<Class<?>, Consumer<?>> messageHandler = new ConcurrentHashMap<>();
    protected Provider<IRegistry> registryProvider;
    protected IRegistry registry;

    public BaseActor() {
    }

    public BaseActor(Provider<IRegistry> registryProvider) {
        this.registryProvider = registryProvider;
    }

    public BaseActor(IRegistry registry) {
        this.registry = registry;
    }

    protected IRegistry getRegistry() {
        if (registry == null) {
            registry = registryProvider != null ? registryProvider.get() : RegistryGlobal.registry;
        }
        return registry;
    }

    /**
     * Convenient method to get actor name.
     *
     * @return
     */
    protected String getActorName() {
        return getClass().getSimpleName();
    }

    /**
     * Convenient method to get actor path.
     *
     * @return
     */
    protected ActorPath getActorPath() {
        return self().path();
    }

    protected <T> BaseActor addMessageHandler(Class<T> clazz, Consumer<T> consumer) {
        messageHandler.put(clazz, consumer);
        return this;
    }

    /**
     * Message channels that the actor are subscribed to.
     *
     * @return
     */
    protected Collection<Class<?>> channelSubscriptions() {
        return null;
    }

    /**
     * Mark if this actor has been inited.
     *
     * @since template-v2.6.r3
     */
    protected boolean actorInited = false;

    /**
     * Convenient method to perform initializing work.
     *
     * @throws Exception
     */
    protected void initActor() throws Exception {
        if (!actorInited) {
            // subscribe to message channels
            Collection<Class<?>> msgChannels = channelSubscriptions();
            if (msgChannels != null && msgChannels.size() > 0) {
                msgChannels.forEach(
                        (clazz) -> getContext().system().eventStream().subscribe(self(), clazz));
            }
            actorInited = true;
        }
    }

    /**
     * Mark if this actor has been destroyed.
     *
     * @since template-v2.6.r3
     */
    protected boolean actorDestroyed = false;

    /**
     * Convenient method to perform cleanup work.
     *
     * @throws Exception
     */
    protected void destroyActor() throws Exception {
        if (!actorDestroyed) {
            // unsubscribe from all message channels
            getContext().system().eventStream().unsubscribe(self());
            actorDestroyed = true;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void preStart() throws Exception {
        initActor();

        super.preStart();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postStop() throws Exception {
        try {
            destroyActor();
        } catch (Exception e) {
            Logger.warn("{" + getActorPath() + "} " + e.getMessage(), e);
        } finally {
            super.postStop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void onReceive(Object message) throws Throwable {
        if (message == null) {
            return;
        }

        AtomicBoolean handled = new AtomicBoolean(false);
        Class msgClazz = message.getClass();
        Consumer exactConsumer = messageHandler.get(msgClazz);
        if (exactConsumer != null) {
            // exact match
            exactConsumer.accept(message);
            handled.set(true);
        } else {
            messageHandler.forEach((Class clazz, Consumer consumer) -> {
                // match interface/sub-class
                if (clazz.isAssignableFrom(msgClazz)) {
                    consumer.accept(message);
                    handled.set(true);
                }
            });
        }

        if (!handled.get()) {
            unhandled(message);
        }
    }

}
