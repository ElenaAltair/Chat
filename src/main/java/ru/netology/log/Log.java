package ru.netology.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Log {

    private final BufferedWriter writer;

    public Log(File outputFile) throws IOException {
        if (!outputFile.exists()) {
            outputFile.createNewFile();
        }

        this.writer = new BufferedWriter(new FileWriter(outputFile, true));
    }

    public void writeLine(String line) throws IOException {
        if (line == null) {
            throw new IllegalArgumentException("line may not be null");
        }

        this.writer.write(line);
        this.writer.newLine();
        this.writer.flush();
    }


    public void close() throws IOException {
        this.writer.close();
    }

    public static synchronized void log(String value, String help1, String help2) {
        Calendar dateTime = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY hh:mm:ss");
        String dateString = sdf.format(dateTime.getTime());
        try {
            Log log = new Log(new File("src/main/java/ru/netology/resources/File.log"));
            log.writeLine(dateString + ": " + help1 + value + help2);
            log.close();
        } catch (IOException e) {
            System.out.println("log exception: " + e);
        }
    }


    /*
    public static void writerLog(String value){
        Logger logger = Logger.getLogger("MyLog");
        try {
            FileHandler fh = new FileHandler("file.log", true);
            logger.addHandler(fh);
            fh.setFormatter(new SimpleFormatter());

            String pattern = "yyyyMMddhhmmss";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String date = simpleDateFormat.format(new Date());

            logger.info(date + ": " + value);
        } catch (SecurityException | IOException e) {
            logger.log(Level.SEVERE, "FileHandler exception: " + e, e);
        }
    }*/
}
