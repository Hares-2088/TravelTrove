import React, { useEffect, useState, useRef } from "react";
import { Button, Table, Modal, Form, Toast, ToastContainer } from "react-bootstrap"; // Import Toast components
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom"; // Import useNavigate for navigation
import { usePackagesApi } from "../../../packages/api/packages.api";
import { useAirportsApi } from "../../../airports/api/airports.api"; // Import the airports API hook
import {
    PackageRequestModel,
    PackageResponseModel,
    PackageStatus
} from "../../../packages/models/package.model";
import { AirportResponseModel } from "../../../airports/models/airports.model"; // Import the airport model
import { FaFilter } from "react-icons/fa"; // Import the filter icon
import { Review } from "../../../tours/models/Review";
import { FaStar, FaStarHalfAlt, FaRegStar } from 'react-icons/fa';
import { useReviewsApi } from "../../../reviews/api/review.api";
import { stat } from "fs";
import axios from 'axios';
import { toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import './TourPackagesTab.css';
import { useSubscriptionsApi } from "../../../packages/api/subscriptions.api";
import { SubscriptionResponseModel } from "../../../packages/models/subscription.model";
import { useUserContext } from "../../../../context/UserContext"; // Use your existing context

interface TourPackagesTabProps {
    tourId: string;
}



const TourPackagesTab: React.FC<TourPackagesTabProps> = ({ tourId }) => {
    const { t } = useTranslation();
    const navigate = useNavigate(); // Initialize useNavigate
    const { getAllPackages, addPackage, updatePackage, updatePackageStatus } = usePackagesApi(); // Destructure updatePackageStatus
    const { getAllAirports } = useAirportsApi(); // Use the airports API hook
    const { getReviewsByPackage, addReview, getAverageRating } = useReviewsApi(); // Use the reviews API hook
    const [packages, setPackages] = useState<PackageResponseModel[]>([]);
    const [airports, setAirports] = useState<AirportResponseModel[]>([]); // State for airports
    const [showModal, setShowModal] = useState(false);
    const [modalType, setModalType] = useState<"create" | "update" | "cancel" | "view" | "review" | "viewReviews">(
        "create"
    );
    const [showReviewList, setShowReviewList] = useState(false);

    const [selectedPackage, setSelectedPackage] =
        useState<PackageResponseModel | null>(null);
    const [reviews, setReviews] = useState<Review[]>([]);  // Assuming Review is the type for reviews
    const [averageRating, setAverageRating] = useState<number>(0);
    const [reviewForm, setReviewForm] = useState({
        rating: 0,
        comment: "",
    });
    const [reviewData, setReviewData] = useState<{ reviewerName: string; description: string; rating: number }>({ reviewerName: "", description: "", rating: 0 });
    const [reviewErrors, setReviewErrors] = useState<{ reviewerName: boolean; description?: boolean; rating?: boolean }>({ reviewerName: false, description: false, rating: false });
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

    const [statusModalVisible, setStatusModalVisible] = useState(false);
    const [newStatus, setNewStatus] = useState<PackageStatus | null>(null);
    const [showToast, setShowToast] = useState(false);
    const dailyIntervalRef = useRef<NodeJS.Timeout | null>(null); // Declare useRef outside useEffect
    const midnightTimeoutRef = useRef<NodeJS.Timeout | null>(null); // Store timeout reference
    const [loading, setLoading] = useState<{ [key: string]: boolean }>({}); // Store loading state per package

    const [notificationMessage, setNotificationMessage] = useState<string>("");
    const { getSubscribedPackages } = useSubscriptionsApi();
    const [subscribedPackages, setSubscribedPackages] = useState<PackageResponseModel[]>([]);
    const [allPackages, setAllPackages] = useState<PackageResponseModel[]>([]);
    const { userId, isAuthenticated, isLoading } = useUserContext();


    useEffect(() => {
        const fetchPackages = async () => {
            if (isLoading || !isAuthenticated || !userId) return;

            try {
                const subscriptions = await getSubscribedPackages(userId);
                const allPackagesData = await getAllPackages();

                const subscribedPackageIds = new Set(subscriptions.map(sub => sub.packageId));
                const subscribedData = allPackagesData.filter(pkg => subscribedPackageIds.has(pkg.packageId));

                setSubscribedPackages(subscribedData);
                setAllPackages(allPackagesData);
            } catch (error) {
                console.error("Error fetching packages", error);
            }
        };

        fetchPackages();
    }, [userId, isAuthenticated, isLoading]);



    const handleUpdateSeats = (packageId: string, quantity: number) => {
        setLoading((prev) => ({ ...prev, [packageId]: true })); // Set loading to true for the specific package
    
        // Find the package and update the available seats
        setPackages((prevPackages) => {
          const updatedPackages = prevPackages.map((pkg) => {
            if (pkg.packageId === packageId) {
              const updatedPackage = { ...pkg, availableSeats: pkg.availableSeats - quantity };
              
              // If available seats are <= 10, trigger a toast warning
              if (updatedPackage.availableSeats <= 10) {
                toast.warning(`Warning: Only ${updatedPackage.availableSeats} seats left!`);
              }
              
              return updatedPackage;
            }
            return pkg;
          });
    
          return updatedPackages;
        });
    
        setLoading((prev) => ({ ...prev, [packageId]: false })); // Set loading to false after update
        toast.success("Seats updated successfully!");
      };

    const getStars = (
        rating: number,
        handleClick?: (rating: number) => void
    ) => {
        const stars = [];
        for (let i = 1; i <= 5; i++) {
            const isFullStar = i <= Math.floor(rating);
            const isHalfStar = !isFullStar && rating > i - 1 && rating < i;

            stars.push(
                <span
                    key={i}
                    style={{
                        cursor: handleClick ? "pointer" : "default",
                        fontSize: "3rem",
                        color: "#ffc107",
                    }}
                    onClick={
                        handleClick
                            ? () => handleClick(isHalfStar ? i - 0.5 : i)
                            : undefined
                    }
                >
                    {isFullStar ? (
                        <FaStar />
                    ) : isHalfStar ? (
                        <FaStarHalfAlt />
                    ) : (
                        <FaRegStar />
                    )}
                </span>
            );
        }
        return stars;
    };

    // Function to check and update package status
    const checkAndUpdatePackageStatus = async (packages: PackageResponseModel[]) => {
        const currentDate = new Date();

        for (const pkg of packages) {
            if (new Date(pkg.endDate) < currentDate && pkg.status !== PackageStatus.COMPLETED) {
                try {
                    //generate a new package status model and put a new status
                    await updatePackageStatus(pkg.packageId, PackageStatus.COMPLETED);
                    //fetch all the packages again
                    await fetchPackages();

                    console.log('updating the status !!!!')
                } catch (error) {
                    console.error(`Error updating status for package ${pkg.packageId}:`, error);
                }
            }
        }
    };

    // Fetch packages and check their status
    const fetchAndCheckPackages = async () => {
        const fetchedPackages = await fetchPackages();
        setPackages(fetchedPackages);
        await checkAndUpdatePackageStatus(fetchedPackages);
    };

    useEffect(() => {
        // Run check immediately on mount
        fetchAndCheckPackages();
        fetchAirports();

        // Calculate the delay until the next midnight check
        const now = new Date();
        const nextMidnight = new Date(now);
        nextMidnight.setHours(24, 0, 0, 0); // Set time to exactly 00:00 next day
        const timeUntilMidnight = nextMidnight.getTime() - now.getTime();

        // Clear any existing timeout before setting a new one
        if (midnightTimeoutRef.current) {
            clearTimeout(midnightTimeoutRef.current);
        }

        // Set a timeout to run the check at midnight
        midnightTimeoutRef.current = setTimeout(() => {
            fetchAndCheckPackages();

            // Clear any existing interval before setting a new one
            if (dailyIntervalRef.current) {
                clearInterval(dailyIntervalRef.current);
            }

            // Set an interval to run every 24 hours after midnight
            dailyIntervalRef.current = setInterval(fetchAndCheckPackages, 24 * 60 * 60 * 1000);
        }, timeUntilMidnight);

        // Cleanup both timeout and interval on component unmount or dependency change
        return () => {
            if (midnightTimeoutRef.current) {
                clearTimeout(midnightTimeoutRef.current);
            }
            if (dailyIntervalRef.current) {
                clearInterval(dailyIntervalRef.current);
            }
        };
    }, [tourId]); // Re-run only when tourId changes

    useEffect(() => {
        if (selectedPackage) {
            fetchReviews(selectedPackage.packageId);
        }
    }, [selectedPackage]);

    useEffect(() => {
        applyFilters();
    }, [filterName, filterStatus, filterDate, sortField, sortOrder, packages]);


    const fetchPackages = async (): Promise<PackageResponseModel[]> => {
        try {
            const data = await getAllPackages({ tourId });
            setPackages(data);
            return data;
        } catch (error) {
            console.error("Error fetching packages:", error);
            return [];
        }
    };

    const fetchReviews = async (packageId: string) => {
        try {
            const data = await getReviewsByPackage(packageId);
            setReviews(data);
        } catch (error) {
            console.error("Error fetching reviews:", error);
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
                await updatePackage(selectedPackage.packageId, formData, notificationMessage);
                toast.success("Package changes recorded successfully!");
            }
            setShowModal(false);
            await fetchPackages();
        } catch (error) {
            console.error("Error saving package:", error);
        }
    };

    const handleCancelPackage = async () => {
        if (selectedPackage) {
            await handleUpdateStatus(selectedPackage, PackageStatus.CANCELLED);
            setShowModal(false);
            setShowToast(true); // Show toast notification
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
            filtered = filtered.filter((pkg) => pkg.status?.toString() === filterStatus);
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


    const handleStarClick = (newRating: number) => {
        setReviewData({ ...reviewData, rating: newRating });
    };


    const handleAddReview = (pkgId: string) => {
        setSelectedPackage(packages.find((pkg) => pkg.packageId === pkgId) || null);
        setModalType("review");
        setReviewForm({ rating: 0, comment: "" });
        setShowModal(true);
    };

    const handleReviewSubmit = (e: React.FormEvent) => {
        e.preventDefault();

        // Initialize errors
        const errors: { reviewerName: boolean; description: boolean; rating: boolean } = {
            reviewerName: !reviewData.reviewerName,
            description: !reviewData.description,
            rating: reviewData.rating < 1 || reviewData.rating > 5,
        };

        // Check if there are any errors
        if (errors.reviewerName || errors.description || errors.rating) {
            setReviewErrors(errors);
        } else {
            // Update the reviews state with the new review
            const newReview: Review = {
                reviewId: Date.now().toString(),
                packageId: selectedPackage?.packageId || "",
                reviewerName: reviewData.reviewerName, // Replace with actual reviewer name if available
                review: reviewData.description,
                date: new Date().toISOString(),
                rating: reviewData.rating
            };
            const updatedReviews = [...reviews, newReview];
            setReviews(updatedReviews);

            // Recalculate the average rating
            const totalRating = updatedReviews.reduce((acc, review) => acc + review.rating, 0);
            const newAverage = totalRating / updatedReviews.length;
            setAverageRating(newAverage);

            // Clear the form fields after submission
            setReviewData({ reviewerName: "", description: "", rating: 0 });
            setReviewErrors({ reviewerName: false, description: false, rating: false });
            setShowModal(false);
        }
    };


    const handleViewAllReviews = (pkgId: string) => {
        const selected = packages.find((pkg) => pkg.packageId === pkgId);
        console.log("Selected package:", selected); // Debugging
        setSelectedPackage(selected || null);
        setShowReviewList(true);
    };


    const calculateAverageRating = (pkgId: string) => {
        const pkgReviews = reviews.filter(review => review.packageId === pkgId);
        if (pkgReviews.length === 0) return "N/A";
        const total = pkgReviews.reduce((sum, review) => sum + review.rating, 0);
        return (total / pkgReviews.length).toFixed(1);
    };

    const handleUpdateStatus = async (pkg: PackageResponseModel, newStatus: PackageStatus) => {
        try {
            if (!newStatus) {
                console.error("Cannot update package status: newStatus is undefined or null");
                return;
            }

            console.log(`handleUpdate method : Updating packageId=${pkg.packageId} to newStatus=${newStatus} from=${pkg.status}`);

            await updatePackageStatus(pkg.packageId, newStatus);
            await fetchPackages(); // Refresh the list

            console.log(`Package status updated successfully for ${pkg.packageId}`);
        } catch (error) {
            console.error(`Error updating package status for ${pkg.packageId}:`, error);
        }
    };


    const handleStatusChange = async () => {
        if (selectedPackage && newStatus) {
            await handleUpdateStatus(selectedPackage, newStatus);
            setStatusModalVisible(false);
        }
    };

    const isStartDateEditable = selectedPackage?.status !== PackageStatus.BOOKING_OPEN;

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

            <div>
                {/* Modal for Viewing All Reviews */}
                <Modal
                    show={showReviewList}
                    onHide={() => setShowReviewList(false)}
                    size="lg"
                    centered
                >
                    <Modal.Header closeButton>
                        <Modal.Title>{t("All Reviews")}</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        {reviews && reviews.length > 0 ? (
                            <div style={{ maxHeight: "400px", overflowY: "auto" }}>
                                <ul style={{ padding: 0, listStyle: "none" }}>
                                    {reviews
                                        .sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime())
                                        .map((review, index) => (
                                            <li key={index} style={{ marginBottom: "20px", borderBottom: "1px solid #ccc", paddingBottom: "10px" }}>
                                                <strong>{review.reviewerName}</strong>
                                                <p style={{ margin: "5px 0" }}>{review.review}</p>
                                                <div>{getStars(review.rating)}</div>
                                                <small>{new Date(review.date).toLocaleString()}</small>
                                            </li>
                                        ))}
                                </ul>
                            </div>
                        ) : (
                            <p>{t("No Reviews Available")}</p>
                        )}
                    </Modal.Body>
                    <Modal.Footer>
                        <Button variant="secondary" onClick={() => setShowReviewList(false)}>
                            {t("Close")}
                        </Button>
                    </Modal.Footer>
                </Modal>
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
                        <th>{t("Avg. Rating")}</th>
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
                            <td>{calculateAverageRating(pkg.packageId)}</td>
                            <td>
                                <Button variant="primary" onClick={() => handleAddReview(pkg.packageId)}>
                                    {t("Add Review")}
                                </Button>
                            </td>
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
                                    disabled={pkg.status === PackageStatus.CANCELLED || pkg.status === PackageStatus.COMPLETED} // Disable if status is CANCELLED
                                >
                                    {t("Edit Package")}
                                </Button>
                                {pkg.status !== PackageStatus.CANCELLED && pkg.status !== PackageStatus.COMPLETED && (
                                    <Button
                                        variant="outline-danger"
                                        className="ms-2"
                                        onClick={() => {
                                            setSelectedPackage(pkg);
                                            setModalType("cancel");
                                            setShowModal(true);
                                        }}
                                    >
                                        {t("Cancel Package")}
                                    </Button>
                                )}
                                <Button
                                    variant="outline-secondary"
                                    className="ms-2"
                                    onClick={() => navigate(`/bookings?packageId=${pkg.packageId}`)}
                                >
                                    {t("View Bookings")}
                                </Button>
                                <Button variant="outline-secondary"
                                    className="ms-2"
                                    onClick={() => handleViewAllReviews(pkg.packageId)}>
                                    {t("View All Reviews")}
                                </Button>
                                <Button variant="outline-secondary"
                                    className="ms-2"
                                    onClick={() => handleUpdateSeats(pkg.packageId, 1)} // Example: Decrease by 1
                        >
                            {loading ? "Decrease Seats" : "Decrease Seats"}
                        </Button>
                                {pkg.status !== PackageStatus.CANCELLED && pkg.status !== PackageStatus.COMPLETED && (
                                    <Button
                                        variant="outline-secondary"
                                        className="ms-2"
                                        onClick={() => {
                                            setSelectedPackage(pkg);
                                            setNewStatus(null);
                                            setStatusModalVisible(true);
                                        }}
                                    >
                                        {t("Change Status")}
                                    </Button>
                                )}
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
                            : modalType === "review"
                                ? t("Add Review")
                                : modalType === "update"
                                    ? t("Edit Package")
                                    : modalType === "cancel"
                                        ? t("Cancel Package")
                                        : modalType === "view"
                                            ? t("View Package")
                                            : t("View Reviews")}
                    </Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    {modalType === "cancel" ? (
                        <p>{t("Are you sure you want to cancel this package? This action cannot be undone.")}</p>
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
                            <p><strong>{("packageStatus")}:</strong> {selectedPackage?.status}</p>
                        </div>
                    ) : modalType === "review" ? (
                        <Form onSubmit={handleReviewSubmit}>
                            <Form.Group className="mb-3">
                                <Form.Label>{t("Reviewer Name")}</Form.Label>
                                <Form.Control
                                    required
                                    type="text"
                                    value={reviewData.reviewerName}
                                    onChange={(e) =>
                                        setReviewData({ ...reviewData, reviewerName: e.target.value })
                                    }
                                    isInvalid={reviewErrors.reviewerName}
                                />
                                <Form.Control.Feedback type="invalid">
                                    {t("Reviewer Name Required")}
                                </Form.Control.Feedback>
                            </Form.Group>

                            <Form.Group className="mb-3">
                                <Form.Label>{t("Review Description")}</Form.Label>
                                <Form.Control
                                    as="textarea"
                                    value={reviewData.description}
                                    onChange={(e) =>
                                        setReviewData({ ...reviewData, description: e.target.value })
                                    }
                                    rows={3}
                                    isInvalid={reviewErrors.description}
                                />
                                <Form.Control.Feedback type="invalid">
                                    {t("Review Description Required")}
                                </Form.Control.Feedback>
                            </Form.Group>
                            <Form.Group className="mb-3">
                                <Form.Label>{t("Rating")}</Form.Label>
                                <div>{getStars(reviewData.rating, handleStarClick)}</div>
                                <Form.Control.Feedback type="invalid">
                                    {t("Rating Required")}
                                </Form.Control.Feedback>
                            </Form.Group>
                            <Modal.Footer>
                                <Button variant="secondary" onClick={() => setShowModal(false)}>
                                    {t("Cancel")}
                                </Button>
                                <Button type="submit" variant="primary">
                                    {t("Submit Review")}
                                </Button>
                            </Modal.Footer>
                        </Form>
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
                                    disabled={!isStartDateEditable} // Disable if booking status is BOOKING_OPEN
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
                                <Form.Label>{t("totalSeats")}</Form.Label>
                                <Form.Control
                                    type="number"
                                    value={formData.totalSeats}
                                    onChange={(e) =>
                                        setFormData({ ...formData, totalSeats: +e.target.value })
                                    }
                                    isInvalid={formErrors.totalSeats}
                                />
                                <Form.Control.Feedback type="invalid">
                                    {t("totalSeatsRequired")}
                                </Form.Control.Feedback>
                            </Form.Group>
                            {modalType === "update" && (
                                <Form.Group className="mb-3">
                                    <Form.Label>{t("notificationMessage")}</Form.Label>
                                    <Form.Control
                                        as="textarea"
                                        rows={3}
                                        value={notificationMessage}
                                        onChange={(e) => setNotificationMessage(e.target.value)}
                                        className="notification-textarea"
                                    />
                                </Form.Group>
                            )}
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
                {modalType === "cancel" && (
                    <Modal.Footer>
                        <Button variant="secondary" onClick={() => setShowModal(false)}>
                            {t("Cancel")}
                        </Button>
                        <Button variant="danger" onClick={handleCancelPackage}>
                            {t("Confirm")}
                        </Button>
                    </Modal.Footer>
                )}
            </Modal>

            <Modal show={statusModalVisible} onHide={() => setStatusModalVisible(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>{t("Change Package Status")}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form.Group>
                        <Form.Label>{t("Select New Status")}</Form.Label>
                        <Form.Control
                            as="select"
                            value={newStatus || ""}
                            onChange={(e) => {
                                const selectedValue = e.target.value as PackageStatus;
                                setNewStatus(selectedValue);
                            }}
                        >
                            <option value="">{t("Select Status")}</option>
                            <option value={PackageStatus.BOOKING_OPEN}>{t("Booking Open")}</option>
                            <option value={PackageStatus.BOOKING_CLOSED}>{t("Booking Closed")}</option>
                            <option value={PackageStatus.COMPLETED}>{t("Completed")}</option>
                        </Form.Control>
                    </Form.Group>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setStatusModalVisible(false)}>
                        {t("Cancel")}
                    </Button>
                    <Button variant="primary" onClick={handleStatusChange} disabled={!newStatus}>
                        {t("Change Status")}
                    </Button>
                </Modal.Footer>
            </Modal>

            <ToastContainer position="top-end" className="p-3">
                <Toast onClose={() => setShowToast(false)} show={showToast} delay={3000} autohide>
                    <Toast.Header>
                        <strong className="me-auto">{t("Notification")}</strong>
                    </Toast.Header>
                    <Toast.Body>{t("Package has been canceled successfully.")}</Toast.Body>
                </Toast>
            </ToastContainer>

            <div>
            <h2>Subscribed Packages</h2>
            <ul>
                {subscribedPackages.map(pkg => (
                    <li key={pkg.packageId}>{pkg.name}</li>
                ))}
            </ul>
        </div>
        </div>
    );
};

export default TourPackagesTab;
