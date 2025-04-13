package com.github.bookproject.readingRecord.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProgressUpdateRequestDTO {

    @DecimalMin("0.0")
    @DecimalMax("1.0")
    private float progress;
}
