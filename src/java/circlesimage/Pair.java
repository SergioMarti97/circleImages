package circlesimage;

public class Pair<B, T> {

    private B first;

    private T second;

    public Pair(B first, T second) {
        this.first = first;
        this.second = second;
    }

    public B getFirst() {
        return first;
    }

    public T getSecond() {
        return second;
    }

    public void setFirst(B first) {
        this.first = first;
    }

    public void setSecond(T second) {
        this.second = second;
    }

}
