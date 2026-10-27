package com.helpflow.support.adapter.in.rest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
@RestControllerAdvice public class ApiExceptionHandler {
 @ExceptionHandler({IllegalArgumentException.class,IllegalStateException.class}) ProblemDetail bad(RuntimeException e,HttpServletRequest r){ var p=ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,e.getMessage()); p.setProperty("timestamp",Instant.now());p.setProperty("traceId",r.getHeader("X-Trace-Id"));return p; }
 @ExceptionHandler(SecurityException.class) ProblemDetail denied(SecurityException e){return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN,e.getMessage());}
}
