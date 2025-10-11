package com.gestion.alojamientos.service;
import com.gestion.alojamientos.exception.ElementNotFoundException;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

/**
 * Interfaz para el servicio de gestión de imágenes con Cloudinary.
 * Define métodos para subir y eliminar imágenes en la nube
 */
public interface CloudinaryService {

    /**
     * Sube una imagen a Cloudinary y retorna la URL segura de la imagen subida.
     * La imagen se almacena en la carpeta "users" por defecto.
     * @param file El archivo de imagen que se desea subir.
     * @return La URL segura de la imagen subida a Cloudinary.
     */
    String uploadPhoto(MultipartFile file);

    /**
     * Elimina una imagen de Cloudinary utilizando su ID.
     * Este método elimina la imagen almacenada en la plataforma Cloudinary.
     * @param idImagen El ID de la imagen que se desea eliminar.
     * @return Un mapa con el resultado de la operación de eliminación.
     * @throws Exception Si ocurre un error durante la eliminación de la imagen.
     */
    Map<String, Object> deletePhoto(String idImagen) throws Exception;
}