package com.codeplay.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "puzzles")
public class Puzzle {
    @Id
    private String id;

    private int levelNumber;
    private String title;
    private String description;
    private String type;          // LOGIC, CODE_FIX, OUTPUT_PREDICT, FILL_BLANK
    private int unlockRequirement; // min points needed
    private String prerequisiteLevel;
    private List<String> hints;
    private String solution;
    private String codeTemplate;
    private int points;
    private String difficulty;
    private boolean active;
    private LocalDateTime createdAt;
}
