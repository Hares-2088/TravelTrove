import React, { useState, useEffect } from "react";
import { Button, Table, Modal, Form } from "react-bootstrap";
import { useTranslation } from "react-i18next";
import { useCountriesApi } from "../../../countries/api/countries.api";
import {
  CountryResponseModel,
  CountryRequestModel,
} from "../../../countries/models/country.model";
import "../../../../shared/css/Scrollbar.css";

const CountriesTab: React.FC = () => {
  const {
    getAllCountries,
    getCountryById,
    addCountry,
    updateCountry,
    deleteCountry,
  } = useCountriesApi();
  const { t } = useTranslation();
  const [countries, setCountries] = useState<CountryResponseModel[]>([]);
  const [showModal, setShowModal] = useState(false);
  const [modalType, setModalType] = useState<"create" | "update" | "delete">("create");
  const [selectedCountry, setSelectedCountry] =
    useState<CountryResponseModel | null>(null);
  const [formData, setFormData] = useState<CountryRequestModel>({
    name: "",
    image: "",
  });
  const [viewingCountry, setViewingCountry] =
    useState<CountryResponseModel | null>(null);

  const [countryNameError, setCountryNameError] = useState(false);
  const [imageError, setImageError] = useState(false);

  useEffect(() => {
    fetchCountries();
  }, []);

  const fetchCountries = async () => {
    try {
      const data = await getAllCountries();
      setCountries(data);
    } catch (error) {
      console.error(t("error.fetchingCountries"), error);
    }
  };

  const handleViewCountry = async (countryId: string) => {
    try {
      const country = await getCountryById(countryId);
      setViewingCountry(country);
    } catch (error) {
      console.error(t("error.fetchingCountryDetails"), error);
    }
  };

  const handleSave = async () => {
    const isCountryNameValid = formData.name.trim() !== "";
    const isImageValid = !!formData.image.trim();
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
      console.error(t("error.savingCountry"), error);
    }
  };

  const handleDelete = async () => {
    try {
      if (selectedCountry) {
        await deleteCountry(selectedCountry.countryId);
        setShowModal(false);
        await fetchCountries();
      }
    } catch (error) {
      console.error(t("error.deletingCountry"), error);
    }
  };

  const uniformButtonStyle = { minWidth: "120px", margin:"0.2rem" };

  return (
    <div>
      {viewingCountry ? (
        <div>
          <Button
            variant="link"
            size="sm"
            className="text-primary mb-3 mx-1"
            onClick={() => setViewingCountry(null)}
            style={{
              textDecoration: "none",
              display: "flex",
              alignItems: "center",
              gap: "5px",
            }}
          >
            <span>&larr;</span> {t('backToListC')}
          </Button>
          <h3>{viewingCountry.name}</h3>
          <p>
            <strong>{t('countryImage')}:</strong>{" "}
            {viewingCountry.image || t('noImage')}
          </p>
        </div>
      ) : (
        <>
          <div className="d-flex justify-content-between align-items-center mb-3">
            <h3>{t('title')}</h3>
            <Button
              variant="primary"
              size="sm"
              style={uniformButtonStyle}
              className="mx-1"
              onClick={() => {
                setModalType("create");
                setFormData({ name: "", image: "" });
                setShowModal(true);
              }}
            >
              {t('createC')}
            </Button>
          </div>
          <div
            
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
                  <th>{t('name')}</th>
                  <th className="text-center">{t('actions')}</th>
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
                    <td className="text-center">
                      <Button
                        variant="outline-primary"
                        size="sm"
                        style={uniformButtonStyle}
                        className="mx-1"
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
                        {t('editC')}
                      </Button>
                      <Button
                        variant="outline-danger"
                        size="sm"
                        style={uniformButtonStyle}
                        className="mx-1"
                        onClick={() => {
                          setSelectedCountry(country);
                          setModalType("delete");
                          setShowModal(true);
                        }}
                      >
                        {t('deleteC')}
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
              ? t('createCountry')
              : modalType === "update"
              ? t('editCountry')
              : t('deleteCountry')}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {modalType === "delete" ? (
            <p>{t('areYouSure')}</p>
          ) : (
            <Form>
              <Form.Group className="mb-3">
                <Form.Label>{t('countryName')}</Form.Label>
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
                  {t('nameRequired')}
                </div>
              </Form.Group>
              <Form.Group className="mb-3">
                <Form.Label>{t('countryImage')}</Form.Label>
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
                <div className="invalid-feedback">
                  {t('imageRequired')}
                </div>
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
            {t('cancelC')}
          </Button>
          <Button
            variant={modalType === "delete" ? "danger" : "primary"}
            size="sm"
            style={uniformButtonStyle}
            className="mx-1"
            onClick={modalType === "delete" ? handleDelete : handleSave}
          >
            {modalType === "delete" ? t('confirmC') : t('saveC')}
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default CountriesTab;
