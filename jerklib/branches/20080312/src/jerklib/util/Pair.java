package jerklib.util;

/**
 * @author <a href="mailto:joeo@enigmastation.com">Joseph B. Ottinger</a>
 * @version $Revision$
 */
public class Pair<A, B> {
    public final A first;
    public final B second;

    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Pair && obj.hashCode() == hashCode()) {
            Pair<A, B> other = (Pair<A, B>) obj;
            return (first != null ? first.equals(other.first) : other.first == null) &&
                    (second != null ? second.equals(other.second) : other.second == null);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = (first != null ? first.hashCode() ^ 42 : 0);
        hash += (second != null ? second.hashCode() : 0);
        return hash;
    }
}