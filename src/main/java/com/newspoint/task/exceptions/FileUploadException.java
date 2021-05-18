package com.newspoint.task.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileUploadException {
    private String fileUploadErr;
}
