import { useAxiosInstance } from "../axios/useAxiosInstance";

export const useS3Upload = () => {
    const axiosInstance = useAxiosInstance();
    const awsRegion = "us-east-2"; // ✅ Define AWS region

    // Get a pre-signed URL from the backend
    const getPresignedUrl = async (fileName: string, fileType: string): Promise<{ url: string; contentType: string }> => {
        const token = localStorage.getItem("authToken");

        const response = await axiosInstance.post<{ presignedUrl: string; contentType: string }>(
            "s3/generate-url",
            { fileName, contentType: fileType },
            { headers: { Authorization: `Bearer ${token}` } }
        );

        console.log("Pre-signed URL response:", response.data);  // ✅ Log response

        // ✅ Check if the URL contains the correct AWS region
        if (!response.data.presignedUrl.includes(`.${awsRegion}.`)) {
            throw new Error(`Incorrect AWS region in presigned URL. Expected region: ${awsRegion}`);
        }

        const presignedUrl = response.data.presignedUrl;
        console.log("Pre-Signed URL from backend:", presignedUrl); // ✅ Log pre-signed URL

        return {
            url: presignedUrl,
            contentType: response.data.contentType || fileType // ✅ Fallback to fileType if missing
        };
    };

    // Upload a file using an **already provided pre-signed URL**
    const uploadFileToS3 = async (file: File, presignedUrl: string, expectedContentType: string): Promise<string> => {
        console.log("Uploading file:", file.name);
        console.log("Using pre-signed URL:", presignedUrl);
        console.log("Decoded URL:", decodeURIComponent(presignedUrl)); // ✅ Log decoded URL
        console.log("Expected Content-Type:", expectedContentType);

        if (!expectedContentType) {
            throw new Error("Missing expected content type. Ensure pre-signed URL response includes contentType.");
        }

        console.log("Uploading to:", new URL(presignedUrl).toString()); // ✅ Log uploading URL

        const response = await fetch(presignedUrl, {
            method: "PUT",
            body: file,
            headers: {
                "Content-Type": expectedContentType, // ✅ Ensure consistency
            },
        });

        if (!response.ok) {
            throw new Error(`S3 upload failed: ${response.status} - ${await response.text()}`);
        }

        return presignedUrl.split("?")[0]; // Extract final file URL
    };

    return {
        getPresignedUrl,
        uploadFileToS3,
    };
};
