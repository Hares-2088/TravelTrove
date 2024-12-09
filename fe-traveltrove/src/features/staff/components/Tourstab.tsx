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
import { getAllEvents } from "../../events/api/events.ts";

const ToursTab: React.FC = () => {
  const [tours, setTours] = useState<TourResponseModel[]>([]);
  const [showModal, setShowModal] = useState(false);
  const [modalType, setModalType] = useState<"create" | "update" | "delete">("create");
  const [selectedTour, setSelectedTour] = useState<TourResponseModel | null>(null);
  const [formData, setFormData] = useState<TourRequestModel>({ name: "", description: "" });
  const [viewingTour, setViewingTour] = useState<TourResponseModel | null>(null);
  const [tourEvents, setTourEvents] = useState<any[]>([]);
  const [availableEvents, setAvailableEvents] = useState<any[]>([]);
  const [seq, setSeq] = useState<number>(5);
  const [seqDesc, setSeqDesc] = useState<string>("Updated arrival description");
  const [isEditing, setIsEditing] = useState(false);
const [currentEvent, setCurrentEvent] = useState<TourEvent | null>(null);
  const [showAddEventModal, setShowAddEventModal] = useState(false); // Separate modal state for adding events

  useEffect(() => {
    fetchTours();
    fetchAvailableEvents();
  }, []);

  const fetchTours = async () => {
    try {
      const data = await getAllTours();
      setTours(data);
    } catch (error) {
      console.error("Error fetching tours:", error);
    }
  };

  const handleEditTourEvent = (event: TourEvent) => {
    setCurrentEvent(event);
    setIsEditing(true); // Open the edit modal or make fields editable
  };

  const saveEditedTourEvent = async (updatedEvent: TourEvent) => {
    if (viewingTour && updatedEvent) {
      try {
        // API call to update the event
        await fetch(`/api/tours/${viewingTour.tourId}/events/${updatedEvent.eventId}`, {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(updatedEvent),
        });
  
        // Update the state with the modified event
        setTourEvents((prevEvents) =>
          prevEvents.map((event) =>
            event.eventId === updatedEvent.eventId ? updatedEvent : event
          )
        );
  
        setIsEditing(false); // Close the modal
        setCurrentEvent(null);
      } catch (error) {
        console.error("Error updating tour event:", error);
      }
    }
  };

  const handleDeleteTourEvent = async (eventId: string) => {
    if (viewingTour) {
      try {
        // API call to delete the event from the tour
        await fetch(`/api/tours/${viewingTour.tourId}/events/${eventId}`, {
          method: "DELETE",
        });
  
        // Update the state to remove the deleted event
        setTourEvents((prevEvents) => prevEvents.filter((event) => event.eventId !== eventId));
  
        // Optionally, add the deleted event back to the available events list
        const deletedEvent = tourEvents.find((event) => event.eventId === eventId);
        if (deletedEvent) {
          setAvailableEvents((prevEvents) => [...prevEvents, deletedEvent]);
        }
      } catch (error) {
        console.error("Error deleting tour event:", error);
      }
    }
  };

  
  const fetchAvailableEvents = async () => {
    try {
      const eventsData = await getAllEvents();
      setAvailableEvents(eventsData);
    } catch (error) {
      console.error("Error fetching available events:", error);
    }
  };

  const handleViewTour = async (tourId: string) => {
    try {
      const tour = await getTourByTourId(tourId);
      setViewingTour(tour);
      const eventsData = await fetch(`/api/tours/${tourId}/events`).then((res) => res.json());
      setTourEvents(eventsData);
    } catch (error) {
      console.error("Error fetching tour details:", error);
    }
  };

  const handleAddTourEvent = async () => {
    if (viewingTour && seq && seqDesc) {
      try {
        const selectedEventId = (document.getElementById("event-dropdown") as HTMLSelectElement).value;
        if (!selectedEventId) return;

        const selectedEvent = availableEvents.find((event) => event.eventId === selectedEventId);
        if (selectedEvent) {
          const payload = {
            seq,
            seqDesc,
            events: selectedEventId,
          };

          await fetch(`/api/v1/tours/${viewingTour.tourId}/events`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload),
          });

          setTourEvents((prevEvents) => [
            ...prevEvents,
            { ...selectedEvent, date: new Date().toISOString() },
          ]);
          setAvailableEvents((prevEvents) => prevEvents.filter((event) => event.eventId !== selectedEventId));
          setShowAddEventModal(false);
        }
      } catch (error) {
        console.error("Error adding tour event:", error);
      }
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
            style={{ textDecoration: "none", display: "flex", alignItems: "center", gap: "5px" }}
          >
            <span>&larr;</span> Back to List
          </Button>
          <h3>{viewingTour.name}</h3>
          <p>
            <strong>Description:</strong> {viewingTour.description || "No description available"}
          </p>
          
          {/* Display Tour Events */}
          <div>
            <h4>Tour Events</h4>
            {tourEvents.length === 0 ? (
              <div>
                <p>No events added yet</p>
                <Button variant="primary" onClick={() => setShowAddEventModal(true)}>
                  Add Tour Event
                </Button>
              </div>
            ) : (
              <div>
                <Button variant="primary" onClick={() => setShowAddEventModal(true)}>
                  Add Tour Event
                </Button>
                <Table bordered hover responsive>
                  <thead>
                    <tr>
                      <th>Seq</th>
                      <th>Event Name</th>
                      <th>Event Date</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {tourEvents.map((event, index) => (
                      <tr key={event.eventId}>
                      <td>{seq}</td>
                      <td>{event.name}</td>
                      <td>{new Date(event.date).toLocaleDateString()}</td>
                      <td>
                      <Button
                          variant="warning"
                          size="sm"
                          className="me-2"
                          onClick={() => handleEditTourEvent(event)}
                        >
                          Edit
                        </Button>
                        <Button
                          variant="danger"
                          size="sm"
                          onClick={() => handleDeleteTourEvent(event.eventId)}
                        >
                          Delete
                        </Button>
                      </td>
                      </tr>
                    ))}
                  </tbody>
                </Table>
              </div>
            )}
          </div>
        </div>
      ) : (
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
          <div className="tours-scrollbar" style={{ maxHeight: "700px", overflowY: "auto" }}>
            <Table bordered hover responsive className="rounded" style={{ borderRadius: "12px", overflow: "hidden" }}>
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
                          setFormData({ name: tour.name, description: tour.description });
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

      {/*Modal for edit tour events  */}
      <Modal show={isEditing} onHide={() => setIsEditing(false)}>
  <Modal.Header closeButton>
    <Modal.Title>Edit Event</Modal.Title>
  </Modal.Header>
  <Modal.Body>
    {currentEvent && (
      <Form>
        {/*<Form.Group>
          <Form.Label>Seq</Form.Label>
          <Form.Control
            type="text"
            value={currentEvent.} // Bind to currentEvent.seq
      onChange={(e) =>
        setCurrentEvent({
          ...currentEvent,
          seq: parseInt(e.target.value), // Update the seq field in currentEvent
        })
      }
    />*/}
  
        
        <Form.Group>
          <Form.Label>Event Name</Form.Label>
          <Form.Control
            type="text"
            value={currentEvent.name}
            onChange={(e) =>
              setCurrentEvent({ ...currentEvent, name: e.target.value })
            }
          />
        </Form.Group>
        <Form.Group>
          <Form.Label>Event Date</Form.Label>
          <Form.Control
            type="date"
            value={new Date(currentEvent.date).toISOString().split("T")[0]}
            onChange={(e) =>
              setCurrentEvent({ ...currentEvent, date: new Date(e.target.value).toISOString() })
            }
          />
        </Form.Group>
      </Form>
    )}
  </Modal.Body>
  <Modal.Footer>
    <Button variant="secondary" onClick={() => setIsEditing(false)}>
      Cancel
    </Button>
    <Button
      variant="primary"
      onClick={() => currentEvent && saveEditedTourEvent(currentEvent)}
    >
      Save Changes
    </Button>
  </Modal.Footer>
</Modal>

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
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                />
              </Form.Group>
              <Form.Group className="mb-3">
                <Form.Label>Tour Description</Form.Label>
                <Form.Control
                  as="textarea"
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
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
          <Button variant={modalType === "delete" ? "danger" : "primary"} onClick={modalType === "delete" ? handleDelete : handleSave}>
            {modalType === "delete" ? "Confirm" : "Save"}
          </Button>
        </Modal.Footer>
      </Modal>

      {/* Modal for adding a tour event */}
      <Modal show={showAddEventModal} onHide={() => setShowAddEventModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Add Tour Event</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form.Group>
            <Form.Label>Select Event to Add</Form.Label>
            <Form.Control
              id="event-dropdown"
              as="select"
              onChange={() => {}}
            >
              <option value="">-- Select Event --</option>
              {availableEvents.map((event) => (
                <option key={event.eventId} value={event.eventId}>
                  {event.name}
                </option>
              ))}
            </Form.Control>
          </Form.Group>
          <Form.Group className="mt-3">
            <Form.Label>Sequence Number</Form.Label>
            <Form.Control
              type="number"
              value={seq}
              onChange={(e) => setSeq(Number(e.target.value))}
            />
          </Form.Group>
          <Form.Group className="mt-3">
            <Form.Label>Sequence Description</Form.Label>
            <Form.Control
              type="text"
              value={seqDesc}
              onChange={(e) => setSeqDesc(e.target.value)}
            />
          </Form.Group>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowAddEventModal(false)}>
            Close
          </Button>
          <Button variant="primary" onClick={handleAddTourEvent}>
            Add Event
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default ToursTab;
