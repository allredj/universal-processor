package org.example;

import org.apache.hc.core5.http.ParseException;

import java.lang.reflect.InvocationTargetException;

public class App 
{
    public static void main( String[] args ) throws ParseException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> clazz = null;
        int argOffset = 1;

        String opArg = args[argOffset];
        System.out.println("opArg:" + opArg);

        // get class by name of operation
        try {
            clazz = Class.forName("org.example." + opArg);
        } catch (ClassNotFoundException e) {
            System.out.println(opArg + " not found. Getting operation from ChatGPT.");
            String body = ChatGPTClient.getGenericBody(opArg);
            ClassWriter.write(opArg, body);
            System.out.println(opArg + " added. Please run again.");
            return;
        }

        Binary multiplier = (Binary) clazz.getDeclaredConstructor().newInstance();
        int a = Integer.parseInt(args[argOffset + 1]);
        int b = Integer.parseInt(args[argOffset + 2]);
        int result = multiplier.op(a, b);
        System.out.println(opArg + " " + a + " " + b + " = " + result);
    }
}
