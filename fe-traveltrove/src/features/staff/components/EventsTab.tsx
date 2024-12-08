import React, { useState, useEffect } from "react";
import { Button, Table, Modal, Form } from "react-bootstrap";
import {
  getAllEvents,
  getEventById,
  addEvent,
  updateEvent,
  deleteEvent,
} from "../../events/api/events.ts";
import { getAllCities } from "../../cities/api/cities.api";
import { getAllCountries } from "../../countries/api/countries.api";
import { EventResponseModel, EventRequestModel } from "../../events/model/models.ts";
import "./EventsTab.css";

const EventsTab: React.FC = () => {
  const [events, setEvents] = useState<EventResponseModel[]>([]);
  const [filteredEvents, setFilteredEvents] = useState<EventResponseModel[]>([]);
  const [cities, setCities] = useState<{ id: string; name: string }[]>([]);
  const [countries, setCountries] = useState<{ id: string; name: string }[]>([]);
  const [showModal, setShowModal] = useState(false);
  const [modalType, setModalType] = useState<"create" | "update" | "delete">("create");
  const [selectedEvent, setSelectedEvent] = useState<EventResponseModel | null>(null);
  const [formData, setFormData] = useState<EventRequestModel>({
    cityId: "",
    countryId: "",
    name: "",
    description: "",
    image: "",
  });
  const [filter, setFilter] = useState({ cityId: "", countryId: "" });
  const [viewingEvent, setViewingEvent] = useState<EventResponseModel | null>(null);

  useEffect(() => {
    fetchEvents();
    fetchCities();
    fetchCountries();
  }, []);

  useEffect(() => {
    applyFilters();
  }, [filter, events]);

  const fetchEvents = async () => {
    try {
      const data = await getAllEvents();
      setEvents(data);
      setFilteredEvents(data);
    } catch (error) {
      console.error("Error fetching events:", error);
    }
  };

  const fetchCities = async () => {
    try {
      const data = await getAllCities();
      setCities(data.map((city) => ({ id: city.cityId, name: city.name })));
    } catch (error) {
      console.error("Error fetching cities:", error);
    }
  };

  const fetchCountries = async () => {
    try {
      const data = await getAllCountries();
      setCountries(data.map((country) => ({ id: country.countryId, name: country.name })));
    } catch (error) {
      console.error("Error fetching countries:", error);
    }
  };

  const applyFilters = () => {
    let filtered = events;
    if (filter.cityId) {
      filtered = filtered.filter((event) => event.cityId === filter.cityId);
    }
    if (filter.countryId) {
      filtered = filtered.filter((event) => event.countryId === filter.countryId);
    }
    setFilteredEvents(filtered);
  };

  const handleViewEvent = async (eventId: string) => {
    try {
      const event = await getEventById(eventId);
      setViewingEvent(event);
    } catch (error) {
      console.error("Error fetching event details:", error);
    }
  };

  const handleSave = async () => {
    try {
      // Validation for required fields
      if (!formData.name.trim()) {
        alert("Event name is required.");
        return;
      }
      if (!formData.description.trim()) {
        alert("Event description is required.");
        return;
      }
  
      if (modalType === "create") {
        await addEvent(formData);
      } else if (modalType === "update" && selectedEvent) {
        await updateEvent(selectedEvent.eventId, formData);
      }
      setShowModal(false);
      await fetchEvents();
    } catch (error) {
      console.error("Error saving event:", error);
    }
  };
  

  const handleDelete = async () => {
    try {
      if (selectedEvent) {
        await deleteEvent(selectedEvent.eventId);
        setShowModal(false);
        await fetchEvents();
      }
    } catch (error) {
      console.error("Error deleting event:", error);
    }
  };

  return (
    <div>
      {viewingEvent ? (
        <div>
          <Button
            variant="link"
            className="text-primary mb-3"
            onClick={() => setViewingEvent(null)}
            style={{
              textDecoration: "none",
              display: "flex",
              alignItems: "center",
              gap: "5px",
            }}
          >
            <span>&larr;</span> Back to List
          </Button>
          <h3>{viewingEvent.name}</h3>
          <p>
            <strong>Event ID:</strong> {viewingEvent.eventId}
          </p>
          <p>
            <strong>Description:</strong> {viewingEvent.description}
          </p>
          <p>
            <strong>Image:</strong> {viewingEvent.image || "No image available"}
          </p>
        </div>
      ) : (
        <>
          <div className="d-flex justify-content-between align-items-center mb-3">
            <h3>Events</h3>
            <Button
              variant="primary"
              onClick={() => {
                setModalType("create");
                setFormData({
                  cityId: "",
                  countryId: "",
                  name: "",
                  description: "",
                  image: "",
                });
                setShowModal(true);
              }}
            >
              Create
            </Button>
          </div>

          {/* Filters */}
          <div className="mb-3">
            <Form>
              <Form.Group className="mb-3">
                <Form.Label>Filter by City</Form.Label>
                <Form.Control
                  as="select"
                  value={filter.cityId}
                  onChange={(e) => setFilter({ ...filter, cityId: e.target.value })}
                >
                  <option value="">All Cities</option>
                  {cities.map((city) => (
                    <option key={city.id} value={city.id}>
                      {city.name}
                    </option>
                  ))}
                </Form.Control>
              </Form.Group>
              <Form.Group>
                <Form.Label>Filter by Country</Form.Label>
                <Form.Control
                  as="select"
                  value={filter.countryId}
                  onChange={(e) => setFilter({ ...filter, countryId: e.target.value })}
                >
                  <option value="">All Countries</option>
                  {countries.map((country) => (
                    <option key={country.id} value={country.id}>
                      {country.name}
                    </option>
                  ))}
                </Form.Control>
              </Form.Group>
            </Form>
          </div>

          <Table bordered hover responsive className="rounded">
            <thead className="bg-light">
              <tr>
                <th>Name</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {filteredEvents.map((event) => (
                <tr key={event.eventId}>
                  <td
                    onClick={() => handleViewEvent(event.eventId)}
                    style={{ cursor: "pointer", color: "#007bff", textDecoration: "underline" }}
                  >
                    {event.name}
                  </td>
                  <td>
                    <Button
                      variant="outline-primary"
                      onClick={() => {
                        setSelectedEvent(event);
                        setModalType("update");
                        setFormData({
                          cityId: event.cityId,
                          countryId: event.countryId,
                          name: event.name,
                          description: event.description,
                          image: event.image,
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
        </>
      )}

      <Modal show={showModal} onHide={() => setShowModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>
            {modalType === "create" ? "Create Event" : "Edit Event"}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {modalType === "delete" ? (
            <p>Are you sure you want to delete this event?</p>
          ) : (
            <Form>
              <Form.Group className="mb-3">
                <Form.Label>Event Name</Form.Label>
                <Form.Control
                  type="text"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  isInvalid={!formData.name.trim()}
                />
                <Form.Control.Feedback type="invalid">
                  Event name is required.
                </Form.Control.Feedback>
              </Form.Group>
              <Form.Group className="mb-3">
                <Form.Label>Event Description</Form.Label>
                <Form.Control
                  as="textarea"
                  rows={3}
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  isInvalid={!formData.description.trim()}
                />
                <Form.Control.Feedback type="invalid">
                  Event description is required.
                </Form.Control.Feedback>
              </Form.Group>
              <Form.Group className="mb-3">
                <Form.Label>City</Form.Label>
                <Form.Control
                  as="select"
                  value={formData.cityId}
                  onChange={(e) => setFormData({ ...formData, cityId: e.target.value })}
                >
                  <option value="">Select a City</option>
                  {cities.map((city) => (
                    <option key={city.id} value={city.id}>
                      {city.name}
                    </option>
                  ))}
                </Form.Control>
              </Form.Group>
              <Form.Group className="mb-3">
                <Form.Label>Country</Form.Label>
                <Form.Control
                  as="select"
                  value={formData.countryId}
                  onChange={(e) => setFormData({ ...formData, countryId: e.target.value })}
                >
                  <option value="">Select a Country</option>
                  {countries.map((country) => (
                    <option key={country.id} value={country.id}>
                      {country.name}
                    </option>
                  ))}
                </Form.Control>
              </Form.Group>
              <Form.Group className="mb-3">
                <Form.Label>Image URL</Form.Label>
                <Form.Control
                  type="text"
                  value={formData.image}
                  onChange={(e) => setFormData({ ...formData, image: e.target.value })}
                />
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

export default EventsTab;
