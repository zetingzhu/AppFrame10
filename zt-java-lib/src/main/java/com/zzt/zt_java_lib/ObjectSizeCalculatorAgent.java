package com.zzt.zt_java_lib;

import java.lang.instrument.Instrumentation;

public class ObjectSizeCalculatorAgent {
    private static volatile Instrumentation globalInstrumentation;

    public static void premain(String agentArgs, Instrumentation inst) {
        globalInstrumentation = inst;
    }

    public static long getObjectSize(Object object) {
        if (globalInstrumentation == null) {
            throw new IllegalStateException("Instrumentation not initialized.");
        }
        return globalInstrumentation.getObjectSize(object);
    }
}    