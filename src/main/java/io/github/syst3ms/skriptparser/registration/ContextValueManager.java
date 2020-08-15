package io.github.syst3ms.skriptparser.registration;

import io.github.syst3ms.skriptparser.lang.TriggerContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ContextValueManager {

    private static final List<ContextValue<?>> contextValues = new ArrayList<>();

    static void register(SkriptRegistration reg) {
        contextValues.addAll(reg.getContextValues());
    }

    /**
     * @return a list of all currently registered context values
     */
    public static List<ContextValue<?>> getContextValues() {
        return contextValues;
    }

    /**
     * A class containing info about a context value.
     */
    public static class ContextValue<T> {
        private final Class<? extends TriggerContext> context;
        private final Class<T> type;
        private final String name;
        private final Function<TriggerContext, T[]> contextFunction;
        private final int timeline;

        public static int PAST = -1;
        public static int PRESENT = 0;
        public static int FUTURE = 1;

        /**
         * Construct a context value.
         *
         * @param context         the specific {@link TriggerContext} class
         * @param name            the suffix of this value
         * @param contextFunction the function to apply to the context
         */
        public ContextValue(Class<? extends TriggerContext> context, Class<T> type, String name, Function<TriggerContext, T[]> contextFunction) {
            this(context, type, name, contextFunction, 0);
        }

        /**
         * Construct a context value.
         *
         * @param context         the specific {@link TriggerContext} class
         * @param name            the suffix of this value
         * @param contextFunction the function to apply to the context
         * @param timeline        whether this value represent a present, past or future state
         */
        public ContextValue(Class<? extends TriggerContext> context, Class<T> type, String name, Function<TriggerContext, T[]> contextFunction, int timeline) {
            this.context = context;
            this.type = type;
            this.name = name.toLowerCase();
            this.contextFunction = contextFunction;
            this.timeline = normalTimeline(timeline);
        }

        public Class<? extends TriggerContext> getContext() {
            return context;
        }

        /**
         * @return the returned type of this context value
         */
        public Class<T> getType() {
            return type;
        }

        /**
         * Returns the name of the context value.
         * If the name, for example, is 'test', the use case will be 'context-test'
         * @return the name of this context value
         */
        public String getName() {
            return name;
        }

        /**
         * @return the function that needs to be applied in order to get the context value
         */
        public Function<TriggerContext, T[]> getContextFunction() {
            return contextFunction;
        }

        /**
         * Returns {@code -1} for the past, {@code 0} for the present and {@code 1} for the future.
         * @return whether this happens in the present, past or future
         */
        public int getTimeline() {
            return timeline;
        }

        public boolean matches(Class<? extends TriggerContext> handledContext, String name, int timeline) {
            return handledContext.equals(this.context)
                    && this.name.equals(name)
                    && this.timeline == normalTimeline(timeline);
        }

        private static int normalTimeline(int timeline) {
            return Integer.compare(timeline, 0);
        }
    }

}