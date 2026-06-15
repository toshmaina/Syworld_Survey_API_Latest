package com.skyworld.survey.util;

import com.skyworld.survey.dto.AnswerFieldDto;
import com.skyworld.survey.dto.CertificateDto;
import com.skyworld.survey.dto.ResponseDto;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.List;

/**
 * Manually builds XML for survey responses using the DOM API.
 *
 * Why not Jackson annotations?
 * The answer field element names (e.g. <full_name>, <email_address>) are
 * runtime values from the database — they cannot be expressed as
 * fixed compile-time Jackson/JAXB annotations.
 */
public class XmlResponseSerializer {

    private XmlResponseSerializer() {}

    public static String serializeResponse(ResponseDto dto) throws Exception {
        Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().newDocument();

        Element root = doc.createElement("question_response");
        doc.appendChild(root);

        if (dto.getResponseId() != null) {
            appendText(doc, root, "response_id", String.valueOf(dto.getResponseId()));
        }

        if (dto.getFields() != null) {
            for (AnswerFieldDto field : dto.getFields()) {
                appendText(doc, root, field.getFieldName(),
                        field.getValue() != null ? field.getValue() : "");
            }
        }

        if (dto.getCertificates() != null
                && dto.getCertificates().getCertificate() != null
                && !dto.getCertificates().getCertificate().isEmpty()) {

            Element certsEl = doc.createElement("certificates");
            for (CertificateDto c : dto.getCertificates().getCertificate()) {
                Element certEl = doc.createElement("certificate");
                if (c.getId() != null) {
                    certEl.setAttribute("id", String.valueOf(c.getId()));
                }
                certEl.setTextContent(c.getFilename());
                certsEl.appendChild(certEl);
            }
            root.appendChild(certsEl);
        }

        if (dto.getDateResponded() != null) {
            appendText(doc, root, "date_responded", dto.getDateResponded());
        }

        return domToString(doc);
    }

    public static String serializeResponseList(
            List<ResponseDto> responses,
            int currentPage, int lastPage, int pageSize, long totalCount) throws Exception {

        Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().newDocument();

        Element root = doc.createElement("question_responses");
        root.setAttribute("current_page", String.valueOf(currentPage));
        root.setAttribute("last_page",    String.valueOf(lastPage));
        root.setAttribute("page_size",    String.valueOf(pageSize));
        root.setAttribute("total_count",  String.valueOf(totalCount));
        doc.appendChild(root);

        for (ResponseDto dto : responses) {
            String xml = serializeResponse(dto);
            Document child = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(new ByteArrayInputStream(xml.getBytes()));
            root.appendChild(doc.importNode(child.getDocumentElement(), true));
        }

        return domToString(doc);
    }

    private static void appendText(Document doc, Element parent, String tag, String value) {
        Element el = doc.createElement(tag);
        el.setTextContent(value);
        parent.appendChild(el);
    }

    private static String domToString(Document doc) throws Exception {
        Transformer t = TransformerFactory.newInstance().newTransformer();
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        StringWriter writer = new StringWriter();
        t.transform(new DOMSource(doc), new StreamResult(writer));
        return writer.toString();
    }
}
