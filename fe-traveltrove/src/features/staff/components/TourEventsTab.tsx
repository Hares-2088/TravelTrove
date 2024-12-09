import React, { useEffect, useState } from "react";
import { Button, Table, Alert } from "react-bootstrap";
import { TourResponseModel } from "../../tours/models/Tour";
import { EventResponseModel } from "../../events/model/models";
import { getEventsByTourId } from "../../tourEvents/api/tourEvent.api";
import { getAllTours } from "../../tours/api/tours.api";

const TourEventsTab: React.FC = () => {
  const [tours, setTours] = useState<TourResponseModel[]>([]); // Assuming tours list is fetched elsewhere
  const [events, setEvents] = useState<EventResponseModel[]>([]);
  const [selectedTour, setSelectedTour] = useState<TourResponseModel | null>(null);

  useEffect(() => {
    // Example fetching for tours
    fetchTours();
  }, []);

  const fetchTours = async () => {
    try {
      // Replace with your actual API call to fetch tours
      const response = await getAllTours();
      setTours(response); // Store fetched tours in the state
    } catch (error) {
      console.error("Error fetching tours:", error);
    }
  };

  const handleViewTourEvents = async (tourId: string) => {
    try {
      const eventsList = await getEventsByTourId(tourId);
      setEvents(eventsList);
      setSelectedTour(tours.find((tour) => tour.tourId === tourId) || null);
    } catch (error) {
      console.error("Error fetching tour events:", error);
    }
  };

  return (
    <div>
      {selectedTour && (
        <>
          <Button
            variant="link"
            onClick={() => setSelectedTour(null)}
            className="text-primary mb-3"
            style={{ textDecoration: "none" }}
          >
            Back to List
          </Button>
          <h3>Events for {selectedTour.name}</h3>
          <Table bordered hover responsive className="rounded">
            <thead className="bg-light">
              <tr>
                <th>Name</th>
                <th>Date</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {events.length === 0 ? (
                <tr>
                  <td colSpan={3}>
                    <Alert variant="info">No events created for this tour yet.</Alert>
                  </td>
                </tr>
              ) : (
                events.map((event) => (
                  <tr key={event.eventId}>
                    <td>{event.name}</td>
                    <td>{event.date}</td>
                    <td>
                      <Button variant="outline-primary" onClick={() => alert(`Edit event ${event.eventId}`)}>
                        Edit
                      </Button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </Table>
        </>
      )}

      <div>
        <h3>Select a Tour</h3>
        <div>
          {tours.map((tour) => (
            <Button
              key={tour.tourId}
              variant="link"
              onClick={() => handleViewTourEvents(tour.tourId)}
              className="text-primary"
            >
              {tour.name}
            </Button>
          ))}
        </div>
      </div>
    </div>
  );
};

export default TourEventsTab;
