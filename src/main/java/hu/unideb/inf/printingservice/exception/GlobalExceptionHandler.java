package hu.unideb.inf.printingservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.Instant;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // 1. Amikor a Service rétegben nem található egy erőforrás (pl. "Felhasználó nem található!")
    @ExceptionHandler(RuntimeException.class)
    public ProblemDetail handleNotFoundOrBusinessError(RuntimeException ex, HttpServletRequest request) {
        log.warn("Üzleti vagy keresési hiba ({}): {}", request.getRequestURI(), ex.getMessage());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("A kért erőforrás nem található vagy a művelet nem hajtható végre");
        problem.setType(URI.create("https://printingservice.unideb.hu/hibak/nem-talalhato"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    // 2. Végső védőháló minden váratlan belső hibára (500) – nem szivárogtat ki stack trace-t!
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpectedException(Exception ex, HttpServletRequest request) {
        log.error("Váratlan szerverhiba a következő útvonalon: {}", request.getRequestURI(), ex);

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Váratlan belső hiba történt a szerveren. Kérjük, próbálja újra később!"
        );
        problem.setTitle("Belső szerverhiba");
        problem.setType(URI.create("https://printingservice.unideb.hu/hibak/szerverhiba"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}