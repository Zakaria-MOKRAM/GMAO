package jway.gmao.api;

import jway.gmao.dao.ReclamationRepository;
import jway.gmao.dao.Reclamation_PJRepository;
import jway.gmao.dao.UtilisateurRepo;
import jway.gmao.model.Reclamation;
import jway.gmao.model.Reclamation_PJ;
import jway.gmao.model.Utilisateur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLConnection;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;


@RestController
@RequestMapping({"/apiMobile/apiupload"})
@CrossOrigin("*")
public class ApiUploadController {
    public SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    // public static final String chemin_pj = "/Users/macbookair/Desktop/JWAY/syndic/syndic/src/main/resources/";
    @Value("${dir.chemin_pj}")
    private String chemin_pj;
    @Autowired
    private Reclamation_PJRepository reclamation_pjRepository;
    @Autowired
    private ReclamationRepository reclamationRepository;
    @Autowired
    private UtilisateurRepo userRepository;

    @PutMapping(value="/upload/{id}/{user_id}")
    public boolean pictureupload(@PathVariable(name = "id") Long id,@PathVariable(name = "user_id") Long user_id, @RequestBody MultipartFile file) {


        String chemin = chemin_pj+"ged/";
        File folder_pj = new File(chemin);
        if (!folder_pj.exists()) {
            folder_pj.mkdirs();
        }

        chemin = chemin.concat("reclamation/");

        File folder_reclamations = new File(chemin);
        if (!folder_reclamations.exists()) {
            folder_reclamations.mkdirs();
        }


        Utilisateur utilisateur = userRepository.findUtilisateurById(user_id);
        Reclamation reclamation = reclamationRepository.findArticleByID(id);

        chemin = chemin.concat(id+"/");

        File folder_reclamation = new File(chemin);
        if (!folder_reclamation.exists()) {
            folder_reclamation.mkdirs();
        }

        try {
            String imageFolder2 = chemin;
            File pt = new File(imageFolder2 + file.getOriginalFilename());
            File p = pt.getParentFile();
            if (!p.exists()) {
                p.mkdirs();
            }


            String pj = file.getOriginalFilename();
            String nom_piece = pj.substring(0,pj.lastIndexOf("."));
            String extension = pj.substring(pj.lastIndexOf("."),pj.length());
            int m=1;

            while (pt.exists()) {

                pj= nom_piece + " (" +m+")"+extension;
                pt = new File (imageFolder2+pj);
                m++;
            }

            file.transferTo(pt);
            Reclamation_PJ piece = new Reclamation_PJ();
            piece.setReclamation(reclamation);
            piece.setDate_ajout(new Date());
            piece.setNom_pj(pj);
            piece.setTaille_pj(file.getSize());
            piece.setAuteur(utilisateur.getFirstName()+" "+utilisateur.getLastName());
            reclamation_pjRepository.save(piece);

            return true;

        }
        catch (IllegalStateException | IOException e) {
            System.out.println("IllegalStateException");
            e.printStackTrace();
            return false;
        }

    }

    @RequestMapping(value = "/getImage/{id}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public byte[] getImage(@PathVariable(name = "id") Long id, @RequestParam(name = "fileName") String fileName) throws IOException {

        String chemin = chemin_pj+"ged/";
        String imageFolder2 = chemin + "reclamation/"+id+"/"+fileName;
        File serverFile = new File(imageFolder2);
        return Files.readAllBytes(serverFile.toPath());
    }

    @RequestMapping(value = "/getDocument/{id}", method = RequestMethod.GET)
    public ResponseEntity getDocument(HttpServletResponse response, @PathVariable(name = "id") Long id, @RequestParam(name = "fileName") String fileName) throws IOException {

        String chemin = chemin_pj+"ged/";
        String imageFolder2 = chemin +"reclamation/"+id+"/"+fileName;
        File file = new File(imageFolder2);


        String mimeType = URLConnection.guessContentTypeFromName(file.getName()); // for you it would be application/pdf
        if (mimeType == null) mimeType = "application/octet-stream";
        response.setContentType(mimeType);
        response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));
        response.setContentLength((int) file.length());
        InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
        FileCopyUtils.copy(inputStream, response.getOutputStream());

        return null;
    }


}
