package seoul.AutoEveryDay.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import seoul.AutoEveryDay.dto.*;
import seoul.AutoEveryDay.entity.Car;
import seoul.AutoEveryDay.entity.RentalHistory;
import seoul.AutoEveryDay.entity.User;
import seoul.AutoEveryDay.service.CarManageService;
import seoul.AutoEveryDay.service.CarRentalService;
import seoul.AutoEveryDay.service.LoginService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/car")
@PreAuthorize(value = "hasAnyAuthority('CAR_RENTAL')")
@RequiredArgsConstructor
public class CarController {
    private final LoginService loginService;
    private final CarManageService carManageService;
    private final CarRentalService carRentalService;

    /* 차량 대여 */
    @GetMapping("/rental")  // 차량 대여 페이지
    public String rentalGet(Model model) {
        List<CarDto> carDtoList = carManageService.getAllCar();
        model.addAttribute("carList", carDtoList);
        Map<Long, List<RentalHistory>> rentalHistoryMap = carRentalService.getRentalHistory(carDtoList);
        model.addAttribute("rentalListMap", rentalHistoryMap);
        model.addAttribute("minDay", LocalDate.now());
        model.addAttribute("maxDay", LocalDate.now().plusDays(7));
        model.addAttribute("returnDay", LocalDate.now().plusDays(14));
        return "carRental";
    }
    @ResponseBody
    @PostMapping("/rental") // 차량 대여 신청
    public JsonBody rentalPost(@Validated RentCarDto rentCarDto) {
        User user = loginService.getLoginUser();
        Car car = carManageService.getCar(rentCarDto.getCarId());
        return JsonBody.builder()
                .message("차량 대여 성공")
                .data(carRentalService.rentCar(rentCarDto, user, car))
                .build();
    }

    @ResponseBody
    @PutMapping("/rental")  // 차량 반납
    public JsonBody rentalPut(@Validated RentCarDto rentCarDto) {
        User user = loginService.getLoginUser();
        Car car = carManageService.getCar(rentCarDto.getCarId());
        return JsonBody.builder()
                .message("차량 반납 성공")
                .data(carRentalService.returnCar(rentCarDto, user, car))
                .build();
    }

    @ResponseBody
    @DeleteMapping("/rental")   // 차량 대여 취소
    public JsonBody rentalDelete(@Validated RentCarDto rentCarDto) {
        User user = loginService.getLoginUser();
        Car car = carManageService.getCar(rentCarDto.getCarId());
        return JsonBody.builder()
                .message("차량 대여 취소 성공")
                .data(carRentalService.deleteRental(rentCarDto, user, car))
                .build();
    }

    /* 차량 관리 */

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/manage")  // 차량 관리 페이지
    public String getCar(Model model) {
        List<CarDto> allCar = carManageService.getAllCar();
        model.addAttribute("carList", allCar);
        return "carManage";
    }
    @ResponseBody
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/manage") // 새로운 차량 등록
    public JsonBody newCar(@Validated CarDto carDto) {
        return JsonBody.builder()
                .message("차량 등록 성공")
                .data(carManageService.createCar(carDto))
                .build();
    }
    @ResponseBody
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/manage")  // 차량 정보 수정
    public JsonBody editCar(@Validated CarDto carDto) {
        return JsonBody.builder()
                .message("차량 정보 수정 성공")
                .data(carManageService.updateCar(carDto))
                .build();
    }
    @ResponseBody
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/manage")   // 차량 삭제
    public JsonBody deleteCar(@RequestParam String number) {
        return JsonBody.builder()
                .message("차량 삭제 성공")
                .data(carManageService.deleteCar(number))
                .build();
    }
}
