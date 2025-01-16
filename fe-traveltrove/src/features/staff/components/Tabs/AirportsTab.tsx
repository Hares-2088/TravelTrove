import React, { useState } from 'react';
import { Button, Table, Modal, Form } from 'react-bootstrap';
import {
  AirportRequestModel,
  AirportResponseModel,
} from '../../../airports/models/airports.model';
import { CityResponseModel } from '../../../cities/models/city.model';
import { useAirportsApi } from '../../../airports/api/airports.api';
import { useCitiesApi } from '../../../cities/api/cities.api';

const AirportsTab: React.FC = () => {
  const {
    getAllAirports,
    getAirportById,
    addAirport,
    updateAirport,
    deleteAirport,
  } = useAirportsApi();
  const { getAllCities } = useCitiesApi();

  const [airports, setAirports] = useState<AirportResponseModel[]>([]);
  const [cities, setCities] = useState<CityResponseModel[]>([]);
  const [showModal, setShowModal] = useState(false);
  const [modalType, setModalType] = useState<'create' | 'update' | 'delete'>(
    'create'
  );
  const [selectedAirport, setSelectedAirport] =
    useState<AirportResponseModel | null>(null);
  const [formData, setFormData] = useState<AirportRequestModel>({
    name: '',
    cityId: '',
  });
  const [viewingAirport, setViewingAirport] =
    useState<AirportResponseModel | null>(null);

  const [nameError, setNameError] = useState(false);
  const [cityError, setCityError] = useState(false);

  const fetchCities = async (): Promise<void> => {
    try {
      const data = await getAllCities();
      setCities(data);
    } catch (error) {
      console.error('Error fetching cities:', error);
    }
  };

  const fetchAirports = async (): Promise<void> => {
    try {
      const data = await getAllAirports();
      setAirports(data);
    } catch (error) {
      console.error('Error fetching airports', error);
    }
  };

  const handleViewAirport = async (airportId: string): Promise<void> => {
    try {
      const airport = await getAirportById(airportId);
      setViewingAirport(airport);
    } catch (error) {
      console.error('Error fetching airport details: ', error);
    }
  };

  const getCityName = (cityId: string): string => {
    const city = cities.find(city => city.cityId === cityId);
    return city ? city.name : 'Unknown City';
  };

  const handleSave = async (): Promise<void> => {
    const isNameValid = formData.name.trim() !== '';
    const isCityValid = !!formData.cityId.trim();

    setNameError(!isNameValid);
    setCityError(!isCityValid);

    if (!isNameValid || !isCityValid) return;

    try {
      if (modalType === 'create') {
        await addAirport(formData);
      } else if (modalType === 'update' && selectedAirport) {
        await updateAirport(formData, selectedAirport.airportId);
      }
      setShowModal(false);
      await fetchAirports();
    } catch (error) {
      console.error('Error saving airport:', error);
    }
  };

  const handleDelete = async (): Promise<void> => {
    try {
      if (selectedAirport) {
        await deleteAirport(selectedAirport.airportId);
        setShowModal(false);
        await fetchAirports();
      }
    } catch (error) {
      console.error('Error deleting airport:', error);
    }
  };

  return (
    <div>
      {viewingAirport ? (
        <div>
          <Button
            variant="link"
            className="text-primary mb-3"
            onClick={() => setViewingAirport(null)}
            style={{
              textDecoration: 'none',
              display: 'flex',
              alignItems: 'center',
              gap: '5px',
            }}
          >
            <span>&larr;</span> Back to List
          </Button>
          <h3>{viewingAirport.name}</h3>
          <p>
            <strong>City: </strong>
            {getCityName(viewingAirport.cityId)}
          </p>
        </div>
      ) : (
        <>
          <div className="d-flex justify-content-between align-items-center mb-3">
            <h3>Airports</h3>
            <Button
              variant="primary"
              onClick={() => {
                fetchCities();
                setModalType('create');
                setFormData({ name: '', cityId: '' });
                setShowModal(true);
              }}
            >
              Create
            </Button>
          </div>
          <Table bordered hover responsive className="rounded">
            <thead className="bg-light">
              <tr>
                <th>Name</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {airports.map(airport => (
                <tr key={airport.airportId}>
                  <td
                    onClick={() => handleViewAirport(airport.airportId)}
                    style={{
                      cursor: 'pointer',
                      color: '#007bff',
                      textDecoration: 'underline',
                    }}
                  >
                    {airport.name}
                  </td>
                  <td>
                    <Button
                      variant="outline-primary"
                      onClick={() => {
                        fetchCities();
                        setSelectedAirport(airport);
                        setModalType('update');
                        setFormData({
                          name: airport.name,
                          cityId: airport.cityId,
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
                        setSelectedAirport(airport);
                        setModalType('delete');
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
        </>
      )}
      <Modal show={showModal} onHide={() => setShowModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>
            {modalType === 'create'
              ? 'Create Airport'
              : modalType === 'update'
                ? 'Edit Airport'
                : 'Delete Airport'}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {modalType === 'delete' ? (
            <p>Are you sure you want to delete this airport?</p>
          ) : (
            <Form>
              <Form.Group className="mb-3">
                <Form.Label>Airport Name</Form.Label>
                <Form.Control
                  required
                  type="text"
                  value={formData.name}
                  onChange={e => {
                    setFormData({ ...formData, name: e.target.value });
                    setNameError(false);
                  }}
                  isInvalid={nameError}
                />
                <div className="invalid-feedback">
                  Airport name is required.
                </div>
              </Form.Group>
              <Form.Group className="mb-3">
                <Form.Label>City</Form.Label>
                <Form.Select
                  value={formData.cityId}
                  onChange={e => {
                    setFormData({ ...formData, cityId: e.target.value });
                    setCityError(false);
                  }}
                  isInvalid={cityError}
                >
                  <option value="">Select a city</option>
                  {cities.map(city => (
                    <option key={city.cityId} value={city.cityId}>
                      {city.name}
                    </option>
                  ))}
                </Form.Select>
              </Form.Group>
            </Form>
          )}
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowModal(false)}>
            Cancel
          </Button>
          <Button
            variant={modalType === 'delete' ? 'danger' : 'primary'}
            onClick={modalType === 'delete' ? handleDelete : handleSave}
          >
            {modalType === 'delete' ? 'Confirm' : 'Save'}
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default AirportsTab;
