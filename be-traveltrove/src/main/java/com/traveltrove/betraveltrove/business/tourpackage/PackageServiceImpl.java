package com.traveltrove.betraveltrove.business.tourpackage;

import com.traveltrove.betraveltrove.business.airport.AirportService;
import com.traveltrove.betraveltrove.business.tour.TourService;
import com.traveltrove.betraveltrove.dataaccess.airport.Airport;
import com.traveltrove.betraveltrove.dataaccess.tourpackage.Package;
import com.traveltrove.betraveltrove.dataaccess.tourpackage.PackageRepository;
import com.traveltrove.betraveltrove.presentation.airport.AirportResponseModel;
import com.traveltrove.betraveltrove.presentation.tour.TourEventResponseModel;
import com.traveltrove.betraveltrove.presentation.tour.TourResponseModel;
import com.traveltrove.betraveltrove.presentation.tourpackage.PackageRequestModel;
import com.traveltrove.betraveltrove.presentation.tourpackage.PackageResponseModel;
import com.traveltrove.betraveltrove.utils.entitymodels.PackageEntityModelUtil;
import com.traveltrove.betraveltrove.utils.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class PackageServiceImpl implements PackageService {

    private final PackageRepository packageRepository;

    private final TourService tourService;

    private final AirportService airportService;

    public PackageServiceImpl(PackageRepository packageRepository, TourService tourService, AirportService airportService) {
        this.packageRepository = packageRepository;
        this.tourService = tourService;
        this.airportService = airportService;
    }

    @Override
    public Flux<PackageResponseModel> getAllPackages(String tourId) {
        return getTour(tourId)
                .flatMapMany(tour -> packageRepository.findPackagesByTourId(tourId)
                        .map(PackageEntityModelUtil::toPackageResponseModel))
                .switchIfEmpty(packageRepository.findAll()
                        .map(PackageEntityModelUtil::toPackageResponseModel));
    }

    @Override
    public Mono<PackageResponseModel> getPackageByPackageId(String packageId) {
        return packageRepository.findPackageByPackageId(packageId)
                .map(PackageEntityModelUtil::toPackageResponseModel)
                .switchIfEmpty(Mono.error(new NotFoundException("Package not found: " + packageId)));
    }

    @Override
    public Mono<PackageResponseModel> createPackage(PackageRequestModel packageRequestModel, String tourId, String airportId) {
        // get the tour and airport to check if they exist, then check if the package request model is valid
        return Mono.zip(getTour(tourId), getAirport(airportId))
                .doOnNext(tuple -> validatePackageRequestModel(packageRequestModel))
                .flatMap(tuple -> {
                    Package pk = PackageEntityModelUtil.toPackage(packageRequestModel, tuple.getT1(), tuple.getT2());
                    // save the package entity model
                    return packageRepository.save(pk)
                            .map(PackageEntityModelUtil::toPackageResponseModel);
                });
    }

    @Override
    public Mono<PackageResponseModel> updatePackage(String packageId, PackageRequestModel packageRequestModel) {
        return packageRepository.findPackageByPackageId(packageId)
                // get the package using the packageId and return a notfound exception if the package is not found
                .switchIfEmpty(Mono.error(new NotFoundException("Package not found: " + packageId)))
                // then check if the package response model is valid using the validatePackageRequestModel method
                .doOnNext(pk -> validatePackageRequestModel(packageRequestModel))
                // then get the tour and airport to check if they exist using the getTour and getAirport methods
                .flatMap(pk -> Mono.zip(getTour(packageRequestModel.getTourId()), getAirport(packageRequestModel.getAirportId()))
                        // then return the package entity model
                        .map(tuple -> PackageEntityModelUtil.toPackage(packageRequestModel, tuple.getT1(), tuple.getT2()))
                        // then save the package entity model
                        .flatMap(packageRepository::save)
                        // then return the package response model
                        .map(PackageEntityModelUtil::toPackageResponseModel));
    }

    @Override
    public Mono<PackageResponseModel> deletePackage(String packageId) {
        return packageRepository.findPackageByPackageId(packageId)
                // get the package using the packageId and return a notfound exception if the package is not found
                .switchIfEmpty(Mono.error(new NotFoundException("Package not found: " + packageId)))
                // then delete the package entity model
                .flatMap(pk -> packageRepository.delete(pk)
                        // then return the package response model using the package entity model we just deleted
                        .thenReturn(PackageEntityModelUtil.toPackageResponseModel(pk)));
    }

    // a method to get the tour using the tourService and the tourId and return a notfound exception if the tour is not found
    private Mono<String> getTour(String tourId) {
        return tourService.getTourByTourId(tourId)
                .switchIfEmpty(Mono.error(new NotFoundException("Tour not found: " + tourId)))
                .map(TourResponseModel::getTourId);
    }

    // a method to get the airport using the airportService and the airportId and return a notfound exception if the airport is not found
    private Mono<String> getAirport(String airportId) {
        return airportService.getAirportById(airportId)
                .switchIfEmpty(Mono.error(new NotFoundException("Airport not found: " + airportId)))
                .map(AirportResponseModel::getAirportId);
    }

    // a method to check that the package request model is valid
    private void validatePackageRequestModel(PackageRequestModel packageRequestModel) {
        // check that the packageRequestModel is not null
        if (packageRequestModel == null) {
            throw new IllegalArgumentException("Package request model is required");
        }
        // check that the packageRequestModel name is not null
        if (packageRequestModel.getName() == null) {
            throw new IllegalArgumentException("Package name is required");
        }
        // check that the packageRequestModel description is not null
        if (packageRequestModel.getDescription() == null) {
            throw new IllegalArgumentException("Package description is required");
        }
        // check that the packageRequestModel startDate is not null
        if (packageRequestModel.getStartDate() == null) {
            throw new IllegalArgumentException("Package start date is required");
        }
        // check that the packageRequestModel endDate is not null
        if (packageRequestModel.getEndDate() == null) {
            throw new IllegalArgumentException("Package end date is required");
        }
        // check that the packageRequestModel priceSingle is not null
        if (packageRequestModel.getPriceSingle() == null) {
            throw new IllegalArgumentException("Package price single is required");
        }
    }
}
