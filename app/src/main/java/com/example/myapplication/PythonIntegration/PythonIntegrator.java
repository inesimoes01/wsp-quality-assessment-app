package com.example.myapplication.PythonIntegration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PythonIntegrator {

//    public static void main(String[] args) {
//        // Call Python script with arguments
////        String result = callPythonScript("app/src/main/python/main.py", "1", "5");
////        System.out.println("Result from Python script: " + result);
//    }



    public static String callPythonScript(String scriptName, String... args) {
        try {
            // Build the command to call Python script
            ProcessBuilder pb = new ProcessBuilder("python", scriptName);
            for (String arg : args) {
                pb.command().add(arg);
            }

            // Start the process and get input/output streams
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            // Read the output of the Python script
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            // Wait for the process to finish and return the result
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return output.toString();
            } else {
                throw new IOException("Python script execution failed with exit code " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
