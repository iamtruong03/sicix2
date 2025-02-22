package com.truong.controller;

import com.truong.entities.Job;
import com.truong.service.JobService;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.truong.entities.User;
import com.truong.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

  @Autowired
  private UserService userService;
  @Autowired
  private JobService jobService;

  @PostMapping("/login")
  public ResponseEntity<String> login(@RequestBody User user) {
    userService.login(user.getUsername(), user.getPassword());
    return ResponseEntity.ok("login successful");
  }

  @PostMapping("/createJob")
  public ResponseEntity<?> createJob(@RequestParam String jobName,
      @RequestParam LocalDate deadline,
      @RequestParam Long createdUserId,
      @RequestParam Long executedUserId) {
    try {
      Job job = jobService.createJob(jobName, deadline, createdUserId, executedUserId);
      return ResponseEntity.ok(job);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }


  }
}