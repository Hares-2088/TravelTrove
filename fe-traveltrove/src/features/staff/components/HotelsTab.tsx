import React, { useState, useEffect } from "react";
import { Button, Table, Modal, Form } from "react-bootstrap";
import { useCitiesApi } from "../../cities/api/cities.api";
import { useHotelsApi } from "../../hotels/api/hotels.api";
import {
  HotelResponseModel,
  HotelRequestModel,
} from "../../hotels/models/hotel.model";
import { CityResponseModel } from "../../cities/models/city.model";
import "../../../shared/css/Scrollbar.css";

const HotelsTab: React.FC = () => {
  const { getAllHotels, getHotelById, addHotel, updateHotel, deleteHotel } = useHotelsApi();
  const { getAllCities } = useCitiesApi();
  const [hotels, setHotels] = useState<HotelResponseModel[]>([]);
  const [cities, setCities] = useState<CityResponseModel[]>([]);
  const [showModal, setShowModal] = useState(false);
  const [modalType, setModalType] = useState<"create" | "update" | "delete">(
    "create"
  );
  const [selectedHotel, setSelectedHotel] = useState<HotelResponseModel | null>(
    null
  );
  const [formData, setFormData] = useState<HotelRequestModel>({
    name: "",
    url: "",
    cityId: "",
  });
  const [viewingHotel, setViewingHotel] = useState<HotelResponseModel | null>(
    null
  );

  const [nameError, setNameError] = useState(false);
  const [urlError, setUrlError] = useState(false);
  const [cityError, setCityError] = useState(false);

  useEffect(() => {
    fetchHotels();
    fetchCities();
  }, []);

  const fetchCities = async () => {
    try {
      const data = await getAllCities();
      setCities(data);
    } catch (error) {
      console.error("Error fetching cities:", error);
    }
  };

  const fetchHotels = async () => {
    try {
      const data = await getAllHotels();
      setHotels(data);
    } catch (error) {
      console.error("Error fetching hotels:", error);
    }
  };

  const handleViewHotel = async (hotelId: string) => {
    try {
      const hotel = await getHotelById(hotelId);
      setViewingHotel(hotel);
    } catch (error) {
      console.error("Error fetching hotel details:", error);
    }
  };

  const getCityName = (cityId: string) => {
    const city = cities.find((city) => city.cityId === cityId);
    return city ? city.name : "Unknown City";
  };

  const handleSave = async () => {
    const isNameValid = formData.name.trim() !== "";
    const isUrlValid = formData.url.trim() !== "";
    const isCityValid = !!formData.cityId.trim();

    setNameError(!isNameValid);
    setUrlError(!isUrlValid)
    setCityError(!isCityValid);

    try {
      if (modalType === "create") {
        await addHotel(formData);
      } else if (modalType === "update" && selectedHotel) {
        await updateHotel(selectedHotel.hotelId, formData);
      }
      setShowModal(false);
      await fetchHotels();
    } catch (error) {
      console.error("Error saving hotel:", error);
    }
  };

  const handleDelete = async () => {
    try {
      if (selectedHotel) {
        await deleteHotel(selectedHotel.hotelId);
        setShowModal(false);
        await fetchHotels();
      }
    } catch (error) {
      console.error("Error deleting hotel:", error);
    }
  };

  return (
    <div>
      {viewingHotel ? (
        <div>
          <Button
            variant="link"
            className="text-primary mb-3"
            onClick={() => setViewingHotel(null)}
            style={{
              textDecoration: "none",
              display: "flex",
              alignItems: "center",
              gap: "5px",
            }}
          >
            <span>&larr;</span> Back to List
          </Button>
          <h3>{viewingHotel.name}</h3>
          <p>
            <strong>Hotel url:</strong> {viewingHotel.url}
          </p>
          <p>
            <strong>City:</strong> {getCityName(viewingHotel.cityId)}
          </p>
        </div>
      ) : (
        <>
          <div className="d-flex justify-content-between align-items-center mb-3">
            <h3>Hotels</h3>
            <Button
              variant="primary"
              onClick={() => {
                fetchCities();
                setModalType("create");
                setFormData({ name: "", url: "", cityId: "" });
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
                {hotels.map((hotel) => (
                  <tr key={hotel.hotelId}>
                    <td
                      onClick={() => handleViewHotel(hotel.hotelId)}
                      style={{
                        cursor: "pointer",
                        color: "#007bff",
                        textDecoration: "underline",
                      }}
                    >
                      {hotel.name}
                    </td>
                    <td>
                      <Button
                        variant="outline-primary"
                        onClick={() => {
                          fetchCities();
                          setSelectedHotel(hotel);
                          setModalType("update");
                          setFormData({
                            name: hotel.name,
                            url: hotel.url,
                            cityId: hotel.cityId,
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
                          setSelectedHotel(hotel);
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
              ? "Create Hotel"
              : modalType === "update"
              ? "Edit Hotel"
              : "Delete Hotel"}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {modalType === "delete" ? (
            <p>Are you sure you want to delete this hotel?</p>
          ) : (
            <Form>
              <Form.Group className="mb-3">
                <Form.Label>Hotel Name</Form.Label>
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
                <div className="invalid-feedback">Hotel name is required.</div>
              </Form.Group>
              <Form.Group className="mb-3">
                <Form.Label>Hotel url</Form.Label>
                <Form.Control
                  required
                  type="text"
                  value={formData.url}
                  onChange={(e) => {
                    setFormData({ ...formData, url: e.target.value });
                    setUrlError(false);
                  }}
                  isInvalid={urlError}
                />
                <div className="invalid-feedback">Hotel url is required.</div>
              </Form.Group>
              <Form.Group className="mb-3">
                <Form.Label>City</Form.Label>
                <Form.Select
                  value={formData.cityId}
                  onChange={(e) => {
                    setFormData({ ...formData, cityId: e.target.value });
                    setCityError(false);
                  }}
                  isInvalid={cityError}
                >
                  <option value="">Select a city</option>
                  {cities.map((city) => (
                    <option key={city.cityId} value={city.cityId}>
                      {city.name}
                    </option>
                  ))}
                </Form.Select>
                <div className="invalid-feedback">
                  A city selection is required.
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

export default HotelsTab;
