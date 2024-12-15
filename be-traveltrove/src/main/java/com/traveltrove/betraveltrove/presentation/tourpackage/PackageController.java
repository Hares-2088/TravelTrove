package com.traveltrove.betraveltrove.presentation.tourpackage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/packages")
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class PackageController {
}
