import React, { useState, useEffect } from "react";
import { Button, Table, Modal, Form } from "react-bootstrap";
import { useCountriesApi } from "../../countries/api/countries.api";
import {
  CountryResponseModel,
  CountryRequestModel,
} from "../../countries/models/country.model";
import "../../../shared/css/Scrollbar.css";

const CountriesTab: React.FC = () => {
  const {
    getAllCountries,
    getCountryById,
    addCountry,
    updateCountry,
    deleteCountry,
  } = useCountriesApi(); // Use Hook

  const [countries, setCountries] = useState<CountryResponseModel[]>([]);
  const [showModal, setShowModal] = useState(false);
  const [modalType, setModalType] = useState<"create" | "update" | "delete">(
    "create"
  );
  const [selectedCountry, setSelectedCountry] =
    useState<CountryResponseModel | null>(null);
  const [formData, setFormData] = useState<CountryRequestModel>({
    name: "",
    image: "",
  });
  const [viewingCountry, setViewingCountry] =
    useState<CountryResponseModel | null>(null);

  // Error Messages
  const [countryNameError, setCountryNameError] = useState(false);
  const [imageError, setImageError] = useState(false);

  // Fetch All Countries
  useEffect(() => {
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

  // View Country Details
  const handleViewCountry = async (countryId: string) => {
    try {
      const country = await getCountryById(countryId);
      setViewingCountry(country);
    } catch (error) {
      console.error("Error fetching country details:", error);
    }
  };

  // Save Country
  const handleSave = async () => {
    const isCountryNameValid = formData.name.trim() !== "";
    const isImageValid = formData.image.trim() !== "";

    setCountryNameError(!isCountryNameValid);
    setImageError(!isImageValid);

    if (!isCountryNameValid || !isImageValid) return;

    try {
      if (modalType === "create") {
        await addCountry(formData);
      } else if (modalType === "update" && selectedCountry) {
        await updateCountry(selectedCountry.countryId, formData);
      }
      setShowModal(false);
      await fetchCountries();
    } catch (error) {
      console.error("Error saving country:", error);
    }
  };

  // Delete Country
  const handleDelete = async () => {
    try {
      if (selectedCountry) {
        await deleteCountry(selectedCountry.countryId);
        setShowModal(false);
        await fetchCountries();
      }
    } catch (error) {
      console.error("Error deleting country:", error);
    }
  };

  return (
    <div>
      {viewingCountry ? (
        <div>
          <Button
            variant="link"
            className="text-primary mb-3"
            onClick={() => setViewingCountry(null)}
            style={{
              textDecoration: "none",
              display: "flex",
              alignItems: "center",
              gap: "5px",
            }}
          >
            <span>&larr;</span> Back to List
          </Button>
          <h3>{viewingCountry.name}</h3>
          <p>
            <strong>Image:</strong>{" "}
            {viewingCountry.image || "No image available"}
          </p>
        </div>
      ) : (
        <>
          <div className="d-flex justify-content-between align-items-center mb-3">
            <h3>Countries</h3>
            <Button
              variant="primary"
              onClick={() => {
                setModalType("create");
                setFormData({ name: "", image: "" });
                setShowModal(true);
              }}
            >
              Create
            </Button>
          </div>
          <div
            className="dashboard-scrollbar"
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
              {countries.map((country) => (
                <tr key={country.countryId}>
                  <td
                    onClick={() => handleViewCountry(country.countryId)}
                    style={{
                      cursor: "pointer",
                      color: "#007bff",
                      textDecoration: "underline",
                    }}
                  >
                    {country.name}
                  </td>
                  <td>
                    <Button
                      variant="outline-primary"
                      onClick={() => {
                        setSelectedCountry(country);
                        setModalType("update");
                        setFormData({
                          name: country.name,
                          image: country.image,
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
                        setSelectedCountry(country);
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
              ? "Create Country"
              : modalType === "update"
                ? "Edit Country"
                : "Delete Country"}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {modalType === "delete" ? (
            <p>Are you sure you want to delete this country?</p>
          ) : (
            <Form>
              <Form.Group className="mb-3">
                <Form.Label>Country Name</Form.Label>
                <Form.Control
                  required
                  type="text"
                  value={formData.name}
                  onChange={(e) => {
                    setFormData({ ...formData, name: e.target.value });
                    setCountryNameError(false);
                  }}
                  isInvalid={countryNameError}
                />
                <div className="invalid-feedback">
                  Country name is required.
                </div>
              </Form.Group>
              <Form.Group className="mb-3">
                <Form.Label>Country Image URL</Form.Label>
                <Form.Control
                  required
                  type="text"
                  value={formData.image}
                  onChange={(e) => {
                    setFormData({ ...formData, image: e.target.value });
                    setImageError(false);
                  }}
                  isInvalid={imageError}
                />
                <div className="invalid-feedback">Image URL is required.</div>
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

export default CountriesTab;
