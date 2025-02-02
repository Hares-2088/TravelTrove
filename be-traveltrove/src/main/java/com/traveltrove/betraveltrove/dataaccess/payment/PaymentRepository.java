package com.traveltrove.betraveltrove.dataaccess.payment;

import com.traveltrove.betraveltrove.presentation.payment.PaymentResponseModel;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
@Repository
public interface PaymentRepository extends ReactiveMongoRepository<Payment, String> {
    Mono<PaymentResponseModel> findByPaymentId(String paymentId);

}
