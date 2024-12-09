import React, { useState, useEffect } from 'react';
import { Button, Table, Modal, Form } from 'react-bootstrap';
import {
  getTourEventsByTourId,
  addTourEvent,
  updateTourEvent,
  deleteTourEvent
} from '../../tourEvents/api/tourevent.api.ts';
import { getAllEvents } from '../../events/api/events.api';
import { TourEventRequestModel, TourEventResponseModel } from '../../tourevents/model/tourevents.model';
import "../../../shared/css/Scrollbar.css";

interface EventResponseModel {
  eventId: string;
  name: string;
}

interface TourEventsTabProps {
  tourId: string;
}

const TourEventsTab: React.FC<TourEventsTabProps> = ({ tourId }) => {
  const [tourEvents, setTourEvents] = useState<TourEventResponseModel[]>([]);
  const [events, setEvents] = useState<EventResponseModel[]>([]);
  const [loading, setLoading] = useState(true);

  const [showModal, setShowModal] = useState(false);
  const [modalType, setModalType] = useState<"create" | "update" | "delete">("create");
  const [selectedEvent, setSelectedEvent] = useState<TourEventResponseModel | null>(null);

  const [formData, setFormData] = useState<TourEventRequestModel>({
    tourId: tourId,
    seq: 0,
    seqDesc: "",
    eventId: "",
  });

  const [seqError, setSeqError] = useState(false);
  const [seqDescError, setSeqDescError] = useState(false);
  const [eventIdError, setEventIdError] = useState(false);

  useEffect(() => {
    fetchTourEvents();
    fetchAllEvents();
  }, [tourId]);

  const fetchTourEvents = async () => {
    setLoading(true);
    try {
      const events = await getTourEventsByTourId(tourId);
      setTourEvents(events);
    } catch (error) {
      console.error("Error fetching tour events:", error);
    } finally {
      setLoading(false);
    }
  };

  const fetchAllEvents = async () => {
    try {
      const allEvents = await getAllEvents();
      setEvents(allEvents);
    } catch (error) {
      console.error("Error fetching events:", error);
    }
  };

  const handleSave = async () => {
    const isSeqValid = formData.seq > 0;
    const isSeqDescValid = formData.seqDesc.trim() !== "";
    const isEventIdValid = formData.eventId.trim() !== "";

    setSeqError(!isSeqValid);
    setSeqDescError(!isSeqDescValid);
    setEventIdError(!isEventIdValid);

    if (!isSeqValid || !isSeqDescValid || !isEventIdValid) return;

    try {
      if (modalType === "create") {
        await addTourEvent(formData);
      } else if (modalType === "update" && selectedEvent) {
        await updateTourEvent(selectedEvent.tourEventId, formData);
      }
      setShowModal(false);
      await fetchTourEvents();
    } catch (error) {
      console.error("Error saving tour event:", error);
    }
  };

  const handleDelete = async () => {
    try {
      if (selectedEvent) {
        await deleteTourEvent(selectedEvent.tourEventId);
        setShowModal(false);
        await fetchTourEvents();
      }
    } catch (error) {
      console.error("Error deleting tour event:", error);
    }
  };

  const getEventNameById = (eventId: string) => {
    const event = events.find((e) => e.eventId === eventId);
    return event ? event.name : "Unknown Event";
  };

  return (
    <div className="tour-events-tab">
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h3>Tour Events</h3>
        <Button
          variant="primary"
          onClick={() => {
            setModalType("create");
            setFormData({
              tourId,
              seq: tourEvents.length + 1,
              seqDesc: "",
              eventId: "",
            });
            setShowModal(true);
          }}
        >
          Create
        </Button>
      </div>

      <div className="dashboard-scrollbar" style={{ maxHeight: "550px", overflowY: "auto" }}>
        <Table bordered hover responsive>
          <thead className="bg-light">
          <tr>
            <th>Sequence</th>
            <th>Description</th>
            <th>Event</th>
            <th>Actions</th>
          </tr>
          </thead>
          <tbody>
          {tourEvents.map((event) => (
            <tr key={event.tourEventId}>
              <td>{event.seq}</td>
              <td>{event.seqDesc}</td>
              <td>{getEventNameById(event.eventId)}</td>
              <td>
                <Button
                  variant="outline-primary"
                  onClick={() => {
                    setSelectedEvent(event);
                    setModalType("update");
                    setFormData({
                      tourId,
                      seq: event.seq,
                      seqDesc: event.seqDesc,
                      eventId: event.eventId,
                    });
                    setShowModal(true);
                  }}
                >
                  Edit
                </Button>

                <Button
                  variant="outline-danger"
                  className="ms-2"
                  onClick={() => {
                    setSelectedEvent(event);
                    setModalType("delete");
                    setShowModal(true);
                  }}
                >
                  Delete
                </Button>
              </td>
            </tr>
          ))}
          </tbody>
        </Table>
      </div>

      <Modal show={showModal} onHide={() => setShowModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>
            {modalType === "create"
              ? "Create Tour Event"
              : modalType === "update"
                ? "Edit Tour Event"
                : "Delete Tour Event"}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {modalType === "delete" ? (
            <p>Are you sure you want to delete this tour event?</p>
          ) : (
            <Form>
              <Form.Group className="mb-3">
                <Form.Label>Sequence</Form.Label>
                <Form.Control
                  type="number"
                  min="1"
                  value={formData.seq}
                  onChange={(e) => {
                    setFormData({ ...formData, seq: Number(e.target.value) });
                    setSeqError(false);
                  }}
                  isInvalid={seqError}
                />
                <div className="invalid-feedback">Sequence must be greater than 0.</div>
              </Form.Group>

              <Form.Group className="mb-3">
                <Form.Label>Description</Form.Label>
                <Form.Control
                  type="text"
                  value={formData.seqDesc}
                  onChange={(e) => {
                    setFormData({ ...formData, seqDesc: e.target.value });
                    setSeqDescError(false);
                  }}
                  isInvalid={seqDescError}
                />
                <div className="invalid-feedback">Description is required.</div>
              </Form.Group>

              <Form.Group className="mb-3">
                <Form.Label>Event</Form.Label>
                <Form.Select
                  value={formData.eventId}
                  onChange={(e) => {
                    setFormData({ ...formData, eventId: e.target.value });
                    setEventIdError(false);
                  }}
                  isInvalid={eventIdError}
                >
                  <option value="">Select an Event</option>
                  {events.map((event) => (
                    <option key={event.eventId} value={event.eventId}>
                      {event.name}
                    </option>
                  ))}
                </Form.Select>
                <div className="invalid-feedback">Event is required.</div>
              </Form.Group>
            </Form>
          )}
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowModal(false)}>
            Cancel
          </Button>
          <Button
            variant={modalType === "delete" ? "danger" : "primary"}
            onClick={modalType === "delete" ? handleDelete : handleSave}
          >
            {modalType === "delete" ? "Confirm" : "Save"}
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default TourEventsTab;
