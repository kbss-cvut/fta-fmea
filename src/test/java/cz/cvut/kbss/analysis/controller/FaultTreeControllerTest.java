package cz.cvut.kbss.analysis.controller;

import cz.cvut.kbss.jsonld.JsonLd;
import cz.cvut.kbss.jsonld.annotation.JsonLdProperty;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class FaultTreeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void create_shouldReturnCreated_shouldReturnObject() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .post("/faultTrees")
                .content(faultTree)
                .header("Authorization", "Bearer {{jwt}}")
                .contentType(JsonLd.MEDIA_TYPE);

        MvcResult result = this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();
    }

    private String faultTree = "{\n" +
            "  \"name\": \"Testing FaultTree\",\n" +
            "  \"manifestingNode\": {\n" +
            "    \"treeNodeType\": \"EVENT\",\n" +
            "    \"event\": {\n" +
            "      \"name\": \"Pump not working\",\n" +
            "      \"description\": \"Pump cannot push the fuel to the engine\",\n" +
            "      \"eventType\": \"TOP_EVENT\",\n" +
            "      \"riskPriorityNumber\": {\n" +
            "        \"probability\": 0.5,\n" +
            "        \"severity\": 4,\n" +
            "        \"detection\": 1,\n" +
            "        \"types\": [\"http://onto.fel.cvut.cz/ontologies/fta-fmea-application/RiskPriorityNumber\"]\n" +
            "      },\n" +
            "      \"types\": [\"http://onto.fel.cvut.cz/ontologies/fta-fmea-application/FaultEvent\"]\n" +
            "    },\n" +
            "    \"types\": [\"http://onto.fel.cvut.cz/ontologies/fta-fmea-application/TreeNode\"]\n" +
            "  },\n" +
            "  \"types\": [\n" +
            "    \"http://onto.fel.cvut.cz/ontologies/fta-fmea-application/FaultTree\"\n" +
            "  ],\n" +
            "  \"@context\": {\n" +
            "    \"name\": \"http://onto.fel.cvut.cz/ontologies/fta-fmea-application/hasName\",\n" +
            "    \"description\": \"http://onto.fel.cvut.cz/ontologies/fta-fmea-application/hasDescription\",\n" +
            "    \"riskPriorityNumber\": \"http://onto.fel.cvut.cz/ontologies/fta-fmea-application/hasRPN\",\n" +
            "    \"probability\": \"http://onto.fel.cvut.cz/ontologies/fta-fmea-application/hasProbability\",\n" +
            "    \"severity\": \"http://onto.fel.cvut.cz/ontologies/fta-fmea-application/hasSeverity\",\n" +
            "    \"detection\": \"http://onto.fel.cvut.cz/ontologies/fta-fmea-application/hasDetection\",\n" +
            "    \"manifestingNode\": \"http://onto.fel.cvut.cz/ontologies/fta-fmea-application/isManifestedBy\",\n" +
            "    \"event\": \"http://onto.fel.cvut.cz/ontologies/fta-fmea-application/holds\",\n" +
            "    \"treeNodeType\": \"http://onto.fel.cvut.cz/ontologies/fta-fmea-application/hasTreeNodeType\",\n" +
            "    \"eventType\": \"http://onto.fel.cvut.cz/ontologies/fta-fmea-application/hasFaultEventType\",\n" +
            "    \"types\": \"@type\"\n" +
            "  }\n" +
            "}";

}