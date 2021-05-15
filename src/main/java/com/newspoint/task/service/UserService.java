package com.newspoint.task.service;

import com.newspoint.task.model.UserDataModel;
import com.newspoint.task.repository.UserDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;


@Service
@Slf4j
public class UserService extends UploadFileService{

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
        boolean hasErrors = false;
        var dataArray = dataLine.trim().split(";");
        if (dataArray.length > 2 && dataArray[0] != null
                                 && dataArray[1]!= null
                                 && dataArray[2] != null ) {

            userData.setFirstName(dataArray[0].trim());
            userData.setLastName(dataArray[1].trim());
            try {
                var birthD = LocalDate.parse(dataArray[2], formatter);
                userData.setBirth(birthD);
                userData.setBirthDate(Period.between(birthD, LocalDate.now()).getYears());
            } catch (Exception e) {
                log.error("Invalid date");
                hasErrors =true;
            }
            if (dataArray.length > 3
                    && dataArray[3].trim().matches("^[0-9]*$")
                    && dataArray[3].trim().length() == 9) {
                var phone = Long.parseLong(dataArray[3]);

                var phoneAlreadyPresent = userDataRepository.findAll()
                        .stream()
                        .filter(tel->tel.getPhoneNumber() != null
                                && tel.getPhoneNumber()
                                .equals(phone))
                        .findFirst();

                if(phoneAlreadyPresent.isEmpty()) {
                    userData.setPhoneNumber(Long.parseLong(dataArray[3]));
                }else{
                    hasErrors = true;
                    log.warn("Such phone number already exists in database!");
                    }
                }
            if(!hasErrors) {
                userDataRepository.save(userData);
            }
        }else{
            log.error("Data in bad condition, cannot save entity to database");
        }
    }

    public Object getOldestUserWithPhone(){
        var oldestUser = userDataRepository.findAll()
                .stream()
                .filter(user -> user.getPhoneNumber() != null )
                .min(Comparator.comparing(UserDataModel::getBirth));
        if(oldestUser.isPresent()){
            return oldestUser.get();
        }
        else log.error("There is not any users with phone number");
        return null;
    }

    public void deleteSpecUser(Long id){
        var isUserPresent = userDataRepository.findById(id).isPresent();
        if(isUserPresent) {
            userDataRepository.deleteById(id);
            log.info("user has been deleted");
        }else
        log.warn("There is no such user");
    }

    public void deleteAllUsers(){
        userDataRepository.deleteAll();
        log.info("All users has been deleted");
    }
    public List<UserDataModel> findSpecUserByLastName(String lastName){
        return userDataRepository.findAll()
                .stream()
                .filter(user -> user.getLastName().toLowerCase()
                        .equals(lastName.toLowerCase())).collect(toList());

    }
    public List<UserDataModel> findAllUsersWithPagination(int page, Integer size){
        if (size == null) {
            size = 5;
            log.info("Page size not specified, setting default size: 5");
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("birthDate"));
        return userDataRepository.findAll(pageable).getContent();
    }

    public long countAllUsers(){
        return userDataRepository.findAll().size();
    }
}
