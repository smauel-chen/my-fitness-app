// package com.example.demo.controller;

// import org.springframework.web.bind.annotation.RestController;

// import com.example.demo.dto.TestDTO;
// import com.example.demo.service.WorkoutSessionService;

// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;


// @RestController
// public class TestSessionController {
    
//     private WorkoutSessionService workoutSessionService;

//     public TestSessionController(WorkoutSessionService workoutSessionService){
//         this.workoutSessionService = workoutSessionService;
//     }

//     @PostMapping("/user/{id}/session/test")
//     public ResponseEntity<?> addSessionTest(@RequestBody  TestDTO testDTO, @PathVariable Long id) {
//         workoutSessionService.addSessionTest(id, testDTO);
//         return ResponseEntity.ok(testDTO);
//     }
    


// }
