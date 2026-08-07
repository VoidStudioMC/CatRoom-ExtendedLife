package catserver.server.executor;

import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionExecutor implements EventExecutor {
    private final Method method;
    private final Class<? extends Event> eventClass;

    public ReflectionExecutor(Method method, Class<? extends Event> eventClass) {
        this.method = method;
        this.eventClass = eventClass;
    }

    @Override
    public void execute(Listener listener, Event event) throws EventException {
        try {
            if (!eventClass.isAssignableFrom(event.getClass())) {
                return;
            }
            // Spigot start
            this.method.invoke(listener, event);
            // Spigot end
        } catch (InvocationTargetException ex) {
            throw new EventException(ex.getCause());
        } catch (Throwable t) {
            throw new EventException(t);
        }
    }
}
