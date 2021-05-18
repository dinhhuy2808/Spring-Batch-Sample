package com.batch.processor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.batch.constant.Category;
import com.batch.constant.QuestionType;
import com.batch.model.Details;
import com.batch.model.ExerciseUploadProcessorInput;
import com.batch.model.ExerciseUploadProcessorOutput;
import com.batch.model.MergedRow;
import com.batch.model.ProcessorInput;
import com.batch.model.ProcessorOutput;
import com.batch.model.QuestionBody;
import com.batch.model.QuestionDescription;
import com.batch.model.RawQuestionBody;
import com.batch.model.RawQuestionDescription;
import com.batch.model.Result;
import com.batch.util.Util;
import com.google.common.collect.ImmutableMap;

public class TestUploadProcessor implements ItemProcessor<ProcessorInput, ProcessorOutput> {
	
	@Autowired
	private Util util;
	
	@Value("${publicImagePath}")
	private String publicImagePath;
	
	@Value("${imageTestPath}")
	private String imageExercisePath;
	
	@Value("${audioTestPath}")
	private String audioExercisePath;

	@Value("${documentRootPath}")
	private String documentRootPath;
	
	private static String IMAGE_SOURCE_TEMP = "!image_source!";
	private static String AUDIO_TEMP = "!audio_temp!";
	private static String QUESTION_DESCRIPTION_TEMP = "!question_description!";
	private static String TYPE_1_TEMPLATE = "<div class=\"qustion\"><div class=\"field-number\">" + "" + "</div> "
			+ "<div class=\"field-image\"> " + " <img src=\"!image_source!\" alt=\"\"> " + "</div> "
			+ "<div class=\"field-audio\"> " + " !audio_temp! " + "</div> "
			+ "<div class=\"text-justify field-subject\"> " + " <p class=\"pinyin\">!question_description!</p> "
			+ "</div></div>";
	private static String ANSWER_CHAR_TEMP = "!answer_char!";
	private static String ANSWER_DESCRIPTION_TEMP = "!answer_description!";
	private static String FIELD_OPTION_TEMPMLATE = "<div class=\"field-option\"> <span class=\"field-no\">!answer_char!</span>\r\n"
			+ "<p><span class=\"label-words\" data-wid=\"875\">!answer_description!</span></p>\r\n" + "</div>";


    public ExerciseUploadProcessorOutput process(ProcessorInput processorInput) {
    	ExerciseUploadProcessorInput exerciseUploadProcessorInput = (ExerciseUploadProcessorInput) processorInput;
		Set<MergedRow> mergedRows = new HashSet<>();
		Sheet sheet = exerciseUploadProcessorInput.getSheet();
		sheet.getMergedRegions();
		for (CellRangeAddress cell : sheet.getMergedRegions()) {
			if (cell.getFirstColumn() != 0 && cell.getLastRow() != 0) {
				MergedRow row = new MergedRow();
				row.setFirstRow(cell.getFirstRow() + 1);
				row.setLastRow(cell.getLastRow() + 1);
				row.setFirstCol(cell.getFirstColumn() + 1);
				row.setLastCol(cell.getLastColumn() + 1);
				mergedRows.add(row);
			}

		}
		List<MergedRow> sortedList = mergedRows
				.stream().sorted(Comparator.comparingInt(MergedRow::getFirstRow)
						.thenComparingInt(MergedRow::getLastRow).thenComparingInt(MergedRow::getFirstCol))
				.collect(Collectors.toList());
		List<MergedRow> quizByMergedRow = sortedList.stream().filter(mergedRow -> mergedRow.getFirstCol() == 4)
				.collect(Collectors.toList());
		Map<MergedRow, List<MergedRow>> childQuizMapFromMergedRow = new HashMap<MergedRow, List<MergedRow>>();
		for (MergedRow merge : quizByMergedRow) {
			childQuizMapFromMergedRow.put(merge,
					sortedList.stream()
							.filter(mergedRow -> mergedRow.getFirstRow() >= merge.getFirstRow()
									&& mergedRow.getLastRow() <= merge.getLastRow() && !mergedRow.equals(merge))
							.collect(Collectors.toList()));
		}
		// let's sort this map by keys first
		childQuizMapFromMergedRow = childQuizMapFromMergedRow.entrySet().stream()
				.sorted(Map.Entry.comparingByKey(Comparator.comparingInt(MergedRow::getFirstRow)))
				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2,
						LinkedHashMap::new));
		List<QuestionDescription> questionDescriptions = new ArrayList<>();
		List<RawQuestionDescription> rawQuestionDescriptions = new ArrayList<>();
		List<Result> results = new ArrayList<Result>();
		for (MergedRow key : childQuizMapFromMergedRow.keySet()) {
			System.out.println("key: " + key);
			System.out.println(childQuizMapFromMergedRow.get(key));
			System.out.println(sheet.getSheetName().trim());
			questionDescriptions.add(getQuiz(key, childQuizMapFromMergedRow.get(key), sheet, String.valueOf(exerciseUploadProcessorInput.getHsk()), results));
			rawQuestionDescriptions.add(getRawQuiz(questionDescriptions.get(questionDescriptions.size()-1)));
			String number = sheet.getRow(key.getFirstRow() - 1).getCell(5).toString();
			if (!StringUtils.isEmpty(number) && !StringUtils.isAlphanumericSpace(number)) {
			}
		}
		questionDescriptions = questionDescriptions.stream().filter(desc -> desc != null)
				.collect(Collectors.toList());
		
		ExerciseUploadProcessorOutput output = new ExerciseUploadProcessorOutput();
		output.setResults(results);
		output.setQuestionDescriptions(questionDescriptions);
		output.setRawQuestionDescriptions(rawQuestionDescriptions);
		output.setHsk(String.valueOf(exerciseUploadProcessorInput.getHsk()));
		output.setName(sheet.getSheetName());
        return output;
    }
    
	private QuestionDescription getQuiz(MergedRow mergedRow, List<MergedRow> childQuizInMergedRow, Sheet sheet,
			String hsk, List<Result> results) {
		QuestionDescription questionDescription = new QuestionDescription();
		List<QuestionBody> bodies = new ArrayList<QuestionBody>();
		String type = String.valueOf(Double.valueOf(sheet.getRow(mergedRow.getFirstRow() - 1).getCell(mergedRow.getFirstCol() - 2).getNumericCellValue()).intValue());
		Pattern progressPattern = Pattern.compile("^(\\(?\\+?[0-9]*\\)?)-[0-9_\\- \\(\\)]*$");// 5-8
		Pattern nummberPattern = Pattern.compile("^(\\(?\\+?[0-9]*\\)?).[0-9_\\- \\(\\)]*$");// 5
		String progress = sheet.getRow(mergedRow.getFirstRow() - 1).getCell(mergedRow.getFirstCol() - 1).toString()
				.replace(" ", "").replace(".", "");
		if (progressPattern.matcher(progress).matches() || nummberPattern.matcher(progress).matches()) {
			if (type.equalsIgnoreCase("1")) {
				QuestionBody questionBody = new QuestionBody();
				questionBody.setNumber(String.valueOf(Double.valueOf(sheet.getRow(mergedRow.getFirstRow() - 1).getCell(5).getNumericCellValue()).intValue()));
				Map<String, String> values = new ImmutableMap.Builder<String, String>()
						.put("A", "<div><i class=\"fa fa-check\"></i></div>")
						.put("B", "<div><i class=\"fa fa-times\"></i></div>")
						.build();
				String questionType = sheet.getRow(mergedRow.getFirstRow() - 1).getCell(0).getStringCellValue();
				String header = TYPE_1_TEMPLATE;
//				if (questionType.equals("NGHE")) {
					String value = sheet.getRow(mergedRow.getFirstRow() - 1).getCell(4).toString();
					String imageHtml = generateImageHtmlBy(value, hsk,
							Integer.parseInt(sheet.getSheetName().trim()), "");
					if (questionType.equals("NGHE")) {
						header = header.replace(QUESTION_DESCRIPTION_TEMP,"");
						questionDescription.setListenContent(value);
					} else {
						header = header.replace(QUESTION_DESCRIPTION_TEMP,value.equals(imageHtml)?value:"");
					}
					header = header.replace(IMAGE_SOURCE_TEMP,value.equals(imageHtml)?"":imageHtml);
					
					String imageTemp = "<div class=\"field-image\"> " + " <img style=\"max-width: 100%;\" src=\"!image_source!\" alt=\"\"> "
							+ "</div> ";
					value = sheet.getRow(mergedRow.getFirstRow() - 1).getCell(6).toString();
					imageHtml = generateImageHtmlBy(value, hsk,
							Integer.parseInt(sheet.getSheetName().trim()), imageTemp);
					questionBody.setHeader(value.equals(imageHtml)?value:imageHtml);
				
				header = header.replace(AUDIO_TEMP,
						generateAudioHtmlBy(sheet.getRow(mergedRow.getFirstRow() - 1).getCell(10), hsk,
								Integer.parseInt(sheet.getSheetName().trim())));
				questionBody.setValue(values);
				bodies.add(questionBody);
				questionDescription.setType(type);
				questionDescription.setNumber(String.valueOf(Double.valueOf(sheet.getRow(mergedRow.getFirstRow() - 1).getCell(5).getNumericCellValue()).intValue()));
				questionDescription.setHeader(header);
				questionDescription.setBody(bodies);
				if (questionDescription.getNumber() != null && StringUtils.isNumeric(questionDescription.getNumber())) {
					Result newResult = new Result();
					newResult.setHsk(Integer.parseInt(hsk));
					newResult.setType(QuestionType.TEST.name());
					newResult.setTest(Integer.parseInt(sheet.getSheetName().trim()));
					newResult.setNumber(Integer.parseInt(questionDescription.getNumber()));
					newResult.setAnswer(sheet.getRow(mergedRow.getFirstRow() - 1).getCell(9).toString());
					results.add(newResult);
				}
			} else if (type.equalsIgnoreCase("2")) {
				Map<Integer, List<MergedRow>> splitRowsByChildQuestion = childQuizInMergedRow.stream()
						.collect(Collectors.groupingBy(MergedRow::getFirstRow)).entrySet().stream()
						.sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey,
								Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
				String header = "";
				for (Integer startRow : splitRowsByChildQuestion.keySet()) {
					String questionType = sheet.getRow(startRow - 1).getCell(0).getStringCellValue();
					List<MergedRow> chilQuiz = splitRowsByChildQuestion.get(startRow);
					Map<String, String> values = new HashMap<String, String>();
					QuestionBody questionBody = new QuestionBody();
					questionBody.setNumber(String.valueOf(Double.valueOf(sheet.getRow(startRow - 1).getCell(5).getNumericCellValue()).intValue()));
					String headerTemplate = "<div class='type2-listen-question-header'>%s</div>";
					questionBody.setHeader(questionType.equals("DOC")
							? sheet.getRow(startRow - 1).getCell(6).toString()
							: String.format(headerTemplate, sheet.getRow(startRow - 1).getCell(6).toString())
									+ generateAudioHtmlBy(sheet.getRow(startRow - 1).getCell(10), hsk,
											Integer.parseInt(sheet.getSheetName().trim())));
					if (questionType.equals("NGHE")) {
						questionBody.setListenContent(sheet.getRow(startRow - 1).getCell(4).getStringCellValue().replace("\n", "</br>"));
					}
					for (int index = startRow - 1; index <= chilQuiz.get(0).getLastRow() - 1; index++) {
						values.put(sheet.getRow(index).getCell(7).toString(),
								sheet.getRow(index).getCell(8).toString());
					}
					questionBody.setValue(values);
					bodies.add(questionBody);
					if (questionBody.getNumber() != null && StringUtils.isNumeric(questionBody.getNumber())) {
						Result newResult = new Result();
						newResult.setHsk(Integer.parseInt(hsk));
						newResult.setType(QuestionType.TEST.name());
						newResult.setTest(Integer.parseInt(sheet.getSheetName().trim()));
						newResult.setNumber(Integer.parseInt(questionBody.getNumber()));
						newResult.setAnswer(sheet.getRow(startRow - 1).getCell(9).toString());
						results.add(newResult);
					}
				}

				questionDescription.setType(type);
				questionDescription.setNumber(sheet.getRow(mergedRow.getFirstRow() - 1).getCell(3).toString());
				String questionType = sheet.getRow(mergedRow.getFirstRow() - 1).getCell(0).getStringCellValue();
				if (questionType.equals("DOC")) {
					questionDescription.setHeader(sheet.getRow(mergedRow.getFirstRow() - 1).getCell(4).toString().replace("\n", "</br>"));
				} else if (questionType.equals("NGHE")) {
					questionDescription
							.setHeader(generateAudioHtmlBy(sheet.getRow(mergedRow.getFirstRow() - 1).getCell(11), hsk,
									Integer.parseInt(sheet.getSheetName().trim())));
				}
				questionDescription.setBody(bodies);

			} else if (type.equalsIgnoreCase("3")) {
				QuestionBody questionBody = new QuestionBody();
				questionBody.setNumber(String.valueOf(Double.valueOf(sheet.getRow(mergedRow.getFirstRow() - 1).getCell(5).getNumericCellValue()).intValue()));
				Map<String, String> values = new HashMap<String, String>();
				for (int index = mergedRow.getFirstRow() - 1; index <= mergedRow.getLastRow() - 1; index++) {
					String imageTemp = "<div class=\"field-image\"> " + " <img style=\"max-width: 100%;\" src=\"!image_source!\" alt=\"\"> "
							+ "</div> ";
					String value = sheet.getRow(index).getCell(8).toString();
					values.put(sheet.getRow(index).getCell(7).toString(),
							generateImageHtmlBy(value, hsk, Integer.parseInt(sheet.getSheetName().trim()), imageTemp));
				}
				String header = TYPE_1_TEMPLATE;
				String questionType = sheet.getRow(mergedRow.getFirstRow() - 1).getCell(0).getStringCellValue();
				if (questionType.equals("NGHE")) {
					header = header
							.replace(QUESTION_DESCRIPTION_TEMP,
									sheet.getRow(mergedRow.getFirstRow() - 1).getCell(6).getStringCellValue() != null
											? sheet.getRow(mergedRow.getFirstRow() - 1).getCell(6).getStringCellValue()
											: "")
							.replace(AUDIO_TEMP, generateAudioHtmlBy(sheet.getRow(mergedRow.getFirstRow() - 1).getCell(10),
									hsk, Integer.parseInt(sheet.getSheetName().trim())));
					questionDescription.setListenContent(sheet.getRow(mergedRow.getFirstRow() - 1).getCell(4).getStringCellValue() != null
											? sheet.getRow(mergedRow.getFirstRow() - 1).getCell(4).getStringCellValue().replace("\n", "</br>")
											: "");
				} else if (questionType.equals("DOC")) {
					header = header
							.replace(QUESTION_DESCRIPTION_TEMP,
									sheet.getRow(mergedRow.getFirstRow() - 1).getCell(4).getStringCellValue() != null
											? sheet.getRow(mergedRow.getFirstRow() - 1).getCell(4).getStringCellValue().replace("\n", "</br>")
											: "")
							.replace(AUDIO_TEMP, generateAudioHtmlBy(sheet.getRow(mergedRow.getFirstRow() - 1).getCell(10),
									hsk, Integer.parseInt(sheet.getSheetName().trim())));
					questionBody.setHeader(sheet.getRow(mergedRow.getFirstRow() - 1).getCell(6).getStringCellValue() != null
											? sheet.getRow(mergedRow.getFirstRow() - 1).getCell(6).getStringCellValue()
											: "");
				}
				if (header.contains(IMAGE_SOURCE_TEMP)) {
					Document doc = Jsoup.parse(header);
					Elements eleImages = doc.select("img");
					for(Element eleImage : eleImages) {
						if(eleImage.attr("src").equals(IMAGE_SOURCE_TEMP)) {
							eleImage.parent().remove();
						}
					}
					header = doc.body().html();
				}
				questionBody.setValue(values);
				bodies.add(questionBody);
				questionDescription.setType(type);
				questionDescription.setNumber(String.valueOf(Double.valueOf(sheet.getRow(mergedRow.getFirstRow() - 1).getCell(5).getNumericCellValue()).intValue()));
				questionDescription.setHeader(header);
				questionDescription.setBody(bodies);
				if (questionDescription.getNumber() != null && StringUtils.isNumeric(questionDescription.getNumber())) {
					Result newResult = new Result();
					newResult.setHsk(Integer.parseInt(hsk));
					newResult.setType(QuestionType.TEST.name());
					newResult.setTest(Integer.parseInt(sheet.getSheetName().trim()));
					newResult.setNumber(Integer.parseInt(questionDescription.getNumber()));
					newResult.setAnswer(sheet.getRow(mergedRow.getFirstRow() - 1).getCell(9).toString());
					results.add(newResult);
				}
			} else if (type.equalsIgnoreCase("4")) {
				QuestionBody questionBody = new QuestionBody();
				questionBody.setNumber(String.valueOf(Double.valueOf(sheet.getRow(mergedRow.getFirstRow() - 1).getCell(5).getNumericCellValue()).intValue()));
				Map<String, String> values = new HashMap<String, String>();
				for (int index = mergedRow.getFirstRow() - 1; index <= mergedRow.getLastRow() - 1; index++) {
					values.put(sheet.getRow(index).getCell(7).toString(), sheet.getRow(index).getCell(8).toString());
				}
				String header = "";
				questionBody.setValue(values);

				bodies.add(questionBody);
				questionDescription.setType(type);
				questionDescription.setNumber(String.valueOf(Double.valueOf(sheet.getRow(mergedRow.getFirstRow() - 1).getCell(5).getNumericCellValue()).intValue()));
				questionDescription.setHeader(header);
				questionDescription.setBody(bodies);
				if (questionDescription.getNumber() != null && StringUtils.isNumeric(questionDescription.getNumber())) {
					Result newResult = new Result();
					newResult.setHsk(Integer.parseInt(hsk));
					newResult.setType(QuestionType.TEST.name());
					newResult.setTest(Integer.parseInt(sheet.getSheetName().trim()));
					newResult.setNumber(Integer.parseInt(questionDescription.getNumber()));
					char[] answer = sheet.getRow(mergedRow.getFirstRow() - 1).getCell(9).toString().trim()
							.toCharArray();
					newResult.setAnswer(answer[0] + "-" + answer[1] + "-" + answer[2]);
					results.add(newResult);
				}
			} else if (type.equalsIgnoreCase("5")) {
				Map<String, String> values = new HashMap<String, String>();
				String header = "";
				String[] answerNames = { "A", "B", "C", "D", "E", "F", "G", "H", "I" };
				int countAnswer = 0;
				for (int index = mergedRow.getFirstRow() - 1; index <= mergedRow.getLastRow() - 1; index++) {
					QuestionBody questionBody = new QuestionBody();
					questionBody.setNumber(String.valueOf(Double.valueOf(sheet.getRow(index).getCell(5).getNumericCellValue()).intValue()));
					String subjectTemplate = "<div class=\"field-subject\"><div>!question_description!</div></div><div class=\"field-select\"></div>";
					bodies.add(questionBody);
					String questionType = sheet.getRow(mergedRow.getFirstRow() - 1).getCell(0).getStringCellValue();
					
					String key = sheet.getRow(index).getCell(7).toString();
					if (!StringUtils.isEmpty(key)) {
						
						String imageTemp = "<div class=\"field-image\"> " + " <img style=\"max-width: 100%;\" src=\"!image_source!\" alt=\"\"> "
								+ "</div> ";
						String imageHtml = generateImageHtmlBy(sheet.getRow(index).getCell(8).toString(), hsk,
								Integer.parseInt(sheet.getSheetName().trim()), imageTemp);
						String audiotemplate = generateAudioHtmlBy(sheet.getRow(index).getCell(10), hsk,
								Integer.parseInt(sheet.getSheetName().trim()));
						if (questionType.equals("NGHE")) {
							questionBody.setHeader(audiotemplate);
							values.put(answerNames[countAnswer], imageHtml);
							questionBody.setListenContent(sheet.getRow(index).getCell(4).getStringCellValue());
						} else {
							questionBody.setHeader(subjectTemplate.replace("!question_description!",
									sheet.getRow(index).getCell(4).toString().replace("\n", "</br>")));
							values.put(answerNames[countAnswer], imageHtml + audiotemplate);
						}
						
						
						countAnswer++;
					} else {
						values.put(sheet.getRow(index).getCell(7).toString(),
								sheet.getRow(index).getCell(8).toString());
					}

					String headerTemp = FIELD_OPTION_TEMPMLATE;
					header += headerTemp.replace(ANSWER_CHAR_TEMP, sheet.getRow(index).getCell(7).toString())
							.replace(ANSWER_DESCRIPTION_TEMP, sheet.getRow(index).getCell(8).toString());

					if (questionBody.getNumber() != null && StringUtils.isNumeric(questionBody.getNumber())) {
						Result newResult = new Result();
						newResult.setHsk(Integer.parseInt(hsk));
						newResult.setType(QuestionType.TEST.name());
						newResult.setTest(Integer.parseInt(sheet.getSheetName().trim()));
						newResult.setNumber(Integer.parseInt(questionBody.getNumber()));
						newResult.setAnswer(sheet.getRow(index).getCell(9).getStringCellValue());
						results.add(newResult);
					}
				}

				questionDescription.setBody(bodies);
				questionDescription.setHeader(header);
				questionDescription.setHeadingOptions(values);
				questionDescription.setNumber(progress);
				questionDescription.setType(type);
			} else if (type.equalsIgnoreCase("6")) {
				Map<String, String> values = new HashMap<String, String>();
				QuestionBody questionBody = new QuestionBody();
				questionBody.setNumber(progress);
				for (int index = mergedRow.getFirstRow() - 1; index <= mergedRow.getLastRow() - 1; index++) {
					values.put(sheet.getRow(index).getCell(7).toString(), sheet.getRow(index).getCell(8).toString());
					Double number = sheet.getRow(index).getCell(5).getNumericCellValue();
					if (number != null) {
						Result newResult = new Result();
						newResult.setHsk(Integer.parseInt(hsk));
						newResult.setType(QuestionType.TEST.name());
						newResult.setTest(Integer.parseInt(sheet.getSheetName().trim()));
						newResult.setNumber(number.intValue());
						newResult.setAnswer(sheet.getRow(index).getCell(9).toString());
						results.add(newResult);
					}
				}
				questionBody.setValue(values);
				bodies.add(questionBody);
				questionDescription.setBody(bodies);

				String header = sheet.getRow(mergedRow.getFirstRow() - 1).getCell(4).toString();
				String regex = "________";
				header = header.replace(regex, "%s");
				String element = "<span class=\"child-number\" data-child=\"31604\""
						+ " id=\"%s\" onclick=\"chooseQuestion(%s)\""
						+ " style=\"cursor: pointer;border-bottom: 1px solid #39a0ff;color:red\" selected=\"false\">%s"
						+ "<span class=\"child-select rev-hide\">" + "</span></span>";
				List<String> listQuestionsElement = new ArrayList<>();
				for (int index = Integer.parseInt(progress.split("-")[0]); index <= Integer
						.parseInt(progress.split("-")[1]); index++) {
					listQuestionsElement
							.add(String.format(element, String.valueOf(index), String.valueOf(index), "___"));
				}
				header = String.format(header, listQuestionsElement.toArray());
				questionDescription.setHeader(header);
				questionDescription.setNumber(progress);
				questionDescription.setType(type);

			} else {
				System.out.println("---------------------------------" + type);
			}
		} else {
			return null;
		}
		String cat = sheet.getRow(mergedRow.getFirstRow() - 1).getCell(0).toString();
		questionDescription.setCategory(cat.equals("NGHE") ? Category.NGHE
				: cat.equals("DOC") ? Category.DOC_HIEU : cat.equals("VIET") ? Category.VIET_VAN : Category.OTHERS);
		return questionDescription;
	}
	

	private RawQuestionDescription getRawQuiz(QuestionDescription questionDescription) {
		RawQuestionDescription rawQuestionDescription = null;
		QuestionDescription questionDescriptionTemp = null;
		try {
			questionDescriptionTemp = (QuestionDescription) questionDescription.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<RawQuestionBody> rawBodies = new ArrayList<RawQuestionBody>();
		String type = questionDescription.getType();
		if (type.equalsIgnoreCase("1")) {
			Details questionHeardetails = getRawHeaderDetail(questionDescription.getHeader());
			Map<String, Details> values = new HashMap<String, Details>();
			values.put("A", null);
			values.put("B", null);
			for (QuestionBody body : questionDescription.getBody()) {
				RawQuestionBody rawBody = new RawQuestionBody(body.getNumber(), getRawHeaderDetail(body.getHeader()),
						values, body.getListenContent());
				rawBodies.add(rawBody);
			}
			rawQuestionDescription = new RawQuestionDescription(type, questionDescriptionTemp.getNumber(),
					questionHeardetails, null, rawBodies, questionDescriptionTemp.getCategory(),
					questionDescriptionTemp.getListenContent());

		} else if (type.equalsIgnoreCase("2")) {
			Details questionHeardetails = getRawHeaderDetail(questionDescription.getHeader());
			Map<String, Details> values = new HashMap<String, Details>();
			for (QuestionBody body : questionDescription.getBody()) {
				for (Entry<String, String> bodyValue : body.getValue().entrySet()) {
					values.put(bodyValue.getKey(), new Details(null, null, bodyValue.getValue()));
				}
				RawQuestionBody rawBody = new RawQuestionBody(body.getNumber(), getRawHeaderDetail(body.getHeader()),
						values, body.getListenContent());
				Document doc = Jsoup.parse(body.getHeader());
				Elements eleDescriptions = doc.select(".type2-listen-question-header");
				for (Element eleDescription : eleDescriptions) {
					rawBody.getHeader().setDescription(eleDescription.text());
				}
				rawBodies.add(rawBody);
			}
			rawQuestionDescription = new RawQuestionDescription(type, questionDescriptionTemp.getNumber(),
					questionHeardetails, null, rawBodies, questionDescriptionTemp.getCategory(),
					questionDescriptionTemp.getListenContent());

		} else if (type.equalsIgnoreCase("3")) {
			Details questionHeardetails = getRawHeaderDetail(questionDescription.getHeader());
			Map<String, Details> values = new HashMap<String, Details>();
			for (QuestionBody body : questionDescription.getBody()) {
				for (Entry<String, String> bodyValue : body.getValue().entrySet()) {
					values.put(bodyValue.getKey(), new Details(null, null, bodyValue.getValue()));
				}
				RawQuestionBody rawBody = new RawQuestionBody(body.getNumber(), getRawHeaderDetail(body.getHeader()),
						values, body.getListenContent());
				rawBodies.add(rawBody);
			}
			rawQuestionDescription = new RawQuestionDescription(type, questionDescriptionTemp.getNumber(),
					questionHeardetails, null, rawBodies, questionDescriptionTemp.getCategory(),
					questionDescriptionTemp.getListenContent());

		} else if (type.equalsIgnoreCase("4")) {
			Details questionHeardetails = getRawHeaderDetail(questionDescription.getHeader());
			Map<String, Details> values = new HashMap<String, Details>();
			for (QuestionBody body : questionDescription.getBody()) {
				for (Entry<String, String> bodyValue : body.getValue().entrySet()) {
					values.put(bodyValue.getKey(), new Details(null, null, bodyValue.getValue()));
				}
				RawQuestionBody rawBody = new RawQuestionBody(body.getNumber(), getRawHeaderDetail(body.getHeader()),
						values, body.getListenContent());
				rawBodies.add(rawBody);
			}
			rawQuestionDescription = new RawQuestionDescription(type, questionDescriptionTemp.getNumber(),
					questionHeardetails, null, rawBodies, questionDescriptionTemp.getCategory(),
					questionDescriptionTemp.getListenContent());

		} else if (type.equalsIgnoreCase("5")) {
			Details questionHeardetails = getRawHeaderDetail(questionDescription.getHeader());
			Map<String, Details> values = new HashMap<String, Details>();
			for (Entry<String, String> bodyValue : questionDescriptionTemp.getHeadingOptions().entrySet()) {
				values.put(bodyValue.getKey(), getRawHeaderDetail(bodyValue.getValue()));
			}
			for (QuestionBody body : questionDescription.getBody()) {
				RawQuestionBody rawBody = new RawQuestionBody(body.getNumber(), getRawHeaderDetail(body.getHeader()),
						null, body.getListenContent());
				rawBodies.add(rawBody);
			}
			rawQuestionDescription = new RawQuestionDescription(type, questionDescriptionTemp.getNumber(),
					questionHeardetails, values, rawBodies, questionDescriptionTemp.getCategory(),
					questionDescriptionTemp.getListenContent().replace("</br>", "\n"));

		} else if (type.equalsIgnoreCase("6")) {
			Details questionHeardetails = getRawHeaderDetail(questionDescription.getHeader());
			Map<String, Details> values = new HashMap<String, Details>();
			for (QuestionBody body : questionDescription.getBody()) {
				for (Entry<String, String> bodyValue : body.getValue().entrySet()) {
					values.put(bodyValue.getKey(), new Details(null, null, bodyValue.getValue()));
				}
				RawQuestionBody rawBody = new RawQuestionBody(body.getNumber(), getRawHeaderDetail(body.getHeader()),
						values, body.getListenContent());
				rawBodies.add(rawBody);
			}
			rawQuestionDescription = new RawQuestionDescription(type, questionDescriptionTemp.getNumber(),
					questionHeardetails, null, rawBodies, questionDescriptionTemp.getCategory(),
					questionDescriptionTemp.getListenContent());

		} else {
			System.out.println("---------------------------------" + type);
		}
		return rawQuestionDescription;
	}
	
	private Details getRawHeaderDetail(String html) {
		if (html == null) {
			return new Details(null, null, null);
		}
		Details details = new Details();
		Document doc = Jsoup.parse(html);
		Elements eleImages = doc.select("img");
		for (Element eleImage : eleImages) {
			String source = eleImage.attr("src");
			if (!source.contains("undo.svg") && !source.contains("redo.svg")) {
				details.setImage(eleImage.attr("src"));
			}
		}
		Elements eleAudioSources = doc.select("audio>source");
		for (Element eleAudioSource : eleAudioSources) {
			details.setAudio(eleAudioSource.attr("src"));
		}
		Elements eleDescriptions = doc.select(".pinyin");
		for (Element eleDescription : eleDescriptions) {
			details.setDescription(eleDescription.text());
		}
		if (details.getDescription() == null || details.getDescription().isEmpty()) {
			details.setDescription(doc.text());
		}
		return details;
	}
	
	private String generateImageHtmlBy(String imageName, String hsk, int lesson, String template) {
		String result = imageName;
		try {
			List<String> fileNames = util.getAllFilesNameInFolder(publicImagePath + "Test/" + hsk);
			for (String fileName : fileNames) {
				if (fileName.substring(0, fileName.lastIndexOf(".")).trim().equalsIgnoreCase(imageName.trim())) {
					if (template.trim().equals("")) {
						result = imageExercisePath.replace("{hsk}", hsk) + fileName;
					} else {
						result = template.replace(IMAGE_SOURCE_TEMP, imageExercisePath.replace("{hsk}", hsk) + fileName);
					}
					break;
				}
			}
		} catch (Exception e) {
			return result;
		}
		
		if (result.contains(IMAGE_SOURCE_TEMP)) {
			Document doc = Jsoup.parse(result);
			Elements eleImages = doc.select("img");
			for(Element eleImage : eleImages) {
				if(eleImage.attr("src").equals(IMAGE_SOURCE_TEMP)) {
					eleImage.parent().remove();
				}
			}
		}
		return result;
	}
	private String generateAudioHtmlBy(Cell cell, String hsk, int lesson) {
		
		String audioTemp = "<audio controls=\"\" controlsList=\"nodownload\"> \r\n" + 
				"  <source src=\"!audio_source!\" type=\"audio/mpeg\"> Your browser does not support the audio element. \r\n" + 
				"</audio>\r\n" + 
				"<div>\r\n" + 
				"   <button class=\"btn\" ><img src=\"assets/images/numbers/undo.svg\" onclick=\"previous(this)\" alt=\"\"  width=\"30px\" height=\"30px\"></button>\r\n" + 
				"   <button class=\"btn mr-4\" ><img src=\"assets/images/numbers/redo.svg\" onclick=\"next(this)\" alt=\"\" width=\"30px\" height=\"30px\"></button>\r\n" + 
				"</div>";
		return cell != null && StringUtils.trimToNull(cell.toString()) != null
				? audioTemp.replace("!audio_source!",
						audioExercisePath.replace("{hsk}", hsk)
								+ util.getCompleteNameInFolder(documentRootPath + "public/audios/Test/" + hsk,
										cell.toString() + ".mp3", String.format("HSK%s-%02d",hsk, lesson)))
				: "";
	}

}
