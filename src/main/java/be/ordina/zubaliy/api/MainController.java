package be.ordina.zubaliy.api;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import lombok.extern.log4j.Log4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import be.ordina.zubaliy.config.Config;
import be.ordina.zubaliy.modules.FileUploader;

@Controller
@RequestMapping(value = "/api")
@Log4j
public class MainController {

	@Autowired
	MongoTemplate mongoTemplate;

	FileUploader fileUploader;

	@PostConstruct
	public void init() {
		fileUploader = new FileUploader();
	}

	@RequestMapping(value = "/config")
	@ResponseBody
	public Map<String, String> getConfig() {

		final HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("upload_folder", Config.UPLOAD_FOLDER);
		hashMap.put("mongodb", mongoTemplate.getDb().getName());

		log.debug(hashMap);
		return hashMap;

	}

	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> uploadFileHandler(@RequestParam("file") final MultipartFile file) {
		// Store file
		final Map<String, Object> result = fileUploader.uploadFile(file);

		return result;
	}

	/**
	 * Get list of files in upload folder
	 */
	@RequestMapping(value = "/files")
	@ResponseBody
	public Map<String, Object> getListOfFiles() {
		final File folder = new File(Config.UPLOAD_FOLDER);
		final File[] listOfFiles = folder.listFiles();
		final Map<String, Object> result = new HashMap<>();
		result.put("files", listOfFiles);

		return result;
	}

}
