import React, { useState, useEffect } from "react";
import { Button, Table, Modal, Form } from "react-bootstrap";
import { getAllCountries } from "../../countries/api/countries.api";
import {
  getAllCities,
  getCityById,
  addCity,
  updateCity,
  deleteCity,
} from "../../cities/api/cities.api";
import {
  CityResponseModel,
  CityRequestModel,
} from "../../cities/models/city.model";
import { CountryResponseModel } from "../../countries/models/country.model";
import "./CitiesTab.css";

const CitiesTab: React.FC = () => {
  const [cities, setCities] = useState<CityResponseModel[]>([]);
  const [countries, setCountries] = useState<CountryResponseModel[]>([]);
  const [showModal, setShowModal] = useState(false);
  const [modalType, setModalType] = useState<"create" | "update" | "delete">(
    "create"
  );
  const [selectedCity, setSelectedCity] = useState<CityResponseModel | null>(
    null
  );
  const [formData, setFormData] = useState<CityRequestModel>({
    name: "",
    countryId: "",
  });
  const [viewingCity, setViewingCity] = useState<CityResponseModel | null>(
    null
  );

  const [nameError, setNameError] = useState(false);
  const [countryError, setCountryError] = useState(false);

  useEffect(() => {
    fetchCities();
    fetchCountries();
  }, []);

  const fetchCountries = async () => {
    try {
      const data = await getAllCountries();
      setCountries(data);
    } catch (error) {
      console.error("Error fetching countries:", error);
    }
  };

  const fetchCities = async () => {
    try {
      const data = await getAllCities();
      setCities(data);
    } catch (error) {
      console.error("Error fetching cities:", error);
    }
  };

  const handleViewCity = async (cityId: string) => {
    try {
      const city = await getCityById(cityId);
      setViewingCity(city);
    } catch (error) {
      console.error("Error fetching city details:", error);
    }
  };

  const getCountryName = (countryId: string) => {
    const country = countries.find((country) => country.countryId === countryId);
    return country ? country.name : "Unknown Country";
  };

  const handleSave = async () => {
    const isNameValid = formData.name.trim() !== "";
    const isCountryValid = !!formData.countryId.trim();

    setNameError(!isNameValid);
    setCountryError(!isCountryValid);

    try {
      if (modalType === "create") {
        await addCity(formData);
      } else if (modalType === "update" && selectedCity) {
        await updateCity(selectedCity.cityId, formData);
      }
      setShowModal(false);
      await fetchCities();
    } catch (error) {
      console.error("Error saving city:", error);
    }
  };

  const handleDelete = async () => {
    try {
      if (selectedCity) {
        await deleteCity(selectedCity.cityId);
        setShowModal(false);
        await fetchCities();
      }
    } catch (error) {
      console.error("Error deleting city:", error);
    }
  };

  return (
    <div>
      {viewingCity ? (
        <div>
          <Button
            variant="link"
            className="text-primary mb-3"
            onClick={() => setViewingCity(null)}
            style={{
              textDecoration: "none",
              display: "flex",
              alignItems: "center",
              gap: "5px",
            }}
          >
            <span>&larr;</span> Back to List
          </Button>
          <h3>{viewingCity.name}</h3>
          <p>
            <strong>City ID:</strong> {viewingCity.cityId}
          </p>
          <p>
            <strong>Country:</strong> {getCountryName(viewingCity.countryId)}
          </p>
        </div>
      ) : (
        <>
          <div className="d-flex justify-content-between align-items-center mb-3">
            <h3>Cities</h3>
            <Button
              variant="primary"
              onClick={() => {
                fetchCountries();
                setModalType("create");
                setFormData({ name: "", countryId: "" });
                setShowModal(true);
              }}
            >
              Create
            </Button>
          </div>
          <div
            className="cities-scrollbar"
            style={{ maxHeight: "700px", overflowY: "auto" }}
          >
            <Table
              bordered
              hover
              responsive
              className="rounded"
              style={{ borderRadius: "12px", overflow: "hidden" }}
            >
              <thead className="bg-light">
                <tr>
                  <th>Name</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {cities.map((city) => (
                  <tr key={city.cityId}>
                    <td
                      onClick={() => handleViewCity(city.cityId)}
                      style={{
                        cursor: "pointer",
                        color: "#007bff",
                        textDecoration: "underline",
                      }}
                    >
                      {city.name}
                    </td>
                    <td>
                      <Button
                        variant="outline-primary"
                        onClick={() => {
                          fetchCountries();
                          setSelectedCity(city);
                          setModalType("update");
                          setFormData({
                            name: city.name,
                            countryId: city.countryId,
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
                          setSelectedCity(city);
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
        </>
      )}

      <Modal show={showModal} onHide={() => setShowModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>
            {modalType === "create"
              ? "Create City"
              : modalType === "update"
              ? "Edit City"
              : "Delete City"}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {modalType === "delete" ? (
            <p>Are you sure you want to delete this city?</p>
          ) : (
            <Form>
              <Form.Group className="mb-3">
                <Form.Label>City Name</Form.Label>
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
                <div className="invalid-feedback">City name is required.</div>
              </Form.Group>
              <Form.Group className="mb-3">
                <Form.Label>Country</Form.Label>
                <Form.Select
                  value={formData.countryId}
                  onChange={(e) => {
                    setFormData({ ...formData, countryId: e.target.value });
                    setCountryError(false);
                  }}
                  isInvalid={countryError}
                >
                  <option value="">Select a country</option>
                  {countries.map((country) => (
                    <option key={country.countryId} value={country.countryId}>
                      {country.name}
                    </option>
                  ))}
                </Form.Select>
                <div className="invalid-feedback">
                  A country selection is required.
                </div>
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

export default CitiesTab;
