package com.traveltrove.betraveltrove.business;

import com.traveltrove.betraveltrove.dataaccess.tour.TourRepository;
import com.traveltrove.betraveltrove.presentation.TourResponseModel;
import com.traveltrove.betraveltrove.utils.EntityModelUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@Slf4j
public class TourServiceImpl implements TourService {

    private final TourRepository tourRepository;

    public TourServiceImpl(TourRepository tourRepository) {
        this.tourRepository = tourRepository;
    }

    @Override
    public Flux<TourResponseModel> getTours() {
        return tourRepository.findAll()
                .map(EntityModelUtil::toTourResponseModel);
    }

}
