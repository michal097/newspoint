package com.newspoint.task.service;

import com.newspoint.task.model.UserDataModel;
import com.newspoint.task.repository.UserDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;


@Service
@Slf4j
public class UserService extends UploadFileService {

    private final UserDataRepository userDataRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    @Autowired
    public UserService(UserDataRepository userDataRepository) {
        this.userDataRepository = userDataRepository;
    }

    public void retrieveData() {
        this.dataFromCSV().forEach(this::validateAndCreateUser);
    }

    public void validateAndCreateUser(String dataLine) {
        var userData = new UserDataModel();
        var hasErrors = false;
        var dataArray = Arrays.stream(dataLine.split(";"))
                .map(String::trim)
                .toArray(String[]::new);

        if (dataArray.length > 2 && notNullChecker(dataArray, 3)) {

            userData.setFirstName(dataArray[0]);
            userData.setLastName(dataArray[1]);
            try {
                var birthD = LocalDate.parse(checkDate(dataArray[2]), formatter);
                userData.setBirth(birthD);
                userData.setBirthDate(Period.between(birthD, LocalDate.now())
                        .getYears());
            } catch (Exception e) {
                log.error("Invalid date");
                hasErrors = true;
            }
            if (dataArray.length > 3 && checkPhoneNumberValid(dataArray[3])) {

                var phone = Long.parseLong(dataArray[3]);
                var phoneAlreadyPresent = userDataRepository.findAll()
                        .stream()
                        .filter(tel -> tel.getPhoneNumber() != null
                                && tel.getPhoneNumber().equals(phone))
                        .findFirst();

                if (phoneAlreadyPresent.isEmpty()) {
                    userData.setPhoneNumber(Long.parseLong(dataArray[3]));
                } else {
                    hasErrors = true;
                    log.warn("Such phone number already exists in database!");
                }
            }
            if (!hasErrors) {
                userDataRepository.save(userData);
            }
        } else {
            log.error("Data in bad condition, cannot save entity to database");
        }
    }

    public boolean notNullChecker(String[] arr, int len) {
        for (int i = 0; i < len; i++) {
            if (arr[i] == null)
                return false;
        }
        return true;
    }

    public boolean checkPhoneNumberValid(String pNum) {
        return pNum.matches("^[0-9]*$") && pNum.length() == 9;
    }

    public String checkDate(String date){
        var splitDate = date.split("\\.");
        for(int i=1; i<splitDate.length; i++){
            if (splitDate[i].matches("^[0-9]$")){
                splitDate[i] = "0" + splitDate[i];
            }
        }
        var concatArray = Arrays.stream(splitDate).reduce((a,b)->a+"."+b);
        return concatArray.orElse(date);
    }

    public UserDataModel getOldestUserWithPhone() {
        var oldestUser = userDataRepository.findAll()
                .stream()
                .filter(user -> user.getPhoneNumber() != null)
                .min(Comparator.comparing(UserDataModel::getBirth));
        return oldestUser.orElse(null);
    }

    public void deleteSpecUser(Long id) {
        var isUserPresent = userDataRepository.findById(id).isPresent();
        if (isUserPresent) {
            userDataRepository.deleteById(id);
            log.info("user has been deleted");
        } else
            log.warn("There is no such user");
    }

    public void deleteAllUsers() {
        userDataRepository.deleteAll();
        log.info("All users has been deleted");
    }

    public List<UserDataModel> findSpecUserByLastName(String lastName) {
        return userDataRepository.findAll()
                .stream()
                .filter(user -> user.getLastName()
                        .toLowerCase()
                        .equals(lastName.toLowerCase()))
                .collect(toList());

    }

    public List<UserDataModel> findAllUsersWithPagination(int page, Integer size) {
        if (size == null || size == 0) {
            size = 5;
            log.info("Page size not specified, setting default size: 5");
        }
        var pageable = PageRequest.of(page, size, Sort.by("birthDate"));
        return userDataRepository.findAll(pageable).getContent();
    }

    public long countAllUsers() {
        return userDataRepository.findAll().size();
    }
}
