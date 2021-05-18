package com.newspoint.task.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
public class UploadFileService {
    @Value("${upload.path}")
    private String DIRECTORY;
    public static String getFileName;

    public void uploadFile(MultipartFile file) throws Exception {

        getFileName = file.getOriginalFilename();
        if (!Objects.requireNonNull(getFileName).endsWith(".csv")) {
            log.error("This is not csv file!");
            log.error("Cannot upload file with name {}", file.getOriginalFilename());
            throw new FileUploadException("Cannot upload file, passed file does not have appropriate extension .csv");
        } else {

            var copyLocation = Paths
                    .get(DIRECTORY + File.separator + StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())));
            Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("File with name {} has been uploaded", getFileName);
        }

    }

    public List<String> dataFromCSV() {
        List<String> filesContent = new ArrayList<>();
        try {
            filesContent = Files.lines(Paths.get(DIRECTORY + "/" + getFileName)).collect(toList());
            return filesContent.subList(1, filesContent.size());
        } catch (IOException e) {
            log.error("There is no such file to read");
        }
        return filesContent;
    }
}
