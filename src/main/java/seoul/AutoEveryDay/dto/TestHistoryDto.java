package seoul.AutoEveryDay.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestHistoryDto {
    private Long id;
    @NotBlank
    private Long testTrackId;
    @NotBlank
    private LocalDate date;
}
