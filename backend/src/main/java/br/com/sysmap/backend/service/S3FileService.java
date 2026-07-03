package br.com.sysmap.backend.service;

import br.com.sysmap.backend.config.S3Properties;
import br.com.sysmap.backend.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3FileService implements FileService {

    private final S3Client s3Client;
    private final S3Properties s3Properties;

    @PostConstruct
    public void init() {
        try {
            s3Client.headBucket(HeadBucketRequest.builder()
                    .bucket(s3Properties.getBucket())
                    .build());
            log.info("Bucket S3 '{}' já existe.", s3Properties.getBucket());
        } catch (NoSuchBucketException e) {
            log.info("Bucket S3 '{}' não encontrado. Criando...", s3Properties.getBucket());
            s3Client.createBucket(CreateBucketRequest.builder()
                    .bucket(s3Properties.getBucket())
                    .build());
            log.info("Bucket S3 '{}' criado com sucesso.", s3Properties.getBucket());
        } catch (Exception e) {
            log.warn("Não foi possível verificar/criar o bucket S3: {}. Verifique se o LocalStack está rodando.", e.getMessage());
        }
    }

    @Override
    public String uploadImage(MultipartFile file, String folder) {
        // Validação da Regra E2
        validateFile(file);

        try {
            String fileName = folder + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
            
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(s3Properties.getBucket())
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            return String.format("%s/%s/%s", s3Properties.getEndpoint(), s3Properties.getBucket(), fileName);

        } catch (IOException e) {
            throw new RuntimeException("Erro ao processar o arquivo de imagem.", e);
        }
    }

    @Override
    public String getDefaultAvatarUrl() {
        return "https://www.gravatar.com/avatar/00000000000000000000000000000000?d=mp&f=y";
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("O arquivo de imagem está vazio.");
        }

        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
            throw new BadRequestException("A imagem deve ser um arquivo PNG ou JPG.");
        }
    }
}
