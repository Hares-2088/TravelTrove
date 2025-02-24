import React, { useState, useEffect } from "react";
import { Button, Table, Modal, Form } from "react-bootstrap";
import { useTranslation } from "react-i18next";
import { useEventsApi } from "../../../events/api/events.api";
import { useCitiesApi } from "../../../cities/api/cities.api";
import { useCountriesApi } from "../../../countries/api/countries.api";
import {
  EventResponseModel,
  EventRequestModel,
} from "../../../events/model/events.model";
import "../../../../shared/css/Scrollbar.css";
import FilterBar from "../../../../shared/components/FilterBar";

const EventsTab: React.FC = () => {
  const { t } = useTranslation();
  const { getAllEvents, getEventById, addEvent, updateEvent, deleteEvent } = useEventsApi();
  const { getAllCities } = useCitiesApi();
  const { getAllCountries } = useCountriesApi();
  const [events, setEvents] = useState<EventResponseModel[]>([]);
  const [cities, setCities] = useState<{ id: string; name: string; countryId: string }[]>([]);
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
  const [viewingEvent, setViewingEvent] = useState<EventResponseModel | null>(null);

  const [selectedCountry, setSelectedCountry] = useState<string>("");
  const [selectedCity, setSelectedCity] = useState<string>("");

  // Uniform button style for consistent sizing
  const uniformButtonStyle = { minWidth: "120px" };

  const fetchEvents = async () => {
    try {
      const fetchedEvents = await getAllEvents();
      setEvents(fetchedEvents);
    } catch (error) {
      console.error(t("error.fetchingEvents"), error);
    }
  };

  const fetchCities = async () => {
    try {
      const fetchedCities = await getAllCities();
      setCities(
        fetchedCities.map((city) => ({
          id: city.cityId,
          name: city.name,
          countryId: city.countryId,
        }))
      );
    } catch (error) {
      console.error(t("error.fetchingCities"), error);
    }
  };

  const fetchCountries = async () => {
    try {
      const fetchedCountries = await getAllCountries();
      setCountries(
        fetchedCountries.map((country) => ({
          id: country.countryId,
          name: country.name,
        }))
      );
    } catch (error) {
      console.error(t("error.fetchingCountries"), error);
    }
  };

  useEffect(() => {
    fetchEvents();
    fetchCities();
    fetchCountries();
  }, []);

  const handleViewEvent = async (eventId: string) => {
    try {
      const event = await getEventById(eventId);
      setViewingEvent(event);
    } catch (error) {
      console.error(t("error.fetchingEventDetails"), error);
    }
  };

  const handleSave = async () => {
    try {
      if (!formData.name.trim()) {
        alert(t("nameRequiredE"));
        return;
      }
      if (!formData.description.trim()) {
        alert(t("descriptionRequired"));
        return;
      }
      if (modalType === "create") {
        await addEvent(formData);
      } else if (modalType === "update" && selectedEvent) {
        await updateEvent(selectedEvent.eventId, formData);
      }
      setShowModal(false);
      await fetchEvents();
      await fetchCities();
      await fetchCountries();
    } catch (error) {
      console.error(t("error.savingEvent"), error);
    }
  };

  const handleDelete = async () => {
    try {
      if (selectedEvent) {
        await deleteEvent(selectedEvent.eventId);
        setShowModal(false);
        await fetchEvents();
        await fetchCities();
        await fetchCountries();
      }
    } catch (error) {
      console.error(t("error.deletingEvent"), error);
    }
  };

  const filteredEvents = events.filter((event) => {
    const matchesCountry = selectedCountry ? event.countryId === selectedCountry : true;
    const matchesCity = selectedCity ? event.cityId === selectedCity : true;
    return matchesCountry && matchesCity;
  });

  return (
    <div>
      {viewingEvent ? (
        <div>
          <Button
            variant="link"
            size="sm"
            className="text-primary mb-3 mx-1"
            onClick={() => setViewingEvent(null)}
            style={{
              textDecoration: "none",
              display: "flex",
              alignItems: "center",
              gap: "5px",
            }}
          >
            <span>&larr;</span> {t("backToListE")}
          </Button>
          <h3>{viewingEvent.name}</h3>
          <p>
            <strong>{t("eventDescription")}:</strong> {viewingEvent.description}
          </p>
          <p>
            <strong>{t("image")}:</strong> {viewingEvent.image || t("noDescription")}
          </p>
        </div>
      ) : (
        <>
          <div className="d-flex justify-content-between align-items-center mb-3">
            <h3>{t("eventTitle")}</h3>
            <Button
              variant="primary"
              size="sm"
              style={uniformButtonStyle}
              className="mx-1"
              onClick={() => {
                fetchCities();
                fetchCountries();
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
              {t("createE")}
            </Button>
          </div>
          <FilterBar
            filters={[
              {
                label: t("selectCountryE"),
                value: selectedCountry,
                options: countries.map((country) => ({
                  value: country.id,
                  label: country.name,
                })),
                onChange: setSelectedCountry,
                onClick: fetchCountries,
              },
              {
                label: t("selectCity"),
                value: selectedCity,
                options: cities
                  .filter((city) => !selectedCountry || city.countryId === selectedCountry)
                  .map((city) => ({
                    value: city.id,
                    label: city.name,
                  })),
                onChange: setSelectedCity,
                onClick: fetchCities,
              },
            ]}
            resetFilters={() => {
              setSelectedCountry("");
              setSelectedCity("");
            }}
          />
          <div className="dashboard-scrollbar" style={{ maxHeight: "700px", overflowY: "auto" }}>
            <Table bordered hover responsive className="rounded">
              <thead className="bg-light">
                <tr>
                  <th>{t("nameE")}</th>
                  <th className="text-center">{t("actions")}</th>
                </tr>
              </thead>
              <tbody>
                {filteredEvents.length > 0 ? (
                  filteredEvents.map((event) => (
                    <tr key={event.eventId}>
                      <td
                        onClick={() => handleViewEvent(event.eventId)}
                        style={{
                          cursor: "pointer",
                          color: "#007bff",
                          textDecoration: "underline",
                        }}
                      >
                        {event.name}
                      </td>
                      <td className="text-center">
                        <Button
                          variant="outline-primary"
                          size="sm"
                          style={uniformButtonStyle}
                          className="mx-1"
                          onClick={() => {
                            fetchCities();
                            fetchCountries();
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
                          {t("editE")}
                        </Button>
                        <Button
                          variant="outline-danger"
                          size="sm"
                          style={uniformButtonStyle}
                          className="mx-1"
                          onClick={() => {
                            setSelectedEvent(event);
                            setModalType("delete");
                            setShowModal(true);
                          }}
                        >
                          {t("deleteE")}
                        </Button>
                      </td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td colSpan={2} className="text-center">
                      {t("noEventsFound")}
                    </td>
                  </tr>
                )}
              </tbody>
            </Table>
          </div>
        </>
      )}

      <Modal show={showModal} onHide={() => setShowModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>
            {modalType === "create"
              ? t("createE")
              : modalType === "update"
              ? t("editE")
              : t("deleteE")}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {modalType === "delete" ? (
            <p>{t("areYouSureE")}</p>
          ) : (
            <Form>
              <Form.Group className="mb-3">
                <Form.Label>{t("eventName")}</Form.Label>
                <Form.Control
                  type="text"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  isInvalid={!formData.name.trim()}
                />
                <Form.Control.Feedback type="invalid">
                  {t("nameRequiredE")}
                </Form.Control.Feedback>
              </Form.Group>
              <Form.Group className="mb-3">
                <Form.Label>{t("eventDescription")}</Form.Label>
                <Form.Control
                  as="textarea"
                  rows={3}
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  isInvalid={!formData.description.trim()}
                />
                <Form.Control.Feedback type="invalid">
                  {t("descriptionRequired")}
                </Form.Control.Feedback>
              </Form.Group>
              <Form.Group className="mb-3">
                <Form.Label>{t("city")}</Form.Label>
                <Form.Control
                  as="select"
                  value={formData.cityId}
                  onChange={(e) => setFormData({ ...formData, cityId: e.target.value })}
                >
                  <option value="">{t("city")}</option>
                  {cities.map((city) => (
                    <option key={city.id} value={city.id}>
                      {city.name}
                    </option>
                  ))}
                </Form.Control>
              </Form.Group>
              <Form.Group className="mb-3">
                <Form.Label>{t("country")}</Form.Label>
                <Form.Control
                  as="select"
                  value={formData.countryId}
                  onChange={(e) => setFormData({ ...formData, countryId: e.target.value })}
                >
                  <option value="">{t("selectCountryE")}</option>
                  {countries.map((country) => (
                    <option key={country.id} value={country.id}>
                      {country.name}
                    </option>
                  ))}
                </Form.Control>
              </Form.Group>
              <Form.Group className="mb-3">
                <Form.Label>{t("image")}</Form.Label>
                <Form.Control
                  type="text"
                  value={formData.image}
                  onChange={(e) => setFormData({ ...formData, image: e.target.value })}
                />
              </Form.Group>
            </Form>
          )}
        </Modal.Body>
        <Modal.Footer className="d-flex justify-content-center">
          <Button
            variant="secondary"
            size="sm"
            style={uniformButtonStyle}
            className="mx-1"
            onClick={() => setShowModal(false)}
          >
            {t("cancelE")}
          </Button>
          <Button
            variant={modalType === "delete" ? "danger" : "primary"}
            size="sm"
            style={uniformButtonStyle}
            className="mx-1"
            onClick={modalType === "delete" ? handleDelete : handleSave}
          >
            {modalType === "delete" ? t("confirmE") : t("saveE")}
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default EventsTab;
