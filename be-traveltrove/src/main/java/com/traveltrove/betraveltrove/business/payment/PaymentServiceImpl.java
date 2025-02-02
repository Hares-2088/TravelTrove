package com.traveltrove.betraveltrove.business.payment;

import com.traveltrove.betraveltrove.dataaccess.payment.Payment;
import com.traveltrove.betraveltrove.dataaccess.payment.PaymentRepository;
import com.traveltrove.betraveltrove.presentation.payment.PaymentRequestModel;
import com.traveltrove.betraveltrove.presentation.payment.PaymentResponseModel;
import com.traveltrove.betraveltrove.utils.entitymodelyutils.PaymentModelUtil;
import com.traveltrove.betraveltrove.utils.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Override
    public Mono<Payment> createPayment(PaymentRequestModel paymentRequestModel, String stripePaymentId) {
        Payment paymentEntity = PaymentModelUtil.toPaymentEntity(stripePaymentId, paymentRequestModel);
        log.info("Saving Payment Entity: {}", paymentEntity); // Log before saving
        return paymentRepository.save(paymentEntity)
                .doOnSuccess(savedPayment -> log.info("Saved Payment Entity: {}", savedPayment)); // Log after saving
    }

    @Override
    public Mono<PaymentResponseModel> updatePayment(String paymentId, String status) {
        return paymentRepository.findById(paymentId)
                .flatMap(existingPayment -> {
                    existingPayment.setStatus(status);
                    return paymentRepository.save(existingPayment);
                })
                .map(updatedPayment -> PaymentModelUtil.toPaymentResponseModel(updatedPayment, null)); // Convert updated entity to response model
    }

    @Override
    public Mono<PaymentResponseModel> getPaymentByPaymentId(String paymentId) {
        return paymentRepository.findByPaymentId(paymentId)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Payment not found"))));
    }

    @Override
    public Flux<PaymentResponseModel> getAllPayments() {
        return paymentRepository.findAll()
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("No payments found"))))
                .map(PaymentModelUtil::toPaymentResponseModelWithoutSession);
    }

//    @Override
//    public Mono<Void> deletePayment(String paymentId) {
//
//        return paymentRepository.delete(paymentId)
//                .doOnSuccess(deleted -> log.info("Deleted Payment Entity: {}", deleted));
//    }
}
