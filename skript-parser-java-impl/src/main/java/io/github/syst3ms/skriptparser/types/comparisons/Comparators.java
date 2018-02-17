package io.github.syst3ms.skriptparser.types.comparisons;

import io.github.syst3ms.skriptparser.types.conversions.Converters;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class Comparators {
    public static final Comparator<Object, Object> EQUALS_COMPARATOR = new Comparator<Object, Object>() {
        @Override
        public Relation apply(Object o, Object o2) {
            return Relation.get(Objects.equals(o, o2));
        }

        @Override
        public boolean supportsOrdering() {
            return false;
        }
    };

    private Comparators() {}

    public final static Collection<ComparatorInfo<?, ?>> comparators = new ArrayList<>();

    /**
     * Registers a {@link Comparator}.
     *
     * @param t1
     * @param t2
     * @param c
     * @throws IllegalArgumentException if any given class is equal to <code>Object.class</code>
     */
    public static <T1, T2> void registerComparator(final Class<T1> t1, final Class<T2> t2, final Comparator<T1, T2> c) {
        if (t1 == Object.class && t2 == Object.class)
            throw new IllegalArgumentException("You must not add a comparator for Objects");
        comparators.add(new ComparatorInfo<>(t1, t2, c));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <F, S> Relation compare(final F o1, final S o2) {
        if (o1 == null || o2 == null)
            return Relation.NOT_EQUAL;
        @SuppressWarnings("null")
        final Comparator<? super F, ? super S> c = getComparator((Class<F>) o1.getClass(), (Class<S>) o2.getClass());
        if (c == null)
            return Relation.NOT_EQUAL;
        return c.apply(o1, o2);
    }

    private final static java.util.Comparator<Object> javaComparator = (o1, o2) -> compare(o1, o2).getRelation();

    public static java.util.Comparator<Object> getJavaComparator() {
        return javaComparator;
    }

    private final static Map<Pair<Class<?>, Class<?>>, Comparator<?, ?>> comparatorsQuickAccess = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static <F, S> Comparator<? super F, ? super S> getComparator(final Class<F> f, final Class<S> s) {
        final Pair<Class<?>, Class<?>> p = new Pair<>(f, s);
        if (comparatorsQuickAccess.containsKey(p))
            return (Comparator<? super F, ? super S>) comparatorsQuickAccess.get(p);
        final Comparator<?, ?> comp = getComparator_i(f, s);
        comparatorsQuickAccess.put(p, comp);
        return (Comparator<? super F, ? super S>) comp;
    }

    @SuppressWarnings("unchecked")
    private static <F, S> Comparator<?, ?> getComparator_i(final Class<F> f, final Class<S> s) {

        // perfect match
        for (final ComparatorInfo<?, ?> info : comparators) {
            if (info.getFirstClass().isAssignableFrom(f) && info.getSecondClass().isAssignableFrom(s)) {
                return info.getComparator();
            } else if (info.getFirstClass().isAssignableFrom(s) && info.getSecondClass().isAssignableFrom(f)) {
                return new InverseComparator<F, S>((Comparator<? super S, ? super F>) info.getComparator());
            }
        }

        // same class but no comparator
        if (s == f && f != Object.class && s != Object.class) {
            return EQUALS_COMPARATOR;
        }

        final boolean[] trueFalse = {true, false};
        Function<? super F, ?> c1;
        Function<? super S, ?> c2;

        // single conversion
        for (final ComparatorInfo<?, ?> info : comparators) {
            for (final boolean first : trueFalse) {
                if (info.getType(first).isAssignableFrom(f)) {
                    c2 = Converters.getConverter(s, info.getType(!first));
                    if (c2 != null) {
                        return first ? new ConvertedComparator<>(info.getComparator(), c2) : new InverseComparator<>(new ConvertedComparator<>(c2,
                                info.getComparator()
                        ));
                    }
                }
                if (info.getType(first).isAssignableFrom(s)) {
                    c1 = Converters.getConverter(f, info.getType(!first));
                    if (c1 != null) {
                        return !first ? new ConvertedComparator<>(c1, info.getComparator()) : new InverseComparator<>(new ConvertedComparator<>(
                                info.getComparator(), c1));
                    }
                }
            }
        }

        // double conversion
        for (final ComparatorInfo<?, ?> info : comparators) {
            for (final boolean first : trueFalse) {
                c1 = Converters.getConverter(f, info.getType(first));
                c2 = Converters.getConverter(s, info.getType(!first));
                if (c1 != null && c2 != null) {
                    return first ? new ConvertedComparator<>(c1, info.getComparator(), c2) : new InverseComparator<>(new ConvertedComparator<>(c2,
                            info.getComparator(), c1));
                }
            }
        }

        return null;
    }

    private final static class ConvertedComparator<T1, T2> implements Comparator<T1, T2> {

        @SuppressWarnings("rawtypes")
        private final Comparator c;
        @SuppressWarnings("rawtypes")
        private final Function c1, c2;

        public ConvertedComparator(final Function<? super T1, ?> c1, final Comparator<?, ?> c) {
            this.c1 = c1;
            this.c = c;
            this.c2 = null;
        }

        public ConvertedComparator(final Comparator<?, ?> c, final Function<? super T2, ?> c2) {
            this.c1 = null;
            this.c = c;
            this.c2 = c2;
        }

        public ConvertedComparator(final Function<? super T1, ?> c1, final Comparator<?, ?> c, final Function<? super T2, ?> c2) {
            this.c1 = c1;
            this.c = c;
            this.c2 = c2;
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        @Override
        public Relation apply(final T1 o1, final T2 o2) {
            final Function c1 = this.c1;
            final Object t1 = c1 == null ? o1 : c1.apply(o1);
            if (t1 == null)
                return Relation.NOT_EQUAL;
            final Function c2 = this.c2;
            final Object t2 = c2 == null ? o2 : c2.apply(o2);
            if (t2 == null)
                return Relation.NOT_EQUAL;
            return c.apply(t1, t2);
        }

        @Override
        public boolean supportsOrdering() {
            return c.supportsOrdering();
        }

        @Override
        public String toString() {
            return "ConvertedComparator(" + c1 + "," + c + "," + c2 + ")";
        }

    }
}
