package com.kai.ticketsystemformutithreadspractice.controller;


import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

@RestController
public class GenerateJmeterCsvController {


    @Operation(summary = "Generate Jmeter CSV file")
    @GetMapping("/generateJmeterCsv")
    public void generateJmeterCsv() {
        try (FileWriter writer = new FileWriter("data.csv")) {
            writer.append("type");
            writer.append(",");
            writer.append("buyerId");
            writer.append("\n");

            Random random = new Random();
            for (int i = 0; i < 1000; i++) {
                // Generate a random type between 1 and 2
                int type = random.nextInt(2) + 1;
                String buyerId = Integer.toString(i+1);

                writer.append(Integer.toString(type));
                writer.append(",");
                writer.append(buyerId);
                writer.append("\n");
            }

            System.out.println("CSV file created successfully.");
        } catch (IOException e) {
            System.out.println("Error while creating CSV file: " + e.getMessage());
        }
    }

}
