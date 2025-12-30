package com.buguagaoshu.tiktube.advice;

import com.buguagaoshu.tiktube.file.FileController;
import com.buguagaoshu.tiktube.vo.ResponseDetails;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.FileNotFoundException;

/**
 *
 * create          2020-09-07 13:14
 */
@ControllerAdvice(basePackageClasses = FileController.class)
public class FileControllerAdvice {
    @ExceptionHandler(value = FileNotFoundException.class)
    public ResponseDetails handFileNotFoundException( FileNotFoundException e) {
        return ResponseDetails.ok(0, e.getMessage());
    }
}
