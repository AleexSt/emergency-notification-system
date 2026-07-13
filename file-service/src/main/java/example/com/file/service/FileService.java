package example.com.file.service;

import example.com.file.client.RecipientClient;
import example.com.file.dto.request.RecipientRequest;
import example.com.file.dto.response.RecipientResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FileService {

    private final RecipientClient recipientClient;

    public Boolean bulkRegistration(Long clientId, MultipartFile file){
        Workbook workbook;
        try{
            workbook = new XSSFWorkbook(file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("wrong format file");
        }

        Map<String, String> error = new HashMap<>();
        Sheet sheet = workbook.getSheetAt(0);
        for(int i = 1; i <= sheet.getLastRowNum()-1; i++){
            Row row = sheet.getRow(i);
            if(row == null){
                break;
            }

            try {
                recipientClient.register(
                        clientId,
                        RecipientRequest.builder()
                                .name(row.getCell(1).toString())
                                .email(row.getCell(2).toString())
                                .phoneNumber(row.getCell(3).toString())
                                .telegramId(row.getCell(4).toString())
                                .build()
                );
            } catch (RuntimeException e) {
                error.put(row.getCell(2).toString(), e.getMessage());
            }
        }

        if (!error.isEmpty()) {
            throw new RuntimeException("error in xls file");
        }

        return true;
    }

    public byte[] downloadXlsx(Long clientId) {
        List<RecipientResponse> recipients = recipientClient.receiveByClientId(clientId)
                .getBody();

        if (recipients == null || recipients.isEmpty()) {
            return null;
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Recipients");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Name");
            headerRow.createCell(2).setCellValue("Email");
            headerRow.createCell(3).setCellValue("Phone Number");
            headerRow.createCell(4).setCellValue("Telegram ID");


            for (int i = 0; i < recipients.size(); i++) {
                Row row = sheet.createRow(i + 1);
                RecipientResponse recipient = recipients.get(i);
                row.createCell(0).setCellValue(recipient.id());
                row.createCell(1).setCellValue(recipient.name());
                row.createCell(2).setCellValue(recipient.email());
                row.createCell(3).setCellValue(recipient.phoneNumber());
                row.createCell(4).setCellValue(recipient.telegramId());
            }

            java.io.ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("error when download xls");
        }
    }

}
