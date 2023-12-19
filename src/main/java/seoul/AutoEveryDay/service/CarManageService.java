package seoul.AutoEveryDay.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import seoul.AutoEveryDay.dto.CarDto;
import seoul.AutoEveryDay.dto.CarModelReq;
import seoul.AutoEveryDay.dto.CarModelRes;
import seoul.AutoEveryDay.dto.ModelImageChangeReq;
import seoul.AutoEveryDay.entity.Car;
import seoul.AutoEveryDay.entity.CarModel;
import seoul.AutoEveryDay.repository.CarModelRepository;
import seoul.AutoEveryDay.repository.CarRepository;
import seoul.AutoEveryDay.repository.RentCarRepository;
import seoul.AutoEveryDay.utils.Converter;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j(topic = "CarManageService")
@Transactional
@RequiredArgsConstructor
public class CarManageService {
    private final CarRepository carRepository;
    private final CarModelRepository carModelRepository;
    private final RentCarRepository rentCarRepository;
    private final Converter converter;

    /** <h3>차량 등록.</h3>
     * 이미 존재하는 차량 번호면 ResponseStatusException 발생 */
    public CarDto createCar(CarDto carDto) {
        // todo: 차량 번호 정규식 검사, 차량 종류 검사, 차량 상태 검사
        if (carRepository.existsByNumber(carDto.getNumber())) {
            log.error("이미 존재하는 차량 번호입니다.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 존재하는 차량 번호입니다.");
        }
        CarModel carModel = carModelRepository.findByName(carDto.getModel()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 차량 종류입니다."));
        Car car = Car.builder()
                .number(carDto.getNumber())
                .carModel(carModel)
                .status(carDto.getStatus())
                .comment(carDto.getComment())
                .build();
        try {
            carRepository.save(car);
        } catch (Exception e) {
            log.error("차량 저장 실패", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "차량 저장 실패");
        }
        return carDto;
    }

    /** <h3>차량 정보 조회.</h3>
     * 차량 번호가 존재하지 않으면 ResponseStatusException 발생 */
    public CarDto getCar(String number) {
        Car car = carRepository.findByNumber(number).orElse(null);
        if (car == null) {
            return null;
        }
        return CarDto.builder()
                .id(car.getId())
                .number(car.getNumber())
                .model(car.getCarModel().getName())
                .status(car.getStatus())
                .comment(car.getComment())
                .image(car.getCarModel().getImage())
                .build();
    }
    public Car getCar(Long id) {
        return carRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 차량 번호입니다."));
    }

    /** <h3>모든 차량 정보 조회.</h3> */
    public List<CarDto> getAllCar() {
        List<Car> carList = carRepository.findAll();
        List<CarDto> carDtoList = new ArrayList<>();

        carList.forEach(car -> {
            carDtoList.add(CarDto.builder()
                    .id(car.getId())
                    .number(car.getNumber())
                    .model(car.getCarModel().getName())
                    .status(car.getStatus())
                    .comment(car.getComment())
                    .image(car.getCarModel().getImage())
                    .build());
        });

        return carDtoList;
    }

    /** <h3>차량 검색.</h3>
     * 입력받은 번호 포함하는 차량 검색. 없으면 빈 리스트 반환
    * */
    public List<CarDto> searchCarDto(String number) {
        List<CarDto> carDtoList = new ArrayList<>();

        if (number == null || number.isEmpty()) {
            return getAllCar();
        } else {
            List<Car> carList = carRepository.findByNumberContaining(number);
            carList.forEach(car -> {
                carDtoList.add(CarDto.builder()
                        .id(car.getId())
                        .number(car.getNumber())
                        .model(car.getCarModel().getName())
                        .status(car.getStatus())
                        .comment(car.getComment())
                        .image(car.getCarModel().getImage())
                        .build());
            });
        }
        return carDtoList;
    }
    public List<CarDto> searchCarDto(Long carModel, String number) {
        List<CarDto> carDtoList = new ArrayList<>();
        List<Car> carList;

        if (number == null || number.isEmpty()) {
            carList = carRepository.findByCarModel_Id(carModel);
        } else {
            carList = carRepository.findByCarModel_IdAndNumberContaining(carModel, number);
        }

        carList.forEach(car -> {
            carDtoList.add(CarDto.builder()
                    .id(car.getId())
                    .number(car.getNumber())
                    .model(car.getCarModel().getName())
                    .status(car.getStatus())
                    .comment(car.getComment())
                    .image(car.getCarModel().getImage())
                    .build());
        });

        return carDtoList;
    }

    /** <h3>차량 정보 수정.</h3>
     * 차량 번호가 존재하지 않으면 ResponseStatusException 발생 */
    public CarDto updateCar(CarDto carDto) {
        Car car = carRepository.findByNumber(carDto.getNumber()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 차량 번호입니다."));
        car.setStatus(carDto.getStatus());
        car.setComment(carDto.getComment());

        return CarDto.builder()
                .id(car.getId())
                .number(car.getNumber())
                .model(car.getCarModel().getName())
                .status(car.getStatus())
                .comment(car.getComment())
                .image(car.getCarModel().getImage())
                .build();
    }

    /** <h3>차량 삭제.</h3>
     * 차량 번호가 존재하지 않으면 ResponseStatusException 발생 */
    public CarDto deleteCar(String number) {
        Car car = carRepository.findByNumber(number).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 차량 번호입니다."));
        if (rentCarRepository.existsByCar_IdAndPickupDateGreaterThanEqual(car.getId(), LocalDate.now())) {
            log.error("대여 중인 차량은 삭제할 수 없습니다.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "대여 중인 차량은 삭제할 수 없습니다.");
        }
        try {
            carRepository.delete(car);
        } catch (Exception e) {
            log.error("차량 삭제 실패", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "차량 삭제 실패");
        }

        return CarDto.builder()
                .id(car.getId())
                .number(car.getNumber())
                .model(car.getCarModel().getName())
                .status(car.getStatus())
                .comment(car.getComment())
                .image(car.getCarModel().getImage())
                .build();
    }

    public List<String> getAllCarModelNames() {
        List<CarModel> carModelList = carModelRepository.findAllByOrderById();
        List<String> carModelNameList = new ArrayList<>();

        carModelList.forEach(carModel -> {
            carModelNameList.add(carModel.getName());
        });

        return carModelNameList;
    }

    public List<CarModelRes> getAllCarModelDto() {
        List<CarModel> carModelList = carModelRepository.findAllByOrderById();
        List<CarModelRes> carModelResList = new ArrayList<>();

        carModelList.forEach(carModel -> {
            carModelResList.add(CarModelRes.builder()
                    .id(carModel.getId())
                    .name(carModel.getName())
                    .image(carModel.getImage())
                    .build());
        });

        return carModelResList;
    }

    public boolean isJPEG(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.equals("image/jpeg");
    }

    public boolean isActualJPEG(MultipartFile file) {
        if (!isJPEG(file)) {
            return false;
        }
        try (InputStream inputStream = file.getInputStream()) {
            byte[] firstTwoBytes = new byte[2];
            byte[] lastTwoBytes = new byte[2];

            inputStream.read(firstTwoBytes);

            if (firstTwoBytes[0] == (byte) 0xFF && firstTwoBytes[1] == (byte) 0xD8) {
                inputStream.skip(file.getSize() - 4);
                inputStream.read(lastTwoBytes);
                return lastTwoBytes[0] == (byte) 0xFF && lastTwoBytes[1] == (byte) 0xD9;
            }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "올바르지 않은 이미지 파일입니다.");
        }
        return false;
    }

    public CarModelRes createCarModel(CarModelReq carModelReq) {
        if (carModelRepository.existsByName(carModelReq.getName())) {
            log.error("이미 존재하는 차량 모델입니다.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 존재하는 차량 모델입니다.");
        }
        if (!isActualJPEG(carModelReq.getImage())) {
            log.error("올바르지 않은 이미지 파일입니다.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "올바르지 않은 이미지 파일입니다.");
        }

        CarModel carModel = CarModel.builder()
                .name(carModelReq.getName())
                .build();
        try {
            carModelRepository.save(carModel);
            String imageUrl = converter.convertImgToUrl(carModelReq.getImage(), "/car", carModel.getId() + ".jpeg");
            carModel.setImage(imageUrl);
        } catch (Exception e) {
            log.error("차량 모델 저장 실패", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "차량 모델 저장 실패");
        }
        return CarModelRes.builder()
                .id(carModel.getId())
                .name(carModel.getName())
                .image(carModel.getImage())
                .build();
    }

    /**
     * <h3>차량 모델 정보 수정. 이미지 수정</h3>
     * */
    public CarModelRes updateCarModel(ModelImageChangeReq carModelReq) {
        CarModel carModel = carModelRepository.findById(carModelReq.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 차량 모델입니다."));

        if (!isActualJPEG(carModelReq.getImage())) {
            log.error("올바르지 않은 이미지 파일입니다.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "올바르지 않은 이미지 파일입니다.");
        }

        String imageUrl = converter.convertImgToUrl(carModelReq.getImage(), "/car", carModel.getId() + ".jpeg");
        carModel.setImage(imageUrl);

        return CarModelRes.builder()
                .id(carModel.getId())
                .name(carModel.getName())
                .image(carModel.getImage())
                .build();
    }

    public CarModelRes deleteCarModel(Long id) {
        CarModel carModel = carModelRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 차량 모델입니다."));
        if (carRepository.existsByCarModel_Id(id)) {
            log.error("차량 모델에 속한 차량이 존재합니다.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "차량 모델에 속한 차량이 존재합니다.");
        }
        try {
            carModelRepository.delete(carModel);
        } catch (Exception e) {
            log.error("차량 모델 삭제 실패", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "차량 모델 삭제 실패");
        }

        return CarModelRes.builder()
                .id(carModel.getId())
                .name(carModel.getName())
                .image(carModel.getImage())
                .build();
    }
}
