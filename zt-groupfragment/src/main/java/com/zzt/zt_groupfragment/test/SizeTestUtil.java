package com.zzt.zt_groupfragment.test;

import sizeof.agent.SizeOfAgent;

/**
 * @author: zeting
 * @date: 2025/4/10
 */
public class SizeTestUtil {

    public static String[] randomStrings(int num) {
        String[] result = new String[num];
        for (int i = 0; i < result.length; i++)
            result[i] = "" + Math.random();
        return result;
    }

    public static String randomString() {
        return "" + Math.random();
    }

    public static void main( ) {
        // NB: The JVM maintains an internal pool of String objects.
        // These "internal" String objects won't get counted, so you might
        // see a count of 0 for some strings
        System.out.println("String: " + SizeOfAgent.fullSizeOf("Hello World"));
        System.out.println("int: " + SizeOfAgent.fullSizeOf(2));
        System.out.println("double: " + SizeOfAgent.fullSizeOf(2.3));
        System.out.println("float: " + SizeOfAgent.fullSizeOf(1.5f));
        System.out.println("Object: " + SizeOfAgent.fullSizeOf(new SizeTest()));
        System.out.println("int array: " + SizeOfAgent.fullSizeOf(new int[]{1, 2, 3, 4}));
        System.out.println("String array: " + SizeOfAgent.fullSizeOf(new String[]{"1", "2", "3", "4"}));
        System.out.println("Object array: " + SizeOfAgent.fullSizeOf(new SizeTest[]{new SizeTest()}));
        System.out.println("random String: " + SizeOfAgent.fullSizeOf(randomString()));
        System.out.println("random String array[1]: " + SizeOfAgent.fullSizeOf(randomStrings(1)));
        System.out.println("random String array[10]: " + SizeOfAgent.fullSizeOf(randomStrings(10)));
        System.out.println("random String array[1000]: " + SizeOfAgent.fullSizeOf(randomStrings(1000)));
        System.out.println("random String array[2000]: " + SizeOfAgent.fullSizeOf(randomStrings(2000)));
    }
}
