import React, { useState, useEffect } from 'react';
import { Button, Table, Modal, Form } from 'react-bootstrap';
import { useCitiesApi } from '../../../cities/api/cities.api';
import { useCountriesApi } from '../../../countries/api/countries.api';
import { useTranslation } from 'react-i18next';
import {
  CityResponseModel,
  CityRequestModel,
} from '../../../cities/models/city.model';
import { CountryResponseModel } from '../../../countries/models/country.model';
import '../../../../shared/css/Scrollbar.css';

const CitiesTab: React.FC = () => {
  const { getAllCities, getCityById, addCity, updateCity, deleteCity } =
    useCitiesApi();
  const { getAllCountries } = useCountriesApi();

  const { t } = useTranslation();
  const [cities, setCities] = useState<CityResponseModel[]>([]);
  const [countries, setCountries] = useState<CountryResponseModel[]>([]);
  const [showModal, setShowModal] = useState(false);
  const [modalType, setModalType] = useState<'create' | 'update' | 'delete'>(
    'create'
  );
  const [selectedCity, setSelectedCity] = useState<CityResponseModel | null>(
    null
  );
  const [formData, setFormData] = useState<CityRequestModel>({
    name: '',
    countryId: '',
  });
  const [viewingCity, setViewingCity] = useState<CityResponseModel | null>(
    null
  );

  const [nameError, setNameError] = useState(false);
  const [countryError, setCountryError] = useState(false);

  const fetchCities: () => Promise<void> = async (): Promise<void> => {
    try {
      const data: CityResponseModel[] = await getAllCities();
      setCities(data);
    } catch (error) {
      console.error('Error fetching cities:', error);
    }
  };

  const fetchCountries = async (): Promise<void> => {
    try {
      const data = await getAllCountries();
      setCountries(data);
    } catch (error) {
      console.error('Error fetching countries:', error);
    }
  };

  useEffect(() => {
    fetchCities();
    fetchCountries();
  }, [fetchCities, fetchCountries]);

  const handleViewCity = async (cityId: string): Promise<void> => {
    try {
      const city = await getCityById(cityId);
      setViewingCity(city);
    } catch (error) {
      console.error('Error fetching city details:', error);
    }
  };

  const getCountryName = (countryId: string): string => {
    const country = countries.find(country => country.countryId === countryId);
    return country ? country.name : t('unknownCountry');
  };

  const handleSave = async (): Promise<void> => {
    const isNameValid = formData.name.trim() !== '';
    const isCountryValid = !!formData.countryId.trim();

    setNameError(!isNameValid);
    setCountryError(!isCountryValid);

    try {
      if (modalType === 'create') {
        await addCity(formData);
      } else if (modalType === 'update' && selectedCity) {
        await updateCity(selectedCity.cityId, formData);
      }
      setShowModal(false);
      await fetchCities();
    } catch (error) {
      console.error('Error saving city:', error);
    }
  };

  const handleDelete = async (): Promise<void> => {
    try {
      if (selectedCity) {
        await deleteCity(selectedCity.cityId);
        setShowModal(false);
        await fetchCities();
      }
    } catch (error) {
      console.error('Error deleting city:', error);
    }
  };

  return (
    <div>
      {viewingCity ? (
        <div>
          <Button
            variant="link"
            className="text-primary mb-3"
            onClick={() => setViewingCity(null)}
            style={{
              textDecoration: 'none',
              display: 'flex',
              alignItems: 'center',
              gap: '5px',
            }}
          >
            <span>&larr;</span> {t('backToList')}
          </Button>
          <h3>{viewingCity.name}</h3>
          <p>
            <strong>{t('country')}:</strong>{' '}
            {getCountryName(viewingCity.countryId)}
          </p>
        </div>
      ) : (
        <>
          <div className="d-flex justify-content-between align-items-center mb-3">
            <h3>{t('cities')}</h3>
            <Button
              variant="primary"
              onClick={() => {
                fetchCountries();
                setModalType('create');
                setFormData({ name: '', countryId: '' });
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
            <Table
              bordered
              hover
              responsive
              className="rounded"
              style={{ borderRadius: '12px', overflow: 'hidden' }}
            >
              <thead className="bg-light">
                <tr>
                  <th>{t('name')}</th>
                  <th>{t('actions')}</th>
                </tr>
              </thead>
              <tbody>
                {cities.map(city => (
                  <tr key={city.cityId}>
                    <td
                      onClick={() => handleViewCity(city.cityId)}
                      style={{
                        cursor: 'pointer',
                        color: '#007bff',
                        textDecoration: 'underline',
                      }}
                    >
                      {city.name}
                    </td>
                    <td>
                      <Button
                        variant="outline-primary"
                        onClick={() => {
                          fetchCountries();
                          setSelectedCity(city);
                          setModalType('update');
                          setFormData({
                            name: city.name,
                            countryId: city.countryId,
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
                          setSelectedCity(city);
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
              ? t('createCity')
              : modalType === 'update'
                ? t('editCity')
                : t('deleteCity')}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {modalType === 'delete' ? (
            <p>{t('deleteConfirmation')}</p>
          ) : (
            <Form>
              <Form.Group className="mb-3">
                <Form.Label>{t('cityName')}</Form.Label>
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
                <div className="invalid-feedback">{t('cityNameRequired')}</div>
              </Form.Group>
              <Form.Group className="mb-3">
                <Form.Label>{t('country')}</Form.Label>
                <Form.Select
                  value={formData.countryId}
                  onChange={e => {
                    setFormData({ ...formData, countryId: e.target.value });
                    setCountryError(false);
                  }}
                  isInvalid={countryError}
                >
                  <option value="">{t('selectCountry')}</option>
                  {countries.map(country => (
                    <option key={country.countryId} value={country.countryId}>
                      {country.name}
                    </option>
                  ))}
                </Form.Select>
                <div className="invalid-feedback">{t('countryRequired')}</div>
              </Form.Group>
            </Form>
          )}
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowModal(false)}>
            {t('cancel')}
          </Button>
          <Button
            variant={modalType === 'delete' ? 'danger' : 'primary'}
            onClick={modalType === 'delete' ? handleDelete : handleSave}
          >
            {modalType === 'delete' ? t('confirm') : t('save')}
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default CitiesTab;
