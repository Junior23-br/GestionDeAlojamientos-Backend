package com.gestion.alojamientos.service.Impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.gestion.alojamientos.exception.ElementNotFoundException;
import com.gestion.alojamientos.exception.InvalidElementException;
import com.gestion.alojamientos.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
/**
 *
 * Implementación de la clase {@link CloudinaryService} para superponer el {@code Service} en la integración con Cloudinary para la carga y gestión de imágenes.
 * Esta clase utiliza la librería {@code Cloudinary} para subir imágenes a la nube y gestionar su eliminación.
 */
@Service
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;
    /**
     * Constructor que configura la conexión con Cloudinary utilizando las credenciales proporcionadas
     * en el archivo application.yml.
     *
     * @param cloudName Nombre de la cuenta de Cloudinary.
     * @param apiKey Clave de API de Cloudinary.
     * @param apiSecret Secreto de la API de Cloudinary.
     */
    public CloudinaryServiceImpl(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret
    ) {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", cloudName);
        config.put("api_key", apiKey);
        config.put("api_secret", apiSecret);

        // Configura el cliente de Cloudinary con las credenciales proporcionadas
        this.cloudinary = new Cloudinary(config);
    }

    @Override
    public String uploadPhoto(MultipartFile file) {
        try {
            // Convertimos el archive MultipartFile a File
            File archive = convertMultipart(file);

            // Subimos el archive a Cloudinary utilizando la versión correcta de la API
            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                    archive, ObjectUtils.asMap("folder", "users"));
            archive.delete(); // Limpieza del archivo temporal
            // Extraemos la URL de la respuesta
            String url = (String) uploadResult.get("url");

            // Devolvemos la URL
            return url;
        } catch (IOException e) {
            throw new InvalidElementException("Error al subir la imagen a Cloudinary", e);
        }
    }

    private File convertMultipart(MultipartFile imagen) throws IOException {
        // Creamos un archivo temporal para almacenar el contenido del MultipartFile
        File file = File.createTempFile("temp-", imagen.getOriginalFilename());

        // Escribimos los bytes del MultipartFile en el archivo temporal
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(imagen.getBytes());
        }

        return file;
    }

    @Override
    public Map<String, Object> deletePhoto(String idImagen) {
        try {
            return cloudinary.uploader().destroy(idImagen, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new InvalidElementException("No se pudo eliminar la imagen de Cloudinary", e);
        }
    }
}
