package com.example.ThreadHub.exception.handler;

import com.example.ThreadHub.exception.*;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Global Exception Handler cho toàn bộ ứng dụng.
 * Class này dùng để bắt và xử lý các exception phát sinh trong Controller,
 * sau đó trả về response lỗi theo một cấu trúc thống nhất.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String TIME_STAMP = "timestamp";
    private static final String STATUS = "status";
    private static final String ERROR = "error";
    private static final String MESSAGE = "message";

    /**
     * Xây dựng body response lỗi chuẩn cho API.
     *
     * @param status  HTTP status tương ứng với lỗi
     * @param message thông báo lỗi (String hoặc List<String>)
     * @return Map chứa thông tin lỗi trả về cho client
     */
    private Map<String, Object> buildBody(HttpStatus status, Object message) {
        Map<String, Object> body = new HashMap<>();
        body.put(TIME_STAMP, LocalDateTime.now());
        body.put(STATUS, status.value());
        body.put(ERROR, status.getReasonPhrase());
        body.put(MESSAGE, message);
        return body;
    }

    /**
     * Xử lý lỗi không tìm thấy tài nguyên (404).
     * Thường xảy ra khi truy vấn dữ liệu theo id nhưng không tồn tại.
     *
     * @param ex NotFoundException
     * @return response lỗi với HTTP 404
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(NotFoundException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(buildBody(status, ex.getMessage()), status);
    }

    /**
     * Xử lý lỗi request không hợp lệ do nghiệp vụ (400).
     *
     * @param ex BadRequestException
     * @return response lỗi với HTTP 400
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(BadRequestException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(buildBody(status, ex.getMessage()), status);
    }

    /**
     * Xử lý lỗi unauthorized (401).
     *
     * @param ex UnauthorizedException
     * @return response lỗi với HTTP 401
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorized(UnauthorizedException ex) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        return new ResponseEntity<>(buildBody(status, ex.getMessage()), status);
    }

    /**
     * Xử lý lỗi Forbidden (403).
     *
     * @param ex ForbiddenException
     * @return response lỗi với HTTP 403
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Map<String, Object>> handleForbidden(ForbiddenException ex) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        return new ResponseEntity<>(buildBody(status, ex.getMessage()), status);
    }

    /**
     * Xử lý lỗi request không hợp lệ do nghiệp vụ (409).
     *
     * @param ex ConflictException
     * @return response lỗi với HTTP 409
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, Object>> handleBusiness(ConflictException ex) {
        HttpStatus status = HttpStatus.CONFLICT;
        return new ResponseEntity<>(buildBody(status, ex.getMessage()), status);
    }

    /**
     * Xử lý lỗi validate dữ liệu đầu vào khi sử dụng @Valid.
     * Trả về danh sách các message lỗi validation.
     *
     * @param ex MethodArgumentNotValidException
     * @return response lỗi với HTTP 400 và danh sách lỗi
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        List<String> messages = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        return new ResponseEntity<>(buildBody(status, messages), status);
    }

    /**
     * Xử lý các exception chưa được định nghĩa cụ thể.
     * Dùng làm fallback để tránh lộ stack trace ra client.
     *
     * @param ex Exception
     * @return response lỗi với HTTP 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(buildBody(status, ex.getMessage()), status);
    }
}


