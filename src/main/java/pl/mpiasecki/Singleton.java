package pl.mpiasecki;

import java.io.Serializable;

public class Singleton implements Cloneable, Serializable {

    private static volatile Singleton instance;

    private Singleton() {
        if (null != instance) {
            throw new UnsupportedOperationException("Instance already exists. Use getInstance instead");
        }
    }

    public static Singleton getInstance() {
        if (null == instance) {
            synchronized (Singleton.class) {
                if (null == instance) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }

    @Override
    public Singleton clone() {
        return getInstance();
    }

    private Object readResolve() {
        return getInstance();
    }

}
