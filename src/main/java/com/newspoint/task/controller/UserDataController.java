package com.newspoint.task.controller;

import com.newspoint.task.model.UserDataModel;
import com.newspoint.task.service.UploadFileService;
import com.newspoint.task.service.UserService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class UserDataController {
    private final UserService userService;
    private final UploadFileService uploadFileService;

    public UserDataController(UserService userService,
                              UploadFileService uploadFileService) {

        this.userService = userService;
        this.uploadFileService = uploadFileService;

    }

    @PostMapping("uploadFile")
    public void uploadFile(@RequestParam MultipartFile file) {
        this.uploadFileService.uploadFile(file);
        this.userService.retrieveData();
    }

    @GetMapping({"allUsersPagination/{page}", "/allUsersPagination/{page}/{size}"})
    public List<UserDataModel> usersPaging(@PathVariable int page, @PathVariable(required = false) Integer size){
       return userService.findAllUsersWithPagination(page, size);
    }

    @GetMapping("getCountUsers")
    public long getCountUsers(){
        return userService.countAllUsers();
    }
    @GetMapping("oldestUser")
    public Object getTheOldestUserWithPhoneNumber(){
        return userService.getOldestUserWithPhone();
    }

    @DeleteMapping("deleteSpecUser/{id}")
    public void deleteUser(@PathVariable Long id){
        userService.deleteSpecUser(id);
    }

    @DeleteMapping("deleteAll")
    public void deleteAllUsers(){
       userService.deleteAllUsers();
    }
    @GetMapping("search/{lastName}")
    //List in case there is more users with same last name
    public List<UserDataModel> findUserByLastName(@PathVariable String lastName){
        return userService.findSpecUserByLastName(lastName);
    }
}
