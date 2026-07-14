package org.example.util;
import java.util.Scanner;

public class InputValidator {
    public static double getDouble(Scanner sc) {
        while (!sc.hasNextDouble()) {
            System.out.print("Invalid input. Enter amount: ");
            sc.next();
        }
        return sc.nextDouble();
    }
}
