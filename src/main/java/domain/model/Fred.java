package domain.model;

public class Fred extends Thread {
    public Fred(Runnable target) {
        super(target);
        System.out.println("Ich bin ein " + target.getClass() + " Fred");
    }
}