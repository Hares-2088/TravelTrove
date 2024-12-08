import { useEffect, useState } from "react";
import { addTour, deleteTour, getAllTours, getTourByTourId, updateTour } from "../../tours/api/tours.api";
import { TourRequestModel, TourResponseModel } from "../../tours/models/Tour";
import { Button, Table, Modal, Form } from "react-bootstrap";


const ToursTab: React.FC = () => {
  const [tours, setTours] = useState<TourResponseModel[]>([]);
  const [showModal, setShowModal] = useState(false);
  const [modalType, setModalType] = useState<"create" | "update" | "delete">(
    "create"
  );
  const [selectedTour, setSelectedTour] = useState<TourResponseModel | null>(null);
  const [formData, setFormData] = useState<TourRequestModel>({ name: "", description: "" });
  const [viewingTour, setViewingTour] = useState<TourResponseModel | null>(null);



  useEffect(() => {
    fetchTours();
  }, []);

  const fetchTours = async () => {
    try {
      const data = await getAllTours();
      setTours(data)
    } catch (error) {
      console.error("Error fetching tours:", error);
    }
  };

  const handleViewTour = async (tourId: string) => {
    try {
      const tour = await getTourByTourId(tourId);
      setViewingTour(tour);
    } catch (error) {
      console.error("Error fetching country details:", error);
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
      console.error("Error deleting country:", error);
    }
  };

  return (
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
              style={{ cursor: "pointer", color: "#007bff", textDecoration: "underline" }}
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
  

  );
};

export default ToursTab;
