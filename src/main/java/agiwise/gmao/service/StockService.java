package jway.gmao.service;

import jway.gmao.dao.*;
import jway.gmao.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class StockService {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat sdfy = new SimpleDateFormat("yyyy");


    @Autowired
    private ArticleStockRepository articleStockRepository;
    @Autowired
    private EntrepotRepository entrepotRepository;
    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private InvPhysiqueDetailRepository invPhysiqueDetailRepository;

    @Autowired
    private InvPhysiqueRepository invPhysiqueRepository;

    @Autowired
    private MouvementArticleRepository mouvementArticleRepository;




    public double[] recalculateCUMPAndStock(Article a , Entrepot e,double cump , double stock) {
        Long idIP = invPhysiqueRepository.findMaxIdByEntrepot(e);
        Inv_Physique lastIP = idIP != null ? invPhysiqueRepository.findByID(idIP): null;
        if (lastIP != null) {
            Inv_Physique_Details lastIPD = invPhysiqueDetailRepository.getInv_Physique_DetailsbyIPAndArticle(lastIP,a);
            if (lastIPD != null) {
                cump = lastIPD.getPrix_unitaire();
                stock = lastIPD.getQuantite();
            }
        }

        List<Mouvements_Article> maList = mouvementArticleRepository.findSortedCurrentMouvementsByArticleAndEntrepot(e, a);
        List<Mouvements_Article> negMaList = new ArrayList<Mouvements_Article>();
        for (Mouvements_Article ma : maList) {
            double pa = ma.getPrix_achat();
            if (pa > 0) { // cad c'est un BR
                double qte = ma.getQuantite();
                if (stock == 0 || cump == 0) {
                    cump = pa;
                } else {
                    cump = qte * pa + stock * cump;
                    cump = cump / (stock + qte);
                }
                stock = stock + qte;
                ma.setCump(cump);
                ma.setStock(stock);
                mouvementArticleRepository.save(ma);
                continue;
            }
            // cas de BS ou réception transfert
            // FIFO
            if (negMaList != null && negMaList.size() > 0) {
                for (int idx=0; idx<negMaList.size(); idx++) {
                    Mouvements_Article negMa = negMaList.get(idx);
                    double qte = negMa.getQuantite();
                    if ((stock + qte) < 0) {
                        continue;
                    }
                    stock = stock + qte;
                    negMa.setCump(cump);
                    negMa.setStock(stock);
                    mouvementArticleRepository.save(negMa);
                    negMaList.remove(negMa);
                    idx--;
                }
            }
            double qte = ma.getQuantite();
            if ((stock + qte) < 0) {
                negMaList.add(ma);
                continue;
            }
            stock = stock + qte;
            ma.setCump(cump);
            ma.setStock(stock);
            mouvementArticleRepository.save(ma);
        }
        // on positionne provisoirement les mvts negatifs à la fin
        if (negMaList != null && negMaList.size() > 0) {
            for (Mouvements_Article negMa : negMaList) {
                double qte = negMa.getQuantite();
                stock = stock + qte;
                negMa.setCump(cump);
                negMa.setStock(stock);
                mouvementArticleRepository.save(negMa);
            }
        }
        return new double[]{cump, stock};

    }

}
