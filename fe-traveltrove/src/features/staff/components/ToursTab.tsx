import { useEffect, useState } from "react";
import { useToursApi } from "../../tours/api/tours.api";
import { TourRequestModel, TourResponseModel } from "../../tours/models/Tour";
import { Button, Table, Modal, Form } from "react-bootstrap";
import TourEventsTab from "./TourEventsTab";
import "../../../shared/css/Scrollbar.css";

const ToursTab: React.FC = () => {
  const { getAllTours, getTourByTourId, addTour, updateTour, deleteTour } = useToursApi();

  const [tours, setTours] = useState<TourResponseModel[]>([]);
  const [showModal, setShowModal] = useState(false);
  const [modalType, setModalType] = useState<"create" | "update" | "delete">(
    "create"
  );
  const [selectedTour, setSelectedTour] = useState<TourResponseModel | null>(null);
  const [formData, setFormData] = useState<TourRequestModel>({
    name: "",
    description: "",
  });
  const [viewingTour, setViewingTour] = useState<TourResponseModel | null>(null);

  const [tourNameError, setTourNameError] = useState(false);
  const [descriptionError, setDescriptionError] = useState(false);

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
    } catch (error) {
      console.error("Error fetching Tour details:", error);
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
        await addTour(formData);
      } else if (modalType === "update" && selectedTour) {
        await updateTour(selectedTour.tourId, formData);
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

  return (
    <div>
      {viewingTour ? (
        <div>
          <Button
            variant="link"
            className="text-primary mb-3"
            onClick={() => setViewingTour(null)}
            style={{
              textDecoration: "none",
              display: "flex",
              alignItems: "center",
              gap: "5px",
            }}
          >
            <span>&larr;</span> Back to List
          </Button>
          <h3>{viewingTour.name}</h3>
          <p>
            <strong>Description:</strong>{" "}
            {viewingTour.description || "No description available"}
          </p>

          {/* Integrate TourEventsTab */}
          <TourEventsTab tourId={viewingTour.tourId} />
        </div>
      ) : (
        <div>
          <div className="d-flex justify-content-between align-items-center mb-3">
            <h3>Tours</h3>
            <Button
              variant="primary"
              onClick={() => {
                setModalType("create");
                setFormData({ name: "", description: "" });
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
            <Table bordered hover responsive className="rounded">
              <thead className="bg-light">
              <tr>
                <th>Name</th>
                <th>Actions</th>
              </tr>
              </thead>
              <tbody>
              {tours.map((tour) => (
                <tr key={tour.tourId}>
                  <td
                    onClick={() => handleViewTour(tour.tourId)}
                    style={{
                      cursor: "pointer",
                      color: "#007bff",
                      textDecoration: "underline",
                    }}
                  >
                    {tour.name}
                  </td>
                  <td>
                    <Button
                      variant="outline-primary"
                      onClick={() => {
                        setSelectedTour(tour);
                        setModalType("update");
                        setFormData({
                          name: tour.name,
                          description: tour.description,
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
                        setSelectedTour(tour);
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
        </div>
      )}

      <Modal show={showModal} onHide={() => setShowModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>
            {modalType === "create"
              ? "Create Tour"
              : modalType === "update"
                ? "Edit Tour"
                : "Delete Tour"}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {modalType === "delete" ? (
            <p>Are you sure you want to delete this tour?</p>
          ) : (
            <Form>
              <Form.Group className="mb-3">
                <Form.Label>Tour Name</Form.Label>
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
                <div className="invalid-feedback">Tour name is required.</div>
              </Form.Group>
              <Form.Group className="mb-3">
                <Form.Label>Tour Description</Form.Label>
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
                <div className="invalid-feedback">Description is required.</div>
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

export default ToursTab;
