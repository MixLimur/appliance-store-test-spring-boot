package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.exception.DuplicateEmailException;
import com.epam.rd.autocode.assessment.appliances.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @RestController
    static class DummyController {
        @GetMapping("/test-not-found")
        public void throwNotFound() {
            throw new ResourceNotFoundException("Not found");
        }

        @GetMapping("/test-no-resource")
        public void throwNoResource() throws NoResourceFoundException {
            throw new NoResourceFoundException(null, "/test-no-resource");
        }

        @GetMapping("/test-access-denied")
        public void throwAccessDenied() {
            throw new AccessDeniedException("Access denied");
        }

        @GetMapping("/test-duplicate-email")
        public void throwDuplicateEmail() {
            throw new DuplicateEmailException("Duplicate email");
        }

        @GetMapping("/test-general")
        public void throwGeneral() {
            throw new RuntimeException("General error");
        }
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new DummyController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("handleResourceNotFound() should return 404 status and error/404 view for ResourceNotFoundException")
    void handleResourceNotFound_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/test-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("error/404"));
    }

    @Test
    @DisplayName("handleResourceNotFound() should return 404 status and error/404 view for NoResourceFoundException")
    void handleNoResourceFound_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/test-no-resource"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("error/404"));
    }

    @Test
    @DisplayName("handleAccessDenied() should return 403 status and error/403 view for AccessDeniedException")
    void handleAccessDenied_ShouldReturn403() throws Exception {
        mockMvc.perform(get("/test-access-denied"))
                .andExpect(status().isForbidden())
                .andExpect(view().name("error/403"));
    }

    @Test
    @DisplayName("handleDuplicateEmail() should return 409 status and error/409 view for DuplicateEmailException")
    void handleDuplicateEmail_ShouldReturn409() throws Exception {
        mockMvc.perform(get("/test-duplicate-email"))
                .andExpect(status().isConflict())
                .andExpect(view().name("error/409"));
    }

    @Test
    @DisplayName("handleGeneralException() should return 500 status and error/500 view for generic Exception")
    void handleGeneralException_ShouldReturn500() throws Exception {
        mockMvc.perform(get("/test-general"))
                .andExpect(status().isInternalServerError())
                .andExpect(view().name("error/500"));
    }

}