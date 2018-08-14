import org.junit.Before;
import org.junit.Test;
import pl.mpiasecki.Singleton;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class SingletonTest {

    private Singleton instance1;
    private Singleton instance2;

    @Before
    public void initTest() {
        instance1 = null;
        instance2 = null;
    }

    @Test
    public void shouldReturnSameInstance() {
        instance1 = Singleton.getInstance();
        instance2 = Singleton.getInstance();
        assertEquals(instance1, instance2);
    }

    @Test(expected = InvocationTargetException.class)
    public void shouldThrowInvocationTargetException() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        instance1 = Singleton.getInstance();
        Constructor<Singleton> constructor = Singleton.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        instance2 = constructor.newInstance();
    }

    @Test
    public void shouldReturnSameInstanceSerialization() throws IOException, ClassNotFoundException {
        instance1 = Singleton.getInstance();
        instance2 = null;

        try (FileOutputStream fos = new FileOutputStream("./singleton.ser");
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(instance1);
        }

        try (FileInputStream fis = new FileInputStream("./singleton.ser");
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            instance2 = (Singleton) ois.readObject();
        }

        assertEquals(instance1, instance2);
    }

    @Test
    public void shouldReturnSameInstanceCloneable() {
        instance1 = Singleton.getInstance();
        instance2 = instance1.clone();
        assertEquals(instance1, instance2);
    }

    @Test
    public void shouldReturnSameInstanceParallel() throws InterruptedException {
        Runnable task1 = () -> instance1 = Singleton.getInstance();
        Runnable task2 = () -> instance2 = Singleton.getInstance();
        int failed = 0;
        for (int i = 0; i < 1000; i++) {
            ExecutorService service = Executors.newFixedThreadPool(2);
            service.submit(task1);
            service.submit(task2);
            service.shutdown();
            service.awaitTermination(1, TimeUnit.SECONDS);
            if (null == instance1 || !instance1.equals(instance2)) {
                failed++;
            }
        }
        assertEquals(0, failed);
    }

}
