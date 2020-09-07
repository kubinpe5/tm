package com.homework.solution.controller;

import com.homework.solution.service.SolutionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class SolutionController {

    private final SolutionService solutionService;

    public SolutionController(final SolutionService solutionService) {
        this.solutionService = solutionService;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/demo-solution")
    public ResponseEntity<String> getDemoSolution() {
        return ResponseEntity.status(HttpStatus.OK).body(solutionService.solution("src/main/resources/example_input.txt"));
    }
}
