package seoul.AutoEveryDay.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import seoul.AutoEveryDay.dto.DriveHistoryDto;
import seoul.AutoEveryDay.dto.TrackDto;
import seoul.AutoEveryDay.entity.Car;
import seoul.AutoEveryDay.entity.DriveHistory;
import seoul.AutoEveryDay.entity.Track;
import seoul.AutoEveryDay.entity.User;
import seoul.AutoEveryDay.repository.CarRepository;
import seoul.AutoEveryDay.repository.DriveHistoryRepository;
import seoul.AutoEveryDay.repository.TrackRepository;
import seoul.AutoEveryDay.repository.UserRepository;

import java.time.Instant;

@Service
@Slf4j(topic = "DriveService")
@RequiredArgsConstructor
public class DriveService {
    private final DriveHistoryRepository driveHistoryRepository;
    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final TrackRepository trackRepository;

    public DriveHistoryDto addDriveHistory(DriveHistoryDto driveHistoryDto) {
        User user = userRepository.findById(driveHistoryDto.getUserId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다.")
        );
        Car car = carRepository.findById(driveHistoryDto.getCarId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 차량입니다.")
        );
        Track track = trackRepository.findById(driveHistoryDto.getTrackId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 트랙입니다.")
        );

        DriveHistory driveHistory = DriveHistory.builder()
                .user(user)
                .car(car)
                .track(track)
                .build();
        try {
            driveHistoryRepository.save(driveHistory);
        } catch (Exception e) {
            log.error("주행 내역 저장 실패", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "주행 내역 저장 실패");
        }
        driveHistoryDto.setId(driveHistory.getId());
        driveHistoryDto.setDate(Instant.now().toString());

        return driveHistoryDto;
    }

    public DriveHistoryDto getDriveHistory(Long id) {
        DriveHistory driveHistory = driveHistoryRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 주행 내역입니다.")
        );
        return DriveHistoryDto.builder()
                .id(driveHistory.getId())
                .userId(driveHistory.getUser().getId())
                .carId(driveHistory.getCar().getId())
                .trackId(driveHistory.getTrack().getId())
                .date(driveHistory.getDate().toString())
                .build();
    }

    public DriveHistoryDto deleteDriveHistory(Long id) {
        DriveHistory driveHistory = driveHistoryRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 주행 내역입니다.")
        );
        try {
            driveHistoryRepository.delete(driveHistory);
        } catch (Exception e) {
            log.error("주행 내역 삭제 실패", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "주행 내역 삭제 실패");
        }
        return DriveHistoryDto.builder()
                .id(driveHistory.getId())
                .userId(driveHistory.getUser().getId())
                .carId(driveHistory.getCar().getId())
                .trackId(driveHistory.getTrack().getId())
                .date(driveHistory.getDate().toString())
                .build();
    }
}
