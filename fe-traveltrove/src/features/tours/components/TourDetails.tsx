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
          console.error(`❌ Failed to fetch tour details for tourId=${tourId}:`, err);
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
        console.error(`❌ Failed to fetch tour events for tourId=${tourId}:`, err);
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
    <div className="tour-details-page">
      <div className="tour-details">
        <header className="tour-header">
          <h1 className="tour-title">{tour.name}</h1>
          <p className="tour-description">{tour.description}</p>
          {tour.tourImageUrl && (
            <img src={tour.tourImageUrl} alt={tour.name} className="tour-main-image" />
          )}
        </header>

        <section className="tour-events">
          <h2 onClick={() => setShowEvents(!showEvents)} className="tour-events-header">
            Tour Events <ChevronDown className={`chevron-icon ${showEvents ? 'open' : ''}`} />
          </h2>
          {showEvents && (
            tourEvents.length === 0 ? (
              <p>No events available for this tour.</p>
            ) : (
              <ul className="event-list">
                {tourEvents.sort((a, b) => a.seq - b.seq).map((event) => (
                  <li key={event.tourEventId} className="event-item">
                    <Calendar className="event-icon" />
                    <div>
                      <h3>{event.seq}. {event.seqDesc}</h3>
                      <p>Event: {events[event.eventId]?.name || "Unknown Event"}</p>
                      <p>Hotel: {hotels[event.hotelId]?.name || "Unknown Hotel"}</p>
                    </div>
                  </li>
                ))}
              </ul>
            )
          )}
        </section>

        {tourId && (
          <>
            <h2 className="packages-header">Packages</h2>
            <PackageList tourId={tourId} />
          </>
        )}
      </div>
    </div>
  );
};

export default TourDetails;