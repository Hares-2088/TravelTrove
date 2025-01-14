import React, { useEffect, useState } from "react";
import { Button, Table, Modal, Form } from "react-bootstrap";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom"; // Import useNavigate for navigation
import { usePackagesApi } from "../../../packages/api/packages.api";
import { useAirportsApi } from "../../../airports/api/airports.api"; // Import the airports API hook
import {
    PackageRequestModel,
    PackageResponseModel,
} from "../../../packages/models/package.model";
import { AirportResponseModel } from "../../../airports/models/airports.model"; // Import the airport model
import { FaFilter } from "react-icons/fa"; // Import the filter icon

interface TourPackagesTabProps {
    tourId: string;
}

const TourPackagesTab: React.FC<TourPackagesTabProps> = ({ tourId }) => {
    const { t } = useTranslation();
    const navigate = useNavigate(); // Initialize useNavigate
    const { getAllPackages, addPackage, updatePackage, deletePackage, getPackageStatus } = usePackagesApi();
    const { getAllAirports } = useAirportsApi(); // Use the airports API hook
    const [packages, setPackages] = useState<PackageResponseModel[]>([]);
    const [airports, setAirports] = useState<AirportResponseModel[]>([]); // State for airports
    const [showModal, setShowModal] = useState(false);
    const [modalType, setModalType] = useState<"create" | "update" | "delete" | "view">(
        "create"
    );
    const [selectedPackage, setSelectedPackage] =
        useState<PackageResponseModel | null>(null);
    
    const [formData, setFormData] = useState<PackageRequestModel>({
        airportId: "",
        tourId: tourId,
        name: "",
        description: "",
        startDate: "",
        endDate: "",
        priceSingle: 0,
        priceDouble: 0,
        priceTriple: 0,
        totalSeats: 0,
    });
    const [formErrors, setFormErrors] = useState({
        name: false,
        description: false,
        startDate: false,
        endDate: false,
        priceSingle: false,
        airportId: false,
        dateOrder: false,
        totalSeats: false,
    });
    const [filteredPackages, setFilteredPackages] = useState<PackageResponseModel[]>([]);

    // States for filters
    const [filterName, setFilterName] = useState("");
    const [filterStatus, setFilterStatus] = useState("");
    const [filterDate, setFilterDate] = useState<Date | null>(null);
    const [sortField, setSortField] = useState<"price" | "date" | "popularity" | null>(null);
    const [sortOrder, setSortOrder] = useState<"asc" | "desc">("asc");
    const [showFilters, setShowFilters] = useState(false); // State to toggle filter visibility


    useEffect(() => {
        fetchPackages();
        fetchAirports(); // Fetch airports when the component mounts
    }, [tourId]);

    useEffect(() => {
        applyFilters();
    }, [filterName, filterStatus, filterDate, sortField, sortOrder, packages]);

    const fetchPackages = async () => {
        try {
            const data = await getAllPackages({ tourId });
            setPackages(data);
        } catch (error) {
            console.error("Error fetching packages:", error);
        }
    };

    const fetchAirports = async () => {
        try {
            const data = await getAllAirports();
            setAirports(data);
        } catch (error) {
            console.error("Error fetching airports:", error);
        }
    };

    const handleSave = async () => {
        const errors = {
            name: !formData.name,
            description: !formData.description,
            startDate: !formData.startDate,
            endDate: !formData.endDate,
            priceSingle: formData.priceSingle === null,
            airportId: !formData.airportId,
            dateOrder: new Date(formData.startDate) >= new Date(formData.endDate),
            totalSeats: formData.totalSeats === null,
        };
        setFormErrors(errors);

        if (Object.values(errors).some((error) => error)) {
            return;
        }

        try {
            if (modalType === "create") {
                await addPackage(formData);
            } else if (modalType === "update" && selectedPackage) {
                await updatePackage(selectedPackage.packageId, formData);
            }
            setShowModal(false);
            await fetchPackages();
        } catch (error) {
            console.error("Error saving package:", error);
        }
    };

    const handleDelete = async () => {
        try {
            if (selectedPackage) {
                await deletePackage(selectedPackage.packageId);
                setShowModal(false);
                await fetchPackages();
            }
        } catch (error) {
            console.error("Error deleting package:", error);
        }
    };

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        handleSave();
    };

    const applyFilters = () => {
        let filtered = packages;

        // Filter by name
        if (filterName) {
            filtered = filtered.filter((pkg) =>
                pkg.name.toLowerCase().includes(filterName.toLowerCase())
            );
        }

        // Filter by status
        if (filterStatus) {
            filtered = filtered.filter((pkg) => pkg.status === filterStatus);
        }

        // Filter by date
        if (filterDate) {
            filtered = filtered.filter((pkg) =>
                pkg.startDate.includes(filterDate?.toISOString().split('T')[0])
            );
        }
    
        // Sort packages
        if (sortField) {
          filtered = filtered.sort((a, b) => {
            const aValue = sortField === "price" ? a.priceSingle : new Date(a.startDate).getTime();
            const bValue = sortField === "price" ? b.priceSingle : new Date(b.startDate).getTime();
            return sortOrder === "asc" ? aValue - bValue : bValue - aValue;
          });
        }
    
        setFilteredPackages(filtered);
      };


      const handleResetFilters = () => {
        setFilterName("");
        setFilterStatus("");
        setFilterDate(null);
        setSortField(null);
        setSortOrder("asc");
    };
      

    return (
        <div>
            <div className="d-flex justify-content-between align-items-center mb-3">

            <Button
                    variant="primary"
                    onClick={() => {
                        setModalType("create");
                        setFormData({
                            airportId: "",
                            tourId: tourId,
                            name: "",
                            description: "",
                            startDate: "",
                            endDate: "",
                            priceSingle: 0,
                            priceDouble: 0,
                            priceTriple: 0,
                            totalSeats: 0,
                        });
                        setFormErrors({
                            name: false,
                            description: false,
                            startDate: false,
                            endDate: false,
                            priceSingle: false,
                            airportId: false,
                            dateOrder: false,
                            totalSeats: false,
                        });
                        setShowModal(true);
                    }}
                >
                    {t("Create Package")}
                </Button>
                <Button variant="outline-secondary" onClick={() => setShowFilters(!showFilters)}>
                    <FaFilter /> {t("Filters")}
                </Button>
            </div>

            {showFilters && (
                <div className="filter-bar">
                    <Form.Control
                        type="text"
                        placeholder={t("Filter by name")}
                        value={filterName}
                        onChange={(e) => setFilterName(e.target.value)}
                    />
                    <Form.Control
                        as="select"
                        value={filterStatus}
                        onChange={(e) => setFilterStatus(e.target.value)}
                    >
                        <option value="">{t("All Statuses")}</option>
                        <option value="Active">{t("Active")}</option>
                        <option value="Inactive">{t("Inactive")}</option>
                        <option value="EXPIRED">{t("Expired")}</option> {/* Added Expired option */}
                    </Form.Control>
                    <Form.Control
                        type="date"
                        value={filterDate ? filterDate.toISOString().split('T')[0] : ""}
                        onChange={(e) => setFilterDate(e.target.value ? new Date(e.target.value) : null)}
                    />
                    <Form.Control
                        as="select"
                        value={sortField || ""}
                        onChange={(e) => setSortField(e.target.value as any)}
                    >
                        <option value="">{t("Sort by")}</option>
                        <option value="price">{t("Price")}</option>
                        <option value="date">{t("Date")}</option>
                        <option value="popularity">{t("Popularity")}</option>
                    </Form.Control>
                    <Form.Control
                        as="select"
                        value={sortOrder}
                        onChange={(e) => setSortOrder(e.target.value as "asc" | "desc")}
                    >
                        <option value="asc">{t("Ascending")}</option>
                        <option value="desc">{t("Descending")}</option>
                    </Form.Control>
                    <Button variant="secondary" onClick={handleResetFilters}>
                        {t("Reset Filters")}
                    </Button>
                </div>
            )}
            
            
            <Table bordered hover responsive className="rounded">
                <thead className="bg-light">
                    <tr>
                        <th>{t("Name")}</th>
                        <th>{t("Package Status")}</th>
                        <th>{t("Start Date")}</th>
                        <th>{t("End Date")}</th>
                        <th>{t("Price")}</th>
                        <th>{t("Available Seats")}</th>
                        <th>{t("Actions")}</th>
                    </tr>
                </thead>
                <tbody>
                    {filteredPackages.map((pkg) => (
                        <tr key={pkg.packageId}>
                            <td>{pkg.name}</td>
                            <td>{pkg.status}</td>
                            <td>{new Date(pkg.startDate).toLocaleDateString()}</td>
                            <td>{new Date(pkg.endDate).toLocaleDateString()}</td>
                            <td>{pkg.priceSingle}</td>
                            <td>{pkg.availableSeats}</td>
                            <td>
                                <Button
                                    variant="outline-primary"
                                    onClick={() => {
                                        setSelectedPackage(pkg);
                                        setModalType("update");
                                        setFormData({
                                            airportId: pkg.airportId,
                                            tourId: pkg.tourId,
                                            name: pkg.name,
                                            description: pkg.description,
                                            startDate: pkg.startDate,
                                            endDate: pkg.endDate,
                                            priceSingle: pkg.priceSingle,
                                            priceDouble: pkg.priceDouble,
                                            priceTriple: pkg.priceTriple,
                                            totalSeats: pkg.totalSeats,
                                        });
                                        setFormErrors({
                                            name: false,
                                            description: false,
                                            startDate: false,
                                            endDate: false,
                                            priceSingle: false,
                                            airportId: false,
                                            dateOrder: false,
                                            totalSeats: false,
                                        });
                                        setShowModal(true);
                                    }}
                                >
                                    {t("Edit Package")}
                                </Button>
                                <Button
                                    variant="outline-danger"
                                    className="ms-2"
                                    onClick={() => {
                                        setSelectedPackage(pkg);
                                        setModalType("delete");
                                        setShowModal(true);
                                    }}
                                >
                                    {t("Delete Package")}
                                </Button>
                                <Button
                                    variant="outline-secondary"
                                    className="ms-2"
                                    onClick={() => navigate(`/bookings?packageId=${pkg.packageId}`)}
                                >
                                    {t("View Bookings")}
                                </Button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </Table>

            <Modal show={showModal} onHide={() => setShowModal(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>
                        {modalType === "create"
                            ? t("Create Package")
                            : modalType === "update"
                                ? t("Edit Package")
                                : modalType === "delete"
                                    ? t("Delete Package")
                                    : t("View Package")}
                    </Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    {modalType === "delete" ? (
                        <p>{t("areYouSureDelete")}</p>
                    ) : modalType === "view" ? (
                        <div>
                            <p><strong>{t("packageName")}:</strong> {selectedPackage?.name}</p>
                            <p><strong>{t("packageDescription")}:</strong> {selectedPackage?.description}</p>
                            <p><strong>{t("startDate")}:</strong> {selectedPackage?.startDate}</p>
                            <p><strong>{t("endDate")}:</strong> {selectedPackage?.endDate}</p>
                            <p><strong>{t("priceSingle")}:</strong> {selectedPackage?.priceSingle}</p>
                            <p><strong>{t("priceDouble")}:</strong> {selectedPackage?.priceDouble}</p>
                            <p><strong>{t("priceTriple")}:</strong> {selectedPackage?.priceTriple}</p>
                            <p><strong>{("availableSeats")}:</strong> {selectedPackage?.availableSeats}</p>
                            <p><strong>{("totalSeats")}:</strong> {selectedPackage?.totalSeats}</p>
                            <p><strong>{("packageStatus")}:</strong> {selectedPackage && getPackageStatus(selectedPackage)}</p>
                        </div>
                    ) : (
                        <Form onSubmit={handleSubmit}>
                            <Form.Group className="mb-3">
                                <Form.Label>{t("packageName")}</Form.Label>
                                <Form.Control
                                    required
                                    type="text"
                                    value={formData.name}
                                    onChange={(e) =>
                                        setFormData({ ...formData, name: e.target.value })
                                    }
                                    isInvalid={formErrors.name}
                                />
                                <Form.Control.Feedback type="invalid">
                                    {t("packageNameRequired")}
                                </Form.Control.Feedback>
                            </Form.Group>
                            <Form.Group className="mb-3">
                                <Form.Label>{t("packageDescription")}</Form.Label>
                                <Form.Control
                                    as="textarea"
                                    value={formData.description}
                                    onChange={(e) =>
                                        setFormData({ ...formData, description: e.target.value })
                                    }
                                    rows={3}
                                    isInvalid={formErrors.description}
                                />
                                <Form.Control.Feedback type="invalid">
                                    {t("packageDescriptionRequired")}
                                </Form.Control.Feedback>
                            </Form.Group>
                            <Form.Group className="mb-3">
                                <Form.Label>{t("startDate")}</Form.Label>
                                <Form.Control
                                    type="date"
                                    value={formData.startDate}
                                    onChange={(e) =>
                                        setFormData({ ...formData, startDate: e.target.value })
                                    }
                                    isInvalid={formErrors.startDate || formErrors.dateOrder}
                                />
                                <Form.Control.Feedback type="invalid">
                                    {formErrors.startDate ? t("startDateRequired") : t("startDateBeforeEndDate")}
                                </Form.Control.Feedback>
                            </Form.Group>
                            <Form.Group className="mb-3">
                                <Form.Label>{t("endDate")}</Form.Label>
                                <Form.Control
                                    type="date"
                                    value={formData.endDate}
                                    onChange={(e) =>
                                        setFormData({ ...formData, endDate: e.target.value })
                                    }
                                    isInvalid={formErrors.endDate || formErrors.dateOrder}
                                />
                                <Form.Control.Feedback type="invalid">
                                    {formErrors.endDate ? t("endDateRequired") : t("startDateBeforeEndDate")}
                                </Form.Control.Feedback>
                            </Form.Group>
                            <Form.Group className="mb-3">
                                <Form.Label>{t("priceSingle")}</Form.Label>
                                <Form.Control
                                    type="number"
                                    value={formData.priceSingle}
                                    onChange={(e) =>
                                        setFormData({ ...formData, priceSingle: +e.target.value })
                                    }
                                    isInvalid={formErrors.priceSingle}
                                />
                                <Form.Control.Feedback type="invalid">
                                    {t("priceSingleRequired")}
                                </Form.Control.Feedback>
                            </Form.Group>
                            <Form.Group className="mb-3">
                                <Form.Label>{t("priceDouble")}</Form.Label>
                                <Form.Control
                                    type="number"
                                    value={formData.priceDouble}
                                    onChange={(e) =>
                                        setFormData({ ...formData, priceDouble: +e.target.value })
                                    }
                                />
                            </Form.Group>
                            <Form.Group className="mb-3">
                                <Form.Label>{t("priceTriple")}</Form.Label>
                                <Form.Control
                                    type="number"
                                    value={formData.priceTriple}
                                    onChange={(e) =>
                                        setFormData({ ...formData, priceTriple: +e.target.value })
                                    }
                                />
                            </Form.Group>
                            <Form.Group className="mb-3">
                                <Form.Label>{t("airport")}</Form.Label>
                                <Form.Control
                                    as="select"
                                    value={formData.airportId}
                                    onChange={(e) =>
                                        setFormData({ ...formData, airportId: e.target.value })
                                    }
                                    isInvalid={formErrors.airportId}
                                >
                                    <option value="">{t("selectAirport")}</option>
                                    {airports.map((airport) => (
                                        <option key={airport.airportId} value={airport.airportId}>
                                            {airport.name}
                                        </option>
                                    ))}
                                </Form.Control>
                                <Form.Control.Feedback type="invalid">
                                    {t("airportRequired")}
                                </Form.Control.Feedback>
                            </Form.Group>
                            <Form.Group className="mb-3">
                                <Form.Label>{("totalSeats")}</Form.Label>
                                <Form.Control
                                    type="number"
                                    value={formData.totalSeats}
                                    onChange={(e) =>
                                        setFormData({ ...formData, totalSeats: +e.target.value })
                                    }
                                    isInvalid={formErrors.totalSeats}
                                />
                                <Form.Control.Feedback type="invalid">
                                    {("totalSeatsRequired")}
                                </Form.Control.Feedback>
                            </Form.Group>
                            <Modal.Footer>
                                <Button variant="secondary" onClick={() => setShowModal(false)}>
                                    {t("cancel")}
                                </Button>
                                <Button type="submit" variant="primary">
                                    {t("save")}
                                </Button>
                            </Modal.Footer>
                        </Form>
                    )}
                </Modal.Body>
                {modalType === "delete" && (
                    <Modal.Footer>
                        <Button variant="secondary" onClick={() => setShowModal(false)}>
                            {t("cancel")}
                        </Button>
                        <Button variant="danger" onClick={handleDelete}>
                            {t("confirm")}
                        </Button>
                    </Modal.Footer>
                )}
            </Modal>
        </div>
    );
};

export default TourPackagesTab;