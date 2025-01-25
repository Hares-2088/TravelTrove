import React, { useState } from "react";
import { useS3Upload } from "./useS3Upload";
import { useToursApi } from "../../features/tours/api/tours.api"; // Import the tours API

interface UploadImageProps {
  onFileSelect: (file: File) => Promise<void>;
  presignedUrl: string | null;
  onUploadComplete: (imageUrl: string) => void;
  tourId: string | null; // Add tourId prop
}

const UploadImage: React.FC<UploadImageProps> = ({ onUploadComplete, tourId }) => {
  const { getPresignedUrl, uploadFileToS3 } = useS3Upload();
  const { updateTourImage } = useToursApi(); // Use the tours API
  const [image, setImage] = useState<File | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [presignedUrl, setPresignedUrl] = useState<string | null>(null);
  const [expectedContentType, setExpectedContentType] = useState<string | null>(null);

  const handleFileChange = async (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.files && event.target.files.length > 0) {
      const selectedFile = event.target.files[0];
      setImage(selectedFile);
      setPresignedUrl(null);
      setError(null);

      try {
        const { url, contentType } = await getPresignedUrl(selectedFile.name, selectedFile.type);
        setPresignedUrl(url);
        setExpectedContentType(contentType);

        console.log("Pre-signed URL:", url);
        console.log("Expected Content-Type:", contentType);
      } catch (error) {
        console.error("Error generating pre-signed URL:", error);
        setError("Failed to generate pre-signed URL.");
      }
    }
  };

  const handleUpload = async () => {
    if (!image) {
      setError("Please select a file.");
      return;
    }
    if (!presignedUrl) {
      setError("No pre-signed URL available. Try selecting the file again.");
      return;
    }
    if (!expectedContentType) {
      setError("Unexpected error: Missing content type.");
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const uploadedImageUrl = await uploadFileToS3(image, presignedUrl, expectedContentType);
      if (tourId) {
        await updateTourImage(tourId, uploadedImageUrl); // Update the tour image in the database
      }
      onUploadComplete(uploadedImageUrl);
      console.log("Upload success! Image URL:", uploadedImageUrl);
    } catch (error) {
      console.error("Upload failed:", error);
      setError("Upload failed. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="upload-container">
      <input type="file" accept="image/*" onChange={handleFileChange} />
      <button onClick={handleUpload} disabled={!image || !presignedUrl || loading}>
        {loading ? "Uploading..." : "Upload"}
      </button>
      {error && <p className="error-message">{error}</p>}
    </div>
  );
};

export default UploadImage;
