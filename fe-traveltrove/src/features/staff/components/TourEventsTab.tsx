import React, { useState, useEffect } from "react";
import { Button, Table, Modal, Form } from "react-bootstrap";
import { useTourEventsApi } from '../../tourevents/api/tourevent.api';
import { useEventsApi } from "../../events/api/events.api";
import { useTranslation } from 'react-i18next';
import {
  TourEventRequestModel,
  TourEventResponseModel,
} from "../../tourevents/model/tourevents.model";
import "../../../shared/css/Scrollbar.css";

interface EventResponseModel {
  eventId: string;
  name: string;
}

interface TourEventsTabProps {
  tourId: string;
}

const TourEventsTab: React.FC<TourEventsTabProps> = ({ tourId }) => {
  const {
    getTourEventsByTourId,
    addTourEvent,
    updateTourEvent,
    deleteTourEvent,
  } = useTourEventsApi();

  const { getAllEvents } = useEventsApi();

  const { t } = useTranslation(); // Initialize translation hook
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
        <h3>{t("header")}</h3>
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
          {t("createButton")}
        </Button>
      </div>

      <div className="dashboard-scrollbar" style={{ maxHeight: "550px", overflowY: "auto" }}>
        <Table bordered hover responsive>
          <thead className="bg-light">
            <tr>
              <th>{t("seqLabel")}</th>
              <th>{t("descLabel")}</th>
              <th>{t("eventLabel")}</th>
              <th>{t("actions")}</th>
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
                    {t("editButton")}
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
                    {t("deleteButton")}
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
              ? t("createTitle")
              : modalType === "update"
                ? t("editTitle")
                : t("deleteTitle")}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {modalType === "delete" ? (
            <p>{t("deleteConfirmationTE")}</p>
          ) : (
            <Form>
              <Form.Group className="mb-3">
                <Form.Label>{t("seqLabel")}</Form.Label>
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
                <div className="invalid-feedback">{t("invalidSeq")}</div>
              </Form.Group>

              <Form.Group className="mb-3">
                <Form.Label>{t("descLabel")}</Form.Label>
                <Form.Control
                  type="text"
                  value={formData.seqDesc}
                  onChange={(e) => {
                    setFormData({ ...formData, seqDesc: e.target.value });
                    setSeqDescError(false);
                  }}
                  isInvalid={seqDescError}
                />
                <div className="invalid-feedback">{t("invalidDesc")}</div>
              </Form.Group>

              <Form.Group className="mb-3">
                <Form.Label>{t("eventLabel")}</Form.Label>
                <Form.Select
                  value={formData.eventId}
                  onChange={(e) => {
                    setFormData({ ...formData, eventId: e.target.value });
                    setEventIdError(false);
                  }}
                  isInvalid={eventIdError}
                >
                  <option value="">{t("selectEvent")}</option>
                  {events.map((event) => (
                    <option key={event.eventId} value={event.eventId}>
                      {event.name}
                    </option>
                  ))}
                </Form.Select>
                <div className="invalid-feedback">{t("invalidEvent")}</div>
              </Form.Group>
            </Form>
          )}
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowModal(false)}>
            {t("cancelTE")}
          </Button>
          <Button
            variant={modalType === "delete" ? "danger" : "primary"}
            onClick={modalType === "delete" ? handleDelete : handleSave}
          >
            {modalType === "delete" ? t("confirmButton") : t("saveTE")}
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default TourEventsTab;
