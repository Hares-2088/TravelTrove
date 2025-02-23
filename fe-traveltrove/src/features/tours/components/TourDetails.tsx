import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { useToursApi } from "../api/tours.api";
import { useTourEventsApi } from "../../tourevents/api/tourevent.api";
import { useEventsApi } from "../../events/api/events.api";
import { useHotelsApi } from "../../hotels/api/hotels.api";
import { TourResponseModel } from "../models/Tour";
import { TourEventResponseModel } from "../../tourevents/model/tourevents.model";
import { EventResponseModel } from "../../events/model/events.model";
import { HotelResponseModel } from "../../hotels/models/hotel.model";
import { Calendar, ChevronDown } from "lucide-react";
import "./TourDetails.css";
import PackageList from '../../packages/components/PackageList';

const TourDetails: React.FC = () => {
  const { getTourByTourId } = useToursApi();
  const { getTourEventsByTourId } = useTourEventsApi();
  const { getEventById } = useEventsApi();
  const { getHotelById } = useHotelsApi();
  const { tourId } = useParams<{ tourId: string }>();

  const [tour, setTour] = useState<TourResponseModel | null>(null);
  const [tourEvents, setTourEvents] = useState<TourEventResponseModel[]>([]);
  const [events, setEvents] = useState<{ [key: string]: EventResponseModel }>({});
  const [hotels, setHotels] = useState<{ [key: string]: HotelResponseModel }>({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showEvents, setShowEvents] = useState(false);

  useEffect(() => {
    if (!tourId) return; // Prevent fetching if tourId is null

    let isMounted = true;

    const fetchTour = async () => {
      try {
        const data = await getTourByTourId(tourId);
        if (isMounted) {
          setTour(data);
        }
      } catch (err) {
        if (isMounted) {
          setError("Failed to fetch tour details.");
        }
      } finally {
        if (isMounted) {
          setLoading(false);
        }
      }
    };

    const fetchTourEvents = async () => {
      try {
        const events = await getTourEventsByTourId(tourId);
        if (isMounted) {
          setTourEvents(events);

          // Fetch event and hotel names
          const eventPromises = events.map(event => getEventById(event.eventId));
          const hotelPromises = events.map(event => getHotelById(event.hotelId));
          const eventResults = await Promise.all(eventPromises);
          const hotelResults = await Promise.all(hotelPromises);

          const eventMap: { [key: string]: EventResponseModel } = {};
          const hotelMap: { [key: string]: HotelResponseModel } = {};

          eventResults.forEach(event => {
            if (event) eventMap[event.eventId] = event;
          });

          hotelResults.forEach(hotel => {
            if (hotel) hotelMap[hotel.hotelId] = hotel;
          });

          setEvents(eventMap);
          setHotels(hotelMap);
        }
      } catch (err) {
      }
    };

    fetchTour();
    fetchTourEvents();

    return () => {
      isMounted = false;
    };
  }, [tourId]);

  if (loading) return <div className="loading">Loading...</div>;
  if (error) return <div className="error">{error}</div>;
  if (!tour) return <div>No tour details found.</div>;

  return (
    <div className="container tour-details-page">
      <div className="row">
        <div className="col-12">
          <header className="tour-header text-center my-4">
            <h1 className="tour-title">{tour.name}</h1>
            <p className="tour-description">{tour.description}</p>
            {tour.tourImageUrl && (
              <img
                src={tour.tourImageUrl}
                alt={tour.name}
                className="tour-main-image img-fluid rounded"
              />
            )}
          </header>
        </div>
      </div>

      <div className="row">
        <div className="col-12">
          <section className="tour-events my-4">
            <h2
              onClick={() => setShowEvents(!showEvents)}
              className="tour-events-header d-flex align-items-center justify-content-between"
              style={{ cursor: "pointer" }}
            >
              Tour Events <ChevronDown className={`chevron-icon ${showEvents ? 'open' : ''}`} />
            </h2>
            {showEvents && (
              tourEvents.length === 0 ? (
                <p>No events available for this tour.</p>
              ) : (
                <ul className="list-group">
                  {tourEvents.sort((a, b) => a.seq - b.seq).map((event) => (
                    <li key={event.tourEventId} className="list-group-item">
                      <div className="d-flex align-items-center">
                        <Calendar className="event-icon me-2" />
                        <div>
                          <h3 className="h5 mb-1">
                            {event.seq}. {event.seqDesc}
                          </h3>
                          <p className="mb-0">
                            Event: {events[event.eventId]?.name || "Unknown Event"}
                          </p>
                          <p className="mb-0">
                            Hotel: {hotels[event.hotelId]?.name || "Unknown Hotel"}
                          </p>
                        </div>
                      </div>
                    </li>
                  ))}
                </ul>
              )
            )}
          </section>
        </div>
      </div>

      {tourId && (
        <div className="row">
          <div className="col-12">
            <h2 className="packages-header my-4">Packages</h2>
            <PackageList tourId={tourId} />
          </div>
        </div>
      )}
    </div>
  );
};

export default TourDetails;
