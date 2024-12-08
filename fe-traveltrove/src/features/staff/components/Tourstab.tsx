import { useEffect, useState } from "react";
import {
  addTour,
  deleteTour,
  getAllTours,
  getTourByTourId,
  updateTour,
} from "../../tours/api/tours.api";
import { TourRequestModel, TourResponseModel } from "../../tours/models/Tour";
import { Button, Table, Modal, Form } from "react-bootstrap";

const ToursTab: React.FC = () => {
  const [tours, setTours] = useState<TourResponseModel[]>([]);
  const [showModal, setShowModal] = useState(false);
  const [modalType, setModalType] = useState<"create" | "update" | "delete">(
    "create"
  );
  const [selectedTour, setSelectedTour] = useState<TourResponseModel | null>(
    null
  );
  const [formData, setFormData] = useState<TourRequestModel>({
    name: "",
    description: "",
  });
  const [viewingTour, setViewingTour] = useState<TourResponseModel | null>(
    null
  );
  const [tourEvents, setTourEvents] = useState<any[]>([]);  // Assuming you have an EventResponseModel for events

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

      // Fetch the events for this specific tour
      const eventsData = await fetchTourEvents(tourId);
      setTourEvents(eventsData);
    } catch (error) {
      console.error("Error fetching Tour details:", error);
    }
  };

  const fetchTourEvents = async (tourId: string) => {
    try {
      // Fetch the events for the selected tour. Replace with actual API call.
      const response = await fetch(`/api/tours/${tourId}/events`);
      const data = await response.json();
      return data;  // assuming the data is an array of events
    } catch (error) {
      console.error("Error fetching events:", error);
      return [];
    }
  };

  const handleSave = async () => {
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
      {/* Viewing a Single Tour */}
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
            <strong>Tour ID:</strong> {viewingTour.tourId}
          </p>
          <p>
            <strong>Description:</strong>{" "}
            {viewingTour.description || "No description available"}
          </p>
          
          {/* Display Tour Events */}
          <div>
            <h4>Tour Events</h4>
            {tourEvents.length === 0 ? (
              <p>No events available for this tour.</p>
            ) : (
              <Table bordered hover responsive>
                <thead>
                  <tr>
                    <th>Event Name</th>
                    <th>Event Date</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {tourEvents.map((event, index) => (
                    <tr key={index}>
                      <td>{event.name}</td>
                      <td>{new Date(event.date).toLocaleDateString()}</td>
                      <td>
                        {/* Add buttons for editing or deleting events */}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </Table>
            )}
          </div>
        </div>
      ) : (
        // Display List of Tours
        <div>
          <div className="d-flex justify-content-between align-items-center mb-3">
            <h3>Tours</h3>
            <Button
              variant="primary"
              onClick={() => {
                fetchTours();
                setModalType("create");
                setFormData({ name: "", description: "" });
                setShowModal(true);
              }}
            >
              Create
            </Button>
          </div>

          <div
            className="tours-scrollbar"
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

      {/* Modal for Create, Update, and Delete */}
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
                  type="text"
                  value={formData.name}
                  onChange={(e) =>
                    setFormData({ ...formData, name: e.target.value })
                  }
                />
              </Form.Group>
              <Form.Group className="mb-3">
                <Form.Label>Tour Description</Form.Label>
                <Form.Control
                  as="textarea"
                  value={formData.description}
                  onChange={(e) =>
                    setFormData({ ...formData, description: e.target.value })
                  }
                  rows={3}
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

export default ToursTab;
