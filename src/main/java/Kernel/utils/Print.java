package Kernel.utils;

import java.util.List;

public class Print {



    public static <T> void printList(List<T> list) {
        for (T element : list) {
            System.out.println(element);
            System.out.println("===========================================================");
        }
    }


    public static <T> void printArray(T[] array) {
        for (T element : array) {
            System.out.println(element);
            System.out.println("===========================================================");
        }
    }
    
    
}
