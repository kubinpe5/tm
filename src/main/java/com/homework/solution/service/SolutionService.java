package com.homework.solution.service;

import com.homework.solution.repository.PaymentRecord;
import com.homework.solution.repository.PaymentRepository;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class SolutionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SolutionService.class);
    private static final String NEW_LINE = "\n";
    private static final String INPUT_DELIMITER = ",";
    private static final String OUTPUT_DELIMITER = "|";

    private final ApplicationContext applicationContext;
    private final ApplicationArguments applicationArguments;
    private final PaymentRepository paymentRepository;

    public SolutionService(final ApplicationContext applicationContext, final ApplicationArguments applicationArguments, final PaymentRepository paymentRepository) {
        this.applicationContext = applicationContext;
        this.applicationArguments = applicationArguments;
        this.paymentRepository = paymentRepository;
        initializeDataset();
    }

    public void initializeDataset() {
        final String[] args = applicationArguments.getSourceArgs();
        if (!ArrayUtils.isEmpty(args)) {
            final String filenameLocation = args[0];
            solution(filenameLocation);
        }
    }

    private boolean writeOutputToFile(final String inputDir, final String output) throws IOException {
        final String filenameDirectory = Paths.get("target", "result.txt").toString();
        try (PrintStream out = new PrintStream(new FileOutputStream(filenameDirectory))) {
            out.print(output);
        }
        return true;
    }

    /**
     * Creates an output file with solution for input file
     * @param location
     * @return
     */
    public String solution(final String location) {
        initPaymentRepository(location);
        final String output = getOrderOfPayments();
        try {
            if (writeOutputToFile(location, output)) {
                LOGGER.info("Output file was created successfully.");
            }
        } catch (IOException e) {
            LOGGER.error("Unable to write output file.", e);
        }
        return output;
    }

    /**
     * Read file from input otherwise log an error message
     * @param fileName Absolute path as String
     */
    private void initPaymentRepository(final String fileName) {
        LOGGER.info("Reading file {}", fileName);
        File file = new File(fileName);
        try {
            if (file.exists()) {
                readFromInputFile(fileName);
                LOGGER.info("File was successfully parsed");
            }
        } catch (IOException e) {
            LOGGER.warn(e.getMessage());
        }
    }

    /**
     * Reads input file
     * @param fileName
     * @throws IOException
     */
    private void readFromInputFile(final String fileName) throws IOException {
        String line;
        BufferedReader bufReader = new BufferedReader(new FileReader(fileName));
        while ((line = bufReader.readLine()) != null) {
            Optional<PaymentRecord> record = cleanRecordLine(line);
            record.ifPresent(paymentRepository::addRecord);
        }
        paymentRepository.addPositionForPartner();
        bufReader.close();
    }

    /**
     * Cleans input record line and parses it to fill paymentRecord entity
     * @param input One line from input file as String
     * @return Optional<PaymentRecord>
     */
    private Optional<PaymentRecord> cleanRecordLine(final String input) {

        final Pattern wholeInputLine = Pattern.compile("^.*,[\\s\\D]+/[\\s0-9]+,[\\s0-9-:]+$");
        final Pattern nameAndPhonePattern = Pattern.compile("^\\D+\\s*/\\s*\\d{9}$");

        if (wholeInputLine.matcher(input).matches()) {
            final PaymentRecord paymentRecord = new PaymentRecord();

            final String[] description = input.trim().split("\"");
            final String[] data = description[description.length - 1].trim().split(INPUT_DELIMITER);

            if (description.length > 1) {
                paymentRecord.setNameOfTransaction(description[1].trim());
            } else {
                paymentRecord.setNameOfTransaction(data[0].trim());
            }

            if (nameAndPhonePattern.matcher(data[1]).matches()) {
                final String partnerName = data[1].split("/")[0].trim();
                paymentRecord.setPartner(partnerName);
            }

            final String cleansedDateTime = data[2].replace("  ", " ").trim().replace(" ", "T");
            LocalDateTime dateTime = LocalDateTime.parse(cleansedDateTime);
            paymentRecord.setDateTime(dateTime);

            return Optional.of(paymentRecord);
        }
        return Optional.empty();
    }

    /**
     * Lists payments and format the position number with proper decimal places
     * @return Payments as string
     */
    public String getOrderOfPayments() {
        final List<PaymentRecord> records = paymentRepository.getOrderedElements();
        final Map<String, Integer> maxForPartners = paymentRepository.getMaxForPartners();
        StringBuilder ret = new StringBuilder();
        for (PaymentRecord record : records) {
            ret.append(record.getPartner()).append(OUTPUT_DELIMITER)
                    .append(StringUtils.leftPad(record.getPosition().toString(),
                            numberOfSignificantDigits(maxForPartners.get(record.getPartner())),
                            "0")).append(OUTPUT_DELIMITER)
                    .append(record.getNameOfTransaction()).append(NEW_LINE);
        }
        return ret.toString();
    }

    /**
     * Computes number of significant digits for output formatting
     * @param lastPartnerPosition Maximum partner position
     * @return Number of significant digits as Integer
     */
    private Integer numberOfSignificantDigits(final Integer lastPartnerPosition) {
        return lastPartnerPosition.toString().length();
    }

}
