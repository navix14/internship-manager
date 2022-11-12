package de.propra.chicken.services;

import de.propra.chicken.domain.model.Klausur;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LSFValidator {

    public boolean checkLsf(Klausur klausur) throws IOException {
        String lsfUrl = "https://lsf.hhu.de/qisserver/rds?state=verpublish&status=init&vmfile=no&publishid="
                + klausur.getLsfId().toString()
                + "&moduleCall=webInfo&publishConfFile=webInfo&publishSubDir=veranstaltung";
        Document doc = Jsoup.connect(lsfUrl).get();

        if(!doc.title().contains(klausur.getFachName()))
            return false;

        Element thBefore = doc.selectFirst("#basic_3");
        Element lsfIdNode = thBefore.nextElementSibling();

        if (lsfIdNode == null)
            return false;

        String lsfIdText = lsfIdNode.text();

        if (lsfIdText.isEmpty())
            return false;

        long parsedLsfId = Long.parseLong(lsfIdNode.text());

        return parsedLsfId == klausur.getLsfId();
    }
}
