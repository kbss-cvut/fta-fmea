package cz.cvut.kbss.analysis.service.util;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.bridge.*;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Document;

import java.awt.geom.Rectangle2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SvgUtils {

    public static String base64SvgToBase64Png(String base64Svg) throws TranscoderException, IOException {
        byte[] svgBytes = Base64.decode(base64Svg);

        String data = new String(svgBytes);
        data = data.replaceAll("pointer-events=\\\"([^\\\"]*)\\\"", "");

        SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(
                XMLResourceDescriptor.getXMLParserClassName());

        InputStream is = new ByteArrayInputStream(data.getBytes());
        Document document = factory.createDocument("tmpUri", is);

        UserAgent agent = new UserAgentAdapter();
        DocumentLoader loader = new DocumentLoader(agent);
        BridgeContext context = new BridgeContext(agent, loader);
        context.setDynamic(true);
        GVTBuilder builder = new GVTBuilder();
        GraphicsNode root = builder.build(context, document);

        double padding = 15;
        Rectangle2D bounds = root.getSensitiveBounds();
        double minX = bounds.getMinX() - padding;
        double minY = bounds.getMinY() - padding;
        double width = bounds.getWidth() + padding;
        double height = bounds.getHeight() + padding;

        String newViewBox = "<svg viewBox=\"" + minX + " " + minY + " " + width + " " + height + "\"";
        data = data.replace("<svg", newViewBox);

        InputStream transcoderIs = new ByteArrayInputStream(data.getBytes());
        TranscoderInput transcoderInput = new TranscoderInput(transcoderIs);

        ByteArrayOutputStream resultByteStream = new ByteArrayOutputStream();
        TranscoderOutput transcoderOutput = new TranscoderOutput(resultByteStream);

        PNGTranscoder pngTranscoder = new PNGTranscoder();
        pngTranscoder.transcode(transcoderInput, transcoderOutput);

        resultByteStream.flush();

        return Base64.encode(resultByteStream.toByteArray());
    }

}
