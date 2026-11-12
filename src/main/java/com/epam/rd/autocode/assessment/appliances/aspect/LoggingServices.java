package com.epam.rd.autocode.assessment.appliances.aspect;

import com.epam.rd.autocode.assessment.appliances.exception.DuplicateEmailException;
import com.epam.rd.autocode.assessment.appliances.exception.ResourceNotFoundException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
public class LoggingServices {
    @Pointcut("within(com.epam.rd.autocode.assessment.appliances.service.impl..*) || " +
            "within(com.epam.rd.autocode.assessment.appliances.controller..*)")
    public void applicationLayersPointcut() {
    }

    @Pointcut("@annotation(com.epam.rd.autocode.assessment.appliances.aspect.Loggable)")
    public void loggableMethodPointcut() {
    }

    @Before("applicationLayersPointcut()")
    public void logMethodEntry(JoinPoint joinPoint) {
        if (log.isDebugEnabled()) {
            log.debug("Enter: {}.{}() with args = [{}]",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    formatArgs(joinPoint.getArgs()));
        }
    }

    @AfterReturning(pointcut = "applicationLayersPointcut()", returning = "result")
    public void logMethodExit(JoinPoint joinPoint, Object result) {
        if (log.isDebugEnabled()) {
            log.debug("Exit: {}.{}() with result = {}",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    formatResult(result));
        }
    }

    @Around("loggableMethodPointcut()")
    public Object logAnnotatedMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        log.info("[@Loggable] Executing {}.{}() with args = [{}]",
                className, methodName, formatArgs(joinPoint.getArgs()));

        long startTime = System.nanoTime();
        try {
            Object result = joinPoint.proceed();
            double durationMs = (System.nanoTime() - startTime) / 1_000_000.0;
            log.info("[@Loggable] Completed {}.{}() in {} ms with result = {}",
                    className, methodName, String.format("%.3f", durationMs), formatResult(result));
            return result;
        } catch (Throwable ex) {
            double durationMs = (System.nanoTime() - startTime) / 1_000_000.0;
            log.warn("[@Loggable] Failed {}.{}() after {} ms with {}: {}",
                    className, methodName, String.format("%.3f", durationMs),
                    ex.getClass().getSimpleName(), ex.getMessage());
            throw ex;
        }
    }

    @AfterThrowing(pointcut = "applicationLayersPointcut()", throwing = "ex")
    public void logException(JoinPoint joinPoint, Throwable ex) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        if (isExpectedBusinessException(ex)) {
            log.warn("Handled exception in {}.{}() - {}: {}",
                    className, methodName, ex.getClass().getSimpleName(), ex.getMessage());
        } else {
            log.error("Unexpected exception in {}.{}() - {}",
                    className, methodName, ex.getMessage(), ex);
        }
    }

    private boolean isExpectedBusinessException(Throwable ex) {
        return ex instanceof ResourceNotFoundException
                || ex instanceof DuplicateEmailException
                || ex instanceof IllegalStateException
                || ex instanceof IllegalArgumentException
                || ex instanceof UsernameNotFoundException
                || ex instanceof AccessDeniedException;
    }

    private String formatArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return "";
        }
        return Arrays.stream(args)
                .map(this::formatSingleArg)
                .collect(Collectors.joining(", "));
    }

    private String formatSingleArg(Object arg) {
        if (arg == null) {
            return "null";
        }
        if (arg instanceof Model) {
            return "Model";
        }
        if (arg instanceof BindingResult bindingResult) {
            return "BindingResult(errors=" + bindingResult.getErrorCount() + ")";
        }
        if (arg instanceof RedirectAttributes) {
            return "RedirectAttributes";
        }
        if (arg instanceof Authentication authentication) {
            return "Authentication(user=" + authentication.getName() + ")";
        }
        if (arg instanceof ServletRequest || arg instanceof ServletResponse) {
            return arg.getClass().getSimpleName();
        }
        return maskSensitiveData(arg.toString());
    }

    private String formatResult(Object result) {
        if (result == null) {
            return "null";
        }
        if (result instanceof Page<?> page) {
            return String.format("Page(page=%d, size=%d, totalElements=%d)",
                    page.getNumber(), page.getSize(), page.getTotalElements());
        }
        if (result instanceof UserDetails userDetails) {
            return String.format("UserDetails(username=%s, authorities=%s)",
                    userDetails.getUsername(), userDetails.getAuthorities());
        }
        return maskSensitiveData(result.toString());
    }

    private String maskSensitiveData(String value) {
        return value.replaceAll("(?i)(password|token|secret)=[^,\\)\\]\\s]+", "$1=***");
    }
}
