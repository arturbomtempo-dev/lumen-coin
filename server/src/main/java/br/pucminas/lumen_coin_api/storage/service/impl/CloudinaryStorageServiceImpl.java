package br.pucminas.lumen_coin_api.storage.service.impl;

import br.pucminas.lumen_coin_api.storage.exception.StorageException;
import br.pucminas.lumen_coin_api.storage.service.StorageService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CloudinaryStorageServiceImpl implements StorageService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif");

    private final Cloudinary cloudinary;

    @Override
    public String upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new StorageException("Image file must not be empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new StorageException(
                    "Unsupported file type. Allowed types: JPEG, PNG, WebP, GIF");
        }

        try {
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            return (String) result.get("secure_url");
        } catch (IOException e) {
            throw new StorageException("Failed to upload image to storage", e);
        }
    }
}
