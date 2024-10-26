package com.management.ManagementInventaris.barcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.management.ManagementInventaris.product.Product;
import com.management.ManagementInventaris.product.ProductRepository;
import com.management.ManagementInventaris.product.variant.Variant;
import com.management.ManagementInventaris.utils.Cryptographic;
import com.management.ManagementInventaris.utils.CurrencyFormatter;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Hashtable;

@Service
public class BarcodeService {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private ProductRepository productRepository;

    @Value("${minio.bucketName}")
    private String bucketName;

    /**
     * This method generates a QR code image for a given product ID.
     * The QR code contains information about the product such as ID, title, description, price, quantity, and variants.
     * The generated QR code is returned as a byte array.
     *
     * @param productId The ID of the product for which the QR code needs to be generated.
     * @return A byte array representing the QR code image.
     * @throws ResponseStatusException If the product with the given ID does not exist.
     */
    public byte[] getQRCodeImage(String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product with id " + productId + " does not exist!"));

        String encryptedProductId = "";
        try {
            encryptedProductId = Cryptographic.encrypt(productId);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        try {
            StringBuilder barcodeContent = new StringBuilder();
            barcodeContent
                    .append("ID: ").append(encryptedProductId).append("\n")
                    .append("Uploaded By: ").append(product.getUploadedBy().displayName()).append("\n")
                    .append("Title: ").append(product.getTitle()).append("\n")
                    .append("Description: ").append(product.getDescription()).append("\n")
                    .append("Price: ").append(product.getPriceRange()).append("\n")
                    .append("Quantity: ").append(product.getQuantity()).append("\n");
            for (Variant variant : product.getVariants()) barcodeContent.append("Variant: ").append(variant.getName()).append(", Price: ").append(CurrencyFormatter.formatIDR(variant.getPrice())).append("\n");
            Hashtable<EncodeHintType, Object> hintMap = new Hashtable<>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(barcodeContent.toString(), BarcodeFormat.QR_CODE, 300, 300, hintMap);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "png", byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void uploadBarcodeToMinio(String productId, byte[] barcode) {
        String objectName = "/barcodes/" + productId + ".png";
        try (InputStream inputStream = new ByteArrayInputStream(barcode)) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(inputStream, inputStream.available(), -1)
                    .contentType("image/png")
                    .build());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}