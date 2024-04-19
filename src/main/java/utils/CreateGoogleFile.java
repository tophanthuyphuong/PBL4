package utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;

import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;

public class CreateGoogleFile {

	// PRIVATE!
	private static File _createGoogleFile(String googleFolderIdParent, String contentType, //
			String customFileName, AbstractInputStreamContent uploadStreamContent) throws IOException {

		File fileMetadata = new File();
		fileMetadata.setName(customFileName);

		List<String> parents = Arrays.asList(googleFolderIdParent);
		fileMetadata.setParents(parents);
		//
		Drive driveService = GoogleDriveUtils.getDriveService();

		File file = driveService.files().create(fileMetadata, uploadStreamContent)
				.setFields("id, webContentLink, webViewLink, parents").execute();

		return file;
	}

	// Create Google File from byte[]
	public static File createGoogleFile(String googleFolderIdParent, String contentType, //
			String customFileName, byte[] uploadData) throws IOException {
		//
		AbstractInputStreamContent uploadStreamContent = new ByteArrayContent(contentType, uploadData);
		//
		return _createGoogleFile(googleFolderIdParent, contentType, customFileName, uploadStreamContent);
	}

	// Create Google File from java.io.File
	public static File createGoogleFile(String googleFolderIdParent, String contentType, //
			String customFileName, java.io.File uploadFile) throws IOException {

		//
		AbstractInputStreamContent uploadStreamContent = new FileContent(contentType, uploadFile);
		//
		return _createGoogleFile(googleFolderIdParent, contentType, customFileName, uploadStreamContent);
	}

	// Create Google File from InputStream
	public static File createGoogleFile(String googleFolderIdParent, String contentType, //
			String customFileName, InputStream inputStream) throws IOException {

        //
        AbstractInputStreamContent uploadStreamContent = new InputStreamContent(contentType, inputStream);
        //
        return _createGoogleFile(googleFolderIdParent, contentType, customFileName, uploadStreamContent);
    }
    public static String getAttachmentLink(String filePath) {
    	java.io.File uploadFile = new java.io.File(filePath);
    	String contentType = URLConnection.guessContentTypeFromName(uploadFile.getName());
    	String fileURL = "";
    	File googleFile = null;
    	System.out.println(filePath);
    	try {
    		googleFile = createGoogleFile(null, contentType, uploadFile.getName(), uploadFile);
    		fileURL = googleFile.getWebContentLink();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return fileURL;
    }

	public static String extractFileIdFromUrl(String downloadLink) {
		String fileId = null;
		if (downloadLink.contains("/d/")) {
			int startIndex = downloadLink.indexOf("/d/") + 3;
			int endIndex = downloadLink.indexOf("/", startIndex);
			fileId = downloadLink.substring(startIndex, endIndex);
		} else if (downloadLink.contains("id=")) {
			int startIndex = downloadLink.indexOf("id=") + 3;
			int endIndex = downloadLink.indexOf("&");
//            fileId = downloadLink.substring(startIndex);
			fileId = downloadLink.substring(startIndex, endIndex);
		}
		return fileId;
	}

	public static String getFileName(String downloadLink) {
		Drive driveService;
		String fileName = "";
		try {
			driveService = GoogleDriveUtils.getDriveService();
			String fileId = extractFileIdFromUrl(downloadLink); // Hàm này để lấy id của file từ đường link download
			System.out.println(fileId);
			File file = driveService.files().get(fileId).execute();
			fileName = file.getName();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fileName;
//		// Lấy thông tin file từ đường link download
//		String downloadLink = "https://drive.google.com/uc?id=1PPuefcj7Pt8uC2PIKDFEWOpU-RljxKMX&export=download";

//		System.out.println("Tên file: " + fileName);
	}

	public static Permission createPublicPermission(String googleFileId) throws IOException {
		// All values: user - group - domain - anyone
		String permissionType = "anyone";
		// All values: organizer - owner - writer - commenter - reader
		String permissionRole = "reader";

		Permission newPermission = new Permission();
		newPermission.setType(permissionType);
		newPermission.setRole(permissionRole);

		Drive driveService = GoogleDriveUtils.getDriveService();
		return driveService.permissions().create(googleFileId, newPermission).execute();
	}
//    public static void main(String[] args) throws IOException {
//
//        java.io.File uploadFile = new java.io.File("C:\\Users\\Dell While\\Downloads\\test.txt");
//
//        // Create Google File:
//
//        File googleFile = createGoogleFile(null, "text/plain", "newfile.txt", uploadFile);
//
//        System.out.println("Created Google file!");
//        System.out.println("WebContentLink: " + googleFile.getWebContentLink() );
//        System.out.println("WebViewLink: " + googleFile.getWebViewLink() );
//
//        System.out.println("Done!");
//    }

}