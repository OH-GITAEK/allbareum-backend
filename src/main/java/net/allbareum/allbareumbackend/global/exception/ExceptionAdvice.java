package net.allbareum.allbareumbackend.global.exception;

import lombok.RequiredArgsConstructor;
import net.allbareum.allbareumbackend.global.dto.response.ErrorResponse;
import net.allbareum.allbareumbackend.global.dto.response.result.ExceptionResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionException;

import static net.allbareum.allbareumbackend.global.exception.ErrorCode.PARAMETER_GRAMMAR_ERROR;
import static net.allbareum.allbareumbackend.global.exception.ErrorCode.PARAMETER_VALIDATION_ERROR;


@RequiredArgsConstructor
@RestControllerAdvice
public class ExceptionAdvice {

    /**
     * 등록되지 않은 에러
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ErrorResponse<ExceptionResult.ServerErrorData> handleUntrackedException(Exception e) {
        ExceptionResult.ServerErrorData serverErrorData = ExceptionResult.ServerErrorData.builder()
                .errorClass(e.getClass().toString())
                .errorMessage(e.getMessage())
                .build();
        return ErrorResponse.of(ErrorCode.SERVER_UNTRACKED_ERROR.getErrorCode(), ErrorCode.SERVER_UNTRACKED_ERROR.getMessage(), serverErrorData);
    }

    /**
     * 파라미터 검증 예외
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    public ErrorResponse<List<ExceptionResult.ParameterData>> handleValidationExceptions(MethodArgumentNotValidException e) {
        List<ExceptionResult.ParameterData> list = new ArrayList<>();

        BindingResult bindingResult = e.getBindingResult();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            ExceptionResult.ParameterData parameterData = ExceptionResult.ParameterData.builder()
                    .key(fieldError.getField())
                    .value(fieldError.getRejectedValue() == null ? null : fieldError.getRejectedValue().toString())
                    .reason(fieldError.getDefaultMessage())
                    .build();
            list.add(parameterData);
        }

        return ErrorResponse.of(PARAMETER_VALIDATION_ERROR.getErrorCode(), PARAMETER_VALIDATION_ERROR.getMessage(), list);
    }


    /**
     * 파라미터 문법 예외
     */
    @ExceptionHandler({HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    public ErrorResponse<String> handleHttpMessageParsingExceptions(HttpMessageNotReadableException e) {
        return ErrorResponse.of(PARAMETER_GRAMMAR_ERROR.getErrorCode(), PARAMETER_GRAMMAR_ERROR.getMessage(), e.getMessage());
    }

    /**
     * 커스텀 예외
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse<?>> handleCustomException(CustomException e) {
        HttpStatus status = HttpStatus.valueOf(e.getErrorCode().getHttpCode()); // ErrorCode에서 HTTP 상태 코드 가져오기
        ErrorResponse<?> response = ErrorResponse.of(e.getErrorCode().getErrorCode(), e.getErrorCode().getMessage());
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(CompletionException.class)
    public ResponseEntity<ErrorResponse<?>> handleCompletionException(CompletionException e) {
        Throwable cause = e.getCause(); // 원본 예외 가져오기

        if (cause instanceof CustomException) {
            // CustomException 처리
            CustomException customException = (CustomException) cause;
            ErrorCode errorCode = customException.getErrorCode();
            ErrorResponse<?> errorResponse = ErrorResponse.of(
                    errorCode.getErrorCode(),
                    errorCode.getMessage()
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(errorCode.getHttpCode()));
        }  else {
            // 그 외의 예외 처리
            ErrorResponse<?> errorResponse = ErrorResponse.of(
                    ErrorCode.SERVER_UNTRACKED_ERROR.getErrorCode(),
                    ErrorCode.SERVER_UNTRACKED_ERROR.getMessage()
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}