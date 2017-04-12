package com.afym;

import java.util.Map;

public class ReadEnvVariables {

    public static void main(String[] args){
        Map<String, String> variables = System.getenv();

        for (String keyName : variables.keySet()) {
            System.out.format("%s=%s%n", keyName, variables.get(keyName));
        }
    }
}
