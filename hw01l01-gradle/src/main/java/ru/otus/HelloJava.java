package ru.otus;

import com.google.common.base.Joiner;

import java.util.Arrays;
import java.util.List;

public class HelloJava {
    public static void main(String[] args) {
        List<String> names = Arrays.asList("John", "Mark", "Paul");
        String result = Joiner.on(" / ").join(names);
    }
}
