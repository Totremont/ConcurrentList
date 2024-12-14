package utn.totremont;

public class Node
{
    private Object value;
    private int key;
    private Node next;


    public Node(Object value, Node next) {
        this.value = value;
        this.key = value.hashCode();
        this.next = next;
    }

    public Node getNext() {
        return next;
    }

    public int getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    public void setNext(Node next) {
        this.next = next;
    }
}
