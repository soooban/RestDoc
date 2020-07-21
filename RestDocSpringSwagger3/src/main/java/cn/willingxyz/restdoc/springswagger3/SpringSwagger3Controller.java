package cn.willingxyz.restdoc.springswagger3;

import cn.willingxyz.restdoc.core.parse.IRestDocParser;
import cn.willingxyz.restdoc.swagger.common.SwaggerUIConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpringSwagger3Controller {

    private final SwaggerUIConfiguration _uiConfiguration;
    private final IRestDocParser _docParser;
    private String _docCache;

    private final ObjectMapper _objectMapper = new ObjectMapper();

    public SpringSwagger3Controller(@Qualifier("swaggerParser") IRestDocParser docParser, @Autowired SwaggerUIConfiguration uiConfiguration)
    {
        _docParser = docParser;
        _uiConfiguration = uiConfiguration;
    }

    @CrossOrigin("*")
    @GetMapping(value = {"/swagger.json"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String swaggerJson()
    {
        _docCache = _docParser.parse();
        return _docCache;
    }

    @GetMapping(value = {"/swagger/swaggerUIConfiguration"})
    public String swaggerUIConfiguration() throws JsonProcessingException {
        return _objectMapper.writeValueAsString(_uiConfiguration);
    }
}
