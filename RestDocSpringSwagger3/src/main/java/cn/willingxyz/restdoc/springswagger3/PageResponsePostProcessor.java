package cn.willingxyz.restdoc.springswagger3;

import cn.willingxyz.restdoc.core.models.ResponseModel;
import cn.willingxyz.restdoc.core.parse.postprocessor.IResponsePostProcessor;
import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

@AutoService(IResponsePostProcessor.class)
@Slf4j
public class PageResponsePostProcessor implements IResponsePostProcessor {

    @Override
    public ResponseModel postProcess(ResponseModel model, Method method) {
        if (!Page.class.isAssignableFrom(method.getReturnType())) {
            return model;
        }

        Map<String, String> descMap = ImmutableMap.<String, String>builder()
            .put("totalPages", "总页数")
            .put("totalElements", "总数量")
            .put("size", "每页数量")
            .put("number", "当前页").build();

        Set<String> notRequiredKey = ImmutableSet.of("pageable", "last", "first", "sort", "empty", "numberOfElements");

        model.getReturnModel().getChildren().forEach(c -> {
            if (descMap.containsKey(c.getName())) {
                c.setDescription(descMap.get(c.getName()));
            }
            if (notRequiredKey.contains(c.getName())) {
                c.setRequired(false);
            }
        });

        return model;
    }
}
