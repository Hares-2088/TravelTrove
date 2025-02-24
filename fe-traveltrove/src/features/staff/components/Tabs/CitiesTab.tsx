import React, { useState, useEffect } from "react";
import { Button, Table, Modal, Form } from "react-bootstrap";
import {
  AirportRequestModel,
  AirportResponseModel,
} from "../../../airports/models/airports.model";
import { CityResponseModel } from "../../../cities/models/city.model";
import { useAirportsApi } from "../../../airports/api/airports.api";
import { useCitiesApi } from "../../../cities/api/cities.api";
import { useTranslation } from "react-i18next";

const AirportsTab: React.FC = () => {
  const { getAllAirports, getAirportById, addAirport, updateAirport, deleteAirport } = useAirportsApi();
  const { getAllCities } = useCitiesApi();
  const { t } = useTranslation();

  const [airports, setAirports] = useState<AirportResponseModel[]>([]);
  const [cities, setCities] = useState<CityResponseModel[]>([]);
  const [showModal, setShowModal] = useState(false);
  const [modalType, setModalType] = useState<"create" | "update" | "delete">("create");
  const [selectedAirport, setSelectedAirport] = useState<AirportResponseModel | null>(null);
  const [formData, setFormData] = useState<AirportRequestModel>({ name: "", cityId: "" });
  const [viewingAirport, setViewingAirport] = useState<AirportResponseModel | null>(null);

  const [nameError, setNameError] = useState(false);
  const [cityError, setCityError] = useState(false);

  useEffect(() => {
    fetchAirports();
    fetchCities();
  }, []);

  const fetchCities = async () => {
    try {
      const data = await getAllCities();
      setCities(data);
    } catch (error) {
      console.error(t("error.fetchingCities"), error);
    }
  };

  const fetchAirports = async () => {
    try {
      const data = await getAllAirports();
      setAirports(data);
    } catch (error) {
      console.error(t("error.fetchingAirports"), error);
    }
  };

  const handleViewAirport = async (airportId: string) => {
    try {
      const airport = await getAirportById(airportId);
      setViewingAirport(airport);
    } catch (error) {
      console.error(t("error.fetchingAirportDetails"), error);
    }
  };

  const getCityName = (cityId: string) => {
    const city = cities.find((city) => city.cityId === cityId);
    return city ? city.name : t("unknownCity");
  };

  const handleSave = async () => {
    const isNameValid = formData.name.trim() !== "";
    const isCityValid = !!formData.cityId.trim();

    setNameError(!isNameValid);
    setCityError(!isCityValid);

    if (!isNameValid || !isCityValid) return;

    try {
      if (modalType === "create") {
        await addAirport(formData);
      } else if (modalType === "update" && selectedAirport) {
        await updateAirport(formData, selectedAirport.airportId);
      }
      setShowModal(false);
      await fetchAirports();
    } catch (error) {
      console.error(t("error.savingAirport"), error);
    }
  };

  const handleDelete = async () => {
    try {
      if (selectedAirport) {
        await deleteAirport(selectedAirport.airportId);
        setShowModal(false);
        await fetchAirports();
      }
    } catch (error) {
      console.error(t("error.deletingAirport"), error);
    }
  };

  const uniformButtonStyle = { minWidth: "120px", margin:"0.2rem" };


  return (
    <div>
      {viewingAirport ? (
        <div>
          <Button
            variant="link"
            size="sm"
            className="text-primary mb-3 mx-1"
            onClick={() => setViewingAirport(null)}
            style={{
              textDecoration: "none",
              display: "flex",
              alignItems: "center",
              gap: "5px",
            }}
          >
            <span>&larr;</span> {t("backToList")}
          </Button>
          <h3>{viewingAirport.name}</h3>
          <p>
            <strong>{t("city")}: </strong>
            {getCityName(viewingAirport.cityId)}
          </p>
        </div>
      ) : (
        <>
          <div className="d-flex justify-content-between align-items-center mb-3">
            <h3>{t("airports")}</h3>
            <Button
              variant="primary"
              size="sm"
              style={uniformButtonStyle}
              className="mx-1"
              onClick={() => {
                fetchCities();
                setModalType("create");
                setFormData({ name: "", cityId: "" });
                setShowModal(true);
              }}
            >
              {t("create")}
            </Button>
          </div>
          <Table bordered hover responsive className="rounded">
            <thead className="bg-light">
              <tr>
                <th>{t("name")}</th>
                <th className="text-center">{t("actions")}</th>
              </tr>
            </thead>
            <tbody>
              {airports.map((airport) => (
                <tr key={airport.airportId}>
                  <td
                    onClick={() => handleViewAirport(airport.airportId)}
                    style={{ cursor: "pointer", color: "#007bff", textDecoration: "underline" }}
                  >
                    {airport.name}
                  </td>
                  <td className="text-center">
                    <Button
                      variant="outline-primary"
                      size="sm"
                      style={uniformButtonStyle}
                      className="mx-1"
                      onClick={() => {
                        fetchCities();
                        setSelectedAirport(airport);
                        setModalType("update");
                        setFormData({ name: airport.name, cityId: airport.cityId });
                        setShowModal(true);
                      }}
                    >
                      {t("edit")}
                    </Button>
                    <Button
                      variant="outline-danger"
                      size="sm"
                      style={uniformButtonStyle}
                      className="mx-1"
                      onClick={() => {
                        setSelectedAirport(airport);
                        setModalType("delete");
                        setShowModal(true);
                      }}
                    >
                      {t("delete")}
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
            {modalType === "create"
              ? t("createAirport")
              : modalType === "update"
              ? t("editAirport")
              : t("deleteAirport")}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {modalType === "delete" ? (
            <p>{t("deleteConfirmation")}</p>
          ) : (
            <Form>
              <Form.Group className="mb-3">
                <Form.Label>{t("airportName")}</Form.Label>
                <Form.Control
                  required
                  type="text"
                  value={formData.name}
                  onChange={(e) => {
                    setFormData({ ...formData, name: e.target.value });
                    setNameError(false);
                  }}
                  isInvalid={nameError}
                />
                <div className="invalid-feedback">{t("nameRequired")}</div>
              </Form.Group>
              <Form.Group className="mb-3">
                <Form.Label>{t("city")}</Form.Label>
                <Form.Select
                  value={formData.cityId}
                  onChange={(e) => {
                    setFormData({ ...formData, cityId: e.target.value });
                    setCityError(false);
                  }}
                  isInvalid={cityError}
                >
                  <option value="">{t("selectCity")}</option>
                  {cities.map((city) => (
                    <option key={city.cityId} value={city.cityId}>
                      {city.name}
                    </option>
                  ))}
                </Form.Select>
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
            {t("cancel")}
          </Button>
          <Button
            variant={modalType === "delete" ? "danger" : "primary"}
            size="sm"
            style={uniformButtonStyle}
            className="mx-1"
            onClick={modalType === "delete" ? handleDelete : handleSave}
          >
            {modalType === "delete" ? t("confirm") : t("save")}
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default AirportsTab;
