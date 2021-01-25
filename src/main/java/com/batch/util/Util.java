package com.batch.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.google.gson.Gson;

@Component
public class Util {

	public <T> T jsonToObject(String json, Class<T> destination) {
		Gson gson = new Gson();
		T result = gson.fromJson(json, destination);
		return result;
	}

	public <T> List<T> jsonToListObject(String json, Type destination) {
		Gson gson = new Gson();
		List<T> result = gson.fromJson(json, destination);
		return result;
	}

	@SuppressWarnings("unchecked")
	public <T> Map<String, T> jsonToMapObject(String json, Map<String, ?> destination) {
		Gson gson = new Gson();
		Map<String, T> result = gson.fromJson(json, destination.getClass());
		return result;
	}

	public <T> String objectToJSON(T t) {
		Gson gson = new Gson();
		String result = gson.toJson(t);
		return result;
	}

	public String getCurrentDate() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
		LocalDateTime now = LocalDateTime.now();
		return dtf.format(now);
	}

	public Properties getDbProperties() {
		Properties prop = new Properties();
		try (InputStream input = new FileInputStream(System.getProperties().get("user.dir") + "/"
				+ "src/main/java/com/lhc/jerseyguice/resource/db.properties")) {

			// load a properties file
			prop.load(input);

		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return prop;
	}

	public String getMd5(String input) {
		try {

			// Static getInstance method is called with hashing MD5
			MessageDigest md = MessageDigest.getInstance("MD5");

			// digest() method is called to calculate message digest
			// of an input digest() return array of byte
			byte[] messageDigest = md.digest(input.getBytes());

			// Convert byte array into signum representation
			BigInteger no = new BigInteger(1, messageDigest);

			// Convert message digest into hex value
			String hashtext = no.toString(16);
			while (hashtext.length() < 32) {
				hashtext = "0" + hashtext;
			}
			return hashtext;
		}

		// For specifying wrong message digest algorithms
		catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public void writeToFile(String output, String fileDestination) {
		FileOutputStream outputStream;
		try {
			outputStream = new FileOutputStream(fileDestination);
			byte[] strToBytes = output.getBytes();
			outputStream.write(strToBytes);

			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public String getJsonStringFromFile(String localtion) {
		BufferedReader bufferedReader = null;
		String json = "";
		try {
			bufferedReader = new BufferedReader(new FileReader(localtion));
			for (String line : Files.readAllLines(Paths.get(localtion))) {
				json += line;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}

	public List<String> getAllFilesNameInFolder(String path) {
		File[] files = new File(path).listFiles();
		try {
			return Arrays.asList(files).stream().map(file -> file.getName()).collect(Collectors.toList());
		} catch (Exception e) {
			return Arrays.asList();
		}
		
	}

	public List<String> getAllCompleteFilesPathInFolder(String path) {
		File[] files = new File(path).listFiles();
		try {
			return Arrays.asList(files).stream().map(file -> file.getAbsolutePath()).collect(Collectors.toList());
		} catch (Exception e) {
			return Arrays.asList();
		}
		
	}

	public String getCompleteNameInFolder(String pathFolder, String endWith, String startWith) {
		List<String> allFilesName = getAllFilesNameInFolder(pathFolder);
		String completeName = "";
		try {
			completeName = allFilesName.stream()
					.filter(fileName -> fileName.startsWith(startWith) && fileName.endsWith(endWith))
					.collect(Collectors.toList()).get(0);
		} catch (NullPointerException | IndexOutOfBoundsException e) {
			return "";
		}
		
		return completeName;
	}

	public static void main(String[] args) {
		Util util = new Util();
		String startWith = String.format("%s", "1");
		System.out.println(startWith);
	}
}
