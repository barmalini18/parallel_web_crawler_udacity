package com.udacity.webcrawler.profiler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/**
 * A method interceptor that checks whether {@link Method}s are annotated with the {@link Profiled}
 * annotation. If they are, the method interceptor records how long the method invocation took.
 */
final class ProfilingMethodInterceptor implements InvocationHandler {

    private final Object targetObject;
    private final ProfilingState state;
    private final Clock clock;

    // TODO: You will need to add more instance fields and constructor arguments to this class.
    ProfilingMethodInterceptor(Object targetObject, ProfilingState state, Clock clock) {
        this.targetObject = Objects.requireNonNull(targetObject);
        this.state = Objects.requireNonNull(state);
        this.clock = Objects.requireNonNull(clock);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // TODO: This method interceptor should inspect the called method to see if it is a profiled
        //       method. For profiled methods, the interceptor should record the start time, then
        //       invoke the method using the object that is being profiled. Finally, for profiled
        //       methods, the interceptor should record how long the method call took, using the
        //       ProfilingState methods.
        //return null;
        Object invoked = null;
        boolean profiled = method.getAnnotation(Profiled.class) != null;
        Instant start = clock.instant();

        if (profiled) {
            try {
                invoked = method.invoke(targetObject, args);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } finally {

                Duration duration = Duration.between(start, clock.instant());
                state.record(targetObject.getClass(), method, duration);

            }
        } else {
            try {
                invoked = method.invoke(targetObject, args);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        }

        return invoked;
    }
}
