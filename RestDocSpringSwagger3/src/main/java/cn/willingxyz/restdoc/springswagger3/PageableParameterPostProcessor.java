package cn.willingxyz.restdoc.springswagger3;

import cn.willingxyz.restdoc.core.models.ParameterModel;
import cn.willingxyz.restdoc.core.models.PropertyModel;
import cn.willingxyz.restdoc.core.parse.postprocessor.IParameterPostProcessor;
import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

@AutoService(IParameterPostProcessor.class)
@Slf4j
public class PageableParameterPostProcessor implements IParameterPostProcessor {


    @Override
    public ParameterModel postProcess(ParameterModel model, Parameter parameter) {
        if (Pageable.class.isAssignableFrom(parameter.getType())) {
            Map<String, String> nameMap = ImmutableMap.<String, String>builder()
                .put("pageSize", "size")
                .put("pageNumber", "page")
                .put("sort", "sort").build();
            Map<String, String> descMap = ImmutableMap.<String, String>builder()
                .put("pageSize", "每页数量")
                .put("pageNumber", "页码")
                .put("sort", "排序").build();

            List<PropertyModel> propertyModels = model.getChildren().stream()
                .filter(m -> nameMap.containsKey(m.getName()))
                .peek(m -> {
                    m.setDescription(descMap.get(m.getName()));
                    m.setRequired(false);
                    if (m.getName().equals("sort")) {
                        m.setChildren(null);
                        m.setPropertyType(String.class);
                        m.getPropertyItem().setPropertyType(String.class);
                    }
                    m.setName(nameMap.get(m.getName()));
                }).collect(Collectors.toList());
            model.setChildren(propertyModels);
        }

        return model;
    }
}
