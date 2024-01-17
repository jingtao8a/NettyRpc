package org.jingtao8a.extension;

import org.junit.Test;
import static org.junit.Assert.*;
import org.jingtao8a.serializer.Serializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExtensionTest {
    @Test
    public void test1(){
        Map<String, Holder<Integer>> map = new HashMap<>();
        map.put("1", new Holder<>());
        Holder holder = map.get("1");
        holder.set(1);
        assertEquals(new Integer(1), map.get("1").get());
    }

    @Test
    public void test2() {

        List<Thread> threadList = new ArrayList<>();
        String[] strings = {"hessian", "kryo", "protostuff"};
        threadList.add(new Thread(()->{
            for (String str : strings) {
                Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(str);
                System.out.println(serializer.getSerializerAlgorithm());
            }
        }));
        threadList.add(new Thread(()->{
            for (String str : strings) {
                Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(str);
                System.out.println(serializer.getSerializerAlgorithm());
            }
        }));
        threadList.add(new Thread(()->{
            for (String str : strings) {
                Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(str);
                System.out.println(serializer.getSerializerAlgorithm());
            }
        }));

        for (Thread thread : threadList) {
            thread.start();
        }
        for (Thread thread : threadList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
