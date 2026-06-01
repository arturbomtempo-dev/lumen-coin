package br.pucminas.lumen_coin_api.storage.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String upload(MultipartFile file);
}
