package cz.cvut.kbss.analysis.controller;

import cz.cvut.kbss.analysis.dto.SvgConversionDTO;
import cz.cvut.kbss.analysis.service.util.SvgUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.batik.transcoder.TranscoderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/svg")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class SvgController {

    @PutMapping(value = "/convert", produces = MediaType.IMAGE_PNG_VALUE)
    public String convertToPng(@RequestBody SvgConversionDTO svgDTO, HttpServletResponse response) throws IOException, TranscoderException {
        log.info("> convertToPng");

        response.addHeader("Content-Disposition", "attachment; filename=\"fta.png\"");
        return SvgUtils.base64SvgToBase64Png(svgDTO.getData());
    }

}
