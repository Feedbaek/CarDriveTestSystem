package seoul.AutoEveryDay.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RentCarDto {
    private Long id;
    @NotNull
    private Long carId;
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate pickupDate;
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate returnDate;
}