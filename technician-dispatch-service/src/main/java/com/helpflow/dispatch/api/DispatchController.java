package com.helpflow.dispatch.api;

import com.helpflow.dispatch.domain.DispatchService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class DispatchController {
    private final DispatchService dispatchService;

    public DispatchController(DispatchService dispatchService) {
        this.dispatchService = dispatchService;
    }

    @PostMapping("/technician-dispatch")
    @ResponseStatus(HttpStatus.OK)
    public DispatchResponse dispatch(@Valid @RequestBody DispatchRequest request) {
        return dispatchService.dispatch(request);
    }
}

