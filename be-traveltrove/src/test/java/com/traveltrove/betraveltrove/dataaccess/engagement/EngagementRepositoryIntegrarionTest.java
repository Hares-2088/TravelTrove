//package com.traveltrove.betraveltrove.dataaccess.engagement;
//
//import static org.junit.jupiter.api.Assertions.*;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import reactor.core.publisher.Mono;
//import reactor.test.StepVerifier;
//
//import java.time.Instant;
//
//@ExtendWith(SpringExtension.class)
//@DataMongoTest
//class EngagementRepositoryIntegrarionTest {
//    @Autowired
//    private EngagementRepository engagementRepository;
//
//    private Engagement sampleEngagement;
//
//    @BeforeEach
//    void setUp() {
//        sampleEngagement = new Engagement(null, "package1", "user1", 10, 5, 2, Instant.now());
//
//        // Ensure a clean database before each test
//        engagementRepository.deleteAll()
//                .then(engagementRepository.save(sampleEngagement))
//                .block();
//    }
//
//    @Test
//    void testFindByPackageId() {
//        Mono<Engagement> engagementMono = engagementRepository.findByPackageId("package1");
//
//        StepVerifier.create(engagementMono)
//                .expectNextMatches(engagement ->
//                        engagement.getPackageId().equals("package1") &&
//                                engagement.getViewCount() == 10 &&
//                                engagement.getWishlistCount() == 5 &&
//                                engagement.getShareCount() == 2
//                )
//                .verifyComplete();
//    }
//
//    @Test
//    void testSaveEngagement() {
//        Engagement newEngagement = new Engagement(null, "package2", "user2", 15, 7, 3, Instant.now());
//
//        Mono<Engagement> savedEngagement = engagementRepository.save(newEngagement);
//
//        StepVerifier.create(savedEngagement)
//                .expectNextMatches(engagement -> engagement.getPackageId().equals("package2") && engagement.getViewCount() == 15)
//                .verifyComplete();
//    }
//
//    @Test
//    void testUpdateEngagement() {
//        Mono<Engagement> updatedEngagement = engagementRepository.findByPackageId("package1")
//                .map(engagement -> {
//                    engagement.setViewCount(20);
//                    return engagement;
//                })
//                .flatMap(engagementRepository::save);
//
//        StepVerifier.create(updatedEngagement)
//                .expectNextMatches(engagement -> engagement.getViewCount() == 20)
//                .verifyComplete();
//    }
//
//    @Test
//    void testDeleteEngagement() {
//        Mono<Void> deleteMono = engagementRepository.deleteById(sampleEngagement.getId());
//
//        StepVerifier.create(deleteMono)
//                .verifyComplete();
//
//        StepVerifier.create(engagementRepository.findByPackageId("package1"))
//                .expectNextCount(0)
//                .verifyComplete();
//    }
//}