package com.traveltrove.betraveltrove.business.payment;

import com.traveltrove.betraveltrove.business.tour.TourService;
import com.traveltrove.betraveltrove.business.tourpackage.PackageService;
import com.traveltrove.betraveltrove.dataaccess.payment.Payment;
import com.traveltrove.betraveltrove.dataaccess.payment.PaymentRepository;
import com.traveltrove.betraveltrove.presentation.payment.PaymentRequestModel;
import com.traveltrove.betraveltrove.presentation.payment.PaymentResponseModel;
import com.traveltrove.betraveltrove.utils.entitymodelyutils.PaymentModelUtil;
import com.traveltrove.betraveltrove.business.booking.BookingService;
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
    private final BookingService bookingService;
    private final PackageService packageService;
    private final TourService tourService;

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

    @Override
    public Mono<PaymentResponseModel> getPaymentByBookingId(String bookingId) {
        return paymentRepository.findByBookingId(bookingId) // Assuming that there should be a single payment for a booking
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Payment not found"))));
    }

    @Override
    public Mono<Long> calculateRevenueByTourId(String tourId) {
        return tourService.getTourByTourId(tourId)
                .doOnNext(tour -> log.info("Found tour: {}", tour))
                .flatMapMany(tour -> packageService.getAllPackages(tour.getTourId())
                        .doOnNext(pkg -> log.info("Found package: {}", pkg))
                        .switchIfEmpty(Flux.defer(() -> {
                            log.info("No packages found for tourId: {}", tourId);
                            return Flux.empty();
                        }))
                )
                .flatMap(pkg -> bookingService.getBookingsByPackageId(pkg.getPackageId())
                        .doOnNext(booking -> log.info("Found booking: {}", booking))
                        .switchIfEmpty(Flux.defer(() -> {
                            log.info("No bookings found for packageId: {}", pkg.getPackageId());
                            return Flux.empty();
                        }))
                )
                .flatMap(booking -> paymentRepository.findByBookingId(booking.getBookingId())
                        .doOnNext(payment -> log.info("Found payment: {}", payment))
                        .switchIfEmpty(Mono.defer(() -> {
                            log.info("No payments found for bookingId: {}", booking.getBookingId());
                            return Mono.empty();
                        }))
                )
                .map(PaymentResponseModel::getAmount)
                .doOnNext(amount -> log.info("Payment amount: {}", amount))
                .reduce(0L, Long::sum)
                .doOnSuccess(totalRevenue -> log.info("Total revenue for tourId {}: {}", tourId, totalRevenue));
    }

}
