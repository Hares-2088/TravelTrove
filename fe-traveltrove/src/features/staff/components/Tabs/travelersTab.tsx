import React, { useState, useEffect } from 'react';
import { Button, Table, Modal, Form } from 'react-bootstrap';
import { useTranslation } from 'react-i18next';
import { useTravelersApi } from '../../../travelers/api/traveler.api';
import { useCitiesApi } from '../../../cities/api/cities.api';
import { useCountriesApi } from '../../../countries/api/countries.api';
import {
  TravelerResponseModel,
  TravelerRequestModel,
} from '../../../travelers/model/traveler.model';
import '../../../../shared/css/Scrollbar.css';

const TravelersTab: React.FC = () => {
  const {
    getAllTravelers,
    getTravelerById,
    addTraveler,
    updateTraveler,
    deleteTraveler,
  } = useTravelersApi();
  const { getAllCities } = useCitiesApi();
  const { getAllCountries } = useCountriesApi();

  const { t } = useTranslation();
  const [travelers, setTravelers] = useState<TravelerResponseModel[]>([]);
  const [cities, setCities] = useState<{ id: string; name: string }[]>([]);
  const [countries, setCountries] = useState<{ id: string; name: string }[]>(
    []
  );
  const [showModal, setShowModal] = useState(false);
  const [modalType, setModalType] = useState<'create' | 'update' | 'delete'>(
    'create'
  );
  const [selectedTraveler, setSelectedTraveler] =
    useState<TravelerResponseModel | null>(null);
  const [formData, setFormData] = useState<TravelerRequestModel>({
    seq: 0,
    firstName: '',
    lastName: '',
    addressLine1: '',
    addressLine2: '',
    city: '',
    state: '',
    email: '',
    countryId: '',
  });
  const [viewingTraveler, setViewingTraveler] =
    useState<TravelerResponseModel | null>(null);

  useEffect(() => {
    fetchTravelers();
    fetchCities();
    fetchCountries();
  }, []);

  const fetchTravelers = async () => {
    try {
      const data = await getAllTravelers();
      setTravelers(data);
    } catch (error) {
      console.error('Error fetching travelers:', error);
    }
  };

  const fetchCities = async () => {
    try {
      const data = await getAllCities();
      setCities(data.map(city => ({ id: city.cityId, name: city.name })));
    } catch (error) {
      console.error('Error fetching cities:', error);
    }
  };

  const fetchCountries = async () => {
    try {
      const data = await getAllCountries();
      setCountries(
        data.map(country => ({ id: country.countryId, name: country.name }))
      );
    } catch (error) {
      console.error('Error fetching countries:', error);
    }
  };

  const handleViewTraveler = async (travelerId: string) => {
    try {
      const traveler = await getTravelerById(travelerId);
      setViewingTraveler(traveler);
    } catch (error) {
      console.error('Error fetching traveler details:', error);
    }
  };

  const handleSave = async () => {
    try {
      if (!formData.firstName.trim()) {
        alert(t('firstNameRequired'));
        return;
      }
      if (!formData.lastName.trim()) {
        alert(t('lastNameRequired'));
        return;
      }
      if (!formData.addressLine1.trim()) {
        alert(t('addressLine1Required'));
        return;
      }
      if (!formData.city.trim()) {
        alert(t('cityRequired'));
        return;
      }
      if (!formData.state.trim()) {
        alert(t('stateRequired'));
        return;
      }
      if (!formData.email.trim()) {
        alert(t('emailRequired'));
        return;
      }
      if (!formData.countryId.trim()) {
        alert(t('countryRequired'));
        return;
      }

      if (modalType === 'create') {
        await addTraveler(formData);
      } else if (modalType === 'update' && selectedTraveler) {
        await updateTraveler(selectedTraveler.travelerId, formData);
      }
      setShowModal(false);
      await fetchTravelers();
    } catch (error) {
      console.error('Error saving traveler:', error);
    }
  };

  const handleDelete = async () => {
    try {
      if (selectedTraveler) {
        await deleteTraveler(selectedTraveler.travelerId);
        setShowModal(false);
        await fetchTravelers();
      }
    } catch (error) {
      console.error('Error deleting traveler:', error);
    }
  };

  return (
    <div>
      {viewingTraveler ? (
        <div>
          <Button
            variant="link"
            className="text-primary mb-3"
            onClick={() => setViewingTraveler(null)}
            style={{
              textDecoration: 'none',
              display: 'flex',
              alignItems: 'center',
              gap: '5px',
            }}
          >
            <span>&larr;</span> {t('backToList')}
          </Button>
          <h3>
            {viewingTraveler.firstName} {viewingTraveler.lastName}
          </h3>
          <p>
            <strong>{t('address')}:</strong> {viewingTraveler.addressLine1},{' '}
            {viewingTraveler.addressLine2}, {viewingTraveler.city},{' '}
            {viewingTraveler.state}
          </p>
          <p>
            <strong>{t('email')}:</strong> {viewingTraveler.email}
          </p>
        </div>
      ) : (
        <>
          <div className="d-flex justify-content-between align-items-center mb-3">
            <h3>{t('travelers')}</h3>
            <Button
              variant="primary"
              onClick={() => {
                setModalType('create');
                setFormData({
                  seq: 0,
                  firstName: '',
                  lastName: '',
                  addressLine1: '',
                  addressLine2: '',
                  city: '',
                  state: '',
                  email: '',
                  countryId: '',
                });
                setShowModal(true);
              }}
            >
              {t('create')}
            </Button>
          </div>

          <div
            className="dashboard-scrollbar"
            style={{ maxHeight: '700px', overflowY: 'auto' }}
          >
            <Table bordered hover responsive className="rounded">
              <thead className="bg-light">
                <tr>
                  <th>{t('name')}</th>
                  <th>{t('actions')}</th>
                </tr>
              </thead>
              <tbody>
                {travelers.map(traveler => (
                  <tr key={traveler.travelerId}>
                    <td
                      onClick={() => handleViewTraveler(traveler.travelerId)}
                      style={{
                        cursor: 'pointer',
                        color: '#007bff',
                        textDecoration: 'underline',
                      }}
                    >
                      {traveler.firstName} {traveler.lastName}
                    </td>
                    <td>
                      <Button
                        variant="outline-primary"
                        onClick={() => {
                          setSelectedTraveler(traveler);
                          setModalType('update');
                          setFormData({
                            seq: traveler.seq,
                            firstName: traveler.firstName,
                            lastName: traveler.lastName,
                            addressLine1: traveler.addressLine1,
                            addressLine2: traveler.addressLine2,
                            city: traveler.city,
                            state: traveler.state,
                            email: traveler.email,
                            countryId: traveler.countryId,
                          });
                          setShowModal(true);
                        }}
                      >
                        {t('edit')}
                      </Button>
                      <Button
                        variant="outline-danger"
                        className="ms-2"
                        onClick={() => {
                          setSelectedTraveler(traveler);
                          setModalType('delete');
                          setShowModal(true);
                        }}
                      >
                        {t('delete')}
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
            {modalType === 'create'
              ? t('create')
              : modalType === 'update'
                ? t('edit')
                : t('delete')}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {modalType === 'delete' ? (
            <p>{t('areYouSureDeleteTraveler')}</p>
          ) : (
            <Form>
              <Form.Group className="mb-3">
                <Form.Label>{t('firstName')}</Form.Label>
                <Form.Control
                  type="text"
                  value={formData.firstName}
                  onChange={e =>
                    setFormData({ ...formData, firstName: e.target.value })
                  }
                  isInvalid={!formData.firstName.trim()}
                />
                <Form.Control.Feedback type="invalid">
                  {t('firstNameRequired')}
                </Form.Control.Feedback>
              </Form.Group>
              <Form.Group className="mb-3">
                <Form.Label>{t('lastName')}</Form.Label>
                <Form.Control
                  type="text"
                  value={formData.lastName}
                  onChange={e =>
                    setFormData({ ...formData, lastName: e.target.value })
                  }
                  isInvalid={!formData.lastName.trim()}
                />
                <Form.Control.Feedback type="invalid">
                  {t('lastNameRequired')}
                </Form.Control.Feedback>
              </Form.Group>
              <Form.Group className="mb-3">
                <Form.Label>{t('addressLine1')}</Form.Label>
                <Form.Control
                  type="text"
                  value={formData.addressLine1}
                  onChange={e =>
                    setFormData({ ...formData, addressLine1: e.target.value })
                  }
                  isInvalid={!formData.addressLine1.trim()}
                />
                <Form.Control.Feedback type="invalid">
                  {t('addressLine1Required')}
                </Form.Control.Feedback>
              </Form.Group>
              <Form.Group className="mb-3">
                <Form.Label>{t('addressLine2')}</Form.Label>
                <Form.Control
                  type="text"
                  value={formData.addressLine2}
                  onChange={e =>
                    setFormData({ ...formData, addressLine2: e.target.value })
                  }
                />
              </Form.Group>
              <Form.Group className="mb-3">
                <Form.Label>{t('city')}</Form.Label>
                <Form.Control
                  type="text"
                  value={formData.city}
                  onChange={e =>
                    setFormData({ ...formData, city: e.target.value })
                  }
                  isInvalid={!formData.city.trim()}
                />
                <Form.Control.Feedback type="invalid">
                  {t('cityRequired')}
                </Form.Control.Feedback>
              </Form.Group>
              <Form.Group className="mb-3">
                <Form.Label>{t('state')}</Form.Label>
                <Form.Control
                  type="text"
                  value={formData.state}
                  onChange={e =>
                    setFormData({ ...formData, state: e.target.value })
                  }
                  isInvalid={!formData.state.trim()}
                />
                <Form.Control.Feedback type="invalid">
                  {t('stateRequired')}
                </Form.Control.Feedback>
              </Form.Group>
              <Form.Group className="mb-3">
                <Form.Label>{t('email')}</Form.Label>
                <Form.Control
                  type="email"
                  value={formData.email}
                  onChange={e =>
                    setFormData({ ...formData, email: e.target.value })
                  }
                  isInvalid={!formData.email.trim()}
                />
                <Form.Control.Feedback type="invalid">
                  {t('emailRequired')}
                </Form.Control.Feedback>
              </Form.Group>
              <Form.Group className="mb-3">
                <Form.Label>{t('country')}</Form.Label>
                <Form.Control
                  as="select"
                  value={formData.countryId}
                  onChange={e =>
                    setFormData({ ...formData, countryId: e.target.value })
                  }
                  isInvalid={!formData.countryId.trim()}
                >
                  <option value="">{t('selectCountry')}</option>
                  {countries.map(country => (
                    <option key={country.id} value={country.id}>
                      {country.name}
                    </option>
                  ))}
                </Form.Control>
                <Form.Control.Feedback type="invalid">
                  {t('countryRequired')}
                </Form.Control.Feedback>
              </Form.Group>
            </Form>
          )}
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowModal(false)}>
            {t('cancel')}
          </Button>
          <Button
            variant="danger"
            onClick={modalType === 'delete' ? handleDelete : handleSave}
          >
            {modalType === 'delete' ? t('delete') : t('save')}
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default TravelersTab;
