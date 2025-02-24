import React, { useEffect, useState } from "react";
import { Button, Table, Modal, Form } from "react-bootstrap";
import { useToursApi } from "../../../tours/api/tours.api";
import { TourRequestModel, TourResponseModel } from "../../../tours/models/Tour";
import { useTranslation } from "react-i18next";
import UploadImage from "../../../../shared/AWS/UploadImage";
import { useS3Upload } from "../../../../shared/AWS/useS3Upload";
import { toast } from "react-toastify";
import { useUserContext } from "../../../../context/UserContext";
import TourEventsTab from "./TourEventsTab";
import TourPackagesTab from "./TourPackagesTab";

const ToursTab: React.FC = () => {
  const {
    getAllTours,
    getTourByTourId,
    addTour,
    updateTour,
    deleteTour,
    calculateRevenueByTourId,
  } = useToursApi();
  const { t } = useTranslation();
  const { getPresignedUrl } = useS3Upload();
  const { roles } = useUserContext();

  const [tours, setTours] = useState<TourResponseModel[]>([]);
  const [showModal, setShowModal] = useState(false);
  const [modalType, setModalType] = useState<"create" | "update" | "delete">("create");
  const [selectedTour, setSelectedTour] = useState<TourResponseModel | null>(null);
  const [formData, setFormData] = useState<TourRequestModel>({ name: "", description: "" });
  const [viewingTour, setViewingTour] = useState<TourResponseModel | null>(null);
  const [tourNameError, setTourNameError] = useState(false);
  const [descriptionError, setDescriptionError] = useState(false);
  const [presignedUrl, setPresignedUrl] = useState<string | null>(null);
  const [selectedImage, setSelectedImage] = useState<File | null>(null);
  const [revenue, setRevenue] = useState<number | null>(null);
  const [imageUploadTourId, setImageUploadTourId] = useState<string | null>(null);

  const isAdmin = roles.includes("Admin");

  const uniformButtonStyle = { minWidth: "120px", margin:"0.2rem" };

  useEffect(() => {
    fetchTours();
  }, []);

  const fetchTours = async () => {
    try {
      const data = await getAllTours();
      setTours(data);
    } catch (error) {
      console.error("Error fetching tours:", error);
    }
  };

  const handleViewTour = async (tourId: string) => {
    try {
      const tour = await getTourByTourId(tourId);
      setViewingTour(tour);
      const rev = await calculateRevenueByTourId(tourId);
      setRevenue(rev);
    } catch (error) {
      console.error("Error fetching tour details:", error);
    }
  };

  const handleSave = async () => {
    const isTourNameValid = formData.name.trim() !== "";
    const isDescriptionValid = formData.description.trim() !== "";

    setTourNameError(!isTourNameValid);
    setDescriptionError(!isDescriptionValid);

    if (!isTourNameValid || !isDescriptionValid) return;

    try {
      if (modalType === "create") {
        await addTour({ ...formData, tourImageUrl: formData.tourImageUrl || "" });
      } else if (modalType === "update" && selectedTour) {
        await updateTour(selectedTour.tourId, { ...formData, tourImageUrl: formData.tourImageUrl || "" });
      }
      setShowModal(false);
      await fetchTours();
    } catch (error) {
      console.error("Error saving tour:", error);
    }
  };

  const handleDelete = async () => {
    try {
      if (selectedTour) {
        await deleteTour(selectedTour.tourId);
        setShowModal(false);
        await fetchTours();
      }
    } catch (error) {
      console.error("Error deleting tour:", error);
    }
  };

  const requestPresignedUrl = async (file: File) => {
    try {
      const { url } = await getPresignedUrl(file.name, file.type);
      setPresignedUrl(url);
      setSelectedImage(file);
    } catch (error) {
      console.error("Error generating pre-signed URL:", error);
    }
  };

  const extractImportantPart = (url: string) => {
    const parts = url.split("/");
    return parts[parts.length - 1];
  };

  const handleImageUploadComplete = (imageUrl: string) => {
    if (imageUploadTourId) {
      setTours((prevTours) =>
        prevTours.map((tour) =>
          tour.tourId === imageUploadTourId ? { ...tour, tourImageUrl: imageUrl } : tour
        )
      );
      setImageUploadTourId(null);
      toast.success("Image updated successfully!");
    }
  };

  return (
    <div className="p-3">
      {viewingTour ? (
        <div>
          <Button
            variant="link"
            size="sm"
            className="text-primary mb-3"
            onClick={() => setViewingTour(null)}
            style={{ textDecoration: "none", display: "flex", alignItems: "center", gap: "5px" }}
          >
            <span>&larr;</span> {t("backToListT")}
          </Button>
          <h3>{viewingTour.name}</h3>
          <p>
            <strong>{t("tourDescription")}:</strong> {viewingTour.description || t("noDescriptionT")}
          </p>
          <p>
            <strong>{t("revenue")}:</strong>{" "}
            {revenue !== null ? `$${(revenue / 100).toFixed(2)}` : t("noRevenue")}
          </p>
          <div className="mb-3">
            <TourEventsTab tourId={viewingTour.tourId} />
          </div>
          <div>
            <TourPackagesTab tourId={viewingTour.tourId} />
          </div>
        </div>
      ) : (
        <>
          <div className="d-flex justify-content-between align-items-center mb-3">
            <h3>{t("tours")}</h3>
            <Button
              variant="primary"
              size="sm"
              style={uniformButtonStyle}
              onClick={() => {
                setModalType("create");
                setFormData({ name: "", description: "" });
                setShowModal(true);
              }}
            >
              {t("createTour")}
            </Button>
          </div>
          <Table bordered hover responsive className="rounded">
            <thead className="bg-light">
              <tr>
                <th>{t("tourName")}</th>
                <th>{t("tourImageUrl")}</th>
                <th className="text-center">{t("actionsT")}</th>
              </tr>
            </thead>
            <tbody>
              {tours.map((tour) => (
                <tr key={tour.tourId}>
                  <td
                    onClick={() => handleViewTour(tour.tourId)}
                    style={{ cursor: "pointer", color: "#007bff", textDecoration: "underline" }}
                  >
                    {tour.name}
                  </td>
                  <td>
                    {tour.tourImageUrl ? (
                      <img
                        src={tour.tourImageUrl}
                        alt="Tour"
                        width="50"
                        height="50"
                        style={{ objectFit: "cover", borderRadius: "5px" }}
                      />
                    ) : (
                      <span>{t("noImage")}</span>
                    )}
                  </td>
                  <td className="text-center">
                    <Button
                      variant="outline-primary"
                      size="sm"
                      style={uniformButtonStyle}
                      className="mx-1"
                      onClick={() => {
                        setSelectedTour(tour);
                        setModalType("update");
                        setFormData({ name: tour.name, description: tour.description });
                        setShowModal(true);
                      }}
                    >
                      {t("editTour")}
                    </Button>
                    <Button
                      variant="outline-secondary"
                      size="sm"
                      style={uniformButtonStyle}
                      className="mx-1"
                      onClick={() => setImageUploadTourId(tour.tourId)}
                    >
                      {t("updateImage")}
                    </Button>
                    {isAdmin && (
                      <Button
                        variant="outline-danger"
                        size="sm"
                        style={uniformButtonStyle}
                        className="mx-1"
                        onClick={() => {
                          setSelectedTour(tour);
                          setModalType("delete");
                          setShowModal(true);
                        }}
                      >
                        {t("deleteTour")}
                      </Button>
                    )}
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
              ? t("createTour")
              : modalType === "update"
              ? t("editTour")
              : t("deleteTour")}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {modalType === "delete" ? (
            <p>{t("areYouSureDelete")}</p>
          ) : (
            <Form>
              <Form.Group className="mb-3">
                <Form.Label>{t("tourName")}</Form.Label>
                <Form.Control
                  required
                  type="text"
                  value={formData.name}
                  onChange={(e) => {
                    setFormData({ ...formData, name: e.target.value });
                    setTourNameError(false);
                  }}
                  isInvalid={tourNameError}
                />
                <div className="invalid-feedback">{t("tourNameRequired")}</div>
              </Form.Group>
              <Form.Group className="mb-3">
                <Form.Label>{t("tourDescription")}</Form.Label>
                <Form.Control
                  as="textarea"
                  value={formData.description}
                  onChange={(e) => {
                    setFormData({ ...formData, description: e.target.value });
                    setDescriptionError(false);
                  }}
                  isInvalid={descriptionError}
                  rows={3}
                />
                <div className="invalid-feedback">{t("tourDescriptionRequired")}</div>
              </Form.Group>
              <Form.Group className="mb-3">
                <Form.Label>{t("tourImage")}</Form.Label>
                <UploadImage
                  onFileSelect={requestPresignedUrl}
                  presignedUrl={presignedUrl}
                  onUploadComplete={(imageUrl) => {
                    setFormData({ ...formData, tourImageUrl: imageUrl });
                  }}
                  tourId={selectedTour ? selectedTour.tourId : null}
                />
                {formData.tourImageUrl && (
                  <div>
                    <img src={formData.tourImageUrl} alt="Tour Preview" width="100" className="mt-2" />
                    <p>{extractImportantPart(formData.tourImageUrl)}</p>
                  </div>
                )}
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
            {t("cancelT")}
          </Button>
          <Button
            variant={modalType === "delete" ? "danger" : "primary"}
            size="sm"
            style={uniformButtonStyle}
            className="mx-1"
            onClick={modalType === "delete" ? handleDelete : handleSave}
          >
            {modalType === "delete" ? t("confirmT") : t("saveT")}
          </Button>
        </Modal.Footer>
      </Modal>

      <Modal show={!!imageUploadTourId} onHide={() => setImageUploadTourId(null)}>
        <Modal.Header closeButton>
          <Modal.Title>{t("updateImage")}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <UploadImage
            onFileSelect={requestPresignedUrl}
            presignedUrl={presignedUrl}
            onUploadComplete={handleImageUploadComplete}
            tourId={imageUploadTourId}
          />
        </Modal.Body>
        <Modal.Footer className="d-flex justify-content-center">
          <Button
            variant="secondary"
            size="sm"
            style={uniformButtonStyle}
            className="mx-1"
            onClick={() => setImageUploadTourId(null)}
          >
            {t("cancelT")}
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default ToursTab;
