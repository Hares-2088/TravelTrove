import React, { useState, useEffect } from "react";
import { Button, Table, Modal, Form } from "react-bootstrap";
import { useTourEventsApi } from "../../../tourevents/api/tourevent.api";
import { useEventsApi } from "../../../events/api/events.api";
import { useHotelsApi } from "../../../hotels/api/hotels.api";
import { useTranslation } from "react-i18next";
import {
  TourEventRequestModel,
  TourEventResponseModel,
} from "../../../tourevents/model/tourevents.model";
import "../../../../shared/css/Scrollbar.css";
import { useUserContext } from "../../../../context/UserContext";

interface EventResponseModel {
  eventId: string;
  name: string;
}

interface HotelResponseModel {
  hotelId: string;
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

  const { getAllHotels } = useHotelsApi();

  const { roles } = useUserContext();
  const isAdmin = roles.includes("Admin");

  const { t } = useTranslation(); // Initialize translation hook
  const [tourEvents, setTourEvents] = useState<TourEventResponseModel[]>([]);
  const [events, setEvents] = useState<EventResponseModel[]>([]);
  const [hotels, setHotels] = useState<HotelResponseModel[]>([]);
  const [loading, setLoading] = useState(true);

  const [showModal, setShowModal] = useState(false);
  const [modalType, setModalType] = useState<"create" | "update" | "delete">(
    "create"
  );
  const [selectedEvent, setSelectedEvent] =
    useState<TourEventResponseModel | null>(null);

  const [formData, setFormData] = useState<TourEventRequestModel>({
    tourId: tourId,
    seq: 0,
    seqDesc: "",
    eventId: "",
    hotelId: "",
  });

  const [seqError, setSeqError] = useState(false);
  const [seqDescError, setSeqDescError] = useState(false);
  const [eventIdError, setEventIdError] = useState(false);
  const [hotelIdError, setHotelIdError] = useState(false);

  useEffect(() => {
    fetchTourEvents();
    fetchAllEvents();
    fetchAllHotels();
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

  const fetchAllHotels = async () => {
    try {
      const allHotels = await getAllHotels();
      setHotels(allHotels);
    } catch (error) {
      console.error("Error fetching hotels:", error);
    }
  };

  const handleSave = async () => {
    const isSeqValid = formData.seq > 0;
    const isSeqDescValid = formData.seqDesc.trim() !== "";
    const isEventIdValid = formData.eventId.trim() !== "";
    const isHotelIdValid = formData.hotelId.trim() !== ""; // Validate hotelId

    setSeqError(!isSeqValid);
    setSeqDescError(!isSeqDescValid);
    setEventIdError(!isEventIdValid);
    setHotelIdError(!isHotelIdValid); // Set error state for hotelId

    if (!isSeqValid || !isSeqDescValid || !isEventIdValid || !isHotelIdValid)
      return;

    try {
      if (modalType === "create") {
        const highestSeq = Math.max(...tourEvents.map((event) => event.seq), 0); // Get highest seq number
        setFormData({ ...formData, seq: highestSeq + 1 }); // Set next seq value
        await addTourEvent(formData); // Save new event with hotelId
      } else if (modalType === "update" && selectedEvent) {
        await updateTourEvent(selectedEvent.tourEventId, formData); // Update existing event
      }
      setShowModal(false);
      await fetchTourEvents(); // Refresh the list after saving
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
    return event ? event.name : t("unknownEvent");
  };

  const getHotelNameById = (hotelId: string) => {
    const hotel = hotels.find((h) => h.hotelId === hotelId);
    return hotel ? hotel.name : t("unknownHotel");
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
              seq: Math.max(...tourEvents.map((event) => event.seq), 0) + 1,
              seqDesc: "",
              eventId: "",
              hotelId: "",
            });
            setShowModal(true);
          }}
        >
          {t("createButton")}
        </Button>
      </div>

      <div style={{ maxHeight: "550px", overflowY: "auto" }}>
        <Table bordered hover responsive>
          <thead className="bg-light">
            <tr>
              <th>{t("seqLabel")}</th>
              <th>{t("descLabel")}</th>
              <th>{t("eventLabel")}</th>
              <th>{t("hotelLabel")}</th>
              <th>{t("actions")}</th>
            </tr>
          </thead>
          <tbody>
            {tourEvents
              .sort((a, b) => a.seq - b.seq) // Sort by `seq` in ascending order
              .map((event) => (
                <tr key={event.tourEventId}>
                  <td>{event.seq}</td>
                  <td>{event.seqDesc}</td>
                  <td>{getEventNameById(event.eventId)}</td>
                  <td>{getHotelNameById(event.hotelId)}</td>
                  <td>
                    <div className="d-flex flex-column flex-md-row gap-2">
                      <Button
                        variant="outline-primary"
                        size="sm"
                        className="flex-grow-1 flex-md-grow-0"
                        onClick={() => {
                          setSelectedEvent(event);
                          setModalType("update");
                          setFormData({
                            tourId,
                            seq: event.seq,
                            seqDesc: event.seqDesc,
                            eventId: event.eventId,
                            hotelId: event.hotelId,
                          });
                          setShowModal(true);
                        }}
                      >
                        {t("editButton")}
                      </Button>

                      <Button
                        variant="outline-danger"
                        size="sm"
                        className="flex-grow-1 flex-md-grow-0"
                        
                        onClick={() => {
                          setSelectedEvent(event);
                          setModalType("delete");
                          setShowModal(true);
                        }}
                      >
                        {t("deleteButton")}
                      </Button>
                    </div>
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

              <Form.Group className="mb-3">
                <Form.Label>{t("hotelLabel")}</Form.Label>
                <Form.Select
                  value={formData.hotelId}
                  onChange={(e) => {
                    setFormData({ ...formData, hotelId: e.target.value });
                    setHotelIdError(false);
                  }}
                  isInvalid={hotelIdError}
                  disabled={loading}
                >
                  <option value="">{t("selectHotel")}</option>
                  {hotels.map((hotel) => (
                    <option key={hotel.hotelId} value={hotel.hotelId}>
                      {hotel.name}
                    </option>
                  ))}
                </Form.Select>
                <div className="invalid-feedback">{t("invalidHotel")}</div>
              </Form.Group>
            </Form>
          )}
        </Modal.Body>
        <Modal.Footer>
          {isAdmin && (
            <Button variant="secondary" onClick={() => setShowModal(false)}>
              {t("cancelTE")}
            </Button>
          )}
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
