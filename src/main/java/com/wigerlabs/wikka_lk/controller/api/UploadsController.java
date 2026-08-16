package com.wigerlabs.wikka_lk.controller.api;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import java.io.File;

@Path("/uploads")
public class UploadsController {

    @GET
    @Path("/{folder}/{filename}")
    public Response getUploadedFile(
            @PathParam("folder") String folder,
            @PathParam("filename") String filename,
            @Context jakarta.servlet.http.HttpServletRequest req) {

        String basePath = req.getServletContext().getRealPath("/");
        if (basePath == null) {
            basePath = System.getProperty("user.dir") + File.separator;
        }
        String filePath = basePath + "uploads/" + folder + "/" + filename;
        File file = new File(filePath);

        if (file.exists() && !file.isDirectory()) {
            String mimeType = req.getServletContext().getMimeType(filePath);
            if (mimeType == null) {
                if (filename.endsWith(".webp")) mimeType = "image/webp";
                else if (filename.endsWith(".png")) mimeType = "image/png";
                else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) mimeType = "image/jpeg";
                else mimeType = "application/octet-stream";
            }
            return Response.ok(file, mimeType).build();
        }

        return Response.status(Response.Status.NOT_FOUND).entity("File not found").build();
    }
}
