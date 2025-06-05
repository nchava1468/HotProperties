package com.hotproperties.hotproperties.util;

import com.hotproperties.hotproperties.entity.Property;
import com.hotproperties.hotproperties.repository.PropertyRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Component
public class PropertyCsvLoader implements CommandLineRunner {

    private final PropertyRepository repository;

    public PropertyCsvLoader(PropertyRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {

        try (
                InputStreamReader reader = new InputStreamReader(
                        new ClassPathResource("homedata.csv").getInputStream(), StandardCharsets.UTF_8);
                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())
        ) {
            for (CSVRecord record : csvParser) {
                try {
                    String title = record.get(0).trim();
                    int price = Integer.parseInt(record.get(1).replaceAll("[^0-9]", ""));
                    String location = record.get(2).trim();
                    int size = Integer.parseInt(record.get(3).replaceAll("[^0-9]", ""));
                    String description = record.get(4).trim();

                    Property property = new Property(title, location, price, size, description);
                    repository.save(property);

                } catch (Exception e) {
                    System.err.println(" Record " + record.getRecordNumber() + " failed:");
                    e.printStackTrace();
                }
            }

            System.out.println(" CSV import complete!");

        } catch (Exception e) {
            System.err.println(" Error loading CSV file!");
            e.printStackTrace();
        }
    }
}
