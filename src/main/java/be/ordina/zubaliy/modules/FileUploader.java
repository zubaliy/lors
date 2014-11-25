package be.ordina.zubaliy.modules;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.log4j.Log4j;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import be.ordina.zubaliy.config.Config;

/**
 * Gets the multipart file and saves it on the server.
 *
 * @author zubaliy
 *
 */
@Log4j
public class FileUploader {

	private final File dir;

	/**
	 * Default constructor that uses default upload path.
	 */
	public FileUploader() {

		dir = new File(Config.UPLOAD_FOLDER);

		createDir();
	}

	/**
	 * Create dir where to store files.
	 */
	private void createDir() {
		if (dir.exists()) {
			log.info("Directory" + dir + " already exists.");
		} else {
			dir.mkdirs();
		}
	}

	/**
	 * Safe multipart file to the server upload folder
	 *
	 * @param file the multipart file
	 *
	 * @return boolean True if succeeded, false if not.
	 */
	public Map<String, Object> uploadFile(final MultipartFile file) {
		final HashMap<String, Object> hashMap = new HashMap<String, Object>();

		if (!file.isEmpty()) {
			try {
				final byte[] bytes = file.getBytes();

				// Create the file on server
				final File serverFile = new File(dir.getAbsolutePath() + File.separator + file.getOriginalFilename());
				final BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(bytes);
				stream.close();

				log.info("Server File Location=" + serverFile.getAbsolutePath());
				hashMap.put("message", "You successfully uploaded file=" + file.getOriginalFilename());
				hashMap.put("filepath", serverFile.getAbsolutePath());
				hashMap.put("extention", FilenameUtils.getExtension(serverFile.getName()));
				hashMap.put("succeed", true);
			} catch (final IOException e) {
				log.info("Upload failed for filename=" + file.getOriginalFilename());
				log.info(e.getMessage());
				hashMap.put("message", "Upload failed for filename=" + file.getOriginalFilename());
				hashMap.put("exception", e.getMessage());
				hashMap.put("succeed", false);
			}
		} else {
			log.info("You failed to upload " + file.getOriginalFilename() + " because the file was empty.");
			hashMap.put("message", "Upload failed for filename=" + file.getOriginalFilename());
			hashMap.put("succeed", false);
		}

		return hashMap;
	}
}
