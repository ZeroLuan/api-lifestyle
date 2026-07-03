package br.com.sysmap.backend.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    
    /**
     * Faz o upload de uma imagem para o S3.
     * @param file Arquivo enviado no request.
     * @param folder Pasta dentro do bucket (ex: "avatars", "activities").
     * @return URL pública para acessar a imagem.
     */
    String uploadImage(MultipartFile file, String folder);

    /**
     * Retorna a URL de uma imagem padrão caso o usuário não tenha feito upload.
     */
    String getDefaultAvatarUrl();
}
