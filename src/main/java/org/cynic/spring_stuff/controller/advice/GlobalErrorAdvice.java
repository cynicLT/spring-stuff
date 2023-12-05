package org.cynic.spring_stuff.controller.advice;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.cynic.spring_stuff.domain.ApplicationException;
import org.cynic.spring_stuff.domain.http.ErrorHttp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentConversionNotSupportedException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalErrorAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalErrorAdvice.class);

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ApplicationException.class)
    public final ErrorHttp handleApplicationException(ApplicationException exception) {
        LOGGER.error("", exception);

        return new ErrorHttp(
            exception.getCode(),
            exception.getValues()
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public final ErrorHttp handleMissingParameterException(MissingServletRequestParameterException exception) {
        LOGGER.error("", exception);

        return new ErrorHttp(
            "error.parameter.missing",
            exception.getParameterName(),
            exception.getParameterType()
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public final ErrorHttp handleTypeException(MethodArgumentTypeMismatchException exception) {
        LOGGER.error("", exception);

        return new ErrorHttp(
            "error.parameter.invalid-type",
            exception.getName(),
            Objects.toString(exception.getValue(), StringUtils.EMPTY),
            ClassUtils.getSimpleName(exception.getRequiredType())
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentConversionNotSupportedException.class)
    public final ErrorHttp handleArgumentConversionException(MethodArgumentConversionNotSupportedException exception) {
        LOGGER.error("", exception);

        return new ErrorHttp(
            "error.parameter.non-convertable",
            exception.getName(),
            Objects.toString(exception.getValue(), StringUtils.EMPTY),
            ClassUtils.getSimpleName(exception.getRequiredType())
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public final ErrorHttp handleHttpMethodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        LOGGER.error("", exception);

        return new ErrorHttp(
            "error.http-method.not-supported",
            exception.getMethod()
        );
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(DataAccessException.class)
    public ErrorHttp handleDataAccessException(DataAccessException exception) {
        LOGGER.error("", exception);

        return new ErrorHttp("error.database", exception);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Throwable.class)
    public final ErrorHttp handleUnknownException(Throwable exception) {
        LOGGER.error("", exception);

        return new ErrorHttp(
            "error.unknown",
            ExceptionUtils.getRootCauseMessage(exception),
            ExceptionUtils.getStackTrace(exception)
        );
    }


    @ExceptionHandler(AccessDeniedException.class)
    public final ResponseEntity<ErrorHttp> handleAccessDeniedException(@AuthenticationPrincipal OidcUser oidcUser,
        AccessDeniedException exception) {
        LOGGER.error("", exception);

        return Optional.ofNullable(oidcUser)
            .map(it -> ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(
                    new ErrorHttp("error.access.forbidden",
                        it.getSubject(),
                        it.getIssuer().toString(),
                        it.getAuthorities()
                            .stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.joining(","))
                    )
                )
            )
            .orElseGet(() ->
                ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorHttp("error.access.denied"))
            );
    }
}
