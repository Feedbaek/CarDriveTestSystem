package seoul.AutoEveryDay.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RentCarReq {
    private String number;
    private LocalDate pickupDate;
    private LocalDate returnDate;
}
